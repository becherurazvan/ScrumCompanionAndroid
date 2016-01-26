package com.colinearproductions.scrumcompanion;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Entities.Project;
import Requests.CreateProjectRequest;
import Requests.CreateProjectResponse;
import Requests.LoginRequest;
import Requests.LoginResponse;
import VolleyClasses.VolleySingleton;


@EActivity
public class InvitesScreen extends AppCompatActivity implements View.OnClickListener,SetTextDialog.Communicator{

    @ViewById(R.id.createProjectText)
    TextView createProject;
    @ViewById(R.id.invitationsText)
    TextView invitationsText;
    @ViewById(R.id.inviter1text)
    TextView inviter1;
    @ViewById(R.id.inviter2text)
    TextView inviter2;
    @ViewById(R.id.project1text)
    TextView project1;
    @ViewById(R.id.project2text)
    TextView project2;
    @ViewById(R.id.noInviteText)
    TextView noInvitesText;


    String email;
    ObjectMapper objectMapper;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_screen);
        setFonts();
        Bundle extras = getIntent().getExtras();
        email = (String)extras.get("email");
        createProject.setOnClickListener(this);
        objectMapper = new ObjectMapper();
        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
    }




    public void setFonts(){
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
        createProject.setTypeface(font);
        invitationsText.setTypeface(font);
        inviter1.setTypeface(font);
        inviter2.setTypeface(font);
        project1.setTypeface(font);
        project2.setTypeface(font);
        noInvitesText.setTypeface(font);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createProjectText:
                FragmentManager manager = getFragmentManager();
                SetTextDialog setTextDialog = new SetTextDialog();
                setTextDialog.show(manager,"set_text_dialog");
                break;

        }
    }

    @Override
    public void onDialogMessage(String message) {
        if(message.contains("OK:")) {
            String projectName = message.replace("OK:","");
            Toast.makeText(getApplicationContext(), "Project name : " + projectName, Toast.LENGTH_SHORT).show();


            try {
                createProject(projectName);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public void createProject(String projectName) throws JsonProcessingException, JSONException {

        CreateProjectRequest projectRequest = new CreateProjectRequest(projectName,email);
        String jsonCreateProjectRequest = objectMapper.writeValueAsString(projectRequest);

        JSONObject jsonBody = new JSONObject(jsonCreateProjectRequest);
        String url = "http://10.32.188.82:4567/create_project";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("TAG", response.toString());


                try {
                    String projectTitle = response.getString("title");
                    String projectId = response.getString("id");
                    projectCreated(projectTitle,projectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              ;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("InvitesScreen","Erorr" + error.toString());


            }
        });

        Log.i("InvitesScreen", "Request: " + jsonCreateProjectRequest);
        queue.add(jsonObjectRequest);

    }


    public void projectCreated(String projectName, String projectId){
        Intent i = new Intent(this,ProjectScreen.class);
        i.putExtra("email",email);
        i.putExtra("projectName",projectName);
        i.putExtra("projectId",projectId);
        startActivity(i);
    }


}
