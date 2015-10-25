package lawonga.giftcardtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by lawonga on 9/26/2015.
 */
public class CardListCreator extends ListFragment {
    public static CustomAdapter adapter;
    public static ArrayList<CardListAdapter> cardData = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CardListAdapter.queryList();
        // Custom View Layout
        adapter = new CustomAdapter(getActivity(), cardData);
        setListAdapter(adapter);
    }

    public static void notifychangeddata() {
        adapter.notifyDataSetChanged();
    }

    public static void clearadapter() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(final ListView l, View v, int position, long id) {
        String cardname = cardData.get(position).getCardName();
        Double cardbalance = cardData.get(position).getCardBalance();
        String cardId = cardData.get(position).getObjectId();
        Intent intent = new Intent(v.getContext(), CardView.class);
        intent.putExtra("cardbalance", cardbalance);
        intent.putExtra("cardname", cardname);
        intent.putExtra("cardId", cardId);
        intent.putExtra("cardposition", position);
        startActivity(intent);
    }
}
