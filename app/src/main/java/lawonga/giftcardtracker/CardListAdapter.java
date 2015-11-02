package lawonga.giftcardtracker;


import android.util.Log;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lawonga on 9/27/2015.
 */
public class CardListAdapter {
    public String cardname, cardnotes, objectId;
    public double cardbalance;

    public CardListAdapter(String cardname, double cardbalance, String cardnotes, String objectId){
        this.cardname = cardname;
        this.cardbalance = cardbalance;
        this.objectId = objectId;
        this.cardnotes = cardnotes;
    }

    public String getCardName(){
        return cardname;
    }
    public double getCardBalance(){
        return cardbalance;
    }
    public String getObjectId(){
        return objectId;
    }
    public String getCardNotes() {
        return cardnotes;
    }

    public static void queryList(){
        // Querys list from the cloudcode via retrievecard.js; retrieves all card

        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", ParseUser.getCurrentUser().getObjectId());
        map.put("currentCard", LogonActivity.currentcard);
        ParseCloud.callFunctionInBackground("retrievecard", map, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(ArrayList<ParseObject> parseObjects, ParseException e) {
                if (e==null){
                    Log.e("ParseObject", parseObjects.toString());
                    String cardnameobject, objectID, cardnotes;
                    Double cardnamebalance;
                    for (ParseObject object : parseObjects){
                        cardnameobject = object.getString("cardname");
                        cardnamebalance = object.getDouble("balance");
                        cardnotes = object.getString("cardnotes");
                        objectID = object.getObjectId();
                        CardListCreator.cardData.add(new CardListAdapter(cardnameobject, cardnamebalance, cardnotes, objectID));
                    }
                    CardListCreator.notifychangeddata();
                } else {
                    Log.e("Error ", e.toString());
                }
            }

        });
    }
}
