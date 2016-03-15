package com.colinearproductions.scrumcompanion;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import RecyclerViewHolders.MembersAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {


    Button signOut;
    MainScreen mainScreen;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.members_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mainScreen = (MainScreen)getActivity();

        MembersAdapter adapter = new MembersAdapter(mainScreen.getProject().getUsers(),mainScreen);

        TextView invCode = (TextView) view.findViewById(R.id.invitation_code_text);
        invCode.setText("Invitation code:" +mainScreen.getProject().getInvitationCode());

        signOut = (Button) view.findViewById(R.id.sign_out_button);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainScreen.logout();
            }
        });


        Log.i("Amoutn Of userS", mainScreen.getProject().getUsers().size()+"");

        recyclerView.setAdapter(adapter);

    }


}
