package co.sisu.mobile.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.MyFirebaseMessagingService;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.NotificationReceiver;
import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.RecordFragment;
import co.sisu.mobile.fragments.RecruitingScoreboardFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AgentModelStringSuperUser;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.AsyncAgentJsonStringSuperUserObject;
import co.sisu.mobile.models.AsyncClientJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.AsyncFirebaseDeviceJsonObject;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncLabelsJsonObject;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.AsyncTeamAgentJsonStringSuperUserObject;
import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.TeamColorSchemeObject;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;
import co.sisu.mobile.system.SaveSharedPreference;
import okhttp3.Response;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncServerEventListener {

    private DataController dataController;
    private NavigationManager navigationManager;
    private ActionBarManager actionBarManager;
    private ApiManager apiManager;
    public ColorSchemeManager colorSchemeManager;
    private MyFirebaseMessagingService myFirebaseMessagingService;
    public ProgressBar parentLoader;
    private String currentSelectedRecordDate = "";
    private boolean clientFinished = false;
    private boolean goalsFinished = false;
    private boolean teamParamFinished = false;
    private boolean activitySettingsParamFinished = false;
    private boolean settingsFinished = false;
    private boolean colorSchemeFinished = false;
    private boolean teamsFinished = false;
    private boolean labelsFinished = false;
    private boolean noNavigation = true;
    private boolean teamSwap = false;
    private boolean shouldDisplayPushNotification = false;
    private String timeline = "month";
    private int timelineSelection = 5;
    private AgentModel agent;
    private NotesObject selectedNote;
    private LruCache<String, Bitmap> mMemoryCache;
    private boolean imageIsExpanded = false;
    private ImageView expanded;
    private FirebaseDeviceObject currentDevice;
    private ConstraintLayout layout;
    private Toolbar toolbar;
    private ListView navViewList;
    private TextView navTitle;
    private boolean isNoteFragment = false;
    private Gson gson;
    private String pushNotificationTitle = "";
    private String pushNotificationBody = "";
    private String pushNotificationIsHTML = "";
    private String pushNotificationPushId = "";
    private String myAgentId = "";
    private Fragment f;
    private Animator mCurrentAnimator;
    private float startScale;
    private final Rect startBounds = new Rect();
    private final Rect finalBounds = new Rect();
    private final Point globalOffset = new Point();
    private int mShortAnimationDuration;
    private TeamObject updatedTeam;
    private int updateTeamPosition;
    private boolean isAdminMode = false;
    private boolean adminTransferring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        parentLoader = findViewById(R.id.parentLoader);
        gson = new Gson();
        dataController = new DataController();
        colorSchemeManager = new ColorSchemeManager();
        navigationManager = new NavigationManager(this);
        apiManager = new ApiManager(this);
        actionBarManager = navigationManager.getActionBarManager();

        pushNotificationTitle = getIntent().getStringExtra("title");
        pushNotificationBody = getIntent().getStringExtra("body");
        pushNotificationIsHTML = getIntent().getStringExtra("has_html");
        pushNotificationPushId = getIntent().getStringExtra("push_id");
        if(pushNotificationTitle != null && pushNotificationBody != null) {
            if(!pushNotificationTitle.equals("") && !pushNotificationBody.equals("")) {
                shouldDisplayPushNotification = true;
            }
        }

        agent = getIntent().getParcelableExtra("Agent");
        dataController.setAgent(agent);
        //MOCKING AN AGENT
//        agent.setAgent_id("5088");
//        dataController.setAgent(agent);
        //
        myAgentId = agent.getAgent_id();


        apiManager.getFirebaseDevices(this, agent.getAgent_id());

        initParentFields();
        initializeButtons();
        apiManager.getTeams(this, agent.getAgent_id());

        initCache();
    }

    private void initCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void initParentFields() {
        layout = findViewById(R.id.parentLayout);
        toolbar = findViewById(R.id.toolbar);
        navViewList = findViewById(R.id.navViewList);
        navTitle = findViewById(R.id.nav_title);
    }

    public void setActivityColors() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.setBackgroundColor(colorSchemeManager.getAppBackground());
                if(isAdminMode) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(ParentActivity.this, R.color.colorYellow));
                }
                else {
                    toolbar.setBackgroundColor(colorSchemeManager.getToolbarBackground());
                }
                navViewList.setBackgroundColor(colorSchemeManager.getAppBackground());
                navTitle.setBackgroundColor(colorSchemeManager.getAppBackground());
                navTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
                //change parentLoader here, if needed
                parentLoader = findViewById(R.id.parentLoader);
                if(colorSchemeManager.getAppBackground() == Color.WHITE) {
                    Rect bounds = parentLoader.getIndeterminateDrawable().getBounds();
                    parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark));
                    parentLoader.getIndeterminateDrawable().setBounds(bounds);
                } else {
                    Rect bounds = parentLoader.getIndeterminateDrawable().getBounds();
                    parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
                    parentLoader.getIndeterminateDrawable().setBounds(bounds);
                }
                navigationManager.updateColorScheme(colorSchemeManager);
            }
        });
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
        else {
            Log.e("Key already exists", "Replacing " + key);
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    private void initializeButtons(){
        ImageView scoreBoardButton = findViewById(R.id.scoreboardView);
        scoreBoardButton.setOnClickListener(this);

        ImageView reportButton = findViewById(R.id.reportView);
        reportButton.setOnClickListener(this);

        ImageView recordButton = findViewById(R.id.recordView);
        recordButton.setOnClickListener(this);

        ImageView leaderBoardButton = findViewById(R.id.leaderBoardView);
        leaderBoardButton.setOnClickListener(this);

        ImageView moreButton = findViewById(R.id.moreView);
        moreButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        parentLoader.setVisibility(View.GONE);
        if(dataController.getUpdatedRecords().size() > 0) {
            updateRecordedActivities();
        }

        if(!teamSwap) {
            switch (v.getId()) {
                case R.id.action_bar_home:
                case R.id.team_icon:
                    navigationManager.toggleDrawer();
                    break;
                case R.id.scoreboardView:
                    if(isRecruiting()) {
                        navigationManager.clearStackReplaceFragment(RecruitingScoreboardFragment.class);
                    }
                    else {
                        navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                    }
                    break;
                case R.id.reportView:
                    noNavigation = false;
                    navigationManager.clearStackReplaceFragment(ReportFragment.class);
                    break;
                case R.id.recordView:
                    noNavigation = false;
                    navigationManager.clearStackReplaceFragment(RecordFragment.class);
                    break;
                case R.id.leaderBoardView:
                    noNavigation = false;
                    navigationManager.clearStackReplaceFragment(LeaderboardFragment.class);
                    break;
                case R.id.moreView:
                    noNavigation = false;
                    navigationManager.clearStackReplaceFragment(MoreFragment.class);
                    break;
                case R.id.cancelButton:
                    navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                    break;
                case R.id.team_agents_title:
                    navigationManager.toggleTeamDrawer();
                    break;
                default:
                    break;
            }

        }

    }

    public void updateRecordedActivities() {
        List<Metric> updatedRecords = dataController.getUpdatedRecords();
        List<UpdateActivitiesModel> updateActivitiesModels = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        AsyncUpdateActivitiesJsonObject activitiesJsonObject = new AsyncUpdateActivitiesJsonObject();
        for(Metric m : updatedRecords) {
            if(currentSelectedRecordDate.equals("")) {
                updateActivitiesModels.add(new UpdateActivitiesModel(formatter.format(d), m.getType(), m.getCurrentNum(), Integer.valueOf(agent.getAgent_id())));
            }
            else {
                updateActivitiesModels.add(new UpdateActivitiesModel(currentSelectedRecordDate, m.getType(), m.getCurrentNum(), Integer.valueOf(agent.getAgent_id())));
            }
        }
        UpdateActivitiesModel[] array = new UpdateActivitiesModel[updateActivitiesModels.size()];
        updateActivitiesModels.toArray(array);

        activitiesJsonObject.setActivities(array);

        apiManager.sendAsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject, getSelectedTeamMarketId());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        parentLoader.setVisibility(View.VISIBLE);
        try {
            TeamObject team = (TeamObject) parent.getItemAtPosition(position);
            updatedTeam = team;
            updateTeamPosition = position;
            navigationManager.updateTeam(updatedTeam);
            navigationManager.updateSelectedTeam(updateTeamPosition);
            FragmentManager fragmentManager = getSupportFragmentManager();
            f = fragmentManager.findFragmentById(R.id.your_placeholder);

            sendTeamSwapApiCalls(team);

            navigationManager.closeDrawer();
        } catch ( ClassCastException cce) {
            //This is what goes off when you click a new team agent.
            AgentModelStringSuperUser selectedAgent = (AgentModelStringSuperUser) parent.getItemAtPosition(position);
            if(selectedAgent.getAgent_id().equals(myAgentId)) {
                // This is what will trigger when you return to yourself
                isAdminMode = false;
                actionBarManager.setAdminMode(isAdminMode);
            }
            else {
                if(isAdminMode) {
                    isAdminMode = false;
                    actionBarManager.setAdminMode(isAdminMode);
                }
                else {
                    isAdminMode = true;
                    actionBarManager.setAdminMode(isAdminMode);
                }
            }
            navigationManager.closeTeamAgentsDrawer();
            apiManager.getAgent(this, selectedAgent.getAgent_id());
        }
    }

    private void sendTeamSwapApiCalls(TeamObject team) {
        teamSwap = true;

        apiManager.getTeamParams(this, dataController.getAgent().getAgent_id(), team.getId());
        apiManager.getAgentGoals(this, dataController.getAgent().getAgent_id(), team.getId());
        apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), team.getId(), getSelectedTeamMarketId());
        apiManager.getClients(this, dataController.getAgent().getAgent_id(), team.getMarket_id());
        apiManager.getLabels(this, dataController.getAgent().getAgent_id(), team.getId());
        apiManager.getColorScheme(this, dataController.getAgent().getAgent_id(), team.getId(), dataController.getColorSchemeId());
    }

    private void executeTeamSwap() {
        if(clientFinished && goalsFinished && teamParamFinished && colorSchemeFinished && labelsFinished && activitySettingsParamFinished) {
            parentLoader.setVisibility(View.INVISIBLE);
            clientFinished = false;
            goalsFinished = false;
            settingsFinished = false;
            teamParamFinished = false;
            colorSchemeFinished = false;
            labelsFinished = false;
            noNavigation = true;
            activitySettingsParamFinished = false;
            teamSwap = false;
            SaveSharedPreference.setLogo(this, colorSchemeManager.getLogo() == null ? "" : colorSchemeManager.getLogo());
            switch (f.getTag()) {
                case "Scoreboard":
                    try {
                        if(isRecruiting()) {
                            ((RecruitingScoreboardFragment) f).teamSwap();
                            navigationManager.clearStackReplaceFragment(RecruitingScoreboardFragment.class);
                        }
                        else {
                            ((ScoreboardFragment) f).teamSwap();
                        }
                    }
                    catch(Exception e) {
                        if(isRecruiting()) {
                            navigationManager.clearStackReplaceFragment(RecruitingScoreboardFragment.class);
                        }
                        else {
                            navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                        }
                    }

                    break;
                case "Record":
                    ((RecordFragment) f).teamSwap();
                    break;
                case "Report":
                    ((ReportFragment) f).teamSwap();
                    break;
                case "Leaderboard":
                    ((LeaderboardFragment) f).teamSwap();
                    break;
                case "More":
                    ((MoreFragment) f).teamSwap();
                    break;
            }
        }
    }

    public TeamObject getCurrentTeam() {
        return navigationManager.getCurrentTeam();
    }

    private void navigateToScoreboard() {
        if(clientFinished && goalsFinished && settingsFinished && teamParamFinished && colorSchemeFinished && labelsFinished && activitySettingsParamFinished && noNavigation && !adminTransferring) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(isRecruiting()) {
                        navigationManager.clearStackReplaceFragment(RecruitingScoreboardFragment.class);
                    }
                    else {
                        navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                    }
                }
            });
            clientFinished = false;
            goalsFinished = false;
            settingsFinished = false;
            teamParamFinished = false;
            colorSchemeFinished = false;
            labelsFinished = false;
            noNavigation = true;
            activitySettingsParamFinished = false;
        }
        else {
            if(adminTransferring) {
                if(clientFinished && goalsFinished && activitySettingsParamFinished && settingsFinished && teamParamFinished && noNavigation) {
                    switch (navigationManager.getCurrentFragment()) {
                        case "Scoreboard":
                            noNavigation = false;
                            adminTransferring = false;
                            System.out.println(navigationManager.getCurrentFragment());
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(isRecruiting()) {
                                        navigationManager.clearStackReplaceFragment(RecruitingScoreboardFragment.class);
                                    }
                                    else {
                                        navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                                    }
                                }
                            });
                            break;
                        case "Report":
                            noNavigation = false;
                            adminTransferring = false;
                            System.out.println(navigationManager.getCurrentFragment());
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                navigationManager.clearStackReplaceFragment(ReportFragment.class);
                                }
                            });
                            break;
                        case "Record":
                            noNavigation = false;
                            adminTransferring = false;
                            System.out.println(navigationManager.getCurrentFragment());
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    navigationManager.clearStackReplaceFragment(RecordFragment.class);
                                }
                            });
                            break;
                        case "Leaderboard":
                            noNavigation = false;
                            adminTransferring = false;
                            System.out.println(navigationManager.getCurrentFragment());
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    navigationManager.clearStackReplaceFragment(LeaderboardFragment.class);
                                }
                            });
                            break;
                        case "More":
                            noNavigation = false;
                            adminTransferring = false;
                            System.out.println(navigationManager.getCurrentFragment());
                            this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    navigationManager.clearStackReplaceFragment(MoreFragment.class);
                                    parentLoader.setVisibility(View.INVISIBLE);
                                }
                            });
                            break;
                    }
                    System.out.println(navigationManager.getCurrentFragment());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        navigationManager.onBackPressed();
    }

    public void showToast(final CharSequence msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(ParentActivity.this, msg,Toast.LENGTH_SHORT);
                View view = toast.getView();
                TextView text = (TextView) view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorCorporateOrange));
                view.setBackgroundResource(R.color.colorCorporateOrange);
                text.setPadding(20, 8, 20, 8);
                toast.show();
            }
        });
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(teamSwap) {
            swappingTeamData(returnObject, returnType);
        }
        else if(returnType == ApiReturnTypes.UPDATE_ACTIVITIES) {
            dataController.clearUpdatedRecords();
        }
        else if(returnType == ApiReturnTypes.GET_TEAM_AGENTS) {
            AsyncTeamAgentJsonStringSuperUserObject teamAgentsObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncTeamAgentJsonStringSuperUserObject.class);
            final AgentModelStringSuperUser[] agents = teamAgentsObject.getAgents();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.initializeTeamAgents(agents, myAgentId);
                }
            });

        }
        else if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            AsyncActivitySettingsJsonObject settingsObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncActivitySettingsJsonObject.class);
            AsyncActivitySettingsObject[] settings = settingsObject.getRecord_activities();

            dataController.setActivitiesSelected(settings);
            activitySettingsParamFinished = true;
            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_AGENT_GOALS) {
            AsyncGoalsJsonObject goals = gson.fromJson(((Response) returnObject).body().charStream(), AsyncGoalsJsonObject.class);
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject, isRecruiting());
            goalsFinished = true;
            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_SETTINGS) {
            AsyncSettingsJsonObject settingsJson = gson.fromJson(((Response) returnObject).body().charStream(), AsyncSettingsJsonObject.class);
            ParameterObject[] settings = settingsJson.getParameters();
            dataController.setSettings(settings); //sets settings, and fills with default alarm notification if empty/not set yet
            List<ParameterObject> newSettings = dataController.getSettings(); //this is the new settings object list including any defaults generated
            settingsFinished = true;
            int hour = 0;
            int minute = 0;
            int reminderActive = 0;
            for (ParameterObject s : newSettings) {
                Log.e(s.getName(), s.getValue());
                switch (s.getName()) {
                    case "daily_reminder_time":
                        String[] values = s.getValue().split(":");
                        try{
                            hour = Integer.parseInt(values[0]);
                            minute = Integer.parseInt(values[1]);
                        } catch(NumberFormatException nfe) {
                            hour = 17;
                            minute = 0;
                        }
                        break;
                    case "daily_reminder":
                        try{
                            reminderActive = Integer.parseInt(s.getValue());

                        } catch(NumberFormatException nfe) {
                            reminderActive = 1;
                        }
                }
            }

            if(reminderActive == 1) {
                createNotificationAlarm(hour, minute, null); //sets the actual alarm with correct times from user settings
            }
            if(teamsFinished) {
                apiManager.getColorScheme(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), dataController.getColorSchemeId());
                apiManager.getLabels(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
            }
            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_TEAMS) {
            AsyncTeamsJsonObject teamsObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncTeamsJsonObject.class);
            dataController.setTeamsObject(ParentActivity.this, teamsObject);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.initializeTeamBar(dataController.getTeamsObject());
                    if(dataController.getTeamsObject().size() > 0) {
                        navigationManager.updateTeam(dataController.getTeamsObject().get(0));
                        dataController.setMessageCenterVisible(true);
                        apiManager.getTeamParams(ParentActivity.this, agent.getAgent_id(), dataController.getTeamsObject().get(0).getId());
                        apiManager.getClients(ParentActivity.this, agent.getAgent_id(), getSelectedTeamMarketId());
                        SaveSharedPreference.setTeam(ParentActivity.this, navigationManager.getSelectedTeamId() + "");
                        if(settingsFinished) {
                            apiManager.getColorScheme(ParentActivity.this, agent.getAgent_id(), navigationManager.getSelectedTeamId(), dataController.getColorSchemeId());
                            apiManager.getLabels(ParentActivity.this, agent.getAgent_id(), navigationManager.getSelectedTeamId());
                        }
                    }
                    else {
                        apiManager.getClients(ParentActivity.this, agent.getAgent_id(), getSelectedTeamMarketId());
                        dataController.setMessageCenterVisible(false);
                        teamParamFinished = true;
                        dataController.setSlackInfo(null);
                    }
                    teamsFinished = true;
                    apiManager.getAgentGoals(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
                    apiManager.getSettings(ParentActivity.this, agent.getAgent_id());
                    apiManager.getActivitySettings(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), getSelectedTeamMarketId());
                    apiManager.getTeamAgents(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
                }
            });
        }
        else if(returnType == ApiReturnTypes.GET_CLIENTS) {
            AsyncClientJsonObject clientObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncClientJsonObject.class);
            dataController.setClientListObject(clientObject, isRecruiting());
            clientFinished = true;
            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_TEAM_PARAMS) {
            AsyncParameterJsonObject settings = gson.fromJson(((Response) returnObject).body().charStream(), AsyncParameterJsonObject.class);
            if(settings.getStatus_code().equals("-1")) {
                dataController.setSlackInfo(null);
            }
            else {
                ParameterObject params = settings.getParameter();
                dataController.setSlackInfo(params.getValue());
            }
            teamParamFinished = true;
            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_FIREBASE_DEVICES) {
            AsyncFirebaseDeviceJsonObject asyncFirebaseDeviceJsonObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncFirebaseDeviceJsonObject.class);
            FirebaseDeviceObject[] devices = asyncFirebaseDeviceJsonObject.getDevices();
            String firebaseDeviceId = SaveSharedPreference.getFirebaseDeviceId(this);

            for(FirebaseDeviceObject fdo : devices) {
                if(fdo.getDevice_id() != null && fdo.getDevice_id().equals(firebaseDeviceId)) {
                    Log.e("Current Device", fdo.getDevice_id());
                    currentDevice = fdo;
                }
            }
            myFirebaseMessagingService = new MyFirebaseMessagingService(apiManager, dataController.getAgent(), this.getApplicationContext(), currentDevice);

            if(firebaseDeviceId.equals("")) {
                myFirebaseMessagingService.initFirebase();
            }
            else {
                myFirebaseMessagingService.refreshToken();
            }
        }
        else if(returnType == ApiReturnTypes.GET_COLOR_SCHEME) {
            AsyncTeamColorSchemeObject colorJson = gson.fromJson(((Response) returnObject).body().charStream(), AsyncTeamColorSchemeObject.class);
            TeamColorSchemeObject[] colorScheme = colorJson.getTheme();
            colorSchemeManager.setColorScheme(colorScheme, dataController.getColorSchemeId());
            setActivityColors();
            colorSchemeFinished = true;
            SaveSharedPreference.setLogo(this, colorSchemeManager.getLogo() == null ? "" : colorSchemeManager.getLogo());
            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_LABELS) {
            AsyncLabelsJsonObject labelObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncLabelsJsonObject.class);
            HashMap<String, String> labels = labelObject.getMarket();
            dataController.setLabels(labels);
            labelsFinished = true;
            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_AGENT) {
            adminTransferring = true;
            AsyncAgentJsonObject agentJsonObject = null;
            String r = null;
            try {
                r = ((Response) returnObject).body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                agentJsonObject = gson.fromJson(r, AsyncAgentJsonObject.class);
            } catch(Exception e) {
                AsyncAgentJsonStringSuperUserObject tempAgent = gson.fromJson(r, AsyncAgentJsonStringSuperUserObject.class);
                agentJsonObject = new AsyncAgentJsonObject(tempAgent);
            }
            noNavigation = true;
            AgentModel agentModel = agentJsonObject.getAgent();
            agent = agentModel;
            dataController.setAgent(agent);
            apiManager.getTeams(this, agent.getAgent_id());
        }
    }

    private void swappingTeamData(Object returnObject, ApiReturnTypes asyncReturnType) {
        if(asyncReturnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            AsyncActivitySettingsJsonObject settingsJson = gson.fromJson(((Response) returnObject).body().charStream(), AsyncActivitySettingsJsonObject.class);
            AsyncActivitySettingsObject[] settings = settingsJson.getRecord_activities();
            dataController.setActivitiesSelected(settings);
            activitySettingsParamFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_AGENT_GOALS) {
            AsyncGoalsJsonObject goals = gson.fromJson(((Response) returnObject).body().charStream(), AsyncGoalsJsonObject.class);
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject, isRecruiting());
            goalsFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_TEAM_PARAMS) {
            AsyncParameterJsonObject settingsJson = gson.fromJson(((Response) returnObject).body().charStream(), AsyncParameterJsonObject.class);
            if(settingsJson.getStatus_code().equals("-1")) {
                dataController.setSlackInfo(null);
            }
            else {
                ParameterObject params = settingsJson.getParameter();
                dataController.setSlackInfo(params.getValue());
            }
            teamParamFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_COLOR_SCHEME) {
            AsyncTeamColorSchemeObject colorJson = gson.fromJson(((Response) returnObject).body().charStream(), AsyncTeamColorSchemeObject.class);
            TeamColorSchemeObject[] colorScheme = colorJson.getTheme();
            colorSchemeManager.setColorScheme(colorScheme, dataController.getColorSchemeId());
            setActivityColors();
            colorSchemeFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_LABELS) {
            AsyncLabelsJsonObject labelObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncLabelsJsonObject.class);
            HashMap<String, String> labels = labelObject.getMarket();
            dataController.setLabels(labels);
            labelsFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_CLIENTS) {
            AsyncClientJsonObject clientObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncClientJsonObject.class);
            dataController.setClientListObject(clientObject, isRecruiting());
            clientFinished = true;
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                executeTeamSwap();
            }
        });
    }

    public void createNotificationAlarm(int currentSelectedHour, int currentSelectedMinute, PendingIntent pendingIntent) {
        if(pendingIntent == null) {
            Intent myIntent = new Intent(this, NotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 1412, myIntent, 0);
        }
        Calendar calendar = Calendar.getInstance();
        long currentTimeInMillis = calendar.getTimeInMillis();
        int interval = 1000 * 60 * 60 * 24; // One day

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, currentSelectedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, currentSelectedHour);

        if(currentTimeInMillis > calendar.getTimeInMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + interval);
        }


        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        Log.e("FAILURE", asyncReturnType);

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {}
        else if(returnType == ApiReturnTypes.GET_AGENT_GOALS) {}
        else if(returnType == ApiReturnTypes.GET_SETTINGS) {}
        else if(returnType == ApiReturnTypes.GET_TEAMS) {}
        else if(returnType == ApiReturnTypes.GET_CLIENTS) {}
        else if(returnType == ApiReturnTypes.GET_TEAM_PARAMS) {}
        else if(returnType == ApiReturnTypes.GET_FIREBASE_DEVICES) {}
        else if(returnType == ApiReturnTypes.GET_COLOR_SCHEME) {}
        else if(returnType == ApiReturnTypes.GET_LABELS) {}
    }

    public void zoomImageFromThumb(View convertView, final View thumbView, Bitmap bmp) {
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        expanded = findViewById(R.id.expanded_image);

        if(imageIsExpanded) {
            unzoomImageFromThumbnail();

        }
        else {
            imageIsExpanded = true;
            // If there's an animation in progress, cancel it
            // immediately and proceed with this one.
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Load the high-resolution "zoomed-in" image.
            expanded.setImageBitmap(bmp);

            // Calculate the starting and ending bounds for the zoomed-in image.
            // This step involves lots of math. Yay, math.
//            final Rect startBounds = new Rect();
//            final Rect finalBounds = new Rect();
//            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            thumbView.getGlobalVisibleRect(startBounds);
            convertView.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            // Adjust the start bounds to be the same aspect ratio as the final
            // bounds using the "center crop" technique. This prevents undesirable
            // stretching during the animation. Also calculate the start scaling
            // factor (the end scaling factor is always 1.0).
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                // Extend start bounds horizontally
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation
            // begins, it will position the zoomed-in view in the place of the
            // thumbnail.
            convertView.setAlpha(.5f);
            expanded.setVisibility(View.VISIBLE);

            // Set the pivot point for SCALE_X and SCALE_Y transformations
            // to the top-left corner of the zoomed-in view (the default
            // is the center of the view).
            expanded.setPivotX(0f);
            expanded.setPivotY(0f);

            // Construct and run the parallel animation of the four translation and
            // scale properties (X, Y, SCALE_X, and SCALE_Y).
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(expanded, View.X,
                            startBounds.left, finalBounds.centerX() / 2))
                    .with(ObjectAnimator.ofFloat(expanded, View.Y,
                            startBounds.top, finalBounds.centerY() / 2))
                    .with(ObjectAnimator.ofFloat(expanded, View.SCALE_X,
                            startScale, 1f))
                    .with(ObjectAnimator.ofFloat(expanded,
                            View.SCALE_Y, startScale, 1f));
            set.setDuration(mShortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;

            // Upon clicking the zoomed-in image, it should zoom back down
            // to the original bounds and show the thumbnail instead of
            // the expanded image.
            expanded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    unzoomImageFromThumbnail();
                }
            });
        }

    }

    public void unzoomImageFromThumbnail() {
        imageIsExpanded = false;
        final float startScaleFinal = startScale;
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(expanded, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expanded,
                                View.Y, startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expanded,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(expanded,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        final View parentView = findViewById(R.id.linearLayout);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                parentView.setAlpha(1f);
                expanded.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                parentView.setAlpha(1f);
                expanded.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }


    // GETTERS AND SETTERS

    public NotesObject getSelectedNote() {
        return selectedNote;
    }

    public void setSelectedNote(NotesObject selectedNote) {
        this.selectedNote = selectedNote;
    }

    public void setSelectedClient(ClientObject client) {
//        if(client.getIs_locked() == null) {
//            client.setIs_locked("0");
//        }
        navigationManager.setSelectedClient(client);
    }

    public ClientObject getSelectedClient() {
        return navigationManager.getSelectedClient();
    }

    public void updateSelectedRecordDate(String formattedDate) {
        this.currentSelectedRecordDate = formattedDate;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public int getTimelineSelection() {
        return timelineSelection;
    }

    public void setTimelineSelection(int timelineSelection) {
        this.timelineSelection = timelineSelection;
    }

    public int getSelectedTeamId() {
        int teamId = navigationManager.getSelectedTeamId();
        return teamId;
    }

    public int getSelectedTeamMarketId() {
        int marketId = navigationManager.getMarketId();
        return marketId;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public DataController getDataController() {
        return dataController;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public ColorSchemeManager getColorSchemeManager() {
        return colorSchemeManager;
    }

    public boolean imageExists(Context context, String id) {
        return "".equals(id) || context.getDir(id, Context.MODE_PRIVATE).exists();
    }

    public HashMap<String, String> getLabels() {
        return dataController.getLabels();
    }

    public String localizeLabel(String toCheck) {
        String toReturn = "";
        if(toCheck != null) {
            toReturn = dataController.localizeLabel(toCheck);
        }
        return toReturn;
    }

    public boolean getIsNoteFragment() {
        return isNoteFragment;
    }

    public void setNoteOrMessage(String type) {
        if(type.equals("Message")) {
            isNoteFragment = false;
        }
        else {
            isNoteFragment = true;
        }
    }

    public Gson getGson() {
        return gson;
    }

    public boolean shouldDisplayPushNotification() {
        return shouldDisplayPushNotification;
    }

    public String getPushNotificationTitle() {
        return pushNotificationTitle;
    }

    public String getPushNotificationBody() {
        return pushNotificationBody;
    }

    public String getPushNotificationIsHTML() {
        return  pushNotificationIsHTML;
    }

    public String getPushNotificationPushId() {
        return pushNotificationPushId;
    }

    public void setShouldDisplayPushNotification(boolean b) {
        shouldDisplayPushNotification = b;
    }

    public boolean isTeamSwapOccurring() {
        return teamSwap;
    }

    public boolean isRecruiting() {
        if(getSelectedTeamMarketId() == 2) {
            return true;
        }
        return false;
    }

    public boolean isAdminMode() {
        return isAdminMode;
    }

    public AgentModel getAgent() {
        return dataController.getAgent();
    }
}

