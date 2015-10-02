package lawonga.giftcardtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
public class MainActivity extends AppCompatActivity {

    // Register things
    String passwordtxt, usernametxt;
    TextView registerhere;
    EditText username, password;
    Button login, forgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ParseInitialization();
        setContentView(R.layout.login_screen);

        // Import buttons, textviews, edittexts...
        registerhere = (TextView)findViewById(R.id.register_here_textviewbutton);
        username = (EditText)findViewById(R.id.username_login);
        password = (EditText)findViewById(R.id.password_login);
        login = (Button)findViewById(R.id.login_button);
        forgotpassword = (Button)findViewById(R.id.forgot_password_button);


        // REGISTER ONCLICK LISTENER
        registerhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterScreen();
            }
        });

        //LOGIN ONCLICK LISTENER
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                ParseUser.logInInBackground(usernametxt, passwordtxt, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (parseUser != null){
                            Intent intent = new Intent(MainActivity.this, MainViewActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sorry, your username or password is incorrect.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public void openRegisterScreen(){
        Intent intent = new Intent(this, RegisterScreen.class);
        startActivity(intent);
    }
}
