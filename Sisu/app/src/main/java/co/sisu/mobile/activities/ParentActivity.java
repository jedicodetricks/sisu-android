package co.sisu.mobile.activities;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.CacheManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.DateManager;
import co.sisu.mobile.controllers.MyFirebaseMessagingService;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.fragments.ClientManageFragment;
import co.sisu.mobile.fragments.main.ClientTileFragment;
import co.sisu.mobile.fragments.main.LeaderboardFragment;
import co.sisu.mobile.fragments.main.MoreFragment;
import co.sisu.mobile.fragments.main.RecordFragment;
import co.sisu.mobile.fragments.main.ScoreboardTileFragment;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FilterObject;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;
import co.sisu.mobile.system.SaveSharedPreference;
import co.sisu.mobile.utils.TileCreationHelper;
import co.sisu.mobile.utils.Utils;
import okhttp3.Response;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    private DataController dataController;
    private NavigationManager navigationManager;
    private DateManager dateManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private ActionBarManager actionBarManager;
    private ProgressBar parentLoader;
    private CacheManager cacheManager;
    private Utils utils;
    private TileCreationHelper tileCreationHelper;
    private boolean teamParamFinished = false;
    private boolean activitySettingsParamFinished = false;
    private boolean teamsFinished = false;
    private boolean tileTemplateFinished = false;
    private boolean scopeFinished = false;
    private boolean clientTilesFinished = false;
    private boolean marketStatusFinished = false;
    private boolean noNavigation = true;
    private boolean teamSwap = false;
    private boolean shouldDisplayPushNotification = false;
    private AgentModel agent;
    private NotesObject selectedNote;
    private FirebaseDeviceObject currentDevice;
    private ConstraintLayout layout;
    private ConstraintLayout paginateInfo;
    private Toolbar toolbar;
    private boolean isNoteFragment = false;
    private Gson gson;
    private String pushNotificationTitle = "";
    private String pushNotificationBody = "";
    private String pushNotificationIsHTML = "";
    private String pushNotificationPushId = "";
    private Fragment f;
    private boolean isAdminMode = false;

    private JSONObject tileTemplate;
    private JSONObject clientTiles;
    private JSONObject recordClientsList;
    private String recordClientListType;
    private boolean isAgentDashboard = true;
    private List<ScopeBarModel> scopeBarList = new ArrayList<>();
    private List<MarketStatusModel> marketStatusBar = new ArrayList<>();
    private ScopeBarModel currentScopeFilter = null;
    private MarketStatusModel currentMarketStatusFilter = null;
    private ImageView addClientButton;
    private FilterObject selectedFilter;

    private PopupMenu teamSelectorPopup;
    private String dashboardType = "agent";

    // TODO: I added a breakpoint on all the scope and market status filters to see if that race condition is gone.
    // TODO: There is a bug when you are in the message center and press the plus button, then press back.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        gson = new Gson();
        dataController = new DataController();
        colorSchemeManager = new ColorSchemeManager(this);
        navigationManager = new NavigationManager(this);
        cacheManager = new CacheManager();
        apiManager = new ApiManager(this, cacheManager);
        dateManager = new DateManager();
        utils = new Utils();
        tileCreationHelper = new TileCreationHelper(this);

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
        //TODO: Don't release with this uncommented, you fucktard.
        //MOCKING AN AGENT
//        agent.setAgent_id("49201"); // This is a good agent for color checking
//        dataController.setAgent(agent);
        //

        initParentFields();
        initButtons();
        initActionBar();

        noNavigation = true;
        dateManager.initTimelineDate();
        apiManager.getFirebaseDevices(this, agent.getAgent_id());
        apiManager.getTeams(this, agent.getAgent_id());
        FirebaseCrashlytics.getInstance().setCustomKey("agent_id", agent.getAgent_id());
    }

    private void initParentFields() {
        layout = findViewById(R.id.parentLayout);
        toolbar = findViewById(R.id.toolbar);
        paginateInfo = findViewById(R.id.paginateInfo);
        parentLoader = findViewById(R.id.parentLoader);
    }

    private void initActionBar() {
        actionBarManager = new ActionBarManager(this);
    }

    private void initButtons(){
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

        addClientButton = findViewById(R.id.addView);
        addClientButton.setOnClickListener(this);
    }

    private void initTeamSelectorPopup() {
        teamSelectorPopup = new PopupMenu(this, findViewById(R.id.team_icon));

        teamSelectorPopup.setOnMenuItemClickListener(item -> {
            TeamObject team = dataController.getTeamsObject().get(item.getItemId());
            dataController.setSelectedTeamObject(team);
            FragmentManager fragmentManager = getSupportFragmentManager();
            f = fragmentManager.findFragmentById(R.id.your_placeholder);
            this.runOnUiThread(() -> parentLoader.setVisibility(View.VISIBLE));
            sendTeamSwapApiCalls(team);
            return false;
        });

        int counter = 0;
        for(TeamObject teamObject : dataController.getTeamsObject()) {
            SpannableString s = new SpannableString(teamObject.getName());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getNormalText()), 0, s.length(), 0);
            teamSelectorPopup.getMenu().add(1, counter, counter, s);
            counter++;
        }
    }

    public void setActivityColors() {
        this.runOnUiThread(() -> {
            layout.setBackgroundColor(colorSchemeManager.getAppBackground());
            if(isAdminMode) {
                toolbar.setBackgroundColor(ContextCompat.getColor(ParentActivity.this, R.color.sisuYellow));
            }
            else {
                toolbar.setBackgroundColor(colorSchemeManager.getBottombarBackground());
            }

            VectorChildFinder plusVector = new VectorChildFinder(this, R.drawable.add_icon, addClientButton);
            VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
            plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
            plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
            addClientButton.invalidate();

            //change parentLoader here, if needed
            parentLoader = findViewById(R.id.parentLoader);
            // TODO: this could probably use the colorSchemeManager
            if(colorSchemeManager.getAppBackground() == Color.WHITE) {
                Rect bounds = parentLoader.getIndeterminateDrawable().getBounds();
                parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark, null));
                parentLoader.getIndeterminateDrawable().setBounds(bounds);
            } else {
                Rect bounds = parentLoader.getIndeterminateDrawable().getBounds();
                parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress, null));
                parentLoader.getIndeterminateDrawable().setBounds(bounds);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(!teamSwap && !noNavigation) {
            if(dataController.getUpdatedRecords().size() > 0) {
                updateRecordedActivities();
            }
            addClientButton.setVisibility(View.GONE);
            setSelectedClient(null);
            parentLoader.setVisibility(View.GONE);
            paginateInfo.setVisibility(View.GONE);
            switch (v.getId()) {
                case R.id.action_bar_home:
                case R.id.team_icon:
                case R.id.team_letter:
                    addClientButton.setVisibility(View.VISIBLE);
                    teamSelectorPopup.show();
                    break;
                case R.id.scoreboardView:
                    addClientButton.setVisibility(View.VISIBLE);
                    scopeFinished = true;
                    marketStatusFinished = true;
                    if(isRecruiting()) {
                        apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", currentScopeFilter.getIdValue());
                    }
                    else {
                        parentLoader.setVisibility(View.VISIBLE);
                        if(currentScopeFilter != null) {
                            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", currentScopeFilter.getIdValue());
                        }
                        else {
                            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + agent.getAgent_id());
                        }

                    }
                    break;
                case R.id.reportView:
                    noNavigation = false;
                    parentLoader.setVisibility(View.VISIBLE);
                    addClientButton.setVisibility(View.VISIBLE);
                    String selectedContextId = agent.getAgent_id();
                    if(currentScopeFilter != null) {
                        if(currentScopeFilter.getIdValue().charAt(0) == 'a') {
                            selectedContextId = currentScopeFilter.getIdValue().substring(1);
                        }
                    }
                    else {
                        Log.e("Garbage", "Garbage");
                    }

                    if(currentMarketStatusFilter != null) {
                        if(currentScopeFilter != null) {
                            apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), currentScopeFilter.getIdValue(), currentMarketStatusFilter.getKey() != null ? currentMarketStatusFilter.getKey() : "", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                        else {
                            apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), "a" + getAgent().getAgent_id(), currentMarketStatusFilter.getKey() != null ? currentMarketStatusFilter.getKey() : "", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                    }
                    else {
                        if(currentScopeFilter != null) {
                            apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), currentScopeFilter.getIdValue(),"", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                        else {
                            apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), "a" + getAgent().getAgent_id(),"", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                    }
                    apiManager.getMarketStatus(this, agent.getAgent_id(), dataController.getCurrentSelectedTeamMarketId());
                    break;
                case R.id.recordView:
                    noNavigation = false;
                    actionBarManager.setToSaveBar("Record");
                    navigationManager.clearStackReplaceFragment(RecordFragment.class);
                    break;
                case R.id.leaderBoardView:
                    noNavigation = false;
                    actionBarManager.setToTitleBar("Leaderboards", false);
                    navigationManager.clearStackReplaceFragment(LeaderboardFragment.class);
                    break;
                case R.id.moreView:
                    noNavigation = false;
                    actionBarManager.setToTitleBar("More", false);
                    navigationManager.clearStackReplaceFragment(MoreFragment.class);
                    break;
                case R.id.addView:
                    actionBarManager.setToSaveBar("Add Client");
                    navigationManager.stackReplaceFragment(ClientManageFragment.class);
                    break;
                default:
                    break;
            }
        }
    }

    public void updateRecordedActivities() {
        List<Metric> updatedRecords = dataController.getUpdatedRecords();
        List<UpdateActivitiesModel> updateActivitiesModels = new ArrayList<>();

        AsyncUpdateActivitiesJsonObject activitiesJsonObject = new AsyncUpdateActivitiesJsonObject();
        String currentSelectedDate = dateManager.getFormattedRecordDate();
        for(Metric m : updatedRecords) {
            updateActivitiesModels.add(new UpdateActivitiesModel(currentSelectedDate, m.getType(), m.getCurrentNum(), Integer.parseInt(agent.getAgent_id())));
        }
        UpdateActivitiesModel[] array = new UpdateActivitiesModel[updateActivitiesModels.size()];
        updateActivitiesModels.toArray(array);

        activitiesJsonObject.setActivities(array);

        apiManager.sendAsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject, dataController.getCurrentSelectedTeamMarketId());
    }

    private void sendTeamSwapApiCalls(@NonNull TeamObject team) {
        teamSwap = true;
        apiManager.getTeamParams(this, dataController.getAgent().getAgent_id(), team.getId());
        apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), team.getId(), dataController.getCurrentSelectedTeamMarketId());
        String dashboardType = "agent";
        if(!isAgentDashboard) {
            dashboardType = "team";
        }
        apiManager.getTileSetup(ParentActivity.this, dataController.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType);
    }

    private void executeTeamSwap() {
        if(teamParamFinished && activitySettingsParamFinished && tileTemplateFinished) {
            parentLoader.setVisibility(View.INVISIBLE);
//            clientFinished = false;
//            goalsFinished = false;
//            settingsFinished = false;
            teamParamFinished = false;
//            colorSchemeFinished = false;
//            labelsFinished = false;
            noNavigation = true;
            activitySettingsParamFinished = false;
            tileTemplateFinished = false;
            teamSwap = false;
            SaveSharedPreference.setLogo(this, colorSchemeManager.getLogo() == null ? "" : colorSchemeManager.getLogo());

            if (f.getTag() != null) {
                switch (f.getTag()) {
                    case "ScoreboardTileFragment":
                        ((ScoreboardTileFragment) f).teamSwap();
                        break;
                    case "Report":
                        ((ClientTileFragment) f).teamSwap();
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
    }

    private void navigateToScoreboard() {
        this.runOnUiThread(() -> {
            if(scopeFinished && tileTemplateFinished && marketStatusFinished) {
                if(getCurrentScopeFilter() != null) {
                    actionBarManager.setToTitleBar(getCurrentScopeFilter().getName(), true);
                }
                else {
                    actionBarManager.setToTitleBar("", true);
                }
                navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class);
                scopeFinished = false;
                tileTemplateFinished = false;
                marketStatusFinished = false;
                teamSwap = false;
                noNavigation = false;
            }

        });
        teamParamFinished = false;
        noNavigation = false;
        activitySettingsParamFinished = false;
        teamsFinished = false;
    }

    @Override
    public void onBackPressed() {
        navigationManager.onBackPressed();
        String currentFragment = navigationManager.getCurrentFragment();
        setSelectedClient(null);
        if(currentFragment != null) {
            if(currentFragment.equalsIgnoreCase("scoreboard") || currentFragment.equalsIgnoreCase("report")) {
                addClientButton.setVisibility(View.VISIBLE);
            }
            if(currentFragment.equalsIgnoreCase("MoreFragment")) {
                actionBarManager.setToTitleBar("More", false);
            }
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(teamSwap) {
            swappingTeamData(returnObject, returnType);
        }
        else {
            String returnString = null;
            try {
                returnString = ((Response) returnObject).body().string();
                if(returnString == null) {
                    // TODO: Should probably throw a custom error here.
                    throw new IOException("ReturnString broken for returnType: " + returnType.name());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(returnType == ApiReturnType.GET_TILES) {
                try {
                    tileTemplate =  new JSONObject(returnString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tileTemplateFinished = true;
                navigateToScoreboard();
            }
            else if(returnType == ApiReturnType.GET_TEAM_CLIENT_TILES) {
                try {
                    JSONObject newClientTiles = new JSONObject(returnString);
                    JSONObject pagination = newClientTiles.getJSONObject("pagination");

                    if(pagination.getInt("page") > 1) {
                        //append tiles
                        JSONArray currentClientTiles = clientTiles.getJSONArray("tile_rows");
                        JSONArray clientTilesToAppend = newClientTiles.getJSONArray("tile_rows");

                        for(int i = 0; i < clientTilesToAppend.length(); i++) {
                            JSONObject tileObject = clientTilesToAppend.getJSONObject(i);
                            JSONArray currentTiles = tileObject.getJSONArray("tiles");
                            JSONObject tile = currentTiles.getJSONObject(0);
                            if(tile.has("type")) {
                                String type = tile.getString("type");
                                switch (type) {
                                    case "clientList":
                                        currentClientTiles.put(tileObject);
                                        break;
                                    case "smallHeader":
                                        break;
                                    default:
                                        Log.e("TYPE", type);
                                        break;
                                }
                            }
                        }
                        clientTiles.put("tile_rows", currentClientTiles);
                        clientTiles.put("pagination", pagination);
                        clientTiles.put("count", currentClientTiles.length());
                    }
                    else {
                        //overwrite tiles
                        clientTiles = newClientTiles;
                    }

                    clientTilesFinished = true;
                    if(marketStatusFinished) {
                        if(getCurrentScopeFilter() != null) {
                            actionBarManager.setToFilterBar(getCurrentScopeFilter().getName());
                        }
                        else {
                            actionBarManager.setToFilterBar("");
                        }
                        navigationManager.clearStackReplaceFragment(ClientTileFragment.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if(returnType == ApiReturnType.GET_MARKET_STATUS) {
                try {
                    JSONObject marketStatusObject = new JSONObject(returnString);
                    try {
                        JSONArray marketStatuses = marketStatusObject.getJSONArray("client_status");
                        for(int k = 0; k < marketStatuses.length(); k++) {
                            JSONObject currentMarketStatus = (JSONObject) marketStatuses.get(k);
                            MarketStatusModel currentModel = new MarketStatusModel(currentMarketStatus.getString("key"), currentMarketStatus.getString("label"), currentMarketStatus.getBoolean("select"));
                            marketStatusBar.add(currentModel);
                            if(currentModel.getKey().equalsIgnoreCase("")) {
                                currentMarketStatusFilter = currentModel;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    marketStatusFinished = true;
                    navigateToScoreboard();
                    if(clientTilesFinished) {
                        if(getCurrentScopeFilter() != null) {
                            actionBarManager.setToFilterBar(getCurrentScopeFilter().getName());
                        }
                        else {
                            actionBarManager.setToFilterBar("");
                        }
                        navigationManager.clearStackReplaceFragment(ClientTileFragment.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if(returnType == ApiReturnType.UPDATE_ACTIVITIES) {
                dataController.clearUpdatedRecords();
            }
            else if(returnType == ApiReturnType.GET_ACTIVITY_SETTINGS) {
                // TODO: I don't think I need this here at all anymore. This can be in the activitySettingsFragment
                try {
                    JSONObject settingsObject = new JSONObject(returnString);
                    dataController.setActivitiesSelected(settingsObject.getJSONArray("record_activities"));
                    activitySettingsParamFinished = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(returnType == ApiReturnType.GET_SETTINGS) {
                try {
                    JSONObject settingsJson = new JSONObject(returnString);
                    JSONArray settings = settingsJson.getJSONArray("parameters");
                    dataController.setSettings(settings); //sets settings, and fills with default alarm notification if empty/not set yet
                    List<ParameterObject> newSettings = dataController.getSettings(); //this is the new settings object list including any defaults generated
//                settingsFinished = true;
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
                        utils.createNotificationAlarm(hour, minute, null, this); //sets the actual alarm with correct times from user settings
                    }
                    // TODO: Don't need to check if teamsFinished anymore I think
                    if(teamsFinished) {
                        apiManager.getLabels(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
                    }
//                navigateToScoreboard();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if(returnType == ApiReturnType.GET_TEAMS) {
                try {
                    JSONObject teamsObject = new JSONObject(returnString);
                    dataController.setTeamsObject(teamsObject.getJSONArray("teams"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.runOnUiThread(() -> {
                    actionBarManager.initTeamBar();
                    initTeamSelectorPopup();
                    if(dataController.getTeamsObject().size() > 0) {
                        dataController.setSelectedTeamObject(dataController.getTeamsObject().get(0));
                        dataController.setMessageCenterVisible(true);
                        FirebaseCrashlytics.getInstance().setCustomKey("team_id", dataController.getCurrentSelectedTeamId());
                        // TODO: I don't need/want the team params to be a race condition
                        apiManager.getTeamParams(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeam().getId());
                        SaveSharedPreference.setTeam(ParentActivity.this, dataController.getCurrentSelectedTeam().getId() + "");
                    }
                    else {
                        dataController.setMessageCenterVisible(false);
                        teamParamFinished = true;
                        dataController.setSlackInfo(null);
                    }
                    teamsFinished = true;
                    scopeFinished = false;
                    apiManager.getScope(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
                    apiManager.getMarketStatus(this, agent.getAgent_id(), dataController.getCurrentSelectedTeamMarketId());
                    apiManager.getSettings(ParentActivity.this, agent.getAgent_id());
                    apiManager.getLabels(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
                    //TODO: Could probably get activity settings later (record or activitySettings page). 5/28/21 - I think activitySettings page.
                    apiManager.getActivitySettings(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dataController.getCurrentSelectedTeamMarketId());
                    apiManager.getTileSetup(ParentActivity.this, dataController.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + agent.getAgent_id());
                });
            }
            else if(returnType == ApiReturnType.GET_SCOPE) {
                try {
                    scopeBarList = new ArrayList<>();
                    JSONObject scopes = new JSONObject(returnString);
                    JSONObject allScopes = scopes.getJSONObject("scopes");

                    if(allScopes.has("team")) {
                        JSONObject scopeTeam = allScopes.getJSONObject("team");
                        scopeBarList.add(new ScopeBarModel(scopeTeam.getString("display_name"), "t" + scopeTeam.getString("team_id")));
                    }
                    if(allScopes.has("groups")) {
                        JSONArray scopeGroups = allScopes.getJSONArray("groups");
                        scopeBarList.add(new ScopeBarModel("-- Groups --", "Groups"));

                        for(int i = 0; i < scopeGroups.length(); i++) {
                            JSONObject currentGroup = (JSONObject) scopeGroups.get(i);
                            scopeBarList.add(new ScopeBarModel(currentGroup.getString("display_name"), "g" + currentGroup.getString("group_id")));
                        }
                    }
                    if(allScopes.has("agents")) {
                        JSONArray scopeAgents = allScopes.getJSONArray("agents");
                        scopeBarList.add(new ScopeBarModel("-- Agents --", "Groups"));

                        for(int i = 0; i < scopeAgents.length(); i++) {
                            JSONObject currentAgent = (JSONObject) scopeAgents.get(i);
                            if(currentAgent.getString("agent_id").equalsIgnoreCase(getAgent().getAgent_id())) {
                                ScopeBarModel agentScope = new ScopeBarModel(currentAgent.getString("display_name"), "a" + currentAgent.getString("agent_id"));
                                scopeBarList.add(0, agentScope);
                                if(currentScopeFilter == null) {
                                    currentScopeFilter = agentScope;
                                }
                                actionBarManager.setTitle(currentScopeFilter.getName());
                                continue;
                            }
                            else {
                                scopeBarList.add(new ScopeBarModel(currentAgent.getString("display_name"), "a" + currentAgent.getString("agent_id")));
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                scopeFinished = true;
                navigateToScoreboard();
            }
            else if(returnType == ApiReturnType.GET_TEAM_PARAMS) {
                try {
                    JSONObject teamParamsObject = new JSONObject(returnString);
                    if(teamParamsObject.getString("status_code").equals("-1")) {
                        dataController.setSlackInfo(null);
                    }
                    else {
                        ParameterObject params = new ParameterObject(teamParamsObject.getJSONObject("parameter"));
                        dataController.setSlackInfo(params.getValue());
                    }
                    teamParamFinished = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(returnType == ApiReturnType.GET_FIREBASE_DEVICES) {
                try {
                    JSONObject firebaseObject = new JSONObject(returnString);
                    JSONArray devices = firebaseObject.getJSONArray("devices");
                    String firebaseDeviceId = SaveSharedPreference.getFirebaseDeviceId(this);

                    for(int i = 0; i < devices.length(); i++) {
                        FirebaseDeviceObject currentDevice = new FirebaseDeviceObject(devices.getJSONObject(i));
                        if(currentDevice.getDevice_id() != null && currentDevice.getDevice_id().equals(firebaseDeviceId)) {
                            Log.e("Current Device", currentDevice.getDevice_id());
                            this.currentDevice = currentDevice;
                        }
                    }

                    MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService(apiManager, dataController.getAgent(), this.getApplicationContext(), currentDevice);

                    if(firebaseDeviceId.equals("") || this.currentDevice == null) {
                        myFirebaseMessagingService.initFirebase();
                    }
                    else {
                        myFirebaseMessagingService.refreshToken();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(returnType == ApiReturnType.GET_LABELS) {
                try {
                    JSONObject labelsObject = new JSONObject(returnString);
                    JSONObject marketLabels = labelsObject.getJSONObject("market");
                    HashMap<String, String> labels = new LinkedHashMap<>();
                    Iterator<String> keys = marketLabels.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        String value = marketLabels.getString(key);
                        labels.put(key, value);
                    }
                    dataController.setLabels(labels);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void swappingTeamData(Object returnObject, ApiReturnType asyncReturnType) {
        if(asyncReturnType == ApiReturnType.GET_ACTIVITY_SETTINGS) {
            // TODO: I don't think I need this here at all anymore. This can be in the activitySettingsFragment
            try {
                String returnString = ((Response) returnObject).body().string();
                JSONObject settingsObject = new JSONObject(returnString);
                dataController.setActivitiesSelected(settingsObject.getJSONArray("record_activities"));
                activitySettingsParamFinished = true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        else if(asyncReturnType == ApiReturnType.GET_TEAM_PARAMS) {
            String returnString;
            try {
                returnString = ((Response) returnObject).body().string();
                JSONObject teamParamsObject = new JSONObject(returnString);
                if(teamParamsObject.getString("status_code").equals("-1")) {
                    dataController.setSlackInfo(null);
                }
                else {
                    ParameterObject params = new ParameterObject(teamParamsObject.getJSONObject("parameter"));
                    dataController.setSlackInfo(params.getValue());
                }
                teamParamFinished = true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        else if(asyncReturnType == ApiReturnType.GET_LABELS) {
            try {
                String returnString = ((Response) returnObject).body().string();
                JSONObject labelsObject = new JSONObject(returnString);
                JSONObject marketLabels = labelsObject.getJSONObject("market");
                HashMap<String, String> labels = new LinkedHashMap<>();
                Iterator<String> keys = marketLabels.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    String value = marketLabels.getString(key);
                    labels.put(key, value);
                }
                dataController.setLabels(labels);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
        else if(asyncReturnType == ApiReturnType.GET_TILES) {
            try {
                String tileString = ((Response) returnObject).body().string();
                tileTemplate =  new JSONObject(tileString);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            tileTemplateFinished = true;
        }
        this.runOnUiThread(this::executeTeamSwap);
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        Log.e("FAILURE", asyncReturnType);
    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {
        Log.e("FAILURE", returnType.name());
    }

    public void resetClientTiles(String clientSearch, int page) {
        parentLoader.setVisibility(View.VISIBLE);
        marketStatusFinished = true;
        String selectedContextId = agent.getAgent_id();
        if(currentScopeFilter != null) {
            if(currentScopeFilter.getIdValue().charAt(0) == 'a') {
                selectedContextId = currentScopeFilter.getIdValue().substring(1);
            }
            actionBarManager.setTitle(currentScopeFilter.getName());
        }
        else {
            Log.e("Garage", "Garbage");
        }

        if(currentMarketStatusFilter != null) {
            if(currentScopeFilter != null) {
                apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), currentScopeFilter.getIdValue(), currentMarketStatusFilter.getKey(), clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
            else {
                apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), selectedContextId, currentMarketStatusFilter.getKey(), clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
        }
        else {
            if(currentScopeFilter != null) {
                apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), currentScopeFilter.getIdValue(), "", clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
            else {
                apiManager.getTeamClients(this, selectedContextId, dataController.getCurrentSelectedTeamId(), "a" + getAgent().getAgent_id(), "", clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
        }
    }

    public void resetClientTilesPresetFilter(JSONObject filters, int page) {
        parentLoader.setVisibility(View.VISIBLE);
        apiManager.getTeamClientsPresetFilter(this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), filters, page);
    }

    public void resetDashboardTiles(boolean scopeSelected) {
        parentLoader.setVisibility(View.VISIBLE);
        if(currentScopeFilter != null) {
            actionBarManager.setTitle(currentScopeFilter.getName());
        }
        else {
            Log.e("Garbage", "Garbage");
        }
        tileTemplateFinished = false;
        if(scopeSelected) {
            scopeFinished = true;
            marketStatusFinished = true;
        }
        else {
            scopeFinished = false;
            marketStatusFinished = false;
            apiManager.getScope(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
            apiManager.getMarketStatus(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
        }

        if(currentScopeFilter != null) {
            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", currentScopeFilter.getIdValue());
        }
        else {
            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + agent.getAgent_id());
        }
    }


    public void updateColorScheme(ColorSchemeManager colorSchemeManager) {
        this.colorSchemeManager = colorSchemeManager;
        initTeamSelectorPopup();
        setActivityColors();
        navigationManager.updateColorSchemeManager(colorSchemeManager);
        actionBarManager.updateColorSchemeManager(colorSchemeManager);
        tileCreationHelper.updateColorScheme(colorSchemeManager);
    }

    // GETTERS AND SETTERS

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public DataController getDataController() {
        return dataController;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public ColorSchemeManager getColorSchemeManager() {
        return colorSchemeManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public Utils getUtils() {
        return utils;
    }

    public TileCreationHelper getTileCreationHelper() {
        return tileCreationHelper;
    }

    public DateManager getDateManager() {
        return dateManager;
    }

    // TODO: Almost every single one of these should be moved to the dataController

    public String getDashboardType() {
        return dashboardType;
    }

    public void setDashboardType(String dashboardType) {
        this.dashboardType = dashboardType;
    }

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
        dataController.setSelectedClient(client);
//        navigationManager.setSelectedClient(client);
    }

    public ClientObject getSelectedClient() {
        return dataController.getSelectedClient();
//        return navigationManager.getSelectedClient();
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
        isNoteFragment = !type.equals("Message");
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

//    public String getPushNotificationPushId() {
//        return pushNotificationPushId;
//    }

    public void setShouldDisplayPushNotification(boolean b) {
        shouldDisplayPushNotification = b;
    }

    public boolean isTeamSwapFinished() {
        return !teamSwap;
    }

    public boolean isRecruiting() {
        return dataController.getCurrentSelectedTeamMarketId() == 2;
    }

    public boolean isAdminMode() {
        return isAdminMode;
    }

    public AgentModel getAgent() {
        return dataController.getAgent();
    }

    public JSONObject getTileTemplate() {
        return tileTemplate;
    }

    public JSONObject getClientTiles() {
        return clientTiles;
    }

    public List<MarketStatusModel> getMarketStatuses() {
        return marketStatusBar;
    }

    public void setTileTemplate(JSONObject tileTemplate) {
        this.tileTemplate = tileTemplate;
    }

    public boolean isAgentDashboard() {
        return isAgentDashboard;
    }

    public void setAgentDashboard(boolean agentDashboard) {
        isAgentDashboard = agentDashboard;
    }

    public List<ScopeBarModel> getScopeBarList() {
        return scopeBarList;
    }

    public void setScopeFilter(ScopeBarModel selectedScope) {
        currentScopeFilter = selectedScope;
    }

    public ScopeBarModel getCurrentScopeFilter() {
        return currentScopeFilter;
    }

    public MarketStatusModel getCurrentMarketStatusFilter() {
        return currentMarketStatusFilter;
    }

    public void setCurrentMarketStatusFilter(MarketStatusModel currentMarketStatusFilter) {
        this.currentMarketStatusFilter = currentMarketStatusFilter;
    }


    public JSONObject getRecordClientsList() {
        return recordClientsList;
    }

    public void setRecordClientsList(JSONObject recordClientsList) {
        this.recordClientsList = recordClientsList;
    }

    public String getRecordClientListType() {
        return recordClientListType;
    }

    public void setRecordClientListType(String recordClientListType) {
        this.recordClientListType = recordClientListType;
    }

    public FilterObject getSelectedFilter() {
        return selectedFilter;
    }

    public void setSelectedFilter(FilterObject selectedFilter) {
        this.selectedFilter = selectedFilter;
    }

}

