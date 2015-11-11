package lawonga.giftcardtracker;

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

/**
 * Created by lawonga on 9/27/2015.
 */
public class CustomAdapter extends ArrayAdapter<CardListAdapter> {
    public CustomAdapter(Context context, ArrayList<CardListAdapter> cardDatas) {
        super(context, 0, cardDatas);
    }

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


        int currentWidth = displayMetrics.widthPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Integer.valueOf((int) (currentWidth*0.45)));
        cardPic.setLayoutParams(layoutParams);
        cardPic.setImageResource(R.drawable.restaurant);


        return convertView;
    }

}
