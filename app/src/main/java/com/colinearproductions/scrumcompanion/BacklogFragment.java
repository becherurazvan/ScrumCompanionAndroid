package com.colinearproductions.scrumcompanion;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;

import Entities.Project;
import Helper.OnStartDragListener;
import Helper.SimpleItemTouchHelperCallback;
import RecyclerViewHolders.UserStoryTaskHolder;
import Scrum.UserStory;


@EFragment
public class BacklogFragment extends Fragment{
    private static final String TAG ="BACKLOG FRAGMENT";
    private ItemTouchHelper mItemTouchHelper;
    RecyclerListAdapterSwipableBacklog adapter;
    private OnBacklogFragmentInteractionListener mListener;

    public BacklogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Product Backlog");

       MainScreen mainScreen = (MainScreen)getActivity();
        if(mainScreen.getProject()!=null){
            backlogLoaded(mainScreen.getProject());
        }


    }

    public void backlogLoaded(Project p) {
        ArrayList<UserStory> stories = p.getProductBacklog().getUserStories();
        adapter.addAllItems(stories);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.backlog_layout, container, false);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RecyclerListAdapterSwipableBacklog(getFragmentManager());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.backlog_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button newUserStory = (Button)view.findViewById(R.id.add_new_user_story_button);
        newUserStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showNewUserStoryDialog();
            }
        });

        Sync();
    }


    public void Sync(){
        MainScreen a = (MainScreen)getActivity();
        Project p = a.getProject();
        backlogLoaded(p);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBacklogFragmentInteractionListener) {
            mListener = (OnBacklogFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnBacklogFragmentInteractionListener {
        // TODO: Update argument type and name
        void showNewUserStoryDialog();
    }
}
