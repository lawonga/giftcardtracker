package lawonga.giftcardtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_list, parent, false);
        }
        // Look up card name & balance
        TextView cardName = (TextView)convertView.findViewById(R.id.card_name);
        TextView cardBalance = (TextView)convertView.findViewById(R.id.card_balance);
        // Populate the data to template view using data object
        cardName.setText(carddatas.cardname);
        cardBalance.setText(Double.toString(carddatas.cardbalance));
        return convertView;
    }

}
