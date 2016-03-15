package com.colinearproductions.scrumcompanion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.common.api.Status;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import Requests.LoginRequest;
import Requests.LoginResponse;
import VolleyClasses.VolleySingleton;
import gcm.MyRegistrationIntentService;
import gcm.MyRegistrationIntentService_;


@EActivity
public class LoginScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    @ViewById(R.id.logoText1)
    TextView logoText1;
    @ViewById(R.id.logoText2)
    TextView logoText2;
    @ViewById(R.id.sign_in_button)
    SignInButton signInButton;

    public static final String LOGIN_URL = "http://10.32.188.82:4567/user/login";
//   public static final String LOGIN_URL = "https://scrum-companion.herokuapp.com/user/login";


    //public static final String SERVER_CLIENT_ID = "735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com"; //GOOD ONE 735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com
    public static final String SERVER_CLIENT_ID = "735068003543-nl7oj4vo98s5nnabv5q13pgo688hkj3l.apps.googleusercontent.com"; // script


    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    GoogleSignInAccount acct;


    RequestQueue queue;
    ObjectMapper objectMapper;

    @App
    MyApp app;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Intent i = new Intent(getApplicationContext(),MainScreen_.class);
       // startActivity(i);

        getRegistrationToken(); // GCM
        app.setCurrentState(MyApp.LOGIN_SCREEN_STATE);

        setContentView(R.layout.activity_login_screen);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        queue = VolleySingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        objectMapper = new ObjectMapper();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestScopes(new Scope("https://www.googleapis.com/auth/drive"))
                //.requestServerAuthCode("735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com", true)// WEB CLIENT ID HERE, ANDROID CLIENT ID IN JSon
                .requestIdToken(SERVER_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        // .addApi(Drive.API)
                .build();

        app.setApiClient(mGoogleApiClient);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        app.printText("Initialized login screen");

    }

    @Override
    public void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            showProgressDialog();
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {


            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
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
            acct = result.getSignInAccount();
            app.setEmail(acct.getEmail());
            app.setUsername(acct.getDisplayName());
            app.setTokenId(acct.getIdToken());
            hideProgressDialog();

            Log.i(TAG, acct.getDisplayName() + " EMAIL: " + acct.getEmail() + " server auth code: " + acct.getServerAuthCode() + " Authentication token " + acct.getIdToken());
            try {
                showProgressDialog();
                succesfullySignedIn(acct);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                hideProgressDialog();
            } catch (JSONException e) {
                e.printStackTrace();
                hideProgressDialog();
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
                hideProgressDialog();
            }
        });
        Log.i(TAG, "Request: " + jsonLoginRequest);
        queue.add(jsonObjectRequest);
    }

    public void succesfullySignedToServer(LoginResponse response) {
        toast(response.getMessage());
        app.setEmail(acct.getEmail());
        app.setUsername(acct.getDisplayName());
        app.setTokenId(acct.getIdToken());
        hideProgressDialog();
        if (response.isSuccesful()) {
            if (response.isPartOfTeam()) {
                Intent i = new Intent(this, MainScreen_.class);
                i.putExtra("projectId", response.getProjectId());
                toast("Is part of a team :D ");
                startActivity(i);
            } else {

                Intent i = new Intent(this, InvitesScreen_.class);
                toast("Not part of a team, join one or create one here");
                startActivity(i);
            }
        } else {
            toast(response.getMessage());
        }
    }


    public void toast(String t) {
        Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT).show();
    }

    public void getRegistrationToken() {
        startService(new Intent(this, MyRegistrationIntentService_.class));
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