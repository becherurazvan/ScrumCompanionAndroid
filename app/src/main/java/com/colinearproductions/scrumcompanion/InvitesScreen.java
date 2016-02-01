package com.colinearproductions.scrumcompanion;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Requests.CreateProjectRequest;
import Requests.JoinProjectRequest;
import Requests.JoinProjectResponse;
import VolleyClasses.VolleySingleton;


@EActivity
public class InvitesScreen extends AppCompatActivity implements View.OnClickListener, SetTextDialog.Communicator {

    @ViewById(R.id.createProjectText)
    TextView createProject;
    @ViewById(R.id.invitationsText)
    TextView invitationsText;
    @ViewById(R.id.invitationCodeEditText)
    EditText invitationCode;
    @ViewById(R.id.createProjectLabelText)
    TextView createProjectLabel;
    @ViewById(R.id.joinButton)
    TextView joinButton;


    String email;
    ObjectMapper objectMapper;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_screen);
        setFonts();
        Bundle extras = getIntent().getExtras();
        email = (String) extras.get("email");
        createProject.setOnClickListener(this);
        objectMapper = new ObjectMapper();
        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        invitationCode = (EditText) findViewById(R.id.invitationCodeEditText);

    }


    public void setFonts() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto_thin.ttf");
        createProject.setTypeface(font);
        invitationsText.setTypeface(font);
        invitationCode.setTypeface(font);
        createProjectLabel.setTypeface(font);
        joinButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createProjectText:
                FragmentManager manager = getFragmentManager();
                SetTextDialog setTextDialog = new SetTextDialog();
                setTextDialog.show(manager, "set_text_dialog");
                break;
            case R.id.joinButton:
                try {
                    joinProject();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    public void onDialogMessage(String message) {
        if (message.contains("OK:")) {
            String projectName = message.replace("OK:", "");
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

        CreateProjectRequest projectRequest = new CreateProjectRequest(projectName, email);
        String jsonCreateProjectRequest = objectMapper.writeValueAsString(projectRequest);

        JSONObject jsonBody = new JSONObject(jsonCreateProjectRequest);
        String url = "http://10.32.188.82:4567/project/create";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("TAG", response.toString());


                try {
                    String projectTitle = response.getString("title");
                    String projectId = response.getString("id");
                    projectCreated(projectTitle, projectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("InvitesScreen", "Erorr" + error.toString());


            }
        });

        Log.i("InvitesScreen", "Request: " + jsonCreateProjectRequest);
        queue.add(jsonObjectRequest);

    }


    public void projectCreated(String projectName, String projectId) {
        Intent i = new Intent(this, ProjectScreen.class);
        i.putExtra("email", email);
        i.putExtra("projectName", projectName);
        i.putExtra("projectId", projectId);
        startActivity(i);
    }

    public void joinProject() throws JsonProcessingException, JSONException {
        String iCode =invitationCode.getText().toString();

        final JoinProjectRequest joinProjectRequest = new JoinProjectRequest(email,iCode);
        String jsonJoinProjectRequest = objectMapper.writeValueAsString(joinProjectRequest);

        JSONObject jsonBody = new JSONObject(jsonJoinProjectRequest);
        String url = "http://10.32.188.82:4567/project/join";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i("TAG", response.toString());
                try {
                    JoinProjectResponse joinProjectResponse = objectMapper.readValue(response.toString(), JoinProjectResponse.class);
                    joinedProject(joinProjectResponse);
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("InvitesScreen", "Erorr" + error.toString());


            }
        });

        Log.i("InvitesScreen", "Request: " + jsonJoinProjectRequest);
        queue.add(jsonObjectRequest);
    }


    public void joinedProject(JoinProjectResponse response){
        if(response.isSuccessful()){
            Toast.makeText(getApplicationContext(),response.getGreeting(),Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ProjectScreen.class);
            i.putExtra("email", email);
            startActivity(i);
        }else{
            Toast.makeText(getApplicationContext(),"UNSUCCESFULL: " +response.getGreeting(),Toast.LENGTH_SHORT).show();
        }
    }

}
