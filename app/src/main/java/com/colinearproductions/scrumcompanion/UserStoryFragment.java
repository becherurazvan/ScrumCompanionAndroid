package com.colinearproductions.scrumcompanion;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;

import RecyclerViewHolders.UserStoryTaskHolder;
import Scrum.UserStory;


@EFragment
public class UserStoryFragment extends Fragment implements View.OnClickListener {

    private UserStory userStory;

    private OnUserStoryFragmentInteractionListener mListener;


    RecyclerView recyclerView;
    UserStoryTaskHolder taskHolder;
    boolean editing = false;

    Button editButton;
    EditText description;

    public UserStoryFragment() {
        // Required empty public constructor
    }

    public void setUserStory(UserStory story){
        this.userStory = story;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_story,container,false);
        rootView.setTag("UserStoryFragment");
        recyclerView = (RecyclerView)rootView.findViewById(R.id.user_story_task_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        taskHolder = new UserStoryTaskHolder(userStory.getTasks());
        description = (EditText)rootView.findViewById(R.id.user_story_description_edittext);
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
        TextView storyId = (TextView) getActivity().findViewById(R.id.user_story_id_text);
        storyId.setText(userStory.getId());
        Button newTask  = (Button) getActivity().findViewById(R.id.user_story_new_task_button);
        newTask.setOnClickListener(this);

        editButton = (Button) getActivity().findViewById(R.id.edit_user_story_description_button);
        editButton.setOnClickListener(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.user_story_new_task_button){
            mListener.createNewTask(userStory.getId());
        }else if(v.getId()==R.id.edit_user_story_description_button){
            if(editing){
                editing=false;
                editButton.setText("Edit");
                description.setEnabled(false);
                editButton.setTextColor(Color.parseColor("#FFE552"));

                userStory.setDescription(description.getText().toString());
                mListener.userStoryEdited(userStory);
            }else{
                editButton.setText("Save");
                editing=true;
                description.setEnabled(true);
                editButton.setTextColor(Color.parseColor("#FF5263"));
            }

        }
    }

    public interface OnUserStoryFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void createNewTask(String userStoryId);
        void userStoryEdited(UserStory story);

    }
}
