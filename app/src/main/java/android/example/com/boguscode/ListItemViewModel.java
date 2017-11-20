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

            // before proceeding, need to check Network state here!


        final String uri = link;

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = uri;

                    // view.getContext() will get context of MainActivity
                Context mainActivityContext = view.getContext();

                    // add preceding http
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                Log.d("ListItemViewModel", "Context: " + view.getContext());
                Log.d("ListItemViewModel", "url: " + url);

                    // start new intent to watch video in default internet browser
                Intent watchVidIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mainActivityContext.startActivity(watchVidIntent);

                //Toast.makeText(view.getContext(), "Opens URL here", Toast.LENGTH_SHORT).show();


            }
        };
    }

    @BindingAdapter("imgUrl")
    public static void loadImage(ImageView view, String url) {

            // before proceeding, need to check Network state here!


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

    // function used for debugging
    public String printVidInfo(Video vid){
        return "title: " + vid.name + "\n vid url: " + vid.link + "\n; user/name: " + vid.user.name + "; user/getName: " + vid.user.getName()
                + "; duration: " + vid.duration;
    }

    public static boolean hasImage(ImageView view){
        return (view.getDrawable() != null);
    }
}
