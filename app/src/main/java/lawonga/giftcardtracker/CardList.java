package lawonga.giftcardtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by lawonga on 9/26/2015.
 */
public class CardList extends ListFragment {
    public static CustomAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<CardDataBase> CardData = CardDataBase.getCardData();
        // Custom View Layout
        adapter = new CustomAdapter(getActivity(), CardData);
        setListAdapter(adapter);
    }

    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Toast.makeText(getActivity(), "On long click listener", Toast.LENGTH_LONG).show();
                // Cloud Delete
                ParseUser user = ParseUser.getCurrentUser();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("DataBase");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject object : list) {
                            String cardname = object.getString("cardname");
                            if (CardDataBase.cardnamex.get(position).equals(cardname)) {
                                object.deleteInBackground();
                            }
                        }
                    }
                });
                CardDataBase.cardnamex.remove(CardDataBase.cardnamex.get(position));
                CardDataBase.cardbalancex.remove(Double.valueOf(CardDataBase.cardbalancex.get(position)));
                CardDataBase.carddatas.remove(new CardDataBase(CardDataBase.cardnamex.get(position), CardDataBase.cardbalancex.get(position)));
                CardList.notifychangeddata();
                adapter.clear();
                CardDataBase.queryList();
                CardList.notifychangeddata();
                return true;
            }
        });
    }*/

    public static void notifychangeddata() {
        adapter.notifyDataSetChanged();
    }

    public static void clearadapter() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(final ListView l, View v, int position, long id) {
        String cardname = CardDataBase.cardnamex.get(position);
        Double cardbalance = CardDataBase.cardbalancex.get(position);
        Toast.makeText(v.getContext(), "Position: "+position+", Cards:"+cardname, Toast.LENGTH_LONG).show();
        logger();
        Intent intent = new Intent(v.getContext(), CardView.class);
        intent.putExtra("cardbalance", cardbalance);
        intent.putExtra("cardname", cardname);
        startActivity(intent);
    }

    public static void logger(){
        for (int i=0; i<CardDataBase.cardnamex.size(); i++) {
            Log.w("myApp", CardDataBase.cardnamex.get(i));
        }
    }

}
