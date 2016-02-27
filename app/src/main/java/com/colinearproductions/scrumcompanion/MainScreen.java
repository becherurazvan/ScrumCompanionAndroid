package com.colinearproductions.scrumcompanion;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

import Entities.Project;
import Requests.AddStoryRequest;
import Requests.AddStoryResponse;
import Requests.AddTaskRequest;
import Requests.CreateSprintRequest;
import Requests.UpdateUserStoryRequest;
import Scrum.Sprint;
import Scrum.Task;
import Scrum.UserStory;
import VolleyClasses.VolleySingleton;
import gcm.MyGcmListenerService;


@EActivity
public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
        , BacklogFragment.OnBacklogFragmentInteractionListener, UserStoryFragment.OnUserStoryFragmentInteractionListener
        , NewTaskDialog.Communicator, NewUserStoryDialog.UserStoryCommunicator,SprintFragment.OnSprintFragmentInteraction {

    @App
    MyApp app;


    public static final String TAG = "MAIN_SCREEN_ACTIVITY";


      public static final String ADD_STORY_LINK="https://scrum-companion.herokuapp.com/scrum/add_userstory";
      public static final String ADD_TASK_LINK="https://scrum-companion.herokuapp.com/scrum/add_task";
      public static final String UPDATE_STORY_LINK="https://scrum-companion.herokuapp.com/scrum/update_userstory";
      public static final String GET_BACKLOG_LINK="https://scrum-companion.herokuapp.com/project/get_project";
    public static final String ADD_SPRINT_LINK = "https://scrum-companion.herokuapp.com/scrum/create_sprint";

//    public static final String UPDATE_STORY_LINK = "http://10.32.188.82:4567/scrum/update_userstory";
//    public static final String ADD_TASK_LINK = "http://10.32.188.82:4567/scrum/add_task";
//    public static final String GET_BACKLOG_LINK = "http://10.32.188.82:4567/project/get_project";
//    public static final String ADD_STORY_LINK = "http://10.32.188.82:4567/scrum/add_userstory";
//    public static final String ADD_SPRINT_LINK = "http://10.32.188.82:4567/scrum/create_sprint";


    Project project;


    public static final String BACKLOG_FRAGMENT_TAG = "backlog_fragment";

    public static final String SPRINTS_FRAGMENT_TAG = "sprints_fragment";
    public static final String MAIN_FRAGMENT_TAG = "main_fragment";
    public static final String USER_STORY_FRAGMENT_TAG = "user_story_fragment";

    RequestQueue queue;
    ObjectMapper objectMapper;

    private BroadcastReceiver receiver;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(MAIN_FRAGMENT_TAG);
        transaction.commit();


        IntentFilter filter = new IntentFilter();
        filter.addAction(MyGcmListenerService.intentFilterString);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString("message");

                if (message.equals(MyGcmListenerService.BACKLOG_UPDATE_AVAILABLE)) {
                    try {
                        loadBacklog();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        registerReceiver(receiver, filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        queue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        objectMapper = new ObjectMapper();

        try {
            loadBacklog();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_backlog:
                BacklogFragment backlogFragment = new BacklogFragment();
                transaction.replace(R.id.main_frame_layout, backlogFragment, BACKLOG_FRAGMENT_TAG);
                transaction.addToBackStack(BACKLOG_FRAGMENT_TAG);
                toolbar.setTitle("Product Backlog");
                transaction.commit();
                break;
            case R.id.nav_sprints:
                SprintFragment sprintFragment = new SprintFragment();

                transaction.replace(R.id.main_frame_layout, sprintFragment, SPRINTS_FRAGMENT_TAG);
                transaction.addToBackStack(SPRINTS_FRAGMENT_TAG);
                toolbar.setTitle("Sprints");
                transaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, getCurrentFragment());
        switch (getCurrentFragment()) {
            case BACKLOG_FRAGMENT_TAG:

                break;
            default:
                break;
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void createNewTask(String userStoryId) {
        FragmentManager manager = getFragmentManager();
        NewTaskDialog newTaskDialog = new NewTaskDialog();
        newTaskDialog.setUserStoryId(userStoryId);
        newTaskDialog.show(manager, "new_task_dialog");
    }

    @Override
    public void userStoryEdited(UserStory story) {
        try {
            updateStory(story);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void loadBacklog() throws JsonProcessingException, JSONException {
        Requests.Request getBacklogRequest = new Requests.Request(app.getTokenId());
        String jsonLoginRequest = objectMapper.writeValueAsString(getBacklogRequest);

        JSONObject jsonBody = new JSONObject(jsonLoginRequest);
        String url = GET_BACKLOG_LINK;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        Log.i(TAG, "Request: " + jsonLoginRequest);
        queue.add(jsonObjectRequest);
    }

    public void addNewUserStory(String storyDescription) throws JsonProcessingException, JSONException {
        AddStoryRequest addStoryRequest = new AddStoryRequest(app.getTokenId(), storyDescription);
        String jsonAddStoryRequest = objectMapper.writeValueAsString(addStoryRequest);

        JSONObject jsonBody = new JSONObject(jsonAddStoryRequest);
        String url = ADD_STORY_LINK;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    AddStoryResponse r = objectMapper.readValue(response.toString(), AddStoryResponse.class);
                    if (r.isSuccesful()) {
                        addedNewStory(r.getStory());
                    } else {
                        toast(r.getMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);

    }


    public void addNewTask(Task t) throws JsonProcessingException, JSONException {


        AddTaskRequest addTaskRequest = new AddTaskRequest(app.getTokenId(), t);
        String jsonAddTaskRequest = objectMapper.writeValueAsString(addTaskRequest);

        JSONObject jsonBody = new JSONObject(jsonAddTaskRequest);
        String url = ADD_TASK_LINK;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Task t = objectMapper.readValue(response.toString(), Task.class);
                    addedNewTask(t);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);

    }


    public void updateStory(UserStory story) throws JsonProcessingException, JSONException {


        UpdateUserStoryRequest updateUserStoryRequest = new UpdateUserStoryRequest(app.getTokenId(), story);
        String updateStoryJson = objectMapper.writeValueAsString(updateUserStoryRequest);

        JSONObject jsonBody = new JSONObject(updateStoryJson);
        String url = UPDATE_STORY_LINK;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    UserStory s = objectMapper.readValue(response.toString(), UserStory.class);
                    updatedUserStory(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);

    }


    public void addedNewStory(UserStory story) {
        BacklogFragment backlogFragment = (BacklogFragment) getSupportFragmentManager().findFragmentByTag(BACKLOG_FRAGMENT_TAG);
        story.refreshStoryPoints();
        project.getProductBacklog().addUserStory(story);
        backlogFragment.backlogLoaded(project);
        backlogFragment.adapter.updateStoryPoints();
    }

    public void addedNewTask(Task t) {
        UserStoryFragment userStoryFragment = (UserStoryFragment) getSupportFragmentManager().findFragmentByTag(USER_STORY_FRAGMENT_TAG);
        BacklogFragment backlogFragment = (BacklogFragment) getSupportFragmentManager().findFragmentByTag(BACKLOG_FRAGMENT_TAG);
        backlogFragment.adapter.updateStoryPoints();

        project.getProductBacklog().addTask(t);
        userStoryFragment.taskHolder.notifyDataSetChanged();


    }


    public void updatedUserStory(UserStory s) {

        BacklogFragment backlogFragment = (BacklogFragment) getSupportFragmentManager().findFragmentByTag(BACKLOG_FRAGMENT_TAG);
        backlogFragment.adapter.updateStoryPoints();
        UserStoryFragment userStoryFragment = (UserStoryFragment) getSupportFragmentManager().findFragmentByTag(USER_STORY_FRAGMENT_TAG);
        project.getProductBacklog().updateUserStory(s);
        userStoryFragment.taskHolder.notifyDataSetChanged();


    }


    public void projectLoaded(Project p) {
        this.project = p;
        BacklogFragment backlogFragment = (BacklogFragment) getSupportFragmentManager().findFragmentByTag(BACKLOG_FRAGMENT_TAG);
        if (backlogFragment != null) // notify the fragment that the project has been updated
            backlogFragment.backlogLoaded(p);

        Sync();
    }

    public Project getProject() {
        return this.project;
    }

    @Override
    public void newTaskCreated(Task newTask) {

        try {
            addNewTask(newTask);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "On main screen notified of new task: " + newTask.getTitle());
    }

    public void toast(String t) {
        Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT).show();
    }

    public String getCurrentFragment() {
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        if (tag == null) {
            return "Nothing on the backstack of the fragment manager";
        }
        return tag;
    }

    @Override
    public void showNewUserStoryDialog() {

        FragmentManager manager = getFragmentManager();
        NewUserStoryDialog userStoryDialog = new NewUserStoryDialog();
        userStoryDialog.show(manager, "new_user_story_dialog");
        Log.i(TAG, "Trying to show dialog for new user story");
    }

    @Override
    public void newUserStoryCreated(UserStory userStory) {
        Log.i(TAG, "New user story to be created:" + userStory.getDescription());
        try {
            addNewUserStory(userStory.getDescription());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void Sync() {

        BacklogFragment backlogFragment = (BacklogFragment) getSupportFragmentManager().findFragmentByTag(BACKLOG_FRAGMENT_TAG);
        if (backlogFragment != null) // notify the fragment that the project has been updated
            backlogFragment.Sync();

        UserStoryFragment userStoryFragment = (UserStoryFragment) getSupportFragmentManager().findFragmentByTag(USER_STORY_FRAGMENT_TAG);
        if(userStoryFragment!=null)
        userStoryFragment.taskHolder.notifyDataSetChanged();
    }




    @Override
    public void onNewSprintButtonClicked(String startDate, String endDate) {
        try {
            createNewSprint(startDate,endDate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void createNewSprint(String startDate, String endDate) throws JsonProcessingException, JSONException {


        CreateSprintRequest createSprintRequest = new CreateSprintRequest(app.getTokenId(),startDate,endDate);
        String createNewSprintJson = objectMapper.writeValueAsString(createSprintRequest);

        JSONObject jsonBody = new JSONObject(createNewSprintJson);
        String url = ADD_SPRINT_LINK;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Sprint s =  objectMapper.readValue(response.toString(), Sprint.class);
                    newSprintAdded(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void newSprintAdded(Sprint s){
        project.getProductBacklog().addSprint(s);
        SprintFragment sprintFragment = (SprintFragment) getSupportFragmentManager().findFragmentByTag(SPRINTS_FRAGMENT_TAG);
        sprintFragment.adapter.setSprints(project.getProductBacklog().getSprints());
        sprintFragment.adapter.notifyDataSetChanged();

    }

}
