package com.colinearproductions.scrumcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.EActivity;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import Entities.UserInfo;
import RecyclerViewHolders.MembersHolder;
import VolleyClasses.VolleySingleton;


@EActivity
public class ProjectScreen extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_screen);
        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        Intent i = getIntent();

        String projectId = i.getStringExtra("projectId");
        getMembersData(projectId);
      //  listMembers();
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void listMembers(){
        mRecyclerView = (RecyclerView) findViewById(R.id.members_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        ArrayList<MembersHolder> myDataSet = new ArrayList<>();

        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));
        myDataSet.add(new MembersHolder("Razvan","Raz@van.com"));



        mAdapter = new MembersListAdapter(myDataSet);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getMembersData(String projectId){
        String url = "http://10.32.188.82:4567/project/list_members?project_id="+projectId;


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayList<UserInfo> memberListResponse;
                try {

                    memberListResponse = objectMapper.readValue(response.toString(), new TypeReference<ArrayList<UserInfo>>(){});
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("RESPONSE", response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ERROR", "Erorr" + error.toString());


            }
        });


        queue.add(jsonObjectRequest);
    }
}
