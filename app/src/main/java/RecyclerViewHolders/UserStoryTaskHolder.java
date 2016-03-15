package RecyclerViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colinearproductions.scrumcompanion.R;

import java.util.ArrayList;

import Scrum.Task;

/**
 * Created by rbech on 2/23/2016.
 */
public class UserStoryTaskHolder extends RecyclerView.Adapter<UserStoryTaskHolder.ViewHolder>{

    ArrayList<Task> tasks;
    TaskEditListener listener;

    public UserStoryTaskHolder(ArrayList<Task> tasks,TaskEditListener listener){
        this.tasks = tasks;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_story_task_item,parent,false);
        ViewHolder vh = new ViewHolder(v,listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task t = tasks.get(position);
        holder.setTaks(t);
        holder.description.setText(t.getDescription());
        holder.pointsId.setText("Points:" + t.getPoints());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public void setTasks(ArrayList<Task> task){

        tasks.clear();
        tasks.addAll(task);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView pointsId;
        TextView description;
        TextView edit;
        Task task;
        TaskEditListener listener;

        public ViewHolder(View v,TaskEditListener listener) {
            super(v);
            pointsId = (TextView) v.findViewById(R.id.user_story_task_item_points);
            description = (TextView) v.findViewById(R.id.user_story_task_item_description);
            edit = (TextView) v.findViewById(R.id.user_story_task_item_edit_btn);
            edit.setOnClickListener(this);
            this.listener= listener;


        }

        public void setTaks(Task t){
            this.task = t;
        }

        @Override
        public void onClick(View v) {
           listener.onTaskEditClicked(task);
        }
    }


    public interface TaskEditListener
    {
        public void onTaskEditClicked(Task t);
    }






}
