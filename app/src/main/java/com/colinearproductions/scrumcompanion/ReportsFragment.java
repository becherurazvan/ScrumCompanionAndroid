package com.colinearproductions.scrumcompanion;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Database.Record;
import Entities.Project;
import Scrum.Sprint;


public class ReportsFragment extends Fragment implements OnChartValueSelectedListener {



    LineChart chart;
    ArrayList<Database.Entry> entries;


    TextView dateText;
    TextView dayOfChart;
    TextView achievedPoints;
    TextView achievedToday;


    int sprintNumber;

    public ReportsFragment() {
        // Required empty public constructor
    }

    public void setSprintNumber(int sprintNumber) {
        this.sprintNumber = sprintNumber;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        dateText = (TextView) rootView.findViewById(R.id.report_date);
        dayOfChart = (TextView) rootView.findViewById(R.id.report_day);
        achievedPoints = (TextView) rootView.findViewById(R.id.report_achieved);
        achievedToday = (TextView) rootView.findViewById(R.id.report_acheved_today);
        chart = (LineChart) rootView.findViewById(R.id.line_chart_view);


        Project p = ((MainScreen) getActivity()).getProject();
        Sprint sprint = p.getProductBacklog().getSprintByNumber(sprintNumber);
        int daysInSprint = sprint.getDayDuration();
        boolean isCurrentSprint = sprint.isStarted();
        int totalPoints = sprint.getTotalPoints();
        entries = p.getRecord().getSprintBurndownByNumber(sprintNumber);


        ArrayList<Entry> chartEntries = new ArrayList<>();
        ArrayList<Entry> recommended = new ArrayList<>();
        ArrayList<Entry> todayData = new ArrayList<>();
        ArrayList<Entry> maximumPoints = new ArrayList<>();

        ArrayList<String> labels = new ArrayList<>();

        float recomendedPointsPerDay = (float) totalPoints / daysInSprint;
        Log.i("Recomanded ppd", recomendedPointsPerDay + " " + totalPoints + " " + daysInSprint + " ");


        Database.Entry lastEntry = null;
        for (int i = 0; i < daysInSprint; i++) {

            boolean done = false;
            Database.Entry e;
            if (entries.size() <= i) {
                e = lastEntry;
                done = true;

            } else {
                e = entries.get(i);
                lastEntry = e;

                if(entries.size()<=i+1){
                    // TODO: 3/14/2016 make it work with current date, right now is artificial
                    if (todayData.size() < 1)
                        todayData.add(new Entry(sprint.getTotalPoints()-e.getTotalAchievedSprintPoints(), i));
                }
            }


            int chartEntryValue = sprint.getTotalPoints()-e.getTotalAchievedSprintPoints();
            Entry chartEntry = new Entry(chartEntryValue, i);

            Entry maximumEntry = new Entry(e.getTotalSprintPoints(),i);



            float recommandedEntryValue = sprint.getTotalPoints() - (recomendedPointsPerDay*(i));
            Entry recomemendedEntry = new Entry(recommandedEntryValue, i);

            if (done)
                labels.add("");
            else
                labels.add(e.getParsedDateText());


            chartEntries.add(chartEntry);
            recommended.add(recomemendedEntry);
            maximumPoints.add(maximumEntry);


        }


        LineDataSet rec = new LineDataSet(recommended, "Recomended");
        rec.setColor(Color.parseColor("#B987D6"));
        rec.setCircleRadius(0);
        rec.setValueTextSize(0f);
        rec.setLineWidth(2f);


        LineDataSet daily = new LineDataSet(chartEntries, "Points left");
        rec.setCircleRadius(0f);
        daily.setColor(Color.parseColor("#00897B"));
        daily.setValueTextSize(0f);
        daily.setLineWidth(2f);
        daily.setCircleColor(Color.parseColor("#00897B"));

        LineDataSet todayEntry = new LineDataSet(todayData, "Today");
        todayEntry.setCircleColor(Color.parseColor("#FF5729"));
        todayEntry.setColor(Color.parseColor("#FF5729"));

        LineDataSet max = new LineDataSet(maximumPoints, "Cumulative points");
        max.setColor(Color.parseColor("#FFBF00"));
        max.setCircleRadius(0);
        max.setValueTextSize(0f);
        max.setLineWidth(2f);


        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(rec);
        lineDataSets.add(daily);
        lineDataSets.add(todayEntry);
        lineDataSets.add(max);

        LineData data = new LineData(labels, lineDataSets);


        chart.setTouchEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chart.setDoubleTapToZoomEnabled(false);
        chart.animateX(2000, Easing.EasingOption.EaseInOutQuart);
        chart.setPinchZoom(true);
        chart.setData(data);
        chart.setDescription("Burndown Chart for sprint " + sprintNumber);


        return rootView;
    }

    public String parseCalendar(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {


        if (entries.size() <= e.getXIndex()) {
            achievedPoints.setText("No data available for this date");
            dateText.setText("No data available for this date");
            dayOfChart.setText("No data available for this date");
            achievedToday.setText("No data available for this date");
            return;
        }
        Database.Entry entry = entries.get(e.getXIndex());

        achievedPoints.setText("Total Achieved Points: " + entry.getTotalAchievedSprintPoints());
        dateText.setText("Date: " + entry.getParsedDateText());
        dayOfChart.setText("#Day " + (e.getXIndex() + 1));

        int todayPoints = 0;
        if (e.getXIndex() > 0) {
            todayPoints = entry.getTotalAchievedSprintPoints() - entries.get(e.getXIndex() - 1).getTotalAchievedSprintPoints();
        } else {
            todayPoints = entry.getTotalAchievedSprintPoints();
        }

        achievedToday.setText("On day achieved points: " + todayPoints + " points");


        Log.i("Record", entry.getParsedDate() + " : " + entry.getSprintDayNumber() + "    : " + entry.getTotalAchievedSprintPoints());

    }

    @Override
    public void onNothingSelected() {

    }

    public static boolean sameDay(Calendar cal1, Calendar cal2) {
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

}
