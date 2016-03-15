package RecyclerViewHolders;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colinearproductions.scrumcompanion.MainScreen;
import com.colinearproductions.scrumcompanion.R;

import java.util.ArrayList;

import Entities.User;

/**
 * Created by rbech on 3/15/2016.
 */
public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {


    ArrayList<User> members;
    MainScreen mainScreen;
    public MembersAdapter (ArrayList<User> members,MainScreen mainScreen){
        this.members =members;
        this.mainScreen =mainScreen;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = members.get(position).getName();
        String email = members.get(position).getEmail();
        Bitmap image = mainScreen.icons.get(email);

        if(image!=null){
            holder.icon.setImageBitmap(image);
        }

        holder.userName.setText(name);
        holder.userEmail.setText(email);

    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        TextView userEmail;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            userEmail = (TextView)itemView.findViewById(R.id.member_email_text);
            userName = (TextView) itemView.findViewById(R.id.member_name_text);
            icon = (ImageView) itemView.findViewById(R.id.member_icon);
        }
    }
}
