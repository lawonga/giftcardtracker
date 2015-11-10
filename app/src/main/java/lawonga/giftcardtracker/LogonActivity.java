package lawonga.giftcardtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

/**
 * Created by lawonga on 9/28/2015.
 * This is the main login screen, with USER & PASSWORD as well as a forgot password and register button
 */
public class LogonActivity extends AppCompatActivity {

    // Register things
    String passwordtxt, usernametxt;
    TextView registerhere;
    EditText username, password;
    Button login;
    static Button forgotpassword;
    public static int currentcard;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ParseInitialization();


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.e("currentuser", currentUser.toString());
            Intent intent = new Intent(LogonActivity.this, MainViewActivity.class);
            startActivity(intent);
            this.finish();
        }

        currentcard = 0;
        setContentView(R.layout.login_layout);

        // Import buttons, textviews, edittexts...
        registerhere = (TextView) findViewById(R.id.register_here_textviewbutton);
        username = (EditText) findViewById(R.id.username_login);
        password = (EditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.login_button);
        forgotpassword = (Button) findViewById(R.id.forgot_password_button);


        // User clicks register
        registerhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), NewUser.class);
                startActivity(intent);
            }
        });

        // User clicks forgot password
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                ResetEmailDialog resetEmailDialog = new ResetEmailDialog();
                resetEmailDialog.show(fragmentManager, "reset_email");
            }
        });

        // User clicks log in
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    login.setEnabled(false);
                    registerhere.setEnabled(false);
                    forgotpassword.setEnabled(false);
                    usernametxt = username.getText().toString();
                    passwordtxt = password.getText().toString();
                    ParseUser.logInInBackground(usernametxt, passwordtxt, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (parseUser != null) {
                                Intent intent = new Intent(LogonActivity.this, MainViewActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Sorry, your username or password is incorrect.", Toast.LENGTH_LONG).show();
                                login.setEnabled(true);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No network connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class ResetEmailDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final EditText emailEditText = new EditText(getActivity());
            emailEditText.setSingleLine();
            LinearLayout container = new LinearLayout(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            // Get 10 dp to pixels to set the margins
            final float scale = getActivity().getResources().getDisplayMetrics().density;
            int pixelMargin = (int) (20 * scale + 0.5f);

            layoutParams.setMargins(pixelMargin, 0, pixelMargin, 0);
            emailEditText.setLayoutParams(layoutParams);
            container.addView(emailEditText);
            builder.setView(container);
            builder.setMessage("Enter your email to reset password")
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String email = emailEditText.getText().toString();
                            ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplication(), "An email has been dispatched with password reset instructions", Toast.LENGTH_LONG).show();
                                        dismiss();
                                    } else {
                                        Toast.makeText(getApplication(), "Error. Please enter a valid email address.", Toast.LENGTH_LONG).show();
                                        LogonActivity.forgotpassword.performClick();
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
            return builder.create();
        }
    }

}
