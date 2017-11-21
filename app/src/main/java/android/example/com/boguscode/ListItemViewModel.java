package android.example.com.boguscode;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.example.com.boguscode.models.*;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.vimeo.networking.model.Video;

import java.net.URL;

/**
 * Created by Steven on 11/16/2017.
 */

public class ListItemViewModel {

    final String TAG = "listItemViewModel";
    private VideoItem vidItem;
    private Context mContext;


    public ListItemViewModel(VideoItem vidItem, Context context){
        this.vidItem = vidItem;
        this.mContext = context;
    }

    /*  Open URL with browser.
    * @param videoItem.vidUrl.
    *        Notice that this Url is passed in using databinding View 'data' objects
    *        inside the view_video_card.xml
    * @return returns my definition of a Listener
    */
    public View.OnClickListener onOpenUrlWithBrowser(String link) {
        final String uri = link;

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    // view.getContext() will get context of MainActivity
                Context mainActivityContext = view.getContext();

                    // if no network state, return
                MainActivity activity  = (MainActivity) mainActivityContext;
                if (!activity.hasNetworkConnection()){
                    return;
                }

                String url = uri;

                    // add preceding http
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Log.d(TAG, "Context: " + view.getContext());
                Log.d(TAG, "url: " + url);

                    // start new intent to watch video in default internet browser
                try {
                    Intent watchVidIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mainActivityContext.startActivity(watchVidIntent);
                } catch (Exception e){
                    Log.e(TAG, "Something went wrong while trying to start open video in Browser");
                }
            }
        };
    }

    MainActivity activity;


    private static boolean visibility = false;
    public static int getVisibility(){
        if (visibility)
            return View.VISIBLE;
        else{
            return View.INVISIBLE;
        }
    }
    public static void setVisibility(boolean setVisible){
        visibility = setVisible;
    }

    @BindingAdapter("imgUrl")
    public static void loadImage(ImageView view, String url) {

            // check if you have network connection before downloading images
        if (!((MainActivity) view.getContext()).hasNetworkConnection()){
            Log.d("ListItemViewModel", "No network connection; didn't fetch image");
            return;
        }

        //Log.d("Setter", "trying to image with url " + url);

        if (url == null) {
            Log.d("CustomSetters imgUrl", "The URL was null. No Image set");
            return;
        }
        /*if (hasImage(view)){
            Log.d("CustomSetters imgUrl", "View already has image. Tried to set with url: " + url );
            return;
        }*/

        try{

            new DownloadImgTask(view).execute(url);

        }catch (Exception e){
            Log.e("ListItemViewModel", "Something went wrong trying to load image from URL on async thread");
        }
    }

        // function used for debugging
    public String printVidInfo(Video vid){
        return "title: " + vid.name + "\n vid url: " + vid.link + "\n; user/name: " + vid.user.name + "; user/getName: " + vid.user.getName()
                + "; duration: " + vid.duration;
    }

    public static boolean hasImage(ImageView view){
        return (view.getDrawable() != null);
    }
}
