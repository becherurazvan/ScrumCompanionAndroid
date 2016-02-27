/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.colinearproductions.scrumcompanion;


import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Helper.ItemTouchHelperViewHolder;
import Scrum.UserStory;


public class RecyclerListAdapterSwipableBacklog extends RecyclerView.Adapter<RecyclerListAdapterSwipableBacklog.ItemViewHolder> {

    private final List<UserStory> mItems = new ArrayList<>();

    FragmentManager fragmentManager;

    public RecyclerListAdapterSwipableBacklog(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.backlog_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view, fragmentManager);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.description.setText(mItems.get(position).getDescription());
        holder.points.setText("Points: " + mItems.get(position).getStoryPoints());
        holder.setUserStory(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder, View.OnClickListener {

        public final TextView description;
        public final TextView points;
        public UserStory story;
        FragmentManager fragmentManager;

        public ItemViewHolder(View itemView, FragmentManager fragmentManager) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.fragmentManager = fragmentManager;
            points = (TextView) itemView.findViewById(R.id.user_story_list_points_text);
            description = (TextView) itemView.findViewById(R.id.user_story_list_description);
        }

        public void setUserStory(UserStory s) {
            this.story = s;
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            UserStoryFragment fragment = new UserStoryFragment();
            fragment.setUserStory(story);
            transaction.replace(R.id.main_frame_layout, fragment, MainScreen.USER_STORY_FRAGMENT_TAG);
            transaction.addToBackStack(MainScreen.USER_STORY_FRAGMENT_TAG);
            transaction.commit();

        }


    }



    public void addItem(UserStory s) {
        mItems.add(s);
        this.notifyDataSetChanged();
    }

    public void addAllItems(ArrayList<UserStory> stories) {
        mItems.clear();
        mItems.addAll(stories);
        this.notifyDataSetChanged();
    }

    public void updateStoryPoints(){
        for(UserStory s:mItems){
            s.refreshStoryPoints();
        }
        notifyDataSetChanged();
    }
}
