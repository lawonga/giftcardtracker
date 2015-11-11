package lawonga.giftcardtracker;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lawonga on 9/28/2015.
 */
public class NewCardFragment extends DialogFragment {
    EditText name, initialbalance;
    Button OK, cancel;
    static String nametxt;
    static String initialbalancetxt;
    static SliderLayout sliderShow;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_card, container);
        name = (EditText)view.findViewById(R.id.new_card_name);
        initialbalance = (EditText)view.findViewById(R.id.new_card_balance);
        OK = (Button)view.findViewById(R.id.ok_create_card);
        cancel = (Button)view.findViewById(R.id.cancel_create_card);
        // SliderLayout is the Android Image Slider
        sliderShow = (SliderLayout) view.findViewById(R.id.slider);
        sliderShow.setDuration(6000);
        sliderShow.setCustomIndicator((PagerIndicator)view.findViewById(R.id.custom_indicator));

        // Set card list
        HashMap<String, Integer> urlMap= new HashMap<>();
        urlMap.put("Cafe", R.drawable.cafe);
        urlMap.put("Gas", R.drawable.gas);
        urlMap.put("General", R.drawable.general);
        urlMap.put("Restaurant", R.drawable.restaurant);
        urlMap.put("Shopping", R.drawable.shopping);

        for (final String name : urlMap.keySet()) {
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .description(name)
                    .image(urlMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView baseSliderView) {
                            Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
                        }
                    });

            sliderShow.addSlider(textSliderView);
        }

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nametxt = name.getText().toString();
                initialbalancetxt = initialbalance.getText().toString();
                if(nametxt.equals("") || initialbalancetxt.equals("")){
                    Toast.makeText(v.getContext(),"Please fill in all fields", Toast.LENGTH_LONG).show();
                } else {
                    createCard(nametxt, initialbalancetxt, "DataBase", "", "DataBase");
                    dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    public static void createCard(final String nametext, final String initialbalancetext,  String databaseclass, String cardnotes, String cardtype){
        Map<String, Object> map = new HashMap<>();
        map.put("cardname", nametext);
        map.put("balance", Double.valueOf(initialbalancetext));
        map.put("database", databaseclass);
        map.put("cardnotes", cardnotes);
        // Check if network is connected before creating the card
        if (CardView.isNetworkConnected) {
            ParseCloud.callFunctionInBackground("createcard", map, new FunctionCallback<String>() {
                @Override
                public void done(String s, ParseException e) {
                    // COMPLETE
                    CardListCreator.clearadapter();
                    CardListAdapter.queryList();
                }
            });
        } else {
            final ParseObject parseObject = new ParseObject(cardtype);
            parseObject.put("cardname", nametext);
            parseObject.put("balance", Double.valueOf(initialbalancetext));
            parseObject.put("cardnotes", cardnotes);
            parseObject.put("user", ParseUser.getCurrentUser());
            parseObject.pinInBackground(String.valueOf(LogonActivity.currentcard), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    CardListCreator.cardData.add(new CardListAdapter(nametext, Double.valueOf(initialbalancetext), "", parseObject.getObjectId()));
                    CardListCreator.notifychangeddata();
                    parseObject.saveEventually();
                }
            });
        }
    }
}
