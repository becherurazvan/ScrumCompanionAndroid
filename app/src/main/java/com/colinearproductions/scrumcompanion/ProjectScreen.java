package com.colinearproductions.scrumcompanion;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import BroadcastReceivers.MyBroadcastListener;
import BroadcastReceivers.MyBroadcastReceiver;
import Entities.Project;
import VolleyClasses.VolleySingleton;
import gcm.MyGcmListenerService;


@EActivity
public class ProjectScreen extends AppCompatActivity implements MyBroadcastListener {
    @App
    MyApp app;

    RequestQueue queue;
    String projectId;
    String tokenId;
    ObjectMapper objectMapper;

    MyBroadcastReceiver receiver;

    public final String UPDATE_PROJECT_URL ="http://10.32.188.82:4567/project/get_project";
    public final String TAG = "ProjectScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_screen);
        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        objectMapper = new ObjectMapper();

        Intent i = getIntent();
        projectId =  i.getStringExtra("projectId");
        tokenId = app.getTokenId();



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(receiver==null)
            receiver = new MyBroadcastReceiver(this);

        IntentFilter intentFilter = new IntentFilter(MyGcmListenerService.intentFilterString);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiver!=null)
            unregisterReceiver(receiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");


        if(message.equals(MyGcmListenerService.NOTIFICATION_PROJECT_READY)){
            try {
                getProject();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        toast("FROM PROJECT SCREEN:" +intent.getStringExtra("message"));
    }




    public void getProject() throws JsonProcessingException, JSONException {

        Requests.Request r = new Requests.Request(tokenId);

        JSONObject jsonBody = new JSONObject(objectMapper.writeValueAsString(r));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPDATE_PROJECT_URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());

                try {
                    Project rsp = objectMapper.readValue(response.toString(),Project.class);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Erorr" + error.toString());
            }
        });
        Log.i("InvitesScreen", "Request: " +jsonBody.toString());
        queue.add(jsonObjectRequest);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }



    public void toast(String t) {
        Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT).show();
    }


}
