package android.example.com.boguscode;

import android.example.com.boguscode.databinding.ViewVideoCardBinding;
import android.example.com.boguscode.models.VideoItem;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class ViewHolder extends RecyclerView.ViewHolder {

    private ViewVideoCardBinding cardBinding;
    public int containerId = 0;
    public CardView cardView;

    public ViewHolder(ViewVideoCardBinding dataBinding, View v) {
        super(dataBinding.getRoot());
        this.cardBinding = dataBinding;

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

    public void bind (VideoItem model)
    {
        /*
            bind method is very strange.
            When you add a new "variable" in the XML, with name "track",
            binding is able to somehow setTrack to set the variable named "track"
        */

        //this.binding.setTrack(model);
        this.cardBinding.setVideoItem(model);

        //this.binding.setHandler(new FavImgClickHandler());

        //this.binding.executePendingBindings();
    }

    public ViewVideoCardBinding getBinding() { return this.cardBinding; }

}
