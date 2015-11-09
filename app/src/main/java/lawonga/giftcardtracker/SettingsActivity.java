package lawonga.giftcardtracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lawonga on 9/26/2015.
 */
public class SettingsActivity extends AppCompatActivity{
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.settings_layout);
        setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        final ListView settingListView = (ListView)findViewById(R.id.listview_settings);
        //
        String[] menuitems= new String[] {"Change Password", "Change Email", "Account Management", "Logout"};
        final ArrayList<String> settinglist = new ArrayList<>();
        for (int i=0; i<menuitems.length; i++){
            settinglist.add(menuitems[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, settinglist);
        settingListView.setAdapter(adapter);
        final Intent logoutIntent = new Intent(this, LogonActivity.class);
        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                switch (position){
                    // Change password code
                    case 0:
                        // Builder builds the dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
                        final EditText newpasswordEditText = new EditText(getApplication());
                        final TextView newPasswordTextView = new TextView(getApplication());
                        newPasswordTextView.setText("Enter your new password");
                        newpasswordEditText.setSingleLine();
                        newpasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        LinearLayout container = new LinearLayout(getApplication());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        container.setOrientation(LinearLayout.VERTICAL);
                        // Get 10 dp to pixels to set the margins
                        final float scale = getApplication().getResources().getDisplayMetrics().density;
                        int pixelMargin = (int) (20 * scale + 0.5f);
                        layoutParams.setMargins(pixelMargin, 0, pixelMargin, 0);
                        container.setLayoutParams(layoutParams);
                        container.addView(newpasswordEditText);
                        container.addView(newPasswordTextView);
                        builder.setView(container);
                        builder.setMessage("Password Reset");
                        // Set the buttons to do whatever
                        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (newpasswordEditText.getText().toString() != ""){
                                    ParseUser parseUser = ParseUser.getCurrentUser();
                                    parseUser.setPassword(newpasswordEditText.getText().toString());
                                    Toast.makeText(getApplication(), "Password reset!", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                    case 1:

                    case 2:

                    // Logout code
                    case 3:
                        CardListCreator.clearadapter();
                        ParseUser.logOut();
                        if (ParseUser.getCurrentUser() == null) {
                            startActivity(logoutIntent);
                            finish();
                        } else {
                            Log.e("Logout", "Failed");
                        }
                }
            }
        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String>{
        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context,  int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }
        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
