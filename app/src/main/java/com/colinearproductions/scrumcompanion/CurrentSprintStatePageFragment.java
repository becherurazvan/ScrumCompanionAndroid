package com.colinearproductions.scrumcompanion;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import RecyclerViewHolders.CurrentSprintPageHolder;
import RecyclerViewHolders.CurrentSprintUSholder;
import Scrum.Sprint;
import Scrum.Task;
import Scrum.UserStory;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentSprintStatePageFragment extends Fragment implements CurrentSprintPageHolder.OnTaskUpdatedListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;




    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public CurrentSprintStatePageFragment() {
        // Required empty public constructor
    }

    public static CurrentSprintStatePageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        CurrentSprintStatePageFragment fragment = new CurrentSprintStatePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_sprint_state_page, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.current_sprint_page_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);


        MainScreen screen = (MainScreen) getActivity();

        Sprint currentSprint = screen.getProject().getProductBacklog().getCurrentSprint();
        ArrayList<Task> tasks = new ArrayList<>();
        Log.i(mPage + "", mPage + "");
        switch (mPage){
            case 0: // will never happend
                break;
            case 1: // not started
                tasks= currentSprint.getTasksByState(Task.NOT_STARTED_STATE);
                break;
            case 2: // working on
                tasks= currentSprint.getTasksByState(Task.WORKING_ON_STATE);
                break;
            case 3: // done
                tasks= currentSprint.getTasksByState(Task.FINISHED_STATE);
                break;
        }


        mAdapter=new CurrentSprintPageHolder(tasks,mPage,mRecyclerView,this,this);
        mRecyclerView.setAdapter(mAdapter);



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainScreen screen = (MainScreen) getActivity();

        Sprint currentSprint = screen.getProject().getProductBacklog().getCurrentSprint();
        ArrayList<Task> tasks = new ArrayList<>();
        Log.i(mPage + "", mPage + "");
        switch (mPage){
            case 0: // will never happend
                break;
            case 1: // not started
                tasks= currentSprint.getTasksByState(Task.NOT_STARTED_STATE);
                break;
            case 2: // working on
                tasks= currentSprint.getTasksByState(Task.WORKING_ON_STATE);
                break;
            case 3: // done
                tasks= currentSprint.getTasksByState(Task.FINISHED_STATE);
                break;
        }


        CurrentSprintPageHolder adapter = (CurrentSprintPageHolder)mAdapter;
        adapter.updateTasks(tasks);

    }


    @Override
    public void taskUpdated(Task t) {
        MainScreen mainScreen = (MainScreen)getActivity();


        mainScreen.updateTaskState(t);
    }



    public String getUserName(){
       return ((MainScreen)getActivity()).app.getUsername();
    }
}
