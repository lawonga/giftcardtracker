package lawonga.giftcardtracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by lawonga on 11/16/2015.
 */
public class SplashScreen extends Activity {
    public final int SPLASH_DISPLAY_LENGTH = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        Thread thread = new Thread(){
            public void run (){
                try{
                    sleep(SPLASH_DISPLAY_LENGTH);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    overridePendingTransition(0, R.anim.fade_out);
                    finish();
                    Intent splashScreenIntent = new Intent(SplashScreen.this, LogonActivity.class);
                    startActivity(splashScreenIntent);
                }
            }
        };
        thread.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
