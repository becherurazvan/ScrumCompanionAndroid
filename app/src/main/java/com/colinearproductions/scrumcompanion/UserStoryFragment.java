package com.colinearproductions.scrumcompanion;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;

import RecyclerViewHolders.UserStoryTaskHolder;
import Scrum.Task;
import Scrum.UserStory;


@EFragment
public class UserStoryFragment extends Fragment implements View.OnClickListener, UserStoryTaskHolder.TaskEditListener {

    private UserStory userStory;
    private OnUserStoryFragmentInteractionListener mListener;
    RecyclerView recyclerView;
    UserStoryTaskHolder taskHolder;
    boolean editing = false;
    TextView editButton;
    EditText description;

    public UserStoryFragment() {
        // Required empty public constructor
    }

    public void setUserStory(UserStory story) {
        this.userStory = story;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_story, container, false);
        rootView.setTag("UserStoryFragment");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.user_story_task_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        taskHolder = new UserStoryTaskHolder(userStory.getTasks(),this);
        description = (EditText) rootView.findViewById(R.id.user_story_description_edittext);
        recyclerView.setAdapter(taskHolder);
        description.setText(userStory.getDescription());
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserStoryFragmentInteractionListener) {
            mListener = (OnUserStoryFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        FloatingActionButton newTask = (FloatingActionButton) getActivity().findViewById(R.id.user_story_new_task_fab);

        if(newTask==null)
            newTask = (FloatingActionButton) getView().findViewById(R.id.user_story_new_task_fab);

        newTask.setOnClickListener(this);

        editButton = (TextView) getActivity().findViewById(R.id.edit_user_story_description);

        if(editButton==null)
            editButton = (TextView) getView().findViewById(R.id.edit_user_story_description);
        editButton.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(userStory.getId());


    }

    public void update(){
        Log.i("USER_STORY_FRAGMENT","Updating...");
        MainScreen mainScreen = (MainScreen) getActivity();
        taskHolder.setTasks(mainScreen.getProject().getProductBacklog().getStoryById(userStory.getId()).getTasks());
        description.setText(userStory.getDescription());
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.user_story_new_task_fab) {
            mListener.createNewTask(userStory.getId());
        } else if (v.getId() == R.id.edit_user_story_description) {
            if (editing) {
                editing = false;
                editButton.setText("Edit");
                description.setEnabled(false);
                editButton.setTextColor(Color.parseColor("#2196F3"));
                userStory.setDescription(description.getText().toString());
                mListener.userStoryEdited(userStory);
            } else {
                editButton.setText("Save");
                editing = true;
                description.setEnabled(true);
                editButton.setTextColor(Color.parseColor("#FF5263"));
            }

        }
    }

    @Override
    public void onTaskEditClicked(Task t) {
            mListener.editTaskClicked(t);
    }

    public interface OnUserStoryFragmentInteractionListener {
        void createNewTask(String userStoryId);

        void editTaskClicked(Task t);

        void userStoryEdited(UserStory story);

    }


}
