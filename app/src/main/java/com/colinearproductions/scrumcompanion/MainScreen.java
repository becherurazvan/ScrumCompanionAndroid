package com.colinearproductions.scrumcompanion;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import Database.Action;
import Database.FeedList;
import Entities.Project;
import Entities.User;
import RecyclerViewHolders.SprintListViewHolder;
import Requests.AddStoryRequest;
import Requests.AddTaskRequest;
import Requests.AddUsToCurrentSprintRequest;
import Requests.CreateSprintRequest;
import Requests.DeleteSprintRequest;
import Requests.EditTaskRequest;
import Requests.GetFeedRequest;
import Requests.RemoveUsFromCurrentSprintRequest;
import Requests.UpdateTaskStateRequest;
import Requests.UpdateUserStoryRequest;
import Scrum.Resource;
import Scrum.Sprint;
import Scrum.Task;
import Scrum.UserStory;
import VolleyClasses.VolleySingleton;
import gcm.MyGcmListenerService;


@EActivity
public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , BacklogFragment.OnBacklogFragmentInteractionListener, UserStoryFragment.OnUserStoryFragmentInteractionListener
        , NewTaskDialog.Communicator, NewUserStoryDialog.UserStoryCommunicator, SprintFragment.OnSprintFragmentInteraction, SprintListViewHolder.OnSprintActionButtonClickedListener, Action.OnResourceClickedListener {

    @App
    MyApp app;


    public static final String TAG = "MAIN_SCREEN_ACTIVITY";


//      public static final String ADD_STORY_LINK="https://scrum-companion.herokuapp.com/scrum/add_userstory";
//      public static final String ADD_TASK_LINK="https://scrum-companion.herokuapp.com/scrum/add_task";
//      public static final String UPDATE_STORY_LINK="https://scrum-companion.herokuapp.com/scrum/update_userstory";
//      public static final String GET_BACKLOG_LINK="https://scrum-companion.herokuapp.com/project/get_project";
//    public static final String ADD_SPRINT_LINK = "https://scrum-companion.herokuapp.com/scrum/create_sprint";

    public static final String UPDATE_STORY_LINK = "http://10.32.188.82:4567/scrum/update_userstory";
    public static final String ADD_TASK_LINK = "http://10.32.188.82:4567/scrum/add_task";
    public static final String GET_BACKLOG_LINK = "http://10.32.188.82:4567/project/get_project";
    public static final String ADD_STORY_LINK = "http://10.32.188.82:4567/scrum/add_userstory";
    public static final String ADD_SPRINT_LINK = "http://10.32.188.82:4567/scrum/create_sprint";
    public static final String UPDATE_TASK_STATE_LINK = "http://10.32.188.82:4567/scrum/task";
    public static final String ADD_STORY_TO_CURRENT_SPRINT_LINK = "http://10.32.188.82:4567/scrum/currentsprint";
    public static final String REMOVE_STORY_FROM_SPRINT_LINK = "http://10.32.188.82:4567/scrum/removeus";
    public static final String EDIT_TASK_LINK = "http://10.32.188.82:4567/scrum/task_edit";
    public static final String DELETE_SPRINT_LINK = "http://10.32.188.82:4567/scrum/remove_sprint";
    public static final String GET_FEED_LINK = "http://10.32.188.82:4567/feed";


    Project project;
    FeedList feedList;

    public static final String BACKLOG_FRAGMENT_TAG = "backlog_fragment";
    public static final String SPRINTS_FRAGMENT_TAG = "sprints_fragment";
    public static final String MAIN_FRAGMENT_TAG = "main_fragment";
    public static final String USER_STORY_FRAGMENT_TAG = "user_story_fragment";
    public static final String CURRENT_SPRINT_FRAGMENT_TAG = "current_sprint_fragment";
    public static final String REPORTS_FRAGMENT_TAG = "reports_fragment";

    RequestQueue queue;
    ObjectMapper objectMapper;
    Menu menu;
    ProgressDialog progressDialog;

    public HashMap<String,Bitmap> icons = new HashMap<>();


    private BroadcastReceiver receiver;

    TextView userName;
    TextView email;
    Toolbar toolbar;

    private GoogleApiClient mGoogleApiClient;

    public static final String SERVER_CLIENT_ID = "735068003543-nl7oj4vo98s5nnabv5q13pgo688hkj3l.apps.googleusercontent.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestScopes(new Scope("https://www.googleapis.com/auth/drive"))
                //.requestServerAuthCode("735068003543-qnqng9c8jpg13q83hu1h3aebjkogapp3.apps.googleusercontent.com", true)// WEB CLIENT ID HERE, ANDROID CLIENT ID IN JSon
                .requestIdToken(SERVER_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();





        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();


        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_user_name);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_user_email);


        IntentFilter filter = new IntentFilter();
        filter.addAction(MyGcmListenerService.intentFilterString);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                String message = extras.getString("message");
                if (message.equals(MyGcmListenerService.BACKLOG_UPDATE_AVAILABLE))
                    loadBacklog();
                else if (message.equals(MyGcmListenerService.FEED_ITEM_AVAILABLE)) {
                    try {
                        getFeedTen();
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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
        loadBacklog();
        try {
            getFeedInitial();
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
                sprintFragment.setOnSprintActionButtonClickedListener(this);
                transaction.replace(R.id.main_frame_layout, sprintFragment, SPRINTS_FRAGMENT_TAG);
                transaction.addToBackStack(SPRINTS_FRAGMENT_TAG);
                toolbar.setTitle("Sprints");
                transaction.commit();
                break;
            case R.id.nav_current_sprint:
                if (project.getProductBacklog().getCurrentSprint() == null) {
                    break;
                }
                CurrentSprintFragment currentSprintFragment = new CurrentSprintFragment();
                transaction.replace(R.id.main_frame_layout, currentSprintFragment, CURRENT_SPRINT_FRAGMENT_TAG);
                transaction.addToBackStack(CURRENT_SPRINT_FRAGMENT_TAG);
                toolbar.setTitle("Current sprint");
                transaction.commit();
                break;
            case R.id.nav_reports:
                if (project.getProductBacklog().getCurrentSprint() == null) {
                    toast("No reports available yet, will become available after the first sprint is created");
                    break;
                } else if (project.getProductBacklog().getCurrentSprint().getDayOfSprint() < 1) {
                    toast("Reports will be available after the first day of a sprint");
                    break;
                }
                showReportForSprint(project.getProductBacklog().getCurrentSprint().getNumber());
                break;
            case R.id.nav_dashboard:
                FeedFragment feedFragment = new FeedFragment();
                transaction.replace(R.id.main_frame_layout, feedFragment, "FEED_FRAGMENT");
                transaction.addToBackStack("FEED_FRAGMENT");
                toolbar.setTitle("Feed page");
                transaction.commit();
                break;
            case R.id.nav_info:
                InfoFragment infoFragment = new InfoFragment();
                transaction.replace(R.id.main_frame_layout, infoFragment, "INFO_FRAGMENT");
                transaction.addToBackStack("INFO_FRAGMENT");
                toolbar.setTitle("Project Info");
                transaction.commit();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showReportForSprint(int number) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ReportsFragment reportsFragment = new ReportsFragment();
        reportsFragment.setSprintNumber(number);
        transaction.replace(R.id.main_frame_layout, reportsFragment, REPORTS_FRAGMENT_TAG);
        transaction.addToBackStack(REPORTS_FRAGMENT_TAG);
        toolbar.setTitle("Reports sprint " + number);
        transaction.commit();
    }


    /////////////////////////////////////////////////////// Frament Callbacks
    public void addUserStoryToSprint(UserStory story) {

        if (project.getProductBacklog().getCurrentSprint() == null) {
            toast("No current sprint active, create a new one");
            return;
        }
        if (story.getTasks().size() <= 0) {
            toast("Add some tasks to the user stories before adding it to a sprint");
            return;
        }
        addUsToSprint(story);

    }

    @Override
    public void createNewTask(String userStoryId) {
        FragmentManager manager = getFragmentManager();
        NewTaskDialog newTaskDialog = new NewTaskDialog();
        newTaskDialog.setUserStoryId(userStoryId);
        newTaskDialog.show(manager, "new_task_dialog");
    }

    @Override
    public void editTaskClicked(Task t) {
        FragmentManager manager = getFragmentManager();
        NewTaskDialog newTaskDialog = new NewTaskDialog();
        newTaskDialog.setTask(t);
        newTaskDialog.show(manager, "edit_task_dialog");
    }

    @Override
    public void taskEditFinished(Task task) {
        Log.i(TAG, "EDIT TASK FINISHED:" + task.getDescription());
        try {
            editTask(task);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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


    @Override
    public void newTaskCreated(Task newTask) {
        try {
            addNewTask(newTask);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "On main screen notified of new task: " + newTask.getDescription());
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

    @Override
    public void onNewSprintButtonClicked(String startDate, String endDate) {
        try {
            createNewSprint(startDate, endDate);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNewUserStoryDialog() {

        FragmentManager manager = getFragmentManager();
        NewUserStoryDialog userStoryDialog = new NewUserStoryDialog();
        userStoryDialog.show(manager, "new_user_story_dialog");
        Log.i(TAG, "Trying to show dialog for new user story");
    }


    public void removeUSfromSprint(UserStory story) {

        try {
            remUsFromSprint(story);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////// NETWORK requests
    public void loadBacklog() {

        email.setText(app.getEmail());
        userName.setText(app.getUsername());

        Requests.Request getBacklogRequest = new Requests.Request(app.getTokenId());
        String jsonLoginRequest = null;
        try {
            jsonLoginRequest = objectMapper.writeValueAsString(getBacklogRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(jsonLoginRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
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
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();

                FeedFragment feedFragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag("FEED_FRAGMENT");
                if (feedFragment != null) {
                    feedFragment.mAdapter.notifyDataSetChanged();
                    feedFragment.swipeRefreshLayout.setRefreshing(false);
                    feedFragment.loading = false;
                }
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

        showProgressDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);

    }


    public void editTask(Task newVersion) throws JsonProcessingException, JSONException {
        EditTaskRequest editTaskRequest = new EditTaskRequest(app.getTokenId(), newVersion);
        String jsonAddStoryRequest = objectMapper.writeValueAsString(editTaskRequest);

        JSONObject jsonBody = new JSONObject(jsonAddStoryRequest);
        String url = EDIT_TASK_LINK;

        showProgressDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);

    }


    public void remUsFromSprint(UserStory story) throws JsonProcessingException, JSONException {


        RemoveUsFromCurrentSprintRequest removeUsFromCurrentSprintRequest = new RemoveUsFromCurrentSprintRequest(app.getTokenId(), story);
        String jsonRemoveRequest = objectMapper.writeValueAsString(removeUsFromCurrentSprintRequest);
        JSONObject jsonBody = new JSONObject(jsonRemoveRequest);
        String url = REMOVE_STORY_FROM_SPRINT_LINK;
        showProgressDialog();

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
                hideProgressDialog();
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);

    }


    public void addUsToSprint(UserStory story) {

        AddUsToCurrentSprintRequest req = new AddUsToCurrentSprintRequest(app.getTokenId(), story);
        String jsonAddStoryRequest = null;
        try {
            jsonAddStoryRequest = objectMapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(jsonAddStoryRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = ADD_STORY_TO_CURRENT_SPRINT_LINK;

        showProgressDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
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

        showProgressDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
        Log.i(TAG, jsonAddTaskRequest);

    }

    public void updateStory(UserStory story) throws JsonProcessingException, JSONException {
        UpdateUserStoryRequest updateUserStoryRequest = new UpdateUserStoryRequest(app.getTokenId(), story);
        String updateStoryJson = objectMapper.writeValueAsString(updateUserStoryRequest);

        JSONObject jsonBody = new JSONObject(updateStoryJson);
        String url = UPDATE_STORY_LINK;

        showProgressDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }

        });
        queue.add(jsonObjectRequest);
    }


    public void updateTaskState(Task t) {
        UpdateTaskStateRequest updateTaskStateRequest = new UpdateTaskStateRequest(app.getTokenId(), t);
        String updateTaskJson = null;
        try {
            updateTaskJson = objectMapper.writeValueAsString(updateTaskStateRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        JSONObject jsonBody = null;
        try {
            jsonBody = new JSONObject(updateTaskJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = UPDATE_TASK_STATE_LINK;

        showProgressDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
        Log.i(TAG, updateTaskJson);
    }

    public void createNewSprint(String startDate, String endDate) throws JsonProcessingException, JSONException {
        CreateSprintRequest createSprintRequest = new CreateSprintRequest(app.getTokenId(), startDate, endDate);
        String createNewSprintJson = objectMapper.writeValueAsString(createSprintRequest);
        JSONObject jsonBody = new JSONObject(createNewSprintJson);
        String url = ADD_SPRINT_LINK;
        showProgressDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Project p = objectMapper.readValue(response.toString(), Project.class);
                    projectLoaded(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hideProgressDialog();
                Log.i(TAG, "Get backlog response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }


    ////////////////////////////////////////////////////////////////// NETWORK callbacks
    public void projectLoaded(Project p) {

        this.project = p;
        MenuItem currentSprintMenuItem = menu.findItem(R.id.nav_current_sprint);
        if (p.getProductBacklog().getCurrentSprint() != null) {
            currentSprintMenuItem.setTitle("Current Sprint: " + p.getProductBacklog().getCurrentSprint().getNumber());
        } else {
            currentSprintMenuItem.setTitle("Start New Sprint");
        }

        BacklogFragment backlogFragment = (BacklogFragment) getSupportFragmentManager().findFragmentByTag(BACKLOG_FRAGMENT_TAG);
        if (backlogFragment != null) // notify the fragment that the project has been updated
            backlogFragment.notifyBacklogUpdate(p);

        SprintFragment sprintFragment = (SprintFragment) getSupportFragmentManager().findFragmentByTag(SPRINTS_FRAGMENT_TAG);
        if (sprintFragment != null) {
            sprintFragment.adapter.setSprints(project.getProductBacklog().getSprints());
        }

        UserStoryFragment userStoryFragment = (UserStoryFragment) getSupportFragmentManager().findFragmentByTag(USER_STORY_FRAGMENT_TAG);
        if (userStoryFragment != null) {
            userStoryFragment.update();
        }

        CurrentSprintFragment currentSprintFragment = (CurrentSprintFragment) getSupportFragmentManager().findFragmentByTag(CURRENT_SPRINT_FRAGMENT_TAG);
        if (currentSprintFragment != null) {
            currentSprintFragment.onResumeCurrentPage();
        }
        hideProgressDialog();

        for(User u:project.getUsers()){
            if(u.pictureUrl!=null)
                loadImage(u.pictureUrl,u.getEmail());
        }

    }

    public void removeSprintRequest(Sprint s) throws JsonProcessingException, JSONException {
        DeleteSprintRequest deleteSprintRequest = new DeleteSprintRequest(app.getTokenId(), s.getNumber());
        String updateStoryJson = objectMapper.writeValueAsString(deleteSprintRequest);

        JSONObject jsonBody = new JSONObject(updateStoryJson);
        String url = DELETE_SPRINT_LINK;

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
        queue.add(jsonObjectRequest);
    }


    public void getFeedInitial() throws JsonProcessingException, JSONException {
        GetFeedRequest getFeedRequest = new GetFeedRequest(app.getTokenId(), 10, null, false);
        String updateStoryJson = objectMapper.writeValueAsString(getFeedRequest);

        JSONObject jsonBody = new JSONObject(updateStoryJson);
        String url = GET_FEED_LINK;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Action> actionArrayList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        Action jo = objectMapper.readValue(response.getJSONObject(i).toString(), Action.class);
                        actionArrayList.add(jo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                initialFeedLoaded(actionArrayList);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });

        queue.add(jsonArrayRequest);
    }


    public void getFeedTen() throws JsonProcessingException, JSONException {
        GetFeedRequest getFeedRequest = new GetFeedRequest(app.getTokenId(), 10, null, false);
        String updateStoryJson = objectMapper.writeValueAsString(getFeedRequest);

        JSONObject jsonBody = new JSONObject(updateStoryJson);
        String url = GET_FEED_LINK;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Action> actionArrayList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        Action jo = objectMapper.readValue(response.getJSONObject(i).toString(), Action.class);
                        actionArrayList.add(jo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                feedUpdated(actionArrayList);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });

        queue.add(jsonArrayRequest);
    }

    public void getFeedBottomTen(Calendar since) throws JsonProcessingException, JSONException {
        GetFeedRequest getFeedRequest = new GetFeedRequest(app.getTokenId(), 10, since, true);
        String updateStoryJson = objectMapper.writeValueAsString(getFeedRequest);

        JSONObject jsonBody = new JSONObject(updateStoryJson);
        String url = GET_FEED_LINK;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                ArrayList<Action> actionArrayList = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        Action jo = objectMapper.readValue(response.getJSONObject(i).toString(), Action.class);
                        actionArrayList.add(jo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                feedUpdated(actionArrayList);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Get backlog Erorr" + error.toString());
            }
        });

        queue.add(jsonArrayRequest);
    }


    public void loadImage(String url, final String email) {
        if(icons.keySet().contains(email) &&icons.get(email)!=null){
            return;
        }

        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        icons.put(email,bitmap);
                        projectLoaded(project);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        icons.put(email, null);
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    ///////////////////////////////////////

    public void initialFeedLoaded(ArrayList<Action> actions) {

        feedList = new FeedList();
        feedList.add(actions);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        FeedFragment feedFragment = new FeedFragment();
        transaction.replace(R.id.main_frame_layout, feedFragment, "FEED_FRAGMENT");
        transaction.addToBackStack("FEED_FRAGMENT");
        toolbar.setTitle("Feed page");
      transaction.commit();
    }

    public void feedUpdated(ArrayList<Action> actions) {
        feedList.add(actions);
        FeedFragment feedFragment = (FeedFragment) getSupportFragmentManager().findFragmentByTag("FEED_FRAGMENT");
        if (feedFragment != null) {
            feedFragment.mAdapter.notifyDataSetChanged();
            feedFragment.swipeRefreshLayout.setRefreshing(false);
            feedFragment.loading = false;
        }

    }


    public Project getProject() {
        return this.project;
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

    public void showUserStoryFragment(UserStory story) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        UserStoryFragment fragment = new UserStoryFragment();
        fragment.setUserStory(story);
        transaction.replace(R.id.main_frame_layout, fragment, MainScreen.USER_STORY_FRAGMENT_TAG);
        transaction.addToBackStack(MainScreen.USER_STORY_FRAGMENT_TAG);
        transaction.commit();
    }


    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Wait while loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void onGoToCurrentSprintClicked(Sprint s) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        CurrentSprintFragment currentSprintFragment = new CurrentSprintFragment();
        transaction.replace(R.id.main_frame_layout, currentSprintFragment, CURRENT_SPRINT_FRAGMENT_TAG);
        transaction.addToBackStack(CURRENT_SPRINT_FRAGMENT_TAG);
        toolbar.setTitle("Current sprint");
        transaction.commit();
    }

    @Override
    public void onReportClicked(Sprint s) {
        showReportForSprint(s.getNumber());
    }

    @Override
    public void onSprintDeleteClicked(Sprint s) {
        try {
            removeSprintRequest(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public FeedList getFeedList() {
        return feedList;
    }

    @Override
    public void onResourceClicked(Resource resource) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (resource.getResourceType()) {
            case Resource.RESOURCE_SPRINT:
                SprintFragment sprintFragment = new SprintFragment();
                sprintFragment.setOnSprintActionButtonClickedListener(this);
                transaction.replace(R.id.main_frame_layout, sprintFragment, SPRINTS_FRAGMENT_TAG);
                transaction.addToBackStack(SPRINTS_FRAGMENT_TAG);
                toolbar.setTitle("Sprints");
                transaction.commit();
                break;
            case Resource.RESOURCE_TASK:
                UserStory s = project.getProductBacklog().getStoryById(resource.getResourceId().split("-")[0]);
                showUserStoryFragment(s);
                break;
            case Resource.RESOURCE_US:
                UserStory story = project.getProductBacklog().getStoryById(resource.getResourceId());
                showUserStoryFragment(story);
                break;
        }
    }

    public void logout(){




        mGoogleApiClient.clearDefaultAccountAndReconnect();
        showProgressDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                hideProgressDialog();
                finish();
            }
        }, 1000);


    }
}
