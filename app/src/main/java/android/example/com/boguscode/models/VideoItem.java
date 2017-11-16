package android.example.com.boguscode.models;

import android.util.Log;

import com.vimeo.networking.model.Video;

/**
 * Created by Steven on 11/13/2017.
 */

public class VideoItem extends Video {

    private Video video;
    public Video getVideo() { return this.video; }
    public String thumbnailUrl;

    public VideoItem(Video video){
        this.video = video;
    }

    String text1;
    String text2;
    public VideoItem(String text1, String text2){
            this.text1 = text1;
            this.text2 = text2;
    }

    public String getName(){
        //return this.video.name;
        return text1;
    }

    public String getUserName(){
        //return this.video.user.name;
        return text2;
    }

        // methods that
    public int getDuration(){
        return this.video.duration;
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
