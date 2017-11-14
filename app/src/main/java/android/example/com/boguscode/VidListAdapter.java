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

import java.util.ArrayList;
import java.util.List;

public class VidListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<String> mDataset;
    ViewVideoCardBinding cardBinding;
    private LayoutInflater inflater;
    private int lastPosition = -1;
    private Context context;

    public VidListAdapter(List<String> myDataset) {
        mDataset = myDataset;
        //cardBinding = Da
    }

    public VidListAdapter(){}

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        //ViewHolder vh = new ViewHolder(v);

        if (inflater == null) {
            context = parent.getContext();
            inflater = LayoutInflater.from(context);
        }
        ViewVideoCardBinding viewBinding = DataBindingUtil.inflate(inflater, R.layout.view_video_card, parent,false);

        return new ViewHolder(viewBinding, parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String textData = mDataset.get(position);
        VideoItem vidItem = new VideoItem(textData, textData);

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.slide_down : R.anim.slide_up);
        lastPosition = position;

        holder.setAnimation(animation);
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

    public void addItems(List<String> list){

        if (mDataset == null)
            mDataset = new ArrayList<String>();

        for (String obj : list){
            mDataset.add(obj);
        }
    }

}
