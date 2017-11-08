package android.example.com.boguscode;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.vimeo.networking.model.Video;
import com.vimeo.networking.model.VideoList;

import java.lang.reflect.Type;

/**
 * Created by Steven on 11/7/2017.
 */

public class VideoListDeserializer implements JsonDeserializer<VideoList>{

    @Override
    public VideoList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        VideoList list = new VideoList();
        list.data.add(new Video());
        return list;
    }

}
