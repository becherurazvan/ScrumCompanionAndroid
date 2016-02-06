package com.colinearproductions.scrumcompanion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import Requests.LoginResponse;
import VolleyClasses.VolleySingleton;
import gcm.MyRegistrationIntentService;


@EActivity
public class LoginScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    @ViewById(R.id.logoText1)
    TextView logoText1;
    @ViewById(R.id.logoText2)
    TextView logoText2;
    @ViewById(R.id.loginText)
    TextView loginText;
    @ViewById(R.id.sign_in_button)
    SignInButton signInButton;

    public static final String LOGIN_URL = "http://10.32.188.82:4567/user/login";
    public static final String SERVER_CLIENT_ID = "735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com";
    private static final String TAG = "SignInActivity";
    private static final String s = "S";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    RequestQueue queue;
    ObjectMapper objectMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getRegistrationToken(); // GCM

        setContentView(R.layout.activity_login_screen);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        objectMapper = new ObjectMapper();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestScopes(new Scope("https://www.googleapis.com/auth/drive"))
                //.requestServerAuthCode("735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com", true)// WEB CLIENT ID HERE, ANDROID CLIENT ID IN JSon
                .requestIdToken("735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
               // .addApi(Drive.API)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.i(TAG, acct.getDisplayName() + " EMAIL: " + acct.getEmail() + " server auth code: " + acct.getServerAuthCode() + " Authentication token " + acct.getIdToken());
            try {
                succesfullySignedIn(acct);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            Log.i(TAG, result.getStatus().toString());
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    public void succesfullySignedIn(GoogleSignInAccount acc) throws JsonProcessingException, JSONException {

        Requests.Request loginRequest = new Requests.Request(acc.getIdToken());
        String jsonLoginRequest = objectMapper.writeValueAsString(loginRequest);

        JSONObject jsonBody = new JSONObject(jsonLoginRequest);
        String url = LOGIN_URL;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    succesfullySignedToServer(objectMapper.readValue(response.toString(), LoginResponse.class));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Login Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Erorr" + error.toString());
            }
        });
        Log.i(TAG, "Request: " + jsonLoginRequest);
        queue.add(jsonObjectRequest);
    }

    public void succesfullySignedToServer(LoginResponse response) {
        toast(response.getMessage());
        if (response.isSuccesful()) {
            if (response.isPartOfTeam()) {
                Intent i = new Intent(this, ProjectScreen_.class);
                i.putExtra("projectId",response.getProjectId());
                toast("Is part of a team :D " );
                startActivity(i);
            } else {

                Intent i = new Intent(this, InvitesScreen_.class);
                toast("Not part of a team, join one or create one here");
                startActivity(i);
            }
        }else{
            toast(response.getMessage());
        }
    }



    public void toast(String t) {
        Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT).show();
    }

    public void getRegistrationToken(){
        startService(new Intent(this, MyRegistrationIntentService.class));
    }





}