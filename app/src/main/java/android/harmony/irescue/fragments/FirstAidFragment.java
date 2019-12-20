package android.harmony.irescue.fragments;

import android.harmony.irescue.R;
import android.harmony.irescue.adapters.YoutubeVideoAdapter;
import android.harmony.irescue.model.YoutubeVideoModel;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FirstAidFragment extends Fragment {
    private RecyclerView mRecyclerView;

    public static Fragment newInstance(){
        return new FirstAidFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_youtube,container,false);
        mRecyclerView=view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        populateRecylerView();
        return view;
    }

    private void populateRecylerView() {
        final ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = generateDummyVideoList();
        YoutubeVideoAdapter adapter = new YoutubeVideoAdapter(getActivity(), youtubeVideoModelArrayList);
        mRecyclerView.setAdapter(adapter);

    }

    private ArrayList<YoutubeVideoModel> generateDummyVideoList() {
        ArrayList<YoutubeVideoModel> youtubeVideoModelArrayList = new ArrayList<>();

        //get the video id array, title array and duration array from strings.xml
        String[] videoIDArray = getResources().getStringArray(R.array.video_ids);
        String[] videoTitleArray = getResources().getStringArray(R.array.videos_names);
        String[] videoDurationArray = getResources().getStringArray(R.array.videos_duration);

        //loop through all items and add them to arraylist
        for (int i = 0; i < videoIDArray.length; i++) {

            YoutubeVideoModel youtubeVideoModel = new YoutubeVideoModel();
            youtubeVideoModel.setVideoId(videoIDArray[i]);
            youtubeVideoModel.setTitle(videoTitleArray[i]);
            youtubeVideoModel.setDuration(videoDurationArray[i]);

            youtubeVideoModelArrayList.add(youtubeVideoModel);

        }

        return youtubeVideoModelArrayList;
    }
}
