package com.colinearproductions.scrumcompanion;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

import Database.FeedList;
import RecyclerViewHolders.FeedListAdapter;


public class FeedFragment extends Fragment {


    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    public SwipeRefreshLayout swipeRefreshLayout;
    MainScreen mainScreen;
    boolean loading = false;

    LinearLayoutManager mLayoutManager;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.feed_recyclerview);

        mLayoutManager= new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        mainScreen =  ((MainScreen)getActivity());

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.feedSwipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    mainScreen.getFeedTen();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });



        final FeedList  feedList=mainScreen.getFeedList();
        mAdapter = new FeedListAdapter(feedList,mainScreen,mainScreen);

        recyclerView.addOnScrollListener(new EndlessOnScrollListener(mLayoutManager) {

            @Override
            public void onScrolledToEnd() {
                if (!loading && mAdapter.getItemCount()>=10) {
                    loading = true;

                    try {
                        mainScreen.getFeedBottomTen(feedList.getOldest());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //// TODO: 3/15/2016 set loading to false from mainscreen
                loading = false;
            }
        });




        recyclerView.setAdapter(mAdapter);



    }



    public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

        // use your LayoutManager instead
        private LinearLayoutManager llm;

        public EndlessOnScrollListener(LinearLayoutManager sglm) {
            this.llm = sglm;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!recyclerView.canScrollVertically(1)) {
                onScrolledToEnd();
            }
        }

        public abstract void onScrolledToEnd();
    }
}
