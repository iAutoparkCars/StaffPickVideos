package android.example.com.boguscode;

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
import com.vimeo.networking.model.VideoList;
import com.vimeo.networking.model.error.VimeoError;

import org.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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

    GetVideosTask getStaffVidsTask;
    GetVideosTask getPremiereVidsTask;
    GetVideosTask getBestMonthVidsTask;
    GetVideosTask getBestYearVidsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureVimeoAuthentication();
        mApiClient = VimeoClient.getInstance();

        getStaffVidsTask = new GetVideosTask(PER_PAGE, "/channels/staffpicks/videos", new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                for (Video vid : videos){
                    Log.d(TAG, vid.name);
                }
            }
        });

        getPremiereVidsTask = new GetVideosTask (PER_PAGE, "/channels/premieres/videos");
        getBestMonthVidsTask = new GetVideosTask (PER_PAGE, "/channels/bestofthemonth/videos");
        getBestYearVidsTask = new GetVideosTask (PER_PAGE, "/channels/bestoftheyear/videos");

        //Log.d(TAG, "id: " + Thread.currentThread().getId());




        /*getStaffVidsTask.getObservableTask().subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {}

            @Override
            public void onNext(String value) {
                Log.d(TAG, "onNext called with value: " + value);
                //List<Video> list = getStaffVidsTask.getVidList();
                //Log.d(TAG, "SIZE: " + list.size());
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete called");
                //List<Video> list = getStaffVidsTask.getVidList();
                //Log.d(TAG, "SIZE: " + list.size());
            }

        });*/

        // ---- Client Credentials Authenticate ----
        if (mApiClient.getVimeoAccount().getAccessToken() == null) {
                // If there is no access token, fetch one
                authenticateWithClientCredentials();

                //new GetVidsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                Log.d(TAG, "Started downloading videos");

            getStaffVidsTask.getObservableTask().subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String value) {
                        Log.e(TAG, "onNext called: " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError called: " + e);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete called");
                    }
                });

                // ---- Subscribe to the networking tasks ----
                /*getVideosObservable(getStaffVidsTask)
                        //.subscribeOn(Schedulers.io())
                        //.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<Video>> () {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(List<Video> value) {
                                Log.d(TAG, "onNext called with value: " + value + "size: " + value.size());
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError called:" + e);
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete called");
                            }
                        });*/
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
