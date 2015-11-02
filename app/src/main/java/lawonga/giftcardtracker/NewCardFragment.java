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
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_card, container);
        name = (EditText)view.findViewById(R.id.new_card_name);
        initialbalance = (EditText)view.findViewById(R.id.new_card_balance);
        OK = (Button)view.findViewById(R.id.ok_create_card);
        cancel = (Button)view.findViewById(R.id.cancel_create_card);

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nametxt = name.getText().toString();
                initialbalancetxt = initialbalance.getText().toString();
                if(nametxt.equals("") || initialbalancetxt.equals("")){
                    Toast.makeText(v.getContext(),"Please fill in all fields", Toast.LENGTH_LONG).show();
                } else {
                    createCard(nametxt, initialbalancetxt, "DataBase", "");
                    dismiss();
                    CardListCreator.clearadapter();
                    CardListAdapter.queryList();
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

    public static void createCard(String nametext, String initialbalancetext,  String databaseclass, String cardnotes){
        Map<String, Object> map = new HashMap<>();
        map.put("cardname", nametext);
        map.put("balance", Double.valueOf(initialbalancetext));
        map.put("database", databaseclass);
        map.put("cardnotes", cardnotes);
        ParseCloud.callFunctionInBackground("createcard", map, new FunctionCallback<String>() {
            @Override
            public void done(String s, ParseException e) {
                // COMPLETE
            }
        });
    }
}
