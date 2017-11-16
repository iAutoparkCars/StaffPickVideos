package android.example.com.boguscode;

import android.databinding.BindingAdapter;
import android.example.com.boguscode.models.*;
import android.util.Log;
import android.widget.ImageView;

import java.net.URL;

/**
 * Created by Steven on 11/16/2017.
 */

public class ListItemSetter {


    @BindingAdapter("imgUrl")
    public static void loadImage(ImageView view, String url) {
        Log.d("Setter", "trying to image with url " + url);


        if (url==null) {
            Log.d("CustomSetters imgUrl", "The URL was null. No Image set");
            return;
        }

        if (hasImage(view)){
            Log.d("CustomSetters imgUrl", "View already has image. Tried to set with url: " + url );
            return;
        }

        new DownloadImgTask(view).execute(url);
    }

    public static boolean hasImage(ImageView view){
        return (view.getDrawable() != null);
    }
}
