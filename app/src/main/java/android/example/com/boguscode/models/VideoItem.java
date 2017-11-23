package android.example.com.boguscode.models;

import android.util.Log;
import android.view.View;

import com.vimeo.networking.model.Video;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Steven on 11/13/2017.
 */

public class VideoItem extends Video {

    private Video video;
    public Video getVideo() { return this.video; }
    public String thumbnailUrl;
    private int progressVisibility = View.VISIBLE;

    public VideoItem(Video video){
        this.video = video;
    }

    public String getName(){
        return (video.name == null || video.name.length() == 0 )? ("") : video.name;
    }

    public String getUserName(){
        return (video.user.name == null || video.user.name.length() == 0 )? ("") : video.user.name;
    }

        // methods that
    public String getDuration(){
        //return Stringthis.video.duration;

        int seconds = video.duration;

        if (seconds <= 0)
            return "";
        else if (seconds < 3600)     // less than an hour
            return toMinSecFormat(seconds);
        else                         // more than an hour; convert to hour: min format
            return toHourMinFormat(seconds);
    }

    private String toMinSecFormat(int seconds){
        return String.format(Locale.getDefault(), "%d:%02d",
                TimeUnit.SECONDS.toMinutes(seconds),
                seconds - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds))
        );
    }

    private String toHourMinFormat(int seconds){
        return String.format(Locale.getDefault(), "%dh%2dm",
                TimeUnit.SECONDS.toHours(seconds),
                TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds))
        );
    }

    public String getThumbnailUrl(){

        if (video == null){
            Log.e("VideoItem", "video is null");
            return "placeholder";
        }else{
            return video.pictures.sizes.get(3).link;
        }
    }

    public Integer getPlayCount(){
        return this.video.playCount();
    }

    public String getVidUrl(){
        return this.video.link;
    }
}
