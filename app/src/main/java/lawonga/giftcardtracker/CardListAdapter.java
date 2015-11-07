package lawonga.giftcardtracker;


import android.util.Log;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
    final static String PINNED_CARD = "pinCard";

    public static void queryList(){
        if (MainViewActivity.networkStatus) {
            // Querys list from the cloudcode via retrievecard.js; retrieves all card
            Map<String, Object> map = new HashMap<>(2);
            map.put("userId", ParseUser.getCurrentUser().getObjectId());
            map.put("currentCard", LogonActivity.currentcard);
            ParseCloud.callFunctionInBackground("retrievecard", map, new FunctionCallback<ArrayList<ParseObject>>() {
                @Override
                public void done(ArrayList<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        Log.e("ParseObject", parseObjects.toString());
                        for (final ParseObject object : parseObjects) {
                            // PER card has different data, hence why these are placed in here
                            String cardnameobject, objectID, currentCardNotes;
                            Double cardnamebalance;
                            cardnameobject = object.getString("cardname");
                            cardnamebalance = object.getDouble("balance");
                            currentCardNotes = object.getString("cardnotes");
                            objectID = object.getObjectId();
                            CardListCreator.cardData.add(new CardListAdapter(cardnameobject, cardnamebalance, currentCardNotes, objectID));
                        }
                        ParseObject.pinAllInBackground(PINNED_CARD, parseObjects);
                        CardListCreator.notifychangeddata();
                    } else {
                        Log.e("Download Error ", e.toString());
                    }
                }

            });
        }  else if (!MainViewActivity.networkStatus) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("DataBase");
            query.orderByAscending("cardname");
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        for (ParseObject object : list) {
                            String cardnameobject, objectID, currentCardNotes;
                            Double cardnamebalance;
                            cardnameobject = object.getString("cardname");
                            currentCardNotes = object.getString("cardnotes");
                            cardnamebalance = object.getDouble("balance");
                            objectID = object.getObjectId();
                            CardListCreator.cardData.add(new CardListAdapter(cardnameobject, cardnamebalance, currentCardNotes, objectID));
                            Log.e("Local Datastore", objectID);
                        }
                    } else {
                        Log.e("Local Datastore", e.toString());
                    }
                }
            });
            Log.e("Network State", "OFF");
        }
    }
}
