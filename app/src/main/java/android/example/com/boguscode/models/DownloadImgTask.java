package android.example.com.boguscode.models;

import android.example.com.boguscode.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vimeo.networking.model.Video;

import java.io.InputStream;

/**
 * Created by Steven on 11/16/2017.
 */

public class DownloadImgTask extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;
    VideoItem videoItem;
    ProgressBar progressBar;
    String url;

    public DownloadImgTask(ImageView bmImage, ProgressBar bar) {
        this.bmImage = bmImage;
        progressBar = bar;
    }

    public void setProgressBar(ProgressBar bar){
        this.progressBar = bar;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        url = urldisplay;
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
            bmImage.setImageBitmap(result);
            progressBar.setVisibility(View.GONE);

            String msg = (progressBar == null)? "progressBar is null" : (""+progressBar.getVisibility()+" url: " + url);
            Log.d("ListItem", msg);
        }

    }
}
