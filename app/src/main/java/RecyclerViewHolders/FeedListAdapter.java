package RecyclerViewHolders;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colinearproductions.scrumcompanion.MainScreen;
import com.colinearproductions.scrumcompanion.R;
import com.colinearproductions.scrumcompanion.SprintFragment;

import java.util.Calendar;

import Database.Action;
import Database.FeedList;
import Entities.Project;
import Scrum.Resource;
import Scrum.Sprint;
import Scrum.Task;
import Scrum.UserStory;

/**
 * Created by rbech on 3/14/2016.
 */
public class FeedListAdapter  extends  RecyclerView.Adapter<FeedListAdapter.ViewHolder> {


    FeedList feedList;
    Action.OnResourceClickedListener listener;
    Project project;
    MainScreen mainScreen;


    public FeedListAdapter(FeedList feedList, Action.OnResourceClickedListener listener,MainScreen mainScreen) {
        this.feedList = feedList;
        this.listener=listener;
        this.project =mainScreen.getProject();
        this.mainScreen=mainScreen;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Action action = feedList.getActions().get(position);
        holder.date.setText(action.getDate().getTime().toString());


        holder.message.setText(action.generateSpannableText(listener));
        holder.message.setMovementMethod(LinkMovementMethod.getInstance());
        holder.userName.setText(" " +action.getMemberName());

        Resource mainResource = action.getMainResource();

        if(mainScreen.icons.containsKey(action.getMemberEmail()))
            holder.imageView.setImageBitmap(mainScreen.icons.get(action.getMemberEmail()));

        if(mainResource==null){
            holder.snapshot.setVisibility(View.INVISIBLE);
            holder.snapshot.setHeight(0);
            return;
        }else{
            holder.setListener(listener,mainResource);
        }




        switch (mainResource.getResourceType()){
            case Resource.RESOURCE_SPRINT:
                Sprint s = project.getProductBacklog().getSprintByNumber(Integer.parseInt(mainResource.getResourceId().split("SPRINT_")[1]));
                holder.snapshot.setText("Sprint number:" + s.getNumber()+ "\n"+s.getStartDate() + " - " + s.getEndDate());
                break;
            case Resource.RESOURCE_TASK:
                UserStory us = project.getProductBacklog().getStoryById(mainResource.getResourceId().split("-")[0]);
                Task t =us.getTaskById(mainResource.getResourceId());
                holder.snapshot.setText(t.getDescription()+"\nPoints:"+t.getPoints());
                break;
            case Resource.RESOURCE_US:
                UserStory story = project.getProductBacklog().getStoryById(mainResource.getResourceId());
                holder.snapshot.setText(story.getDescription() + "\nPoints:" + story.countSotryPoints() + " Subtasks: " +story.getTasks().size());
                break;
        }



    }

    @Override
    public int getItemCount() {
        return feedList.getActions().size();
    }

    public Calendar getOldest(){
        return feedList.getOldest();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView message;
        TextView date;
        TextView userName;
        TextView snapshot;
        Action.OnResourceClickedListener listener;
        Resource resource;


        public ViewHolder(View v) {
            super(v);
            imageView=(ImageView)v.findViewById(R.id.feed_item_image);
            message = (TextView)v.findViewById(R.id.feed_item_messagetext);
            date = (TextView)v.findViewById(R.id.feed_item_date);
            userName =(TextView)v.findViewById(R.id.feed_item_user_name);
            snapshot = (TextView)v.findViewById(R.id.feed_item_snapshot);
            snapshot.setOnClickListener(this);

        }

        public void setListener(Action.OnResourceClickedListener listener,Resource resource){
            this.listener = listener;
            this.resource = resource;
        }


        @Override
        public void onClick(View v) {
            listener.onResourceClicked(resource);
        }
    }




}
