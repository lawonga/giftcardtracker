package lawonga.giftcardtracker.CardLogic;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import lawonga.giftcardtracker.R;

/**
 * Created by lawonga on 9/27/2015.
 */
public class CardListFragment extends ArrayAdapter<CardListAdapter> {
    public CardListFragment(Context context, ArrayList<CardListAdapter> cardDatas) {
        super(context, 0, cardDatas);
    }

    /* Fragment that holds the individual card; height of card is determined by
    finding out the current width of the screen and then calculating it by 0.45;
    that way we get consistency between all devices without having to make
    multiple layouts */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardListAdapter carddatas = getItem(position);
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_list_layout, parent, false);
        }
        // Get percentage size of screen
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        // Look up card name & balance
        TextView cardName = (TextView)convertView.findViewById(R.id.card_name);
        TextView cardBalance = (TextView)convertView.findViewById(R.id.card_balance);
        ImageView cardPic = (ImageView)convertView.findViewById(R.id.card_pic_thumb);
        // Populate the data to template view using data object
        cardName.setText(carddatas.cardname);
        cardBalance.setText(Double.toString(carddatas.cardbalance));

        // Calculates the current width of the screen, times it by 0.45 to get the card in pixels
        int currentWidth = displayMetrics.widthPixels;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Integer.valueOf((int) (currentWidth*0.45)));

        // Set image bitmap
        cardPic.setLayoutParams(layoutParams);
        cardPic.setImageBitmap(carddatas.cardpic);
        return convertView;
    }
}
