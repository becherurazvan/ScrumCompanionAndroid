package RecyclerViewHolders;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.colinearproductions.scrumcompanion.R;
import com.colinearproductions.scrumcompanion.SprintFragment;

import java.util.ArrayList;
import java.util.Calendar;

import Scrum.Sprint;
import Scrum.Task;
import Scrum.UserStory;

/**
 * Created by rbech on 2/23/2016.
 */
public class SprintListViewHolder extends RecyclerView.Adapter<SprintListViewHolder.ViewHolder> {

    ArrayList<Sprint> sprints;
    OnSprintActionButtonClickedListener listener;

    public SprintListViewHolder(OnSprintActionButtonClickedListener listener) {
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sprints_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v,listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sprint s = sprints.get(position);

        String title = "Sprint "+s.getNumber();
        holder.setSprint(s);



        if (s.isStarted()) {
            title = title + " - Current Sprint";
            holder.actionButton.setText("Go To");
        } else if(s.isFinished()){
            holder.actionButton.setText("Show Report");
        }else if(!s.isStarted() && !s.isFinished()){
            holder.actionButton.setText("Delete");
            holder.actionButton.setTextColor(Color.parseColor("#ff593f"));
        }




        holder.sprintNumberStatus.setText(title);
        holder.startDate.setText(s.getStartDate());
        holder.endDate.setText(s.getEndDate());

        holder.points.setText(s.getAchievedPoints() + "/" + s.getTotalPoints());

        int totalTasks=0;
        int finishedTasks=0;

        for(UserStory story:s.getUserStories()){
            for(Task t:story.getTasks()){
                totalTasks++;
                if(t.isDone())
                    finishedTasks++;
            }
        }

        holder.tasks.setText(finishedTasks+"/"+totalTasks);

        holder.progressBar.setMax(s.getTotalPoints());
        holder.progressBar.setProgress(s.getAchievedPoints());




    }

    @Override
    public int getItemCount() {
        return sprints.size();
    }

    public void setSprints(ArrayList<Sprint> sprints) {
        this.sprints = sprints;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView sprintNumberStatus;
        TextView startDate;
        TextView endDate;
        TextView points;
        TextView tasks;
        ProgressBar progressBar;
        TextView actionButton;
        Sprint s;
        OnSprintActionButtonClickedListener listener;

        public ViewHolder(View v, OnSprintActionButtonClickedListener listener) {
            super(v);
            sprintNumberStatus = (TextView) v.findViewById(R.id.sprint_number_list_item);
            startDate = (TextView) v.findViewById(R.id.start_date_sprint_item);
            endDate = (TextView) v.findViewById(R.id.end_date_sprint_item);
            points= (TextView) v.findViewById(R.id.sprint_item_points_text);
            tasks=(TextView) v.findViewById(R.id.sprints_item_tasks_text);
            progressBar=(ProgressBar) v.findViewById(R.id.sprints_item_progressbar);
            actionButton = (TextView)v.findViewById(R.id.sprint_item_actionbtn);
            actionButton.setOnClickListener(this);
            this.listener = listener;
        }

        public void setSprint(Sprint s){
            this.s = s;
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.sprint_item_actionbtn) {
                if (s.isStarted()) { // go to
                    listener.onGoToCurrentSprintClicked(s);
                } else if(s.isFinished()){ // report
                    listener.onReportClicked(s);
                }else if(!s.isStarted() && !s.isFinished()){ // delete
                    listener.onSprintDeleteClicked(s);
                }
            }
        }
    }

    public Calendar getMinDate() {

        Calendar latest = Calendar.getInstance();
        for (Sprint sprint : sprints) {
            Calendar calendar = sprint.getEndDateCalendar();
            if (latest.compareTo(calendar) < 0) {
                latest = calendar;
            }
        }
        return latest;

    }

    public interface OnSprintActionButtonClickedListener{
        public void onGoToCurrentSprintClicked(Sprint s);

        public void onReportClicked(Sprint s);

        public void onSprintDeleteClicked(Sprint s);

    }


}
