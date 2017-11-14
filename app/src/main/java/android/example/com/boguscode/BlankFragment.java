package android.example.com.boguscode;

/**
 * Created by Steven on 11/12/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;;import com.vimeo.networking.model.Video;

import java.util.ArrayList;
import java.util.List;

public class BlankFragment extends Fragment {

    private RecyclerView recView;
    VidListAdapter adapter;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            // Inflate the list layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_vid_list, container, false);
        recView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);


        recView.setHasFixedSize(true);

            // using an ArrayList for better performance since I will be adding often
        List<String> list = new ArrayList<String>();
        list.add("test one");
        list.add("test two");
        list.add("test three");
        list.add("test four");
        list.add("test five");
        list.add("test six");
        list.add("test seven");
        list.add("test 8");

        adapter = new VidListAdapter();
        recView.setAdapter(adapter);

        adapter.addItems(list);
        adapter.notifyDataSetChanged();

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(llm);

        return rootView;
    }

        // add items to adapter here, then call adapter.notifyItemRangeInserted
    public void addItems(List<Video> videos){
    }
}