package lawonga.giftcardtracker;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    String cardname;
    double cardbalance;
    private TextView cardnameview, cardbalanceview;
    private EditText cardnotes;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.delete){

            // Delete card
            // Cloud Delete
            ParseUser user = ParseUser.getCurrentUser();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DataBase");
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
            CardDataBase.cardnamex.remove(cardname);
            CardDataBase.cardbalancex.remove(Double.valueOf(cardbalance));
            CardDataBase.carddatas.remove(new CardDataBase(cardname, cardbalance));

            MainViewActivity.afterBackPressed();
            CardList.logger();
            finish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CardList.logger();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cardview, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.card_layout);


        cardnameview = (TextView)findViewById(R.id.card_name);
        cardbalanceview = (TextView)findViewById(R.id.card_balance);

        //Get data from CardList
        Intent intent = getIntent();
        cardname = intent.getStringExtra("cardname");
        cardbalance = intent.getDoubleExtra("cardbalance", cardbalance);

        // Sets the texts from data we just got
        cardnameview.setText(cardname);
        cardbalanceview.setText(Double.toString(cardbalance));

        // Enables action bar & home button
        /*actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);*/
        setTitle(cardname);


    }

}
