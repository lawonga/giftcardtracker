package lawonga.giftcardtracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by lawonga on 9/26/2015.
 *
 * This class handles all of the cards being created in each of the listviews within the container.
 * Instead of going to find the ListView in the XML, set everything programmatically here.
 * 1 class run = 1 new card
 * Using CardListAdapter.querylist to run and grab a card (may take some time)
 * Creates the blank adapter known as CardData first
 * CardListAdapter places things into the CardData and notifies the list to add the stuff in and update
 */
public class CardListCreator extends ListFragment {
    public static CustomAdapter adapter;
    public static ArrayList<CardListAdapter> cardData = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Custom View Layout
        if (adapter == null) {
            CardListAdapter.queryList();
            adapter = new CustomAdapter(getActivity(), cardData);
        }
        setListAdapter(adapter);
    }

    // Sets things after view has been created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = getListView();
        listView.setDivider(null);
        listView.setDividerHeight(0);
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
        String cardnotes = cardData.get(position).getCardNotes();
        String cardId = cardData.get(position).getObjectId();
        Bitmap cardpic = cardData.get(position).getCardPic();
        byte[] byteArray = null;
        // Convert cardpic to bytearray into intent
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(cardpic != null) {
            cardpic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }


        Intent intent = new Intent(v.getContext(), CardView.class);
        intent.putExtra("networkstatus", MainViewActivity.networkStatus);
        intent.putExtra("cardposition", position);
        intent.putExtra("cardbalance", cardbalance);
        intent.putExtra("cardname", cardname);
        intent.putExtra("cardId", cardId);
        intent.putExtra("cardnotes", cardnotes);
        intent.putExtra("cardpicture", byteArray);
        startActivity(intent);
    }
}
