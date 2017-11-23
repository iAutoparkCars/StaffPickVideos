package android.example.com.boguscode;

import android.content.Context;
import android.example.com.boguscode.models.*;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.vimeo.networking.Configuration;
import com.vimeo.networking.GsonDeserializer;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.AuthCallback;
import com.vimeo.networking.model.Picture;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.error.VimeoError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

//import io.reactivex.Observable;
//import io.reactivex.ObservableSource;

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

    Map<String, DownloadVidsTask> tasks;
    DownloadVidsTask getStaffVidsTask;
    DownloadVidsTask getPremiereVidsTask;
    DownloadVidsTask getBestMonthVidsTask;
    DownloadVidsTask getBestYearVidsTask;

    final List<Video> staffVids = new ArrayList<Video>();
    final List<Video> premiereVids = new ArrayList<Video>();
    final List<Video> bestMonthVids = new ArrayList<Video>();
    final List<Video> bestYearVids = new ArrayList<Video>();

    BlankFragment recentFragment;
    BlankFragment premiereFragment;
    BlankFragment monthFragment;
    BlankFragment yearFragment;

    boolean staffTaskDone = false;
    boolean premiereTaskDone = false;
    boolean monthTaskDone = false;
    boolean yearTaskDone = false;

    TabLayout tabLayout;
    TabPagerAdapter pagerAdapter;

    // variables for observing network state
    private Disposable networkDisposable;
    private Disposable internetDisposable;
    private boolean isConnected;
    public boolean hasNetworkConnection() { return this.isConnected; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

            // start 4 fragments; set their names; map names to its task
        initFragments();
        setFragmentNames();
        mapNamesToTask();

            // Get the ViewPager and set it's TabPagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        viewPager.setAdapter(pagerAdapter);

            // Set up the view_tab layout with my viewPager
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

            // set the layouts for each list
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }

            // authenticate this app to make API calls
        configureVimeoAuthentication();
        mApiClient = VimeoClient.getInstance();

        // ---- After task finishes download, load results into its proper list ----
        final OnTaskCompleted staffDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                staffTaskDone = true;

                if (vidsResponseError(videos, getStaffVidsTask.getName())) return;

                getFragment(recentFragment, STAFF_VIDS).addItems(videos);
            }
        };

        final OnTaskCompleted premiereDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                premiereTaskDone = true;

                if (vidsResponseError(videos, getPremiereVidsTask.getName())) return;

                getFragment(premiereFragment, PREMIERE_VIDS).addItems(videos);
            }
        };

        final OnTaskCompleted bestMonthDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                monthTaskDone = true;

                if (vidsResponseError(videos, getBestMonthVidsTask.getName())) return;

                getFragment(monthFragment, MONTH_VIDS).addItems(videos);
            }
        };

        OnTaskCompleted bestYearDone = new OnTaskCompleted() {
            @Override
            public void onDownloadTaskCompleted(List<Video> videos) {
                yearTaskDone = true;

                if (vidsResponseError(videos, getBestYearVidsTask.getName())) return;

                getFragment(yearFragment, YEAR_VIDS).addItems(videos);
            }
        };

        getStaffVidsTask = new DownloadVidsTask(STAFF_VIDS, PER_PAGE, "/channels/staffpicks/videos", staffDone);
        getPremiereVidsTask = new DownloadVidsTask(PREMIERE_VIDS, PER_PAGE, "/channels/premieres/videos", premiereDone);
        getBestMonthVidsTask = new DownloadVidsTask(MONTH_VIDS, PER_PAGE, "/channels/bestofthemonth/videos", bestMonthDone);
        getBestYearVidsTask = new DownloadVidsTask(YEAR_VIDS, PER_PAGE, "/channels/bestoftheyear/videos", bestYearDone);

        recentFragment.setTask(getStaffVidsTask);
        premiereFragment.setTask(getPremiereVidsTask);
        monthFragment.setTask(getBestMonthVidsTask);
        yearFragment.setTask(getBestYearVidsTask);

        // ---- Client Credentials Authenticate ----
        if (mApiClient.getVimeoAccount().getAccessToken() == null) {
                // If there is no access token, fetch one

                // comment this section out when testing to prevent mass generation of auth codes
                authenticateWithClientCredentials();
                getStaffVidsTask.downloadVideos();
                getPremiereVidsTask.downloadVideos();
                getBestMonthVidsTask.downloadVideos();
                getBestYearVidsTask.downloadVideos();

                //new GetVidsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    private void mapNamesToTask() {
        tasks = new HashMap<String, DownloadVidsTask>();
        tasks.put(STAFF_VIDS, getStaffVidsTask);
        tasks.put(PREMIERE_VIDS, getPremiereVidsTask);
        tasks.put(MONTH_VIDS, getBestMonthVidsTask);
        tasks.put(YEAR_VIDS, getBestYearVidsTask);
    }

    private void setFragmentNames() {
        recentFragment.setName(STAFF_VIDS);
        premiereFragment.setName(PREMIERE_VIDS);
        monthFragment.setName(MONTH_VIDS);
        yearFragment.setName(YEAR_VIDS);
    }

    private void initFragments() {
        // instantiate fragments for each list
        recentFragment = new BlankFragment();
        premiereFragment = new BlankFragment();
        monthFragment = new BlankFragment();
        yearFragment = new BlankFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNetworkState();
    }

    private void checkNetworkState() {
        // observing network is more refined. Tells you if connected to MOBILE or INTERNET
        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Connectivity>() {
                    @Override
                    public void accept(Connectivity connectivity) throws Exception {
                        Log.d(TAG, connectivity.toString());
                        final NetworkInfo.State state = connectivity.getState();

                        if (state == NetworkInfo.State.DISCONNECTED || state == NetworkInfo.State.DISCONNECTING){
                            //Log.d(TAG, "Disconnected");
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.networkDisconnected), Toast.LENGTH_LONG);
                        }

                        if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING){
                            MainActivity.this.isConnected = true;
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.networkConnected), Toast.LENGTH_SHORT);
                            //Log.d(TAG, "There is now network connection.");
                        }

                        final String name = connectivity.getTypeName();
                        Log.d(TAG, String.format("state: %s, typeName: %s", state, name));
                    }
                });

        // observing internet tells me if I have any connection or not
        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isConnected) throws Exception {
                        Log.d(TAG, "is Connected? " + isConnected);
                        //MainActivity.this.isConnected = isConnected;
                    }
                });
    }

    @Override protected void onPause() {
        super.onPause();
        safelyDispose(networkDisposable, internetDisposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        safelyDispose(networkDisposable, internetDisposable);
    }
        // free up resources by disposing
    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
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
        */

    /*public Observable<List<Video>> getVideosObservable(DownloadVidsTask task){
        final DownloadVidsTask t = task;
        return Observable.defer(new Callable<ObservableSource<? extends List<Video>>>() {
            @Override
            public ObservableSource<? extends List<Video>> call() throws Exception {
                return Observable.just(t.downloadVideos());
            }
        });
    }*/

    public boolean vidsResponseError(List<Video> videos, String taskName){
        if (videos == null){
            Log.e(TAG, "API call failed; no JSON response");
            return true;
        }
        else if (videos.size() == 0) {
            Log.e(TAG, "JSON response success, but List<Video> for is empty for task " + taskName);
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

    class TabPagerAdapter extends FragmentPagerAdapter {

        String tabTitles[] = new String[] { "Recent", "Premieres", "Month", "Year" };
        Context context;

        public TabPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

                // specify which of the four lists (or Fragments) to get
            switch (position) {
                case 0:
                    return recentFragment;
                case 1:
                    return premiereFragment;
                case 2:
                    return monthFragment;
                case 3:
                    return yearFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
                // make title based on item position
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.tab_title);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

        // function used for debugging
    public String printVidInfo(Video vid){
        int size = vid.pictures.sizes.size();
        return "title: " + vid.name + "\n vid url: " + vid.link + "\n; user/name: " + vid.user.name + "; user/getName: " + vid.user.getName()
                + "\n; pic_urls: " + "size: " + size +" " +vid.pictures.sizes.get(1).link + "; duration: " + vid.duration + "; play#: " + vid.stats.plays;
    }

    public BlankFragment getFragment(BlankFragment fragment, String name){
        if (fragment == null){
            fragment = new BlankFragment();
            fragment.setName(name);
        }
        return fragment;
    }
}
