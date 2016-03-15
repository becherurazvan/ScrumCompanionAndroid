package com.colinearproductions.scrumcompanion;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import Scrum.Task;

/**
 * Created by rbech on 2/23/2016.
 */
public class NewTaskDialog extends DialogFragment implements View.OnClickListener {

    EditText taskDescription;
    EditText storyPoints;
    TextView ok;
    TextView cancel;
    Communicator communicator;
    String userStoryId;
    Task task;
    boolean edit=false;

    public NewTaskDialog() {

    }

    public void setTask(Task t){
        this.task = t;
        edit=true;
    }


    public void setUserStoryId(String userStoryId) {
        this.userStoryId = userStoryId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
        Log.i("Dialog:", activity.getTitle().toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task_dialog, null);
        ok = (TextView) view.findViewById(R.id.new_task_dialog_ok);
        cancel = (TextView) view.findViewById(R.id.new_task_dialog_cancel);
        taskDescription = (EditText) view.findViewById(R.id.new_task_description_text);
        storyPoints = (EditText) view.findViewById(R.id.new_task_points_text);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        if(edit){
            taskDescription.setText(task.getDescription());
            storyPoints.setText(String.valueOf(task.getPoints()));
        }

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if(!edit) {
            if (v.getId() == R.id.new_task_dialog_ok) {
                Toast.makeText(getActivity(), "Ok pressed", Toast.LENGTH_SHORT).show();
                String description = taskDescription.getText().toString();
                int points = 1;
                if (storyPoints.getText().toString().length() > 0)
                    points = Integer.parseInt(storyPoints.getText().toString());
                communicator.newTaskCreated(new Task(description, points, userStoryId));
                dismiss();
            } else {
                dismiss();
            }
        }else{
            if (v.getId() == R.id.new_task_dialog_ok) {
                task.setDescription(taskDescription.getText().toString());
                int points = 1;
                if (storyPoints.getText().toString().length() > 0)
                    points = Integer.parseInt(storyPoints.getText().toString());
                task.setPoints(points);
                communicator.taskEditFinished(task);
                dismiss();
            } else {
                dismiss();
            }

        }
    }

    interface Communicator {
        public void newTaskCreated(Task newTask);
        public void taskEditFinished(Task task);
    }
}
