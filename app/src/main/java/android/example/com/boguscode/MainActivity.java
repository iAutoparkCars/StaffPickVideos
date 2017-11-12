package android.example.com.boguscode;

import android.example.com.boguscode.models.GetVideosTask;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.vimeo.networking.Configuration;
import com.vimeo.networking.GsonDeserializer;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.AuthCallback;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.error.VimeoError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

public class MainActivity extends AppCompatActivity{

    static List<Video> list;
    public static void getVideos(List<Video> video){
        list = video;
    };


        // realistically, this would be stored somewhere more safe
    private String ACCESS_TOKEN = "b529a655ff2fc32a8b58bf2b405f39b0";

    private final String CLIENT_ID = "de892f6652ca3657e278db46cd3af801465643e8";
    private final String CLIENT_SECRET = "ZlxBVGjMRLFCC9GIkf0JPwqvssHTFbbYNu56RwiPtplSFR1PWBTdjZkfTSfrdjXA8p" +
                                         "HVGt1U6vtP7NQekqs8FmXnTdr3ub5DpHp4os0fUGcQ10liWfrTALtP850zicpX";
    private final int PER_PAGE = 10;
    private VimeoClient mApiClient;

    public final String TAG = getClass().getName();

    private ListView mListView;
    private VideoAdapter mAdapter;
    private ArrayList<JSONObject> items = new ArrayList<>();

    private final String STAFF_VIDS = "Staff Vids";
    private final String PREMIERE_VIDS = "Premiere Vids";
    private final String MONTH_VIDS = "Month Vids";
    private final String YEAR_VIDS = "Year Vids";

    GetVideosTask getStaffVidsTask;
    GetVideosTask getPremiereVidsTask;
    GetVideosTask getBestMonthVidsTask;
    GetVideosTask getBestYearVidsTask;

    final List<Video> staffVids = new ArrayList<Video>();
    final List<Video> premiereVids = new ArrayList<Video>();
    final List<Video> bestMonthVids = new ArrayList<Video>();
    final List<Video> bestYearVids = new ArrayList<Video>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureVimeoAuthentication();
        mApiClient = VimeoClient.getInstance();

        // ---- After task finishes download, videos stored in staffVids List<Video> ----
        OnTaskCompleted staffTaskDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {

                if (vidsResponseError(videos, getStaffVidsTask.getName())) return;

                for (Video vid : videos) { staffVids.add(vid); };

                // the rest of the logic has to be done in here...

            }
        };

        OnTaskCompleted premiereTaskDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                if (vidsResponseError(videos, getPremiereVidsTask.getName())) return;

                for (Video vid : videos) { Log.d(TAG, vid.name); premiereVids.add(vid); };

            }
        };

        final OnTaskCompleted bestMonthTaskDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                if (vidsResponseError(videos, getBestMonthVidsTask.getName())) return;

                for (Video vid : videos) { Log.d(TAG, vid.name); bestMonthVids.add(vid); };
            }
        };

        OnTaskCompleted bestYearTaskDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                if (vidsResponseError(videos, getBestYearVidsTask.getName())) return;

                for (Video vid : videos) { Log.d(TAG, vid.name); bestYearVids.add(vid); };



            }
        };

        getStaffVidsTask = new GetVideosTask(STAFF_VIDS, PER_PAGE, "/channels/staffpicks/videos", staffTaskDone);
        getPremiereVidsTask = new GetVideosTask(PREMIERE_VIDS, PER_PAGE, "/channels/premieres/videos", premiereTaskDone);
        getBestMonthVidsTask = new GetVideosTask (MONTH_VIDS, PER_PAGE, "/channels/bestofthemonth/videos", bestMonthTaskDone);
        getBestYearVidsTask = new GetVideosTask (YEAR_VIDS, PER_PAGE, "/channels/bestoftheyear/videos", bestYearTaskDone);


        // ---- Client Credentials Authenticate ----
        if (mApiClient.getVimeoAccount().getAccessToken() == null) {
                // If there is no access token, fetch one
                authenticateWithClientCredentials();
                getStaffVidsTask.downloadVideos();
                getPremiereVidsTask.downloadVideos();
                getBestMonthVidsTask.downloadVideos();
                getBestYearVidsTask.downloadVideos();

                //new GetVidsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        mListView = (ListView) findViewById(R.id.activity_main_listview);
        mAdapter = new VideoAdapter(this, R.id.list_item_video_name_textview, items);
        mListView.setAdapter(mAdapter);

        String baseUri = "https://api.vimeo.com/channels/staffpicks/videos";

    }

    private void configureVimeoAuthentication(){
        Configuration.Builder configBuilder =
                new Configuration.Builder(CLIENT_ID, CLIENT_SECRET, "public")
                        .setCacheDirectory(this.getCacheDir()).setGsonDeserializer(new GsonDeserializer());
        VimeoClient.initialize(configBuilder.build());
    }

    /* After authorization is granted on success(), start GET request for videos*/
    private void authenticateWithClientCredentials() {

        VimeoClient.getInstance().authorizeWithClientCredentialsGrant(new AuthCallback() {
            @Override
            public void success() {
                String accessToken = VimeoClient.getInstance().getVimeoAccount().getAccessToken();
                ACCESS_TOKEN = accessToken;
                Log.d(TAG, "Client Credentials Authorization Success with Access Token: " + accessToken);
            }

            @Override
            public void failure(VimeoError error) {
                String errorMessage = error.getDeveloperMessage();
                Log.d(TAG, "Client Credentials Authorization Failure: " + errorMessage);
            }
        });
    }

    private List<List<Video>> getVidsTask(){

        List<List<Video>> vidList = new ArrayList<List<Video>>();
        int per_page = 5;

        List<Video> staffVids = getStaffVidsTask.downloadVideos();
        if (staffVids != null)
            vidList.add(staffVids);

        List<Video> premiereVids = getPremiereVidsTask.downloadVideos();
        if (premiereVids != null)
            vidList.add(premiereVids);

        List<Video> bestMonthVids = getBestMonthVidsTask.downloadVideos();
        if (bestMonthVids != null)
            vidList.add(bestMonthVids);

        List<Video> bestYearVids = getBestYearVidsTask.downloadVideos();
        if (bestYearVids != null)
            vidList.add(bestYearVids);

        // return value here piped to onPostExecute
        Log.d(TAG, "finished exeuction of async");
        return vidList;
    }

        /*this method won't execute until there's a subscriber


          it's likely this method has to be called in the success method inCallBacks class
          the (model) CallBacks class

          the getVideosObservable method will not run anything until there is a subscriber,
          so I do subscribe in the on success??

          I just know that in the success() function I have to call Observable.just
        */

    public Observable<List<Video>> getVideosObservable(GetVideosTask task){
        final GetVideosTask t = task;
        return Observable.defer(new Callable<ObservableSource<? extends List<Video>>>() {
            @Override
            public ObservableSource<? extends List<Video>> call() throws Exception {
                return Observable.just(t.downloadVideos());
            }
        });
    }

    public boolean vidsResponseError(List<Video> videos, String taskName){
        if (videos.size() == 0) {
            Log.e(TAG, "JSON response success, but List<Video> for is empty for task " + taskName);
            return true;
        }
        else if (videos == null){
            Log.e(TAG, "API call failed; no JSON response");
            return true;
        }
        else{
            return false;
        }
    }

    private class GetVidsAsyncTask extends AsyncTask<Void, Void, List<List<Video>> >{

        @Override
        protected List<List<Video>> doInBackground(Void... params) {

            List<List<Video>> vidList = new ArrayList<List<Video>>();
            int per_page = 5;

           /* GetVideosTask getStaffVidsTask = new GetVideosTask (per_page);
            List<Video> staffVids = getStaffVidsTask.downloadVideos("/channels/staffpicks/videos");
            if (staffVids != null)
                vidList.add(staffVids);

            GetVideosTask getPremiereVidsTask = new GetVideosTask (per_page);
            List<Video> premiereVids = getPremiereVidsTask.downloadVideos("/channels/premieres/videos");
            if (premiereVids != null)
                vidList.add(premiereVids);

            GetVideosTask getBestMonthVidsTask = new GetVideosTask (per_page);
            List<Video> bestMonthVids = getBestMonthVidsTask.downloadVideos("/channels/bestofthemonth/videos");
            if (bestMonthVids != null)
                vidList.add(bestMonthVids);

            GetVideosTask getBestYearVidsTask = new GetVideosTask (per_page);
            List<Video> bestYearVids = getBestYearVidsTask.downloadVideos("/channels/bestoftheyear/videos");
            if (bestYearVids != null)
                vidList.add(bestYearVids);*/

                // return value here piped to onPostExecute
            Log.d(TAG, "finished exeuction of async");
            return vidList;
        }

        @Override
        protected void onPostExecute(List<List<Video>> result) {

            Log.d(TAG, "onPostExecute called. results size: " + result.size());
            int i = 0;
            for (List<Video> list : result){
                Log.d(TAG, "\t list " + i + ":");
                for (Video vid : list){
                   Log.d(TAG, vid.name);
                }
            }

            //items = result;
            mAdapter.addAll(items);
            mAdapter.notifyDataSetChanged();
        }
    }

}
