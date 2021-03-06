package lawonga.giftcardtracker.DialogFragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lawonga.giftcardtracker.CardLogic.CardListAdapter;
import lawonga.giftcardtracker.CardLogic.CardListCreator;
import lawonga.giftcardtracker.LogInActivity;
import lawonga.giftcardtracker.R;

/**
 * Created by lawonga on 9/28/2015.
 */
public class NewCardFragment extends DialogFragment {
    EditText name, initialbalance, cardcode;
    Button OK, cancel;
    private int ACTION_CAPTURE = 1234;
    static String nametxt, initialbalancetxt, cardcodetxt;
    static SliderLayout sliderShow;
    public static HashMap<String, File> cardMap;
    private boolean picturetaken = false;
    private ArrayList<String> currentOrder;
    static File file;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_card, container);
        name = (EditText) view.findViewById(R.id.new_card_name);
        initialbalance = (EditText) view.findViewById(R.id.new_card_balance);
        OK = (Button) view.findViewById(R.id.ok_create_card);
        cancel = (Button) view.findViewById(R.id.cancel_create_card);
        cardcode = (EditText)view.findViewById(R.id.new_card_code);
        // SliderLayout is the Android Image Slider
        sliderShow = (SliderLayout) view.findViewById(R.id.slider);

        // Set card list
        cardMap = new HashMap<>();
        cardMap.put("Gas", drawableFile(R.drawable.gas, "Gas"));
        cardMap.put("General", drawableFile(R.drawable.general, "General"));
        cardMap.put("Restaurant", drawableFile(R.drawable.restaurant, "Restaurant"));
        cardMap.put("Shopping", drawableFile(R.drawable.shopping, "Shopping"));
        cardMap.put("Coffee", drawableFile(R.drawable.cafe, "Coffee"));
        cardMap.put("Take Photo", drawableFile(R.drawable.camera, "Take Photo"));

        currentOrder = new ArrayList<>(Arrays.asList("Shopping", "Take Photo", "Gas", "Coffee", "Restaurant", "General"));

        for (final String name : cardMap.keySet()) {
            final TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView
                    .description(name)
                    .image(cardMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(BaseSliderView baseSliderView) {
                            if (name == "Take Photo" && picturetaken == false) {
                                captureCameraPhoto();
                            }
                        }
                    });
            sliderShow.stopAutoCycle();
            sliderShow.setCustomIndicator((PagerIndicator) view.findViewById(R.id.custom_indicator));
            sliderShow.addSlider(textSliderView);
        }

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                nametxt = name.getText().toString();
                initialbalancetxt = initialbalance.getText().toString();
                cardcodetxt = cardcode.getText().toString();

                int slidePosition = sliderShow.getCurrentPosition();

                // Check if any text was actualy entered
                if (nametxt.equals("") || initialbalancetxt.equals("")) {
                    Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                } else {
                    // Get the card picture we're about to upload
                    file = cardMap.get(currentOrder.get(slidePosition));
                    if (picturetaken && currentOrder.get(slidePosition) == "cardImage") {
                        file = getActivity().getFileStreamPath("cardImage");
                    }
                    // Do a check if network is connected, if it is then run createCard, if it is not then run createCardNotConnected
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if(isNetworkConnected()) {
                        createCard(nametxt, initialbalancetxt, "DataBase", "", "DataBase", bitmap, cardcodetxt);
                        getActivity().getFileStreamPath("cardImage").delete();
                    } else {
                        createCardNotConnected(nametxt, initialbalancetxt, "DataBase", "", "DataBase", bitmap, cardcodetxt);
                        getActivity().getFileStreamPath("cardImage").delete();
                    }
                    dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().deleteFile("cardImage");
                dismiss();
            }
        });
        return view;
    }

    /**
     * Converts drawable int [to bitmap and then] to file
     **/
    private File drawableFile(int drawInt, String name) {
        // new NewCardFragmentAsynctask().execute(drawInt);

        file = new File(getActivity().getCacheDir(), name);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawInt);
        FileOutputStream fileOutputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            byte[] bitmapdata = byteArrayOutputStream.toByteArray();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bitmapdata);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return file;
    }

    @Override
    public void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    // Captures the picture
    public void captureCameraPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, ACTION_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String name = "Take Photo";
        if (requestCode == ACTION_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] byteArray = bos.toByteArray();
            try {
                bos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                OutputStream os = getActivity().openFileOutput("cardImage", Context.MODE_PRIVATE);
                os.write(byteArray);
                os.flush();
                os.close();
                File file = getActivity().getFileStreamPath("cardImage");
                Log.e("MADEFILE", file.toString());
                cardMap.put(name, file);
                // ADD SLIDER
                TextSliderView textSliderView = new TextSliderView(getActivity());
                textSliderView
                        .description("Custom Photo")
                        .image(cardMap.get(name))
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {
                                Toast.makeText(getActivity(), String.valueOf(sliderShow.getCurrentPosition()), Toast.LENGTH_SHORT).show();
                            }
                        });
                try {
                    sliderShow.removeSliderAt(6);
                } catch (Exception ignored) {
                }
                sliderShow.addSlider(textSliderView);
                sliderShow.movePrevPosition();
                sliderShow.movePrevPosition();
                currentOrder.add("cardImage");
                picturetaken = true;
                bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createCard(final String nametext, final String initialbalancetext, String databaseclass, String cardnotes, String cardtype, Bitmap picture, String cardcode) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Map<String, Object> map = new HashMap<>();
        map.put("cardname", nametext);
        map.put("balance", Double.valueOf(initialbalancetext));
        map.put("database", databaseclass);
        map.put("cardnotes", cardnotes);
        map.put("picture", byteArray);
        map.put("cardcode", cardcode);
        ParseCloud.callFunctionInBackground("createcard", map, new FunctionCallback<String>() {
            @Override
            public void done(String s, ParseException e) {
                // COMPLETE
                CardListCreator.clearadapter();
                CardListAdapter.queryList();
            }
        });
    }
    public static void createCardNotConnected(final String nametext, final String initialbalancetext, String databaseclass, final String cardnotes, final String cardtype, final Bitmap picture, final String cardcode) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final ParseFile parseFile = new ParseFile("photo.png", byteArray);
        final ParseObject parseObject = new ParseObject(cardtype);
        parseObject.put("cardname", nametext);
        parseObject.put("balance", Double.valueOf(initialbalancetext));
        parseObject.put("cardnotes", cardnotes);
        parseObject.put("user", ParseUser.getCurrentUser());
        parseObject.put("cardpicture", parseFile);
        parseObject.put("cardcode", cardcode);
        parseObject.pinInBackground(String.valueOf(LogInActivity.currentcard), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                CardListCreator.cardData.add(new CardListAdapter(nametext, Double.valueOf(initialbalancetext), "", parseObject.getObjectId(), picture, cardcode));
                CardListCreator.notifychangeddata();
                parseObject.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) Log.e("Pin Save", "Success");
                        else Log.e("Pin Save", e.toString());
                    }
                });
                if (e == null) Log.e("Sucess.. but internet", "is not running");
                else Log.e("Fail.. ", e.toString());
            }
        });
        }

    // Network state check
    public boolean isNetworkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }
}
