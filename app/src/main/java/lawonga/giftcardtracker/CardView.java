package lawonga.giftcardtracker;

import android.app.FragmentManager;
import android.content.Intent;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainViewActivity.active = false;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.card_layout);

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
        if (id == R.id.delete) {
            // Delete card
            // Cloud Delete
            ParseUser user = ParseUser.getCurrentUser();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DataBase");
            query.whereEqualTo("user", user);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    for (ParseObject object : list) {
                        String cardnameobject = object.getString("cardname");
                        if (cardname.equals(cardnameobject)) {
                            try {
                                object.delete();
                            } catch (ParseException e1) {
                                object.deleteInBackground();
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            });
            CardListCreator.cardData.remove(cardposition);
            CardListCreator.adapter.notifyDataSetChanged();
            finish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Back press = execute savedata
        savedata();
    }

    // Saves data to cloud (forcefully, without getting object first, using pointers)
    public void savedata(){
        ParseObject point = ParseObject.createWithoutData("DataBase", cardId);
        point.put("cardnotes", cardnotesview.getText().toString());
        point.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    // Saved successfully
                    Log.e("Save status ", "OK");
                    CardListCreator.cardData.set(cardposition, new CardListAdapter(cardname, Double.valueOf(cardbalanceview.getText().toString()), cardnotesview.getText().toString(), cardId));
                } else {
                    Log.e("Save status ", e.toString());
                }
                CardListCreator.notifychangeddata();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardview, menu);
        return true;
    }
}
