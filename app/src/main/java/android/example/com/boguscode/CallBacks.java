package android.example.com.boguscode;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoList;
import com.vimeo.networking.model.error.VimeoError;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Callback;

/**
 * Created by Steven on 11/9/2017.
 */

public class CallBacks extends ModelCallback<VideoList> implements Callback<VideoList> {

    private final String TAG = getClass().getName();
    private OnTaskCompleted listener;


    @Override
    public void success(VideoList videoList) {
        if (videoList != null && videoList.data != null && !videoList.data.isEmpty()) {

            /* Pass total # of results (total should not change) on 1st success.
             update the # page fetched. Page cannot surpass max page.     */

            // Create List of Vimeo Videos
            List<Video> videos = new ArrayList<>();
            Log.d(TAG, "GOT VIDEOS SUCCESS");

            for(Video video : videoList.data) {
                // Add Vimeo video to videos List
                //Log.d(TAG, i+": " + video.name);
                videos.add(video);
            }
            listener.onDownloadTaskCompleted(videos);
        }
    }

    @Override
    public void failure(VimeoError error) {

    }

    public CallBacks(OnTaskCompleted listener){
        super(VideoList.class);
        this.listener = listener;
    }
}
