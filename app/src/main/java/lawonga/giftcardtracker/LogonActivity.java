package lawonga.giftcardtracker;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by lawonga on 9/28/2015.
 * This is the main login screen, with USER & PASSWORD as well as a forgot password and register button
 */
public class LogonActivity extends AppCompatActivity {

    // Register things
    String passwordtxt, usernametxt;
    TextView registerhere;
    EditText username, password;
    Button login, forgotpassword;
    public static int currentcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ParseInitialization();


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null){
            Log.e("currentuser", currentUser.toString());
            Intent intent = new Intent(LogonActivity.this, MainViewActivity.class);
            startActivity(intent);
            this.finish();
        }

        currentcard = 0;
        setContentView(R.layout.login_screen);

        // Import buttons, textviews, edittexts...
        registerhere = (TextView)findViewById(R.id.register_here_textviewbutton);
        username = (EditText)findViewById(R.id.username_login);
        password = (EditText)findViewById(R.id.password_login);
        login = (Button)findViewById(R.id.login_button);
        forgotpassword = (Button)findViewById(R.id.forgot_password_button);


        // This starts up the new user screen
        registerhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), NewUser.class);
                startActivity(intent);
            }
        });

        // This sets the login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    login.setEnabled(false);
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
}
