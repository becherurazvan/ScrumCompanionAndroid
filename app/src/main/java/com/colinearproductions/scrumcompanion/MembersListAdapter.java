package com.colinearproductions.scrumcompanion;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import RecyclerViewHolders.MembersHolder;

/**
 * Created by rbech on 2/3/2016.
 */
public class MembersListAdapter extends RecyclerView.Adapter<MembersListAdapter.ViewHolder>{

    private ArrayList<MembersHolder> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView emailTextView;
        public ViewHolder(View v) {
            super(v);
            nameTextView = (TextView)v.findViewById(R.id.user_name);
            emailTextView = (TextView) v.findViewById(R.id.email_card_text);
        }
    }

    public MembersListAdapter(ArrayList<MembersHolder> myDataSet) {
        this.mDataset = myDataSet;
    }

    @Override
    public MembersListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_card_view, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MembersListAdapter.ViewHolder holder, int position) {
        holder.nameTextView.setText(mDataset.get(position).getName());
        holder.emailTextView.setText(mDataset.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
