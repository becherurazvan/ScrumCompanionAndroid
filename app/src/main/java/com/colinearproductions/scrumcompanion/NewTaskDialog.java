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

    public NewTaskDialog() {

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

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_task_dialog_ok) {
            Toast.makeText(getActivity(), "Ok pressed", Toast.LENGTH_SHORT).show();
            String description = taskDescription.getText().toString();
            int points = 0;
            if (storyPoints.getText().toString().length() > 0)
                points = Integer.parseInt(storyPoints.getText().toString());
            communicator.newTaskCreated(new Task(description, points, userStoryId));
            dismiss();
        } else {
            dismiss();
        }
    }

    interface Communicator {
        public void newTaskCreated(Task newTask);
    }
}
