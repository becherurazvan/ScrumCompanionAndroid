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
import Scrum.UserStory;

/**
 * Created by rbech on 2/23/2016.
 */
public class NewUserStoryDialog extends DialogFragment implements View.OnClickListener {

    EditText taskDescription;
    TextView ok;
    TextView cancel;
    UserStoryCommunicator communicator;

    public NewUserStoryDialog(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator= (UserStoryCommunicator) activity;
        Log.i("Dialog:", activity.getTitle().toString());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_user_story_dialog,null);
        ok =(TextView) view.findViewById(R.id.new_US_dialog_ok);
        cancel = (TextView) view.findViewById(R.id.new_US_dialog_cancel);
        taskDescription = (EditText) view.findViewById(R.id.new_US_description_text);
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
        if(v.getId()==R.id.new_US_dialog_ok){
            Toast.makeText(getActivity(), "Ok pressed", Toast.LENGTH_SHORT).show();
            String description = taskDescription.getText().toString();
            communicator.newUserStoryCreated(new UserStory(description));
            dismiss();
        }else{
            dismiss();
        }
    }

    interface UserStoryCommunicator{
        public void newUserStoryCreated(UserStory userStory);
    }
}
