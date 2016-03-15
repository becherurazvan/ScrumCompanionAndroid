package com.colinearproductions.scrumcompanion;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import org.androidannotations.annotations.EApplication;

import Entities.Project;
import Scrum.ProductBacklog;

@EApplication
public class MyApp  extends Application{

    String email;
    String tokenId;
    String currentState;
    String gcmToken;
    String username;

    GoogleApiClient mGoogleApiClient;


    public static String LOGIN_SCREEN_STATE ="LOGIN";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void printText(String txt){
        Log.i("APP", txt);
    }

    public void setCurrentState(String state){
        currentState=state;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public boolean isEmailSet(){
        return email!=null;
    }

    public String getUsername(){return username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        Log.i("TOKEN","TOken set:" + gcmToken);
        this.gcmToken = gcmToken;
    }

    public void setApiClient(GoogleApiClient mGoogleApiClient){
        this.mGoogleApiClient = mGoogleApiClient;
    }

}
