package lawonga.giftcardtracker;


import android.util.Log;
import android.view.View;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    static String accesslocation;
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
        if (objectId == null){
            return "temp_id";
        } else return objectId;
    }
    public String getCardNotes() {
        return cardnotes;
    }

    public static void queryList(){
        if (LogonActivity.currentcard == 0){
            accesslocation = "DataBase";
        } else if (LogonActivity.currentcard == 1){
            accesslocation = "Archive";
        }
        if (MainViewActivity.networkStatus) {
            // Unpins all as to avoid duplicates
            try {
                ParseObject.unpinAll();
                Log.e("Unpin Objects", "success");
            } catch (ParseException ignored){}
            cloudQuery(0);
            cloudQuery(1);
        } else if (!MainViewActivity.networkStatus || !CardView.isNetworkConnected) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(accesslocation);
            query.orderByAscending("cardname");
            query.fromPin(String.valueOf(LogonActivity.currentcard));
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
                            CardListCreator.notifychangeddata();
                        }
                        Log.e("Current request", String.valueOf(LogonActivity.currentcard));
                        MainViewActivity.animatedCircleLoadingView.stopOk();
                        MainViewActivity.animatedCircleLoadingView.setVisibility(View.INVISIBLE);
                    } else {
                        Log.e("Local Datastore", e.toString());
                        MainViewActivity.animatedCircleLoadingView.stopFailure();
                        MainViewActivity.animatedCircleLoadingView.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Log.e("Network State", "OFF");
        }
    }

    public static void cloudQuery(final int cardTarget){
        // Querys list from the cloudcode via retrievecard.js; retrieves all card
        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", ParseUser.getCurrentUser().getObjectId());
        map.put("currentCard", cardTarget);
        ParseCloud.callFunctionInBackground("retrievecard", map, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(ArrayList<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Log.e("ParseObject", parseObjects.toString());
                    if (cardTarget == LogonActivity.currentcard) {
                        // Grabs data from the downloaded object
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
                        MainViewActivity.animatedCircleLoadingView.stopOk();
                        MainViewActivity.animatedCircleLoadingView.setVisibility(View.INVISIBLE);
                    }
                    CardListCreator.notifychangeddata();
                    // Repins the updated data
                    ParseObject.pinAllInBackground(String.valueOf(cardTarget), parseObjects);
                } else {
                    Log.e("Download Error ", e.toString());
                    MainViewActivity.animatedCircleLoadingView.stopFailure();
                    MainViewActivity.animatedCircleLoadingView.setVisibility(View.INVISIBLE);
                }
            }

        });
    }
}
