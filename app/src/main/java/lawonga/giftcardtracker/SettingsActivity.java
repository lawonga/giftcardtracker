package lawonga.giftcardtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
                if(position == 0) {
                    FragmentManager fragmentManager = getFragmentManager();
                    ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
                    changePasswordDialog.show(fragmentManager, "reset_password");
                }
                if(position == 1){
                    FragmentManager fragmentManager = getFragmentManager();
                    ChangeEmailDialog changeEmailDialog= new ChangeEmailDialog();
                    changeEmailDialog.show(fragmentManager, "reset_email");
                }


                    // Logout code
                if (position == 3) {
                    CardListCreator.clearadapter();
                    ParseUser.logOut();
                    if (ParseUser.getCurrentUser() == null) {
                        setResult(RESULT_OK, null);
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

    public class ChangePasswordDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Change password code
            // Builder builds the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final TextView newPasswordTextView = new TextView(getActivity());
            final EditText newPasswordEditText = new EditText(getActivity());
            newPasswordTextView.setText("Enter your new password");
            newPasswordEditText.setSingleLine();
            newPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout container = new LinearLayout(getActivity());
            container.setOrientation(LinearLayout.VERTICAL);
            // Get 10 dp to pixels to set the margins
            final float scale = getActivity().getResources().getDisplayMetrics().density;
            layoutParams.setMargins(ReusableLogic.densityPixel(scale, 10), 0, ReusableLogic.densityPixel(scale, 10), 0);
            newPasswordTextView.setLayoutParams(layoutParams);
            newPasswordEditText.setLayoutParams(layoutParams);
            container.setLayoutParams(layoutParams);
            container.addView(newPasswordEditText);
            container.addView(newPasswordTextView);
            builder.setView(container);
            builder.setMessage("Password Change");
            // Set the buttons to do whatever
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (newPasswordEditText.getText().toString() != "") {
                        ParseUser parseUser = ParseUser.getCurrentUser();
                        parseUser.setPassword(newPasswordEditText.getText().toString());
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
                                    changePasswordDialog.show(fragmentManager, "reset_password");
                                } else Toast.makeText(getBaseContext(), "Password change successful", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return builder.create();
        }
    }

    public class ChangeEmailDialog extends DialogFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Change password code
            // Builder builds the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final TextView newPasswordTextView = new TextView(getActivity());
            final EditText newEmailEditText = new EditText(getActivity());
            newPasswordTextView.setText("Enter your new email");
            newEmailEditText.setSingleLine();
            LinearLayout container = new LinearLayout(getActivity());
            container.setOrientation(LinearLayout.VERTICAL);
            final float scale = getActivity().getResources().getDisplayMetrics().density;
            layoutParams.setMargins(ReusableLogic.densityPixel(scale, 10), 0, ReusableLogic.densityPixel(scale, 10), 0);
            newPasswordTextView.setLayoutParams(layoutParams);
            newEmailEditText.setLayoutParams(layoutParams);
            container.setLayoutParams(layoutParams);
            container.addView(newEmailEditText);
            container.addView(newPasswordTextView);
            builder.setView(container);
            builder.setMessage("Email Change");
            // Set the buttons to do whatever
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (newEmailEditText.getText().toString() != "") {
                        ParseUser parseUser = ParseUser.getCurrentUser();
                        parseUser.setEmail(newEmailEditText.getText().toString());
                        parseUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e!=null){
                                    FragmentManager fragmentManager = getFragmentManager();
                                    ChangeEmailDialog changeEmailDialog= new ChangeEmailDialog();
                                    changeEmailDialog.show(fragmentManager, "reset_email");
                                } else Toast.makeText(getBaseContext(), "Email change successful", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return builder.create();
        }
    }
}
