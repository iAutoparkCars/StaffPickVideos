package android.example.com.boguscode.models;

import android.example.com.boguscode.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by Steven on 11/16/2017.
 */

public class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;

    public DownloadImgTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {

        if (result != null) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ListItemViewModel.setVisibility(true);

            bmImage.setImageBitmap(result);
        }

    }
}
