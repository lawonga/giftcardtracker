package lawonga.giftcardtracker;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

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
        ParseUser user = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("DataBase");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objectList, ParseException e) {
                String cardnameobject = "", objectID = "";
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
    }
}
