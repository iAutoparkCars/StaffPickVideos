package android.example.com.boguscode;

/**
 * Created by Steven on 11/12/2017.
 */

import android.databinding.DataBindingUtil;
import android.example.com.boguscode.databinding.ViewVideoCardBinding;
import android.example.com.boguscode.models.VideoItem;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import  android.example.com.boguscode.ViewHolder;

public class VidListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private String[] mDataset;
    ViewVideoCardBinding cardBinding;
    private LayoutInflater inflater;

    // Provide a suitable constructor (depends on the kind of dataset)
    public VidListAdapter(String[] myDataset) {
        mDataset = myDataset;
        //cardBinding = Da
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        //ViewHolder vh = new ViewHolder(v);

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        ViewVideoCardBinding viewBinding = DataBindingUtil.inflate(inflater, R.layout.view_video_card, parent,false);

        return new ViewHolder(viewBinding, parent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String textData = mDataset[position];
        VideoItem vidItem = new VideoItem(textData, textData);

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
        return mDataset.length;
    }
}
