package lawonga.giftcardtracker.Logic;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by lawonga on 11/10/2015.
 */
public class ReusableLogic {

    // Converts pixels to dp
    public static int densityPixel(float scale, int pixel){
        // Get 10 dp to pixels to set the margins
        int densityPixel = (int) (pixel * scale + 0.5f);
        return densityPixel;
    }

    // Returns height of screen (kinda broken, its more like 1/4 of the screen)
    public static int heightPercentage(DisplayMetrics displayMetrics){

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int intHeight = Integer.valueOf((int) dpHeight);

        return intHeight;
    }

    // Calculates width percentage of screen
    public static int widthPercentage(DisplayMetrics displayMetrics){

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int intWidth = Integer.valueOf((int) dpWidth);

        return intWidth;
    }

    // Cleans out bitmap
    public void disposeBitmap(Bitmap bitmap){
        if (bitmap != null){
            bitmap.recycle();
            bitmap = null;
        }
    }
}
