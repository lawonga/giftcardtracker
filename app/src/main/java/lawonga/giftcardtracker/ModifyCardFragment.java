package lawonga.giftcardtracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lawonga on 10/24/2015.
 */
public class ModifyCardFragment extends DialogFragment {
    Boolean add_or_subtract;
    String cardname, cardId;
    Double cardvalue;
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
        View view = inflater.inflate(R.layout.card_modify, container);
        // Initialize
        addorsubtract_textview = (TextView)view.findViewById(R.id.add_or_subtract);
        addorsubtract_edittextview = (EditText)view.findViewById(R.id.edit_add_or_subtract);
        addorsubtract_cancel = (Button)view.findViewById(R.id.modify_cancel);
        addorsubctract_add = (Button)view.findViewById(R.id.modify_ok);
        addorsubctract_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use add_or_subtract boolean value to determine whether to add or subtract; if true = add
                if (add_or_subtract){
                    cardvalue += Float.valueOf(addorsubtract_edittextview.getText().toString());
                    Map<String, Object> map = new HashMap<>(2);
                    // map.put("cardvalue", cardvalue);
                    map.put("cardmodifier", Float.valueOf(addorsubtract_edittextview.getText().toString()));
                    map.put("cardId", cardId);
                    ParseCloud.callFunction("modifycard", map, new FunctionCallback<>() {
                        @Override
                        public void done(Object o, ParseException e) {

                        }
                    });
                } else {

                }
            }
        });
        addorsubtract_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
