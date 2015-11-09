package lawonga.giftcardtracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by lawonga on 9/28/2015.
 */
public class NewUser extends Activity {
    // Initialization
    EditText name, username, email, password, passwordagain;
    Button registerbutton;
    String nametxt, usernametxt, emailtxt, passwordtxt, passwordagaintxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        name = (EditText)findViewById(R.id.name_register);
        username = (EditText)findViewById(R.id.username_register);
        email = (EditText)findViewById(R.id.email_register);
        password = (EditText)findViewById(R.id.password_register);
        passwordagain = (EditText)findViewById(R.id.password_register_again);
        registerbutton = (Button)findViewById(R.id.register_button);

        // User clicks register button
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nametxt = name.getText().toString();
                usernametxt = username.getText().toString();
                emailtxt = email.getText().toString();
                passwordtxt = password.getText().toString();
                passwordagaintxt= passwordagain.getText().toString();

                // Login setup
                if(nametxt.equals("") || usernametxt.equals("") || emailtxt.equals("") || passwordtxt.equals("") || passwordagaintxt.equals("")){
                    Toast.makeText(getApplicationContext(),"Please fill in the form completely", Toast.LENGTH_LONG).show();
                } else if(!passwordtxt.equals(passwordagaintxt)){
                    Toast.makeText(getApplicationContext(),"Please enter the same password again", Toast.LENGTH_LONG).show();
                } else {
                    // Use parse to set user/pass
                    ParseUser user = new ParseUser();
                    user.put("name", nametxt);
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.setEmail(emailtxt);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText(getApplicationContext(),"Account Created!", Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(),"Account Creation Failed: Unknown Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
