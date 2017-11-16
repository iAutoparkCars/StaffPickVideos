package android.example.com.boguscode;

/**
 * Created by Steven on 11/12/2017.
 */

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.example.com.boguscode.databinding.ViewVideoCardBinding;
import android.example.com.boguscode.models.VideoItem;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import  android.example.com.boguscode.ViewHolder;

import com.vimeo.networking.model.Video;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VidListAdapter extends RecyclerView.Adapter<ViewHolder> {

    private String TAG = getClass().getName();
    private List<VideoItem> mDataset;
    ViewVideoCardBinding cardBinding;
    private LayoutInflater inflater;
    public int lastPosition = -1;
    private Context context;
    public String name;

    public VidListAdapter(List<VideoItem> myDataset) {

    }

    public VidListAdapter(){
        mDataset = new LinkedList<VideoItem>();
        //setHasStableIds(true);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // inflate a new view
        if (inflater == null) {
            context = parent.getContext();
            inflater = LayoutInflater.from(context);
        }
        ViewVideoCardBinding viewBinding = DataBindingUtil.inflate(inflater, R.layout.view_video_card, parent,false);

        return new ViewHolder(viewBinding, parent);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Log.d(TAG, "onBind lastPos: " + lastPosition);

        VideoItem vidItem = mDataset.get(position);

        if (lastPosition != -5) {
            Animation animation;
            animation = AnimationUtils.loadAnimation(context,
                    (position < lastPosition) ? R.anim.slide_down : R.anim.slide_up);
            holder.setAnimation(animation);
        }
        lastPosition = position;
        holder.bind(vidItem);

        /*holder.mTextView.setText(mDataset[position]);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentValue = mDataset[position];
                Log.d("CardView", "CardView Clicked: " + currentValue);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // -- Override getItemId, getItemViewType to prevent repeated cards on scroll --
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void addItems(List<VideoItem> list){

        if (mDataset == null)
            mDataset = new LinkedList<>();

        for (VideoItem item : list){
            mDataset.add(item);
        }
    }


    /*public void addItems(List<String> list){

        if (mDataset == null)
            mDataset = new ArrayList<String>();

        for (String obj : list){
            mDataset.add(obj);
        }
    }*/

    /*public void addItems(List<String> list){

        if (mDataset == null)
            mDataset = new ArrayList<VideoItem>();

        int numInserted = list.size();
        int oldLastPos = mDataset.size()-1;

        for (VideoItem vid : list){
            mDataset.add(vid);
        }

        notifyItemRangeInserted(oldLastPos + 1, numInserted);
    }*/

    public void addItem(VideoItem vid){

        mDataset.add(vid);
        //this.notifyDataSetChanged();

        //Log.d(TAG, "MainActivity tried to add item to list");

    }

}
