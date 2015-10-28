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
    public String cardname, objectId;
    public double cardbalance;

    public CardListAdapter(String cardname, double cardbalance, String objectId){
        this.cardname = cardname;
        this.cardbalance = cardbalance;
        this.objectId = objectId;
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

    public static void queryList(){
        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", ParseUser.getCurrentUser().getObjectId());
        map.put("poopId", "poop");
        ParseCloud.callFunctionInBackground("retrievecard", map, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(ArrayList<ParseObject> parseObjects, ParseException e) {
                if (e==null){
                    Log.e("Result", "Success");
                    Log.e("ParseObject", parseObjects.toString());
                } else {
                    Log.e("Error: ", e.toString());
                }

                /*for (ParseObject object : parseObjects){
                    /*String cardnameobject, objectID, cardnamebalance;
                    cardnameobject = object.getString("cardname");
                    cardnamebalance = object.getString("cardbalance");
                    objectID = object.getObjectId();
                    Log.e("Retrieved", cardnameobject + cardnamebalance + objectID);
                    CardListCreator.cardData.add(new CardListAdapter(cardnameobject, Double.valueOf(cardnamebalance), objectID));
                }*/
            }

        });


        /*********************************/
        /*
        ParseUser user = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("DataBase");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objectList, ParseException e) {
                String cardnameobject, objectID;
                Double cardnamebalance;
                if (e == null) {
                    for (ParseObject object : objectList) {
                        cardnameobject = object.getString("cardname");
                        cardnamebalance = object.getDouble("balance");
                        objectID = object.getObjectId();
                        CardListCreator.cardData.add(new CardListAdapter(cardnameobject, cardnamebalance, objectID));
                    }
                    CardListCreator.notifychangeddata();
                } else {
                    CardListCreator.notifychangeddata();
                }
            }
        });
        */
    }
}
