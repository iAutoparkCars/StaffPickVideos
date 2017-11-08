package android.example.com.boguscode;

import android.util.Log;

import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoList;
import com.vimeo.networking.model.error.VimeoError;

import java.util.ArrayList;

/**
 * Created by Steven on 11/7/2017.
 */

public class GetVideosTask {

    public int page;
    public final int per_page;


    public GetVideosTask(int page, int per_page){
        this.page = page;
        this.per_page = per_page;
    }
    private final String TAG = getClass().getName();

    public void getVideos(String uri){

            // "/channels/<channel_name>/videos?page=1&per_page=15"
        String refinedUri = new StringBuilder(uri)
          .append("?page=").append(page).append("&per_page=").append(per_page).toString();

        if (VimeoClient.getInstance() == null)
            Log.e(TAG, "VIMEO CLIENT IS NULL");

        VimeoClient.getInstance().fetchNetworkContent(refinedUri, new ModelCallback<VideoList>(VideoList.class) {

            @Override
            public void success(VideoList videoList) {
                if (videoList != null && videoList.data != null && !videoList.data.isEmpty()) {
                    // Create array of Vimeo Videos called videos
                    ArrayList<Video> videos = new ArrayList<>();

                    Log.d(TAG, "GOT VIDEOS SUCCESS");
                    // For Vimeo video represented as Vimeo response data in the Json

                    int i = 1;
                    for(Video video : videoList.data) {
                        // Add Vimeo video to videos array previously created
                        Log.d(TAG, i + " name: " + video.name);
                        videos.add(video);
                        i++;
                    }
                }
            }

            @Override
            public void failure(VimeoError error) {
                Log.e(TAG, "getVideos error: " + error);
            }
        });



    }


}
