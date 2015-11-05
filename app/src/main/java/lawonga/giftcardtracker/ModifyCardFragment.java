package lawonga.giftcardtracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lawonga on 10/24/2015.
 */
public class ModifyCardFragment extends DialogFragment {
    Boolean add_or_subtract;
    String cardname, cardId, cardnotes;
    Double cardvalue;
    Double finalcardmodifier;
    Double finalcardvalue;
    Double roundedfinalcardvalue;
    int cardposition;
    TextView addorsubtract_textview;
    EditText addorsubtract_edittextview;
    Button addorsubctract_add, addorsubtract_cancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get values from previous activity
        add_or_subtract = getArguments().getBoolean("add_or_subtract");
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
        addorsubctract_add.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (addorsubtract_edittextview.getText().toString().equals("")){
                      addorsubtract_edittextview.setText("0");
                  }
                  // Use add_or_subtract boolean value to determine whether to add or subtract; if true = add
                  if (add_or_subtract) {
                      finalcardmodifier = Double.valueOf(Math.round(Double.valueOf(addorsubtract_edittextview.getText().toString())*100)/100);
                  } else {
                      finalcardmodifier = Double.valueOf(Math.round(Double.valueOf(addorsubtract_edittextview.getText().toString())*-100)/100);
                  }
                  addorsubctract_add.setEnabled(false);
                  Map<String, Object> map = new HashMap<>(2);
                  map.put("cardmodifier", finalcardmodifier);
                  map.put("cardId", cardId);
                  ParseCloud.callFunctionInBackground("modifycard", map, new FunctionCallback<String>() {
                      @Override
                      public void done(String s, ParseException e) {
                          if (e == null) {
                              cardvalue = Double.valueOf(CardView.cardbalanceview.getText().toString());
                              finalcardvalue = cardvalue + finalcardmodifier;
                              CardView.cardbalanceview.setText(String.valueOf(finalcardvalue));
                          } else {
                              Log.e("Error: ", e.toString());
                              Toast.makeText(getActivity(), "Unknown Error", Toast.LENGTH_LONG).show();
                          }
                          dismiss();
                      }
                  });
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
}
