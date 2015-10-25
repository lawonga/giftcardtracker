package lawonga.giftcardtracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.List;

/**
 * Created by lawonga on 9/27/2015.
 */
public class CardView extends AppCompatActivity {
    String cardname, cardId;
    int cardposition;
    double cardbalance;
    private TextView cardnameview, cardbalanceview;
    private EditText cardnotes;
    private Button cardadd, cardsubtract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.card_layout);

        cardnameview = (TextView)findViewById(R.id.card_name);
        cardbalanceview = (TextView)findViewById(R.id.card_balance);
        cardadd = (Button)findViewById(R.id.card_add);
        cardsubtract = (Button)findViewById(R.id.card_subtract);

        //Get data from CardListCreator
        Intent intent = getIntent();
        cardId = intent.getStringExtra("cardId");
        cardposition = intent.getIntExtra("cardposition", -1);
        cardname = intent.getStringExtra("cardname");
        cardbalance = intent.getDoubleExtra("cardbalance", cardbalance);

        // Sets the texts from data we just got
        cardnameview.setText(cardname);
        cardbalanceview.setText(Double.toString(cardbalance));

        // Enables action bar & home button
        setTitle(cardname);

        // Set the bundles so we dont duplicate code
        final Bundle bundle = new Bundle();
        bundle.putString("cardname", cardname);
        bundle.putDouble("cardbalance", cardbalance);
        bundle.putString("cardId", cardId);

        // Set action for adding money
        cardadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putBoolean("add_or_subtract", true);
                FragmentManager modify_card_fm = getFragmentManager();
                ModifyCardFragment modify_card = new ModifyCardFragment();
                modify_card.setArguments(bundle);
                modify_card.show(modify_card_fm, "modify_card");
            }
        });

        // Set action for spending money
        cardsubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putBoolean("add_or_subtract", false);
                FragmentManager modify_card_fm = getFragmentManager();
                ModifyCardFragment modify_card = new ModifyCardFragment();
                modify_card.setArguments(bundle);
                modify_card.show(modify_card_fm, "modify_card");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.delete){
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
            // MainViewActivity.afterBackPressed();
            finish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardview, menu);
        return true;
    }



}
