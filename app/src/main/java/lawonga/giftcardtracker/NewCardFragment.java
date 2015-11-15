package lawonga.giftcardtracker;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lawonga on 9/28/2015.
 */
public class NewCardFragment extends DialogFragment {
    EditText name, initialbalance;
    Button OK, cancel;
    private int ACTION_CAPTURE = 1234;
    static String nametxt;
    static String initialbalancetxt;
    static SliderLayout sliderShow;
    public static HashMap<String, File> cardMap;
    private boolean picturetaken = false;
    private ArrayList<String> currentOrder;

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
        // SliderLayout is the Android Image Slider
        sliderShow = (SliderLayout) view.findViewById(R.id.slider);
        sliderShow.setDuration(Integer.MAX_VALUE);
        sliderShow.setCustomIndicator((PagerIndicator) view.findViewById(R.id.custom_indicator));

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
                            Toast.makeText(getActivity(), String.valueOf(sliderShow.getCurrentPosition()), Toast.LENGTH_SHORT).show();
                            if (name == "Take Photo" && picturetaken == false) {
                                captureCameraPhoto();
                            }
                        }
                    });
            sliderShow.addSlider(textSliderView);
        }

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file;
                nametxt = name.getText().toString();
                initialbalancetxt = initialbalance.getText().toString();
                int slidePosition = sliderShow.getCurrentPosition();

                if (nametxt.equals("") || initialbalancetxt.equals("")) {
                    Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                } else {
                    file = cardMap.get(currentOrder.get(slidePosition));
                    if (picturetaken && currentOrder.get(slidePosition) == "cardImage") {
                        file = getActivity().getFileStreamPath("cardImage");
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if(isNetworkConnected()) {
                        createCard(nametxt, initialbalancetxt, "DataBase", "", "DataBase", bitmap);
                        getActivity().getFileStreamPath("cardImage").delete();
                    } else {
                        createCardNotConnected(nametxt, initialbalancetxt, "DataBase", "", "DataBase", bitmap);
                        getActivity().getFileStreamPath("cardImage").delete();
                    }
                    dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean deleteFile = getActivity().deleteFile("cardImage");
                Toast.makeText(getActivity(), String.valueOf(deleteFile), Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
        return view;
    }

    /**
     * Converts drawable int [to bitmap and then] to file
     **/
    private File drawableFile(int drawInt, String name) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                drawInt);
        File file = new File(getActivity().getCacheDir(), name);
        FileOutputStream fileOutputStream = null;
        try {
            file.createNewFile();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
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

    public static void createCard(final String nametext, final String initialbalancetext, String databaseclass, String cardnotes, String cardtype, Bitmap picture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Map<String, Object> map = new HashMap<>();
        map.put("cardname", nametext);
        map.put("balance", Double.valueOf(initialbalancetext));
        map.put("database", databaseclass);
        map.put("cardnotes", cardnotes);
        map.put("picture", byteArray);
        ParseCloud.callFunctionInBackground("createcard", map, new FunctionCallback<String>() {
            @Override
            public void done(String s, ParseException e) {
                // COMPLETE
                CardListCreator.clearadapter();
                CardListAdapter.queryList();
            }
        });
    }
    public static void createCardNotConnected(final String nametext, final String initialbalancetext, String databaseclass, String cardnotes, String cardtype, final Bitmap picture) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        final ParseObject parseObject = new ParseObject(cardtype);
        parseObject.put("cardname", nametext);
        parseObject.put("balance", Double.valueOf(initialbalancetext));
        parseObject.put("cardnotes", cardnotes);
        parseObject.put("user", ParseUser.getCurrentUser());
        parseObject.put("cardpicture", byteArray);
        parseObject.pinInBackground(String.valueOf(LogonActivity.currentcard), new SaveCallback() {
            @Override
            public void done(ParseException e) {
                CardListCreator.cardData.add(new CardListAdapter(nametext, Double.valueOf(initialbalancetext), "", parseObject.getObjectId(), picture));
                CardListCreator.notifychangeddata();
                parseObject.saveEventually();
                Log.e("Internet", "is not running");
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
