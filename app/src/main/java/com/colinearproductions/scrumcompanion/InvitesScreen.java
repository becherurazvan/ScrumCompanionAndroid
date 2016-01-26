package com.colinearproductions.scrumcompanion;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


@EActivity
public class InvitesScreen extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_screen);
        setFonts();
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
}
