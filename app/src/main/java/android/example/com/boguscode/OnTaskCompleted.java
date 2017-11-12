package android.example.com.boguscode;

import com.vimeo.networking.model.Video;

import java.util.List;

/**
 * Created by Steven on 11/9/2017.
 */

public interface OnTaskCompleted {
    void onDownloadTaskCompleted(List<Video> videos);
}
