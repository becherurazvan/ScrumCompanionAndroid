package com.colinearproductions.scrumcompanion;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import Entities.Project;
import RecyclerViewHolders.SprintListViewHolder;
import RecyclerViewHolders.UserStoryTaskHolder;
import Scrum.ProductBacklog;
import Scrum.Sprint;
import Scrum.Task;


public class SprintFragment extends Fragment implements View.OnClickListener {




    RecyclerView recyclerView;
    SprintListViewHolder adapter;

    EditText startDate;
    EditText endDate;

    Button newSprintButton;

    private OnSprintFragmentInteraction mListener;

    public SprintFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sprint,container,false);
        rootView.setTag("SprintsListFragment");
        adapter = new SprintListViewHolder();


        recyclerView = (RecyclerView)rootView.findViewById(R.id.sprints_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        startDate = (EditText) rootView.findViewById(R.id.start_date_edit_text);
        startDate.setOnClickListener(this);
        endDate = (EditText) rootView.findViewById(R.id.end_date_edit_text);
        endDate.setOnClickListener(this);

        recyclerView.setAdapter(adapter);
        newSprintButton = (Button)rootView.findViewById(R.id.add_sprint_button);
        newSprintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.onNewSprintButtonClicked(startDate.getText().toString(),endDate.getText().toString());
                startDate.setText("");
                endDate.setText("");
                newSprintButton.setEnabled(false);
            }
        });


        MainScreen a = (MainScreen)getActivity();
        Project p = a.getProject();
        adapter.setSprints(p.getProductBacklog().getSprints());


        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSprintFragmentInteraction) {
            mListener = (OnSprintFragmentInteraction) context;
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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.start_date_edit_text){
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    startDate.setText(dayOfMonth+" " +getMonth(monthOfYear) +" " +year );
                    if(endDate.getText().toString().length()>0){
                        newSprintButton.setEnabled(true);
                    }else{
                        newSprintButton.setEnabled(false);
                    }
                }
            });
            newFragment.show(getActivity().getFragmentManager(),"datePicker");

        }else if(v.getId()==R.id.end_date_edit_text){
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    endDate.setText(dayOfMonth+" " +getMonth(monthOfYear) +" " +year );
                    if(startDate.getText().toString().length()>0){
                        newSprintButton.setEnabled(true);
                    }else{
                        newSprintButton.setEnabled(false);
                    }
                }
            });
            newFragment.show(getActivity().getFragmentManager(),"datePicker");
        }
    }


    public interface OnSprintFragmentInteraction {
        void onNewSprintButtonClicked(String startDate,String endDate);
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }



}
