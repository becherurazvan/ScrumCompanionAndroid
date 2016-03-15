package RecyclerViewHolders;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colinearproductions.scrumcompanion.CurrentSprintStatePageFragment;
import com.colinearproductions.scrumcompanion.R;

import java.util.ArrayList;

import Scrum.Task;

/**
 * Created by rbech on 2/29/2016.
 */
public class CurrentSprintPageHolder extends RecyclerView.Adapter<CurrentSprintPageHolder.ViewHolder>{


    private ArrayList<Task> mDataset;
    private int page;
    RecyclerView recyclerView;
    CurrentSprintStatePageFragment pageFragment;
    OnTaskUpdatedListener listener;



    public interface OnTaskUpdatedListener{
        public void taskUpdated(Task t);
    }



    public CurrentSprintPageHolder(ArrayList<Task> mDataset, int page, RecyclerView recyclerView,
                                   CurrentSprintStatePageFragment pageFragment,OnTaskUpdatedListener listener) {
        this.mDataset = mDataset;
        this.page = page;
        this.recyclerView = recyclerView;
        this.pageFragment = pageFragment;
        this.listener=listener;



    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_sprint_page_recycler_item,parent,false);

        CurrentSprintPageHolder.ViewHolder vh =new CurrentSprintPageHolder.ViewHolder(v, new ViewHolder.OnStateTaskStateChangListener() {
            @Override
            public void OnStateUpClicked(View v, int pos) {
                Log.i("TAG", "Clicked up on task " + mDataset.get(pos).getDescription());
                switch (mDataset.get(pos).getState()){
                    case Task.NOT_STARTED_STATE:
                        listener.taskUpdated(mDataset.get(pos).changeState(Task.WORKING_ON_STATE));
                        break;
                    case Task.WORKING_ON_STATE:
                        listener.taskUpdated(mDataset.get(pos).changeState(Task.FINISHED_STATE));
                        break;
                    case Task.FINISHED_STATE:
                        listener.taskUpdated(mDataset.get(pos).changeState(Task.FINISHED_STATE));
                        break;

                }

            }

            @Override
            public void OnStateDownClicked(View v,int pos) {
                Log.i("TAG", "Clicked Down on task " + mDataset.get(pos).getDescription());

                switch (mDataset.get(pos).getState()){
                    case Task.NOT_STARTED_STATE:
                        break;
                    case Task.WORKING_ON_STATE:
                        listener.taskUpdated(mDataset.get(pos).changeState(Task.NOT_STARTED_STATE));
                        break;
                    case Task.FINISHED_STATE:
                        listener.taskUpdated(mDataset.get(pos).changeState(Task.WORKING_ON_STATE));
                        break;


                }


            }
        });
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.description.setText(mDataset.get(position).getDescription());



        if(mDataset.get(position).getAssignedTo()!=null){
            holder.assignedTo.setText("Assigned To: " +mDataset.get(position).getAssignedTo());
            Log.i("USerNAme",pageFragment.getUserName());
        }

        holder.points.setText("Points: "+ mDataset.get(position).getPoints());


        switch (page){
            case 1: // not started
                holder.back.setVisibility(View.INVISIBLE);
                holder.back.setEnabled(false);
                holder.done.setText("Take");
                break;
            case 2:
                holder.back.setText("Cancel");
                holder.done.setText("Finish");
                break;
            case 3:
                holder.back.setVisibility(View.INVISIBLE);
                holder.back.setEnabled(false);
                holder.done.setText("Clone");
                holder.done.setEnabled(true);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateTasks(ArrayList<Task> tasks){
        mDataset.clear();
        mDataset.addAll(tasks);
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView back;
        TextView done;
        TextView description;
        TextView assignedTo;
        TextView points;

        OnStateTaskStateChangListener listener;

        public ViewHolder(View itemView,OnStateTaskStateChangListener listener) {
            super(itemView);
            this.listener=listener;
            back= (TextView) itemView.findViewById(R.id.csspi_back_btn);
            back.setOnClickListener(this);
            done = (TextView) itemView.findViewById(R.id.csspi_next_btn);
            done.setOnClickListener(this);
            description = (TextView) itemView.findViewById(R.id.csspi_description);
            assignedTo = (TextView) itemView.findViewById(R.id.csspi_assiged_to);
            points = (TextView) itemView.findViewById(R.id.csspi_points);
        }


        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.csspi_back_btn){
                listener.OnStateDownClicked(v,getAdapterPosition());
            }else if(v.getId()==R.id.csspi_next_btn){
                listener.OnStateUpClicked(v,getAdapterPosition());
            }

        }

        public interface OnStateTaskStateChangListener{
             void OnStateUpClicked(View v, int position);
             void OnStateDownClicked(View v,int position);
        }
    }
}
