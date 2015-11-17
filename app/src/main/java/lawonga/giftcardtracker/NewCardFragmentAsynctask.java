package lawonga.giftcardtracker;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Andy W on 2015-11-15.
 */
public class NewCardFragmentAsynctask extends AsyncTask<Integer, byte[], File> {
    private int data = 0;
    private Bitmap bitmap;
    private FileOutputStream fileOutputStream = null;

    @Override
    protected File doInBackground(Integer... params) {
        data = params[0];
        bitmap = BitmapFactory.decodeResource(Resources.getSystem(), data);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bitmapdata = byteArrayOutputStream.toByteArray();
        publishProgress(bitmapdata);
        return NewCardFragment.file;
    }

    @Override
    protected void onProgressUpdate(byte[]... values) {
        try {
            fileOutputStream = new FileOutputStream(NewCardFragment.file);
            fileOutputStream.write(values[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(File file) {
        NewCardFragment.file = file;
    }
}
