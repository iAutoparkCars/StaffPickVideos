package android.example.com.boguscode;

/**
 * Created by Steven on 11/12/2017.
 */

import android.example.com.boguscode.models.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;;import com.vimeo.networking.model.Video;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BlankFragment extends Fragment {

    private final String TAG = getClass().getName();

    private RecyclerView recView;
    private DownloadVidsTask task;

    VidListAdapter adapter;
    public String fragmentName;

    public void setName(String name) { this.fragmentName = name; }
    public void setTask(DownloadVidsTask task) { this.task = task; }

    public BlankFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = getAdapter();

    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.lastPosition = -5;
        //Log.d(TAG, "onResume >> " + fragmentName + "lastPos: " + adapter.lastPosition);
    }

    @Override
    public void onPause(){
        super.onPause();
        adapter.lastPosition = -1;
        //Log.d(TAG, "onPause >> " + fragmentName + "lastPos: " + adapter.lastPosition);
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

        if (adapter != null && visible) {
            //Log.d(TAG, "onVisible >> " + fragmentName + "lastPos: " + adapter.lastPosition);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            // Inflate the list layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vid_list, container, false);
        recView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);

        recView.setHasFixedSize(true);

            // using an ArrayList for better performance since I will be adding often
        List<VideoItem> list = new ArrayList<>();
        list.add(new VideoItem("test one", "test one"));
        list.add(new VideoItem("test 2", "test 2"));

        //adapter = new VidListAdapter();
        recView.setAdapter(getAdapter());

        //adapter.addItems(list);
        //getAdapter().addItem(new VideoItem("test one", "test one"));
        //getAdapter().addItem(new VideoItem("test 2", "test 2"));
        getAdapter().notifyDataSetChanged();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(llm);

            // infinite scrolling here
        setInfiniteScrolling(llm);

        return rootView;
    }

    public void setInfiniteScrolling(final LinearLayoutManager layoutManager){

        recView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int pastVisiblesItems, visibleItemCount, totalItemCount;
            boolean loading = true;

            int oldLastItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {

                if(dy > 0) // if scroll is a downscroll
                {
                    //Log.d(TAG, "dx: " + dx + " dy: " + dy);
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    Log.d(TAG, "visibleItemCount: " + visibleItemCount + " totalItemCount: " + totalItemCount
                            + " pastVisibleItems: " + pastVisiblesItems);

                    if (loading) {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            //DownloadVidsTask task = ((MainActivity) getActivity()).tasks.get(fragmentName);

                            Log.d(TAG, "Last Item Wow. activity: " + task.getName() + " total: " + task.getPage());
                            task.downloadVideos();
                        }
                    }
                }

                /* reset when last item is out of view(below) on down or up scroll
                 * if you want to wait for 2 items to disappear to reset, do totalItemCount-1, so forth
                */
                if ( (visibleItemCount+pastVisiblesItems) < totalItemCount) {
                    loading = true;
                }
            }
        });
    }


        // add items to adapter here, then call adapter.notifyItemRangeInserted
    public void addItems(List<Video> videos){

        int oldSize = getAdapter().getItemCount();
        int itemCount = videos.size();

        Log.d(TAG, "Before add, data size: " + oldSize);

        for (Video vid : videos){
            getAdapter().addItem(new VideoItem(vid));
        }

        Log.d(TAG, "AFTER add, data size: " + getAdapter().getItemCount());

        //getAdapter().addItem(item);
        //getAdapter().notifyDataSetChanged();

        getAdapter().notifyItemRangeInserted(oldSize, itemCount);



        /*List<VideoItem> vids = new ArrayList<>();
        for (Video vid : videos){
            vids.add(new VideoItem(vid));
        }

        if (adapter == null){

        }*/



        //Log.d(TAG, "link 1st vid of list:\n " + vids.get(0).getVidUrl());
    }

    public VidListAdapter getAdapter(){
        if (adapter == null)
            adapter = new VidListAdapter();
        return adapter;
    }
}