package android.example.com.boguscode;

import android.content.Context;
import android.example.com.boguscode.databinding.ViewVideoCardBinding;
import android.example.com.boguscode.models.VideoItem;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;


public class ViewHolder extends RecyclerView.ViewHolder {

    private String TAG = getClass().getName();
    public ViewVideoCardBinding cardBinding;
    public int containerId = 0;
    public CardView cardView;
    private Animation animation = null;

    public ViewHolder(ViewVideoCardBinding dataBinding, View v) {
        super(dataBinding.getRoot());
        this.cardBinding = dataBinding;
    }

    public void setAnimation(Animation animation){
        this.animation = animation;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    /*public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            //mCardView = (CardView) v.findViewById(R.id.card_view);
            //mTextView = (TextView) v.findViewById(R.id.tv_text);
        }
    }*/

    public void bind (VideoItem model, Context context)
    {
        /*
            bind method is very strange.
            When you add a new "variable" in the XML, with name "track",
            binding is able to somehow setTrack to set the variable named "track"
        */


        //this.binding.setTrack(model);

        if (animation != null){
            //Log.e(TAG, "Animation started.");
            //itemView.startAnimation(animation);
            getBinding().cardView.startAnimation(animation);
        }

        this.cardBinding.setVideoItem(model);

        // call this to bind the ViewModel to this item in RecView (the ViewHolder)
        this.cardBinding.setItemViewModel(new ListItemViewModel(model, context));

        // all these must be called after image is rendered
       /* this.cardBinding.textBackground.setVisibility(View.VISIBLE);
        this.cardBinding.duration.setVisibility(View.VISIBLE);
        this.cardBinding.userName.setVisibility(View.VISIBLE);
        this.cardBinding.videoName.setVisibility(View.VISIBLE);*/


        //this.binding.setHandler(new FavImgClickHandler());
        //this.binding.executePendingBindings();
    }

    public ViewVideoCardBinding getBinding() { return this.cardBinding; }

}
