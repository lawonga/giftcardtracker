package lawonga.giftcardtracker;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lawonga on 9/27/2015.
 */
public class CardView extends AppCompatActivity {
    String cardname, cardId, cardNotes;
    int cardposition;
    double cardbalance;
    private TextView cardnameview;
    public static TextView cardbalanceview;
    private EditText cardnotesview;
    private Button cardadd, cardsubtract;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Checks to see if we're in archived cards or my cards
        MenuItem archiveMenu = menu.findItem(R.id.archive);
        MenuItem unarchiveMenu = menu.findItem(R.id.unarchive);
        if (LogonActivity.currentcard == 1) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainViewActivity.active = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.card_layout);

        // Initialize stuff
        cardnameview = (TextView) findViewById(R.id.card_name);
        cardbalanceview = (TextView) findViewById(R.id.card_balance);
        cardnotesview = (EditText)findViewById(R.id.note_view);
        cardadd = (Button) findViewById(R.id.card_add);
        cardsubtract = (Button) findViewById(R.id.card_subtract);

        // Get data from CardListCreator
        Intent intent = getIntent();
        cardId = intent.getStringExtra("cardId");
        cardposition = intent.getIntExtra("cardposition", -1);
        cardname = intent.getStringExtra("cardname");
        cardNotes = intent.getStringExtra("cardnotes");
        cardbalance = intent.getDoubleExtra("cardbalance", cardbalance);

        // Sets the texts from data we just got
        cardnameview.setText(cardname);
        cardbalanceview.setText(Double.toString(cardbalance));
        cardnotesview.setText(cardNotes);

        // Enables action bar & home button
        setTitle(cardname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Set the bundles so we dont duplicate code
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

        // Set action for adding money
        cardadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putBoolean("add_or_subtract", true);
                modify_card.show(modify_card_fm, "modify_card");
            }
        });

        // Set action for spending money
        cardsubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putBoolean("add_or_subtract", false);
                modify_card.show(modify_card_fm, "modify_card");
            }
        });
    }

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
            NewCardFragment.createCard(nametxt, initialbalancetxt, "Archive", cardNotes);
            deleteCurrentCard();
            Toast.makeText(getApplication(), "Card Archived", Toast.LENGTH_LONG).show();
        } else if (id == R.id.unarchive){
            NewCardFragment.createCard(nametxt, initialbalancetxt, "DataBase", cardNotes);
            deleteCurrentCard();
            Toast.makeText(getApplication(), "Card Unarchived", Toast.LENGTH_LONG).show();
        } else if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return false;
    }

    public void deleteCurrentCard(){
        // Delete card in cloud
        Map<String, Object> map = new HashMap<>();
        map.put("currentCard", LogonActivity.currentcard);
        map.put("cardId", cardId);

        if (isNetworkConnected()) {
            ParseCloud.callFunctionInBackground("deletecard", map, new FunctionCallback<String>() {
                @Override
                public void done(String s, ParseException e) {
                    if (e != null) Log.e("Error ", e.toString());
                    else Log.e("Success", "aw ye");
                }
            });
            CardListCreator.cardData.remove(cardposition);
            CardListCreator.adapter.notifyDataSetChanged();
            finish();
        } else {
            ParseObject object = ParseObject.createWithoutData("DataBase", cardId);
            object.fetchFromLocalDatastoreInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e==null){
                        parseObject.deleteEventually();
                        Log.e("Offline", "Save Successful");
                    } else {
                        Log.e("Offline", "Save failed");
                    }
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Saves data to cloud (forcefully, without getting object first, using pointers)
        ParseObject point = ParseObject.createWithoutData("DataBase", cardId);
        point.put("cardnotes", cardnotesview.getText().toString());
        CardListAdapter cardListAdapter = new CardListAdapter(cardname, Double.valueOf(cardbalanceview.getText().toString()), cardnotesview.getText().toString(), cardId);
        CardListCreator.cardData.set(cardposition, cardListAdapter);
        CardListCreator.adapter.notifyDataSetChanged();
        point.saveEventually();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardview, menu);
        return true;
    }

    // Network state check
    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }
}
