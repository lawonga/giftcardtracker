package lawonga.giftcardtracker.DialogFragments;

import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.HashMap;
import java.util.Map;

import lawonga.giftcardtracker.CardActivity;
import lawonga.giftcardtracker.CardLogic.CardListAdapter;
import lawonga.giftcardtracker.CardLogic.CardListCreator;
import lawonga.giftcardtracker.R;

/**
 * Created by lawonga on 10/24/2015.
 */
public class ModifyCardFragment extends DialogFragment {
    String cardname, cardId, cardnotes;
    Double cardvalue;
    Double finalcardmodifier;
    Double finalcardvalue;
    int cardposition, add_or_subtract;
    TextView addorsubtract_textview;
    EditText addorsubtract_edittextview;
    Button addorsubctract_add, addorsubtract_cancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get values from previous activity
        add_or_subtract = getArguments().getInt("add_or_subtract");
        cardname = getArguments().getString("cardname");
        cardvalue = getArguments().getDouble("cardbalance");
        cardId = getArguments().getString("cardId");
        cardposition = getArguments().getInt("cardposition");
        cardnotes = getArguments().getString("cardnotes");

        Log.e("CardId equals", cardId);
        View view = inflater.inflate(R.layout.card_modify, container);
        // Initialize
        addorsubtract_textview = (TextView) view.findViewById(R.id.add_or_subtract);
        addorsubtract_edittextview = (EditText) view.findViewById(R.id.edit_add_or_subtract);
        addorsubtract_cancel = (Button) view.findViewById(R.id.modify_cancel);
        addorsubctract_add = (Button) view.findViewById(R.id.modify_ok);
        if (add_or_subtract == 2) {
            addorsubtract_textview.setText("Enter new card name");
            addorsubtract_edittextview.setInputType(InputType.TYPE_CLASS_TEXT);
            addorsubtract_edittextview.setText(cardname);
        } else if (add_or_subtract == 0) addorsubtract_textview.setText("Reload Amount");
        else if (add_or_subtract == 1) addorsubtract_textview.setText("Pay Amount");

                // Set aciton for when user opens the fragment
        addorsubctract_add.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  boolean isNetworkConnected = isNetworkConnected();
                  if (addorsubtract_edittextview.getText().toString().equals("")){
                      addorsubtract_edittextview.setText("0");
                  }
                  // Use add_or_subtract boolean value to determine whether to add or subtract; if true = add
                  if (add_or_subtract == 0) {
                      finalcardmodifier = Math.round(Double.valueOf(addorsubtract_edittextview.getText().toString()) * 100.0) / 100.0;
                  } else if (add_or_subtract == 1){
                      finalcardmodifier = Math.round(Double.valueOf(addorsubtract_edittextview.getText().toString()) * -100.0) / 100.0;
                  } else if (add_or_subtract == 2){
                      // If editing the card name
                  }
                  addorsubctract_add.setEnabled(false);
                  if (add_or_subtract == 0 || add_or_subtract == 1) {
                      cardvalue = Double.valueOf(CardActivity.cardbalanceview.getText().toString());
                      finalcardvalue = cardvalue + finalcardmodifier;
                      CardActivity.cardbalanceview.setText(String.valueOf(finalcardvalue));
                      if (isNetworkConnected) {
                          Map<String, Object> map = new HashMap<>(2);
                          map.put("cardmodifier", finalcardmodifier);
                          map.put("cardId", cardId);
                          ParseCloud.callFunctionInBackground("modifycard", map, new FunctionCallback<String>() {
                              @Override
                              public void done(String s, ParseException e) {
                                  if (e != null) {
                                      Log.e("Error: ", e.toString());
                                      Toast.makeText(getActivity(), "Unknown Error", Toast.LENGTH_LONG).show();
                                  }
                                  CardListCreator.clearadapter();
                                  CardListAdapter.queryList();
                                  dismiss();
                              }
                          });
                      } else {
                          ParseObject point = ParseObject.createWithoutData("DataBase", cardId);
                          point.put("balance", finalcardvalue);
                          point.saveEventually();
                          CardListCreator.clearadapter();
                          CardListAdapter.queryList();
                          dismiss();
                      }
                  } else if (add_or_subtract == 2) {
                      if (isNetworkConnected){
                      ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("DataBase");
                      parseQuery.getInBackground(cardId, new GetCallback<ParseObject>() {
                          @Override
                          public void done(ParseObject parseObject, ParseException e) {
                              if (e == null) {
                                  parseObject.put("cardname", addorsubtract_edittextview.getText().toString());
                                  parseObject.saveInBackground(new SaveCallback() {
                                      @Override
                                      public void done(ParseException e) {
                                          CardActivity.collapsingToolBar.setTitle(addorsubtract_edittextview.getText().toString());
                                          CardListCreator.clearadapter();
                                          CardListAdapter.queryList();
                                          dismiss();
                                      }
                                  });
                              }
                          }
                      });
                      } else {
                          ParseObject parseObject = ParseObject.createWithoutData("DataBase", cardId);
                          parseObject.put("cardname", addorsubtract_edittextview.getText().toString());
                          parseObject.saveEventually();
                          CardListCreator.clearadapter();
                          CardListAdapter.queryList();
                          dismiss();
                      }
                  }
              }
          }
        );
        addorsubtract_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
    // Network state check
    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }
}
