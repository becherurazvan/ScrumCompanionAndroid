package com.colinearproductions.scrumcompanion;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Requests.CreateProjectRequest;
import Requests.CreateProjectResponse;
import Requests.JoinProjectRequest;
import Requests.SetTokenRequest;
import VolleyClasses.VolleySingleton;


@EActivity
public class InvitesScreen extends AppCompatActivity implements View.OnClickListener, CreateProjectDialog.Communicator, GoogleApiClient.OnConnectionFailedListener {


    @ViewById(R.id.invitation_code)
    EditText invitationCode;
    @ViewById(R.id.create_project_button)
    Button createProjectButton;
    @ViewById(R.id.join_project_button)
    Button joinButton;




    public static final String CREATE_PROJECT_URL = "http://10.32.188.82:4567/project/create";
    public static final String JOIN_PROJECT_URL = "http://10.32.188.82:4567/project/join";
    public static final String SET_TOKEN_URL = "http://10.32.188.82:4567/user/set_token";


//
//    //HEROKU
//    public static final String CREATE_PROJECT_URL = "https://scrum-companion.herokuapp.com/project/create";
//    public static final String JOIN_PROJECT_URL = "https://scrum-companion.herokuapp.com/project/join";
//    public static final String SET_TOKEN_URL = "https://scrum-companion.herokuapp.com/user/set_token";
//

    //public static final String SERVER_CLIENT_ID = "735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com"; //GOOD ONE
    public static final String SERVER_CLIENT_ID = "735068003543-nl7oj4vo98s5nnabv5q13pgo688hkj3l.apps.googleusercontent.com"; // script


    ProgressDialog progressDialog;
    private static final int RC_SIGN_IN = 8001;

    public static final String TAG = "InvitesActivity:";
    ObjectMapper objectMapper;
    RequestQueue queue;
    GoogleSignInAccount acct;
    private GoogleApiClient mGoogleApiClientDRIVE;


    @App
    MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_screen);;

        createProjectButton.setOnClickListener(this);
        joinButton.setOnClickListener(this);
        objectMapper = new ObjectMapper();
        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();




        GoogleSignInOptions gso2 = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("https://www.googleapis.com/auth/drive"))
                .requestScopes(new Scope("https://www.googleapis.com/auth/documents"))
                .requestScopes(new Scope("https://www.googleapis.com/auth/script.external_request"))
                .requestServerAuthCode(SERVER_CLIENT_ID, true)// WEB CLIENT ID HERE, ANDROID CLIENT ID IN JSon
                .requestIdToken(SERVER_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClientDRIVE = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso2)
                .addApi(Drive.API)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClientDRIVE);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mGoogleApiClientDRIVE != null) {
            showProgressDialog();
            mGoogleApiClientDRIVE.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClientDRIVE != null && mGoogleApiClientDRIVE.isConnected()) {
            mGoogleApiClientDRIVE.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.i(TAG,"Succesfully got the authentication tokens");
            acct = result.getSignInAccount();
            hideProgressDialog();
            try {
                sendTokens();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_project_button:
                FragmentManager manager = getFragmentManager();
                CreateProjectDialog createProjectDialog = new CreateProjectDialog();
                createProjectDialog.show(manager, "create_project_dialog");
                break;
            case R.id.join_project_button:
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

        String tokenId = acct.getIdToken();

        CreateProjectRequest projectRequest = new CreateProjectRequest(tokenId, projectName);
        String jsonCreateProjectRequest = objectMapper.writeValueAsString(projectRequest);

        JSONObject jsonBody = new JSONObject(jsonCreateProjectRequest);
        String url = CREATE_PROJECT_URL;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());


                try {
                    CreateProjectResponse rsp = objectMapper.readValue(response.toString(), CreateProjectResponse.class);
                    projectCreated(rsp);
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
        Log.i("InvitesScreen", "Request: " + jsonCreateProjectRequest);
        queue.add(jsonObjectRequest);

    }

    public void sendTokens() throws JsonProcessingException, JSONException {

        String tokenId = acct.getIdToken();

        SetTokenRequest setTokenRequest = new SetTokenRequest(tokenId, acct.getServerAuthCode(),app.getGcmToken());
        JSONObject jsonBody = new JSONObject(objectMapper.writeValueAsString(setTokenRequest));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SET_TOKEN_URL, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, response.toString());

                try {
                    Requests.Response rsp = objectMapper.readValue(response.toString(),Requests.Response.class);
                    if(!rsp.isSuccesful()){
                        hideProgressDialog();
                        try {
                            sendTokens();
                            Log.i(TAG,"Error ocurred, trying again");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

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
        Log.i("InvitesScreen", "Request: " + jsonBody.toString());
        queue.add(jsonObjectRequest);

    }

    public void projectCreated(CreateProjectResponse project) {
        Intent i = new Intent(this, MainScreen_.class);
        i.putExtra("projectId", project.getProjectId());
        startActivity(i);
    }

    public void joinProject() throws JsonProcessingException, JSONException {
        String iCode = invitationCode.getText().toString();

        final JoinProjectRequest joinProjectRequest = new JoinProjectRequest(app.getTokenId(), iCode);
        String jsonJoinProjectRequest = objectMapper.writeValueAsString(joinProjectRequest);

        JSONObject jsonBody = new JSONObject(jsonJoinProjectRequest);
        String url = JOIN_PROJECT_URL;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.i(TAG, response.toString());
                try {
                    Requests.Response joinProjectResponse = objectMapper.readValue(response.toString(), Requests.Response.class);
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
                Log.i(TAG, "Erorr" + error.toString());


            }
        });

        Log.i(TAG, "Request: " + jsonJoinProjectRequest);
        queue.add(jsonObjectRequest);
    }


    public void joinedProject(Requests.Response response) {
        if (response.isSuccesful()) {
            Toast.makeText(getApplicationContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainScreen_.class);
            i.putExtra("projectId", invitationCode.getText().toString());
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "UNSUCCESFULL: " + response.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void showProgressDialog(){
        if(progressDialog== null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Wait while loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public void hideProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.hide();
        }
    }





}
