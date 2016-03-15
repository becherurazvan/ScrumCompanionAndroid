package com.colinearproductions.scrumcompanion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {


    DatePickerDialog.OnDateSetListener listener;

    Calendar minDate;

    public void setMinDate(Calendar calendar){
        this.minDate = calendar;
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it

        DatePickerDialog datePickerDialog  = new DatePickerDialog(getActivity(), listener, year, month, day);
        DatePicker datePicker= datePickerDialog.getDatePicker();

        if(minDate==null){
            datePicker.setMinDate(c.getTimeInMillis());
        }else{
            datePicker.setMinDate(minDate.getTimeInMillis());
        }


        return datePickerDialog;
    }



}