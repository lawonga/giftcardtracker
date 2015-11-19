package lawonga.giftcardtracker;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.Map;

import lawonga.giftcardtracker.CardLogic.CardListAdapter;
import lawonga.giftcardtracker.CardLogic.CardListCreator;
import lawonga.giftcardtracker.DialogFragments.ModifyCardFragment;
import lawonga.giftcardtracker.DialogFragments.NewCardFragment;

/**
 * Created by lawonga on 9/27/2015.
 */
public class CardActivity extends AppCompatActivity {
    Bitmap cardpicturebmp;
    byte[] cardpicturefile;
    String cardname, cardId, cardNotes, cardcode;
    int cardposition;
    double cardbalance;
    private TextView cardnameview;
    public static TextView cardbalanceview;
    private EditText cardnotesview, cardcodeET;
    private Button cardadd, cardsubtract;
    private ImageView cardpicture;
    boolean networkstatus;
    public static boolean isNetworkConnected;
    public static Toolbar toolbar;
    public static CollapsingToolbarLayout collapsingToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainViewActivity.active = false;
        setContentView(R.layout.card_layout);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.pureBlack));

        // Initialize stuff
        cardnameview = (TextView) findViewById(R.id.card_name);
        cardbalanceview = (TextView) findViewById(R.id.card_balance);
        cardnotesview = (EditText)findViewById(R.id.note_view);
        cardadd = (Button) findViewById(R.id.card_add);
        cardsubtract = (Button) findViewById(R.id.card_subtract);
        cardpicture = (ImageView) findViewById(R.id.card_picture);
        cardcodeET = (EditText)findViewById(R.id.card_code);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get data from CardListCreator
        Intent intent = getIntent();
        cardId = intent.getStringExtra("cardId");
        cardposition = intent.getIntExtra("cardposition", -1);
        cardname = intent.getStringExtra("cardname");
        cardNotes = intent.getStringExtra("cardnotes");
        cardbalance = intent.getDoubleExtra("cardbalance", cardbalance);
        networkstatus = intent.getBooleanExtra("networkstatus", true);
        cardpicturefile = intent.getByteArrayExtra("cardpicture");
        cardcode = intent.getStringExtra("cardcode");

        // Bitmap decoding
        if (cardpicturefile != null) {
            cardpicturebmp = BitmapFactory.decodeByteArray(cardpicturefile, 0, cardpicturefile.length);
        }

        // Sets the texts from data we just got
        cardnameview.setText(cardname);
        cardbalanceview.setText(Double.toString(cardbalance));
        cardnotesview.setText(cardNotes);
        cardcodeET.setText(cardcode);

        // Enables action bar & home button
        collapsingToolBar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolBar.setTitle(cardname);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setTitle(cardname);

        // Set the bundles so we don't duplicate code
        final Bundle bundle = new Bundle();
        bundle.putString("cardname", cardname);
        bundle.putDouble("cardbalance", cardbalance);
        bundle.putString("cardId", cardId);
        bundle.putString("cardnotes", cardNotes);
        bundle.putInt("cardposition", cardposition);
        Log.e("CardId is:", cardId);

        // Set the values for below
        final FragmentManager modify_card_fm = getFragmentManager();
        final ModifyCardFragment modify_card = new ModifyCardFragment();
        modify_card.setArguments(bundle);

        /* add_or_subtract:
        0 = add
        1 = minus
        2 = change card name
         */
        // Set action for adding money
        cardadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt("add_or_subtract", 0);
                modify_card.show(modify_card_fm, "modify_card");
            }
        });

        // Set action for spending money
        cardsubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt("add_or_subtract", 1);
                modify_card.show(modify_card_fm, "modify_card");
            }
        });

        // Sets onClickListener for changing cardname
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt("add_or_subtract", 2);
                modify_card.show(modify_card_fm, "modify_card");
            }
        });

        // Set the picture
        cardpicture.setImageBitmap(cardpicturebmp);
    }

    /* Checks to see if we're in archived cards or my cards
    if LogInActivity.currentcard = 1, then it means we are in archive, and sets the menus accordingly
    if LogInActivity.currentcard = 0, then it means we are in my cards, and sets the menus accordingly
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem archiveMenu = menu.findItem(R.id.archive);
        MenuItem unarchiveMenu = menu.findItem(R.id.unarchive);
        if (LogInActivity.currentcard == 1) {
            archiveMenu.setVisible(false);
            archiveMenu.setEnabled(false);
            unarchiveMenu.setVisible(true);
            unarchiveMenu.setEnabled(true);
        } else {
            archiveMenu.setVisible(true);
            archiveMenu.setEnabled(true);
            unarchiveMenu.setVisible(false);
            unarchiveMenu.setEnabled(false);
        }
        return true;
    }

    // Create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardview, menu);
        return true;
    }


    /* This method does the option items
    if delete = do a delete card
    if archive = delete the card in DataBase and recreate the card in Archive
    if unarchive = deletes the card in Archive and recreate the card in Delete
    if home (press back navigation) it goes home
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.e("Current cards ", CardListCreator.cardData.toString());
        String nametxt = cardname;
        String initialbalancetxt =  String.valueOf(cardbalance);
        if (id == R.id.delete) {
            deleteCurrentCard();
            Toast.makeText(getApplication(), "Card Deleted", Toast.LENGTH_LONG).show();
        } else if (id == R.id.archive){
            // Create card in archive + delete card in cloud
            NewCardFragment.createCard(nametxt, initialbalancetxt, "Archive", cardNotes, "Archive", cardpicturebmp, cardcode);
            deleteCurrentCard();
            Toast.makeText(getApplication(), "Card Archived", Toast.LENGTH_LONG).show();
        } else if (id == R.id.unarchive){
            NewCardFragment.createCard(nametxt, initialbalancetxt, "DataBase", cardNotes, "DataBase", cardpicturebmp, cardcode);
            deleteCurrentCard();
            Toast.makeText(getApplication(), "Card Unarchived", Toast.LENGTH_LONG).show();
        } else if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return false;
    }

    /* This method is for the deleteCurrentCard... deletes the card from the cloud
    if we are connected to the internet. Otherwise deletes the card locally if we are not */
    public void deleteCurrentCard(){
        // Delete card in cloud
        Map<String, Object> map = new HashMap<>();
        map.put("currentCard", LogInActivity.currentcard);
        map.put("cardId", cardId);
        // Check if network connected before deleting
        if (isNetworkConnected()) {
            ParseCloud.callFunctionInBackground("deletecard", map, new FunctionCallback<String>() {
                @Override
                public void done(String s, ParseException e) {
                    if (e != null) Log.e("Error ", e.toString());
                    else Log.e("Success", "aw ye");
                    CardListCreator.clearadapter();
                    CardListAdapter.queryList();
                }
            });
            finish();
        } else {
            ParseObject object = ParseObject.createWithoutData("DataBase", cardId);
            object.fetchFromLocalDatastoreInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e==null){
                        parseObject.deleteEventually();
                        Log.e("Offline", "Delete Successful");
                        finish();
                    } else {
                        Log.e("Offline", "Delete failed");
                    }
                }
            });

        }
    }

    // On back press, we save data to cloud, and if network is not connected then we query the list again (to reflect that we're not connected to a network)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Saves data to cloud (forcefully, without getting object first, using pointers)
        ParseObject point = ParseObject.createWithoutData("DataBase", cardId);
        point.put("cardnotes", cardnotesview.getText().toString());
        point.put("cardcode", cardcodeET.getText().toString());
        point.saveEventually();
        Log.e ("Isnetworkconnected", String.valueOf(isNetworkConnected()));
        Log.e("networkstatus", String.valueOf(networkstatus));
        isNetworkConnected = isNetworkConnected();
        if (networkstatus && !isNetworkConnected){
            CardListAdapter.queryList();
            Log.e("networkstatus", "!isnetworkconnected");
        } else {
            CardListCreator.cardData.get(cardposition).cardnotes = cardnotesview.getText().toString();
            CardListCreator.cardData.get(cardposition).cardcode = cardcodeET.getText().toString();
        }
    }

    // Network state check; return true if we're connected to the network
    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }
}
