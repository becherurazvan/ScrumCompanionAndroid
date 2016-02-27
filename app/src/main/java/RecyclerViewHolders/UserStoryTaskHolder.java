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

import Scrum.Task;
import Scrum.UserStory;

/**
 * Created by rbech on 2/23/2016.
 */
public class UserStoryTaskHolder extends RecyclerView.Adapter<UserStoryTaskHolder.ViewHolder>{

    ArrayList<Task> tasks;

    public UserStoryTaskHolder(ArrayList<Task> tasks){
        this.tasks = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_story_task_item,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task t = tasks.get(position);
        holder.setTaks(t);
        holder.description.setText(t.getTitle());
        holder.pointsId.setText("ID: " + t.getId() + " Points:" + t.getPoints());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView pointsId;
        TextView description;
        Button edit;
        Task task;

        public ViewHolder(View v) {
            super(v);
            pointsId = (TextView) v.findViewById(R.id.user_story_task_item_id_points);
            description = (TextView) v.findViewById(R.id.user_story_task_item_description);
            edit = (Button) v.findViewById(R.id.user_story_task_item_edit_btn);
            edit.setOnClickListener(this);



        }

        public void setTaks(Task t){
            this.task = t;
        }

        @Override
        public void onClick(View v) {
            Log.i("HEHEHE",task.getTitle());
        }
    }


}
