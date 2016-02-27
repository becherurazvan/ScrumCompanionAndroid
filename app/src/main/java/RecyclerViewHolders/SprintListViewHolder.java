package RecyclerViewHolders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.colinearproductions.scrumcompanion.R;

import java.util.ArrayList;

import Scrum.Sprint;
import Scrum.Task;
import Scrum.UserStory;

/**
 * Created by rbech on 2/23/2016.
 */
public class SprintListViewHolder extends RecyclerView.Adapter<SprintListViewHolder.ViewHolder>{

    ArrayList<Sprint> sprints;

    public SprintListViewHolder(){

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sprints_list_item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sprint s = sprints.get(position);
        holder.setSprint(s);

        holder.sprintNumber.setText("Sprint " + s.getNumber());
        holder.startDate.setText(s.getStartDate());
        holder.endDate.setText(s.getEndDate());

    }

    @Override
    public int getItemCount() {
        return sprints.size();
    }

    public void setSprints(ArrayList<Sprint> sprints){
        this.sprints = sprints;
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView sprintNumber;
        TextView startDate;
        TextView endDate;

        Sprint sprint;

        public ViewHolder(View v) {
            super(v);

            sprintNumber =(TextView)v.findViewById(R.id.sprint_number_list_item);
            startDate = (TextView)v.findViewById(R.id.start_date_sprint_item);
            endDate = (TextView)v.findViewById(R.id.end_date_sprint_item);

        }

        public void setSprint(Sprint s){
            this.sprint = s;
        }

        @Override
        public void onClick(View v) {
            Log.i("HEHEHE", sprint.getNumber() + "");


        }
    }


}
