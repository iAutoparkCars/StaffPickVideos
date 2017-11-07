package android.example.com.boguscode;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.vimeo.networking.Configuration;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.AuthCallback;
import com.vimeo.networking.model.error.VimeoError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

        // realistically, this would be stored somewhere more safe
    private String ACCESS_TOKEN = "b529a655ff2fc32a8b58bf2b405f39b0";

    private final String CLIENT_ID = "de892f6652ca3657e278db46cd3af801465643e8";
    private final String CLIENT_SECRET = "ZlxBVGjMRLFCC9GIkf0JPwqvssHTFbbYNu56RwiPtplSFR1PWBTdjZkfTSfrdjXA8p" +
                                         "HVGt1U6vtP7NQekqs8FmXnTdr3ub5DpHp4os0fUGcQ10liWfrTALtP850zicpX";

    private VimeoClient mApiClient;

    public final String TAG = getClass().getName();

    private ListView mListView;
    private VideoAdapter mAdapter;
    private ArrayList<JSONObject> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureVimeoAuthentication();
        mApiClient = VimeoClient.getInstance();

        // ---- Client Credentials Auth ----
        if (mApiClient.getVimeoAccount().getAccessToken() == null) {
            // If there is no access token, fetch one on first app open
            authenticateWithClientCredentials();
        }

        mListView = (ListView) findViewById(R.id.activity_main_listview);
        mAdapter = new VideoAdapter(this, R.id.list_item_video_name_textview, items);
        mListView.setAdapter(mAdapter);
        new StaffPicksAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void configureVimeoAuthentication(){
        Configuration.Builder configBuilder =
                new Configuration.Builder(CLIENT_ID, CLIENT_SECRET, "public")
                        .setCacheDirectory(this.getCacheDir());
        VimeoClient.initialize(configBuilder.build());
    }

    // You can't make any requests to the api without an access token. This will get you a basic
    // "Client Credentials" grant which will allow you to make requests. This requires a client id and client secret.
    private void authenticateWithClientCredentials() {
        VimeoClient.getInstance().authorizeWithClientCredentialsGrant(new AuthCallback() {
            @Override
            public void success() {
                String accessToken = VimeoClient.getInstance().getVimeoAccount().getAccessToken();
                Log.d(TAG, "Client Credentials Authorization Success with Access Token: " + accessToken);
                ACCESS_TOKEN = accessToken;
            }

            @Override
            public void failure(VimeoError error) {
                String errorMessage = error.getDeveloperMessage();
                Log.d(TAG, "Client Credentials Authorization Failure: " + errorMessage);
            }
        });
    }


    private class StaffPicksAsyncTask extends AsyncTask<Void, Void, ArrayList<JSONObject>> {

        @Override
        protected ArrayList<JSONObject> doInBackground(Void... params) {
            //String url = "https://api.vimeo.com/channels/staffpicks/videos";

            String url = "https://api.vimeo.com/channels/premieres/videos";

            //working token
            //String token = "bearer b529a655ff2fc32a8b58bf2b405f39b0";

            String token = "bearer " + ACCESS_TOKEN;

            ArrayList<JSONObject> videos = new ArrayList<>();

            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Authorization", token);
            try {
                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject object = new JSONObject(builder.toString());
                JSONArray data = object.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    videos.add(data.getJSONObject(i));
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return videos;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> result) {
            items = result;
            mAdapter.addAll(items);
            mAdapter.notifyDataSetChanged();
        }
    }
}
