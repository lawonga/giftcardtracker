package lawonga.giftcardtracker;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lawonga on 9/27/2015.
 */
public class CardDataBase {
    public static ArrayList<CardDataBase> carddatas;
    public static List<String> cardnamex = new ArrayList<String>();
    public static List<Double> cardbalancex = new ArrayList<Double>();
    // public static String[] cardnamex = new String[]{""};
    // public static Double[] cardbalancex = new Double[]{0.00};
    public String cardname;
    public double cardbalance;

    public CardDataBase(String cardname, double cardbalance){
        this.cardname = cardname;
        this.cardbalance = cardbalance;
    }

    public static ArrayList<CardDataBase> getCardData(){
        carddatas = new ArrayList<CardDataBase>();
        for (int i=0; i<cardnamex.size()-1; i++) {
            if(cardnamex.contains(cardnamex.get(i))){
                continue;
            }
            carddatas.add(new CardDataBase(cardnamex.get(i), cardbalancex.get(i)));
        }
        return carddatas;
    }

    public static void queryList(){
        ParseUser user = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("DataBase");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objectList, ParseException e) {
                String cardnameobject = "", cardnamebalance = "", objectID = "";
                if (e==null) {
                    for(ParseObject object : objectList) {
                        cardnameobject = object.getString("cardname");
                        cardnamebalance = object.getString("balance");
                        objectID = object.getString("objectID");
                        NewCard(cardnameobject,cardnamebalance, objectID);
                        CardList.notifychangeddata();
                    }
                } else {
                    CardList.notifychangeddata();
                }
            }
        });
    }

    // Required to clear lists
    public static void clearList(){
        cardnamex.clear();
        cardbalancex.clear();
        carddatas.clear();
        CardList.notifychangeddata();
    }


    public static ArrayList<CardDataBase> NewCard(String cardnameobject, String cardnamebalance, String objectID){
        cardnamex.add(cardnameobject);
        cardbalancex.add(Double.parseDouble(cardnamebalance));
        carddatas.add(new CardDataBase(cardnamex.get(cardnamex.size()-1),cardbalancex.get(cardbalancex.size()-1)));
        CardList.notifychangeddata();
        return carddatas;
    }
}
