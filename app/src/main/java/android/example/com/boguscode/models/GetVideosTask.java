package android.example.com.boguscode.models;

import android.example.com.boguscode.OnTaskCompleted;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.model.Video;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Steven on 11/7/2017.
 */

public class GetVideosTask {

        // page first calls 1, then increments as user requests more pages
    private int page;
    private final int per_page;
    private int total = 0;
    private List<Video> vidList;
    private int max_page;
    private String uri;
    private String name;

        // observable to notify main thread data is ready
    private Observable<String> observableTask;
    AppCompatActivity activity;
    OnTaskCompleted listener;
    private CallBacks callbacks;

        // getters
    public int getPage() {return page;}
    public int getPer_page() {
        return per_page;
    }
    public List<Video> getVidList() { return vidList; }
    public Observable<String> getObservableTask() { return this.observableTask; }
    public CallBacks getCallback() {return this.callbacks; }
    public String getName() {return this.name; }

    public GetVideosTask(int per_page, String uri){
        this.page = 0;
        this.per_page = per_page;
        this.uri = uri;
    }

    public GetVideosTask(String name, int per_page, String uri, OnTaskCompleted listener){
        this.name = name;
        this.page = 0;
        this.per_page = per_page;
        this.uri = uri;
        this.listener = listener;
    }

    private final String TAG = getClass().getName();

        // if this function returns null, print at bottom of Video Reccycler List that there are no more videos to load.
    public List<Video> downloadVideos(){

            // decide whether to proceed. If max_page reached, don't make any GET requests
        page++;
        if (totalNumberResultsInitialized()){
            max_page = (int) Math.ceil(  ((double)total/(double)per_page) );
            if (page > max_page)
                return null;
        }

            // "/channels/<channel_name>/videos?page=1&per_page=15"
        String refinedUri = new StringBuilder(this.uri)
          .append("?page=").append(page).append("&per_page=").append(per_page).toString();
        Log.d(TAG, "url: " + refinedUri);

        if (VimeoClient.getInstance() == null)
            Log.e(TAG, "VIMEO CLIENT IS NULL");

        callbacks = new CallBacks(this.listener);
        VimeoClient.getInstance().fetchNetworkContent(refinedUri, callbacks);

        if (callbacks.totalNumberResultsInitialized() && !totalNumberResultsInitialized()){
            this.total = callbacks.getTotal();
            Log.d(TAG, "Total results: " + this.total);
        }

        /*VimeoClient.getInstance().fetchNetworkContent(refinedUri,

                // ModelCallback all runs on Main thread
                new ModelCallback<VideoList>(VideoList.class) {

            @Override
            public void success(VideoList videoList) {
                if (videoList != null && videoList.data != null && !videoList.data.isEmpty()) {

                        *//* Pass total # of results (total should not change) on 1st success.
                           update the # page fetched. Page cannot surpass max page.
                        *//*
                    total = videoList.total;

                        // Create List of Vimeo Videos
                    List<Video> videos = new ArrayList<Video>();
                    Log.d(TAG, "GOT VIDEOS SUCCESS");

                    for(Video video : videoList.data) {
                        // Add Vimeo video to videos List
                        //Log.d(TAG, i+": " + video.name);
                        videos.add(video);
                    }

                    vidList = videos;
                    observableTask = Observable.just("Hello");
                    Log.d(TAG, "id: " + Thread.currentThread().getId());
                }
            }

            @Override
            public void failure(VimeoError error) {
                Log.e(TAG, "downloadVideos error: " + error);
            }
        });*/

        return vidList;
    }

    public boolean totalNumberResultsInitialized(){
        return (total!=0);
    }
}
