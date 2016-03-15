package com.colinearproductions.scrumcompanion;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import RecyclerViewHolders.CurrentSprintUSholder;
import Scrum.UserStory;

public class CurrentSprintListSprints extends Fragment implements  CurrentSprintUSholder.ViewHolder.SprintUSInteractionListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    public CurrentSprintListSprints() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.i("CurrentSprintList", "View has been created");
        View rootView = inflater.inflate(R.layout.fragment_current_sprint_list_sprints,container,false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.current_sprint_us_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        MainScreen screen = (MainScreen) getActivity();
        ArrayList<UserStory> storiesInCurrentSprint = screen.getProject().getProductBacklog().getCurrentSprint().getUserStories();
        mAdapter=new CurrentSprintUSholder(storiesInCurrentSprint,this);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainScreen screen = (MainScreen) getActivity();
        ArrayList<UserStory> storiesInCurrentSprint = screen.getProject().getProductBacklog().getCurrentSprint().getUserStories();
        mAdapter=new CurrentSprintUSholder(storiesInCurrentSprint,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onUSClicked(UserStory s) {
        ((MainScreen)getActivity()).showUserStoryFragment(s);
    }

    @Override
    public void onUSRemoveClicked(UserStory s) {

        ((MainScreen)getActivity()).removeUSfromSprint(s);
    }
}
