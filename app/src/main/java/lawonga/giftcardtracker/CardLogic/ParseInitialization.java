package lawonga.giftcardtracker.CardLogic;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

import lawonga.giftcardtracker.Logic.keys;

/**
 * Created by lawonga on 9/28/2015.
 */
public class ParseInitialization extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Parse Initialization
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, keys.key1, keys.key2);
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
