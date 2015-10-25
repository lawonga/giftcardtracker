package lawonga.giftcardtracker;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by lawonga on 9/28/2015.
 */
public class NewCardFragment extends DialogFragment {
    EditText name, initialbalance;
    Button OK, cancel;
    String nametxt, initialbalancetxt;
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
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseObject dbObject = new ParseObject("DataBase");
                    dbObject.put("cardname", nametxt);
                    dbObject.put("balance", initialbalancetxt);
                    dbObject.put("user", user);
                    dbObject.saveInBackground();
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
}
