package android.example.com.boguscode;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.example.com.boguscode.models.*;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.net.URL;

/**
 * Created by Steven on 11/16/2017.
 */

public class ListItemViewModel {

    private VideoItem vidItem;
    private Context mContext;

    public ListItemViewModel(VideoItem vidItem, Context context){
        this.vidItem = vidItem;
        this.mContext = context;
    }

    public View.OnClickListener onOpenUrlWithBrowser() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ListItemViewModel", "HERE YOU OPEN BROWSER");
                Toast.makeText(view.getContext(), "Opens URL here", Toast.LENGTH_SHORT).show();

                //here I can start new intent for activity to watch video
            }
        };
    }

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
