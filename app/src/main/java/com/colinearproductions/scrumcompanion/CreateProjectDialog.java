package com.colinearproductions.scrumcompanion;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CreateProjectDialog extends DialogFragment implements View.OnClickListener{

    EditText projectName;
    TextView ok;
    TextView cancel;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator= (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_project_dialog,null);
        ok =(TextView) view.findViewById(R.id.okButton);
        cancel = (TextView) view.findViewById(R.id.cancelButton);
        projectName = (EditText) view.findViewById(R.id.projectNameText);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);


        return view;


        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.okButton){
            Toast.makeText(getActivity(),"Ok pressed",Toast.LENGTH_SHORT).show();
            String projectNameText = projectName.getText().toString();
            communicator.onDialogMessage("OK:"+projectName.getText());
            dismiss();
        }else{
            Toast.makeText(getActivity(),"Cancel pressed",Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    interface Communicator{
        public void onDialogMessage(String message);
    }
}
