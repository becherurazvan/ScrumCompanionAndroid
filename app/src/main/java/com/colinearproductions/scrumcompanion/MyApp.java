package com.colinearproductions.scrumcompanion;

import android.app.Application;
import android.util.Log;

import org.androidannotations.annotations.EApplication;

import Entities.Project;
import Scrum.ProductBacklog;

@EApplication
public class MyApp  extends Application{

    String email;
    String tokenId;
    String currentState;
    String gcmToken;

    Project project;


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

    public String getCurrentState() {
        return currentState;
    }

    public void setProject(Project project){
        this.project = project;
    }

    public void updateBacklog(Project project){
        this.project = project;
    }

    public Project getProject(){
        return project;
    }
}
