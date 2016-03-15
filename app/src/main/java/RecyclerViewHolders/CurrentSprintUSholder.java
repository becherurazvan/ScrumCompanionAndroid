package RecyclerViewHolders;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.colinearproductions.scrumcompanion.R;

import java.util.ArrayList;
import java.util.Random;

import Scrum.Sprint;
import Scrum.UserStory;

/**
 * Created by rbech on 2/28/2016.
 */
public class CurrentSprintUSholder extends RecyclerView.Adapter<CurrentSprintUSholder.ViewHolder>{


    private ArrayList<UserStory> mDataset;

    ViewHolder.SprintUSInteractionListener listener;


    public CurrentSprintUSholder(ArrayList<UserStory> mDataset,ViewHolder.SprintUSInteractionListener listener) {
        this.mDataset = mDataset;
        this.listener = listener;
    }

    @Override
    public CurrentSprintUSholder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_sprint_user_story_item,parent,false);
        CurrentSprintUSholder.ViewHolder vh =new CurrentSprintUSholder.ViewHolder(v,listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(CurrentSprintUSholder.ViewHolder holder, int position) {

        holder.description.setText(mDataset.get(position).getDescription());
        holder.setSprint(mDataset.get(position));

        int subtasks = mDataset.get(position).getTasks().size();
        int allPoints = mDataset.get(position).countSotryPoints();
        int points = mDataset.get(position).getAchievedPoints();


        holder.details.setText("Subtasks: "+subtasks + "   Points:"+allPoints);

        holder.progressBar.setMax(allPoints);
        holder.progressBar.setProgress(points);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView description;
        TextView details;
        TextView remove;
        ProgressBar progressBar;
        UserStory s;
        SprintUSInteractionListener listener;

        public ViewHolder(View itemView,SprintUSInteractionListener listener) {
            super(itemView);

            description=(TextView) itemView.findViewById(R.id.csusi_description_text);
            details = (TextView) itemView.findViewById(R.id.csusi_info);
            remove = (TextView) itemView.findViewById(R.id.csusi_remove_button);
            progressBar = (ProgressBar) itemView.findViewById(R.id.csusi_progressbar);

            itemView.findViewById(R.id.csui_layout).setOnClickListener(this);

            remove.setOnClickListener(this);
          //  description.setOnClickListener(this);


            this.listener= listener;

        }
        public void setSprint(UserStory s){
            this.s = s;
        }

        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.csusi_remove_button){
                listener.onUSRemoveClicked(s);
            }else if(v.getId() == R.id.csui_layout){
                listener.onUSClicked(s);
            }
        }

        public interface SprintUSInteractionListener{
            public void onUSClicked(UserStory s);
            public void onUSRemoveClicked(UserStory s);
        }
    }
}
