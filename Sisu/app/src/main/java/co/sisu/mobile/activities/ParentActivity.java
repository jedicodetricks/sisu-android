package co.sisu.mobile.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import co.sisu.mobile.controllers.NotificationReceiver;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.fragments.ClientManageFragment;
import co.sisu.mobile.fragments.main.ClientTileFragment;
import co.sisu.mobile.fragments.main.LeaderboardFragment;
import co.sisu.mobile.fragments.main.MoreFragment;
import co.sisu.mobile.fragments.main.RecordFragment;
import co.sisu.mobile.fragments.main.ScoreboardTileFragment;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.AsyncAgentJsonStringSuperUserObject;
import co.sisu.mobile.models.AsyncClientJsonObject;
import co.sisu.mobile.models.AsyncFirebaseDeviceJsonObject;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncLabelsJsonObject;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FilterObject;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.models.TeamColorSchemeObject;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;
import co.sisu.mobile.oldFragments.ScoreboardFragment;
import co.sisu.mobile.system.SaveSharedPreference;
import okhttp3.Response;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AsyncServerEventListener {

    private DataController dataController;
    private NavigationManager navigationManager;
    private DateManager dateManager;
    private ApiManager apiManager;
    public ColorSchemeManager colorSchemeManager;
    public ActionBarManager actionBarManager;
    public ProgressBar parentLoader;
    public CacheManager cacheManager;
//    private boolean clientFinished = false;
//    private boolean goalsFinished = false;
    private boolean teamParamFinished = false;
    private boolean activitySettingsParamFinished = false;
//    private boolean settingsFinished = false;
//    private boolean colorSchemeFinished = false;
    private boolean teamsFinished = false;
//    private boolean labelsFinished = false;
    private boolean tileTemplateFinished = false;
    private boolean scopeFinished = false;
    private boolean clientTilesFinished = false;
    private boolean marketStatusFinished = false;
    private boolean noNavigation = true;
    private boolean teamSwap = false;
    private boolean shouldDisplayPushNotification = false;
    private AgentModel agent;
    private NotesObject selectedNote;
//    private LruCache<String, Bitmap> mMemoryCache;
    private boolean imageIsExpanded = false;
    private ImageView expanded;
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
    private Animator mCurrentAnimator;
    private float startScale;
    private final Rect startBounds = new Rect();
    private final Rect finalBounds = new Rect();
    private final Point globalOffset = new Point();
    private int mShortAnimationDuration;
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

    // TODO: I added a breakpoint on all the scope and market status filters to see if that race condition is gone.
    // TODO: I should create an enum with the fragment names so that I only have to change it in one spot in the future if I change a fragment name
    // TODO: The old Scoreboard fragment might still be getting called. Gotta fix that.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        gson = new Gson();
        dataController = new DataController();
        colorSchemeManager = new ColorSchemeManager();
        navigationManager = new NavigationManager(this);
        cacheManager = new CacheManager();
        apiManager = new ApiManager(this, cacheManager);
        dateManager = new DateManager();
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

    public void setActivityColors() {
        this.runOnUiThread(() -> {
            layout.setBackgroundColor(colorSchemeManager.getAppBackground());
            if(isAdminMode) {
                toolbar.setBackgroundColor(ContextCompat.getColor(ParentActivity.this, R.color.colorYellow));
            }
            else {
                toolbar.setBackgroundColor(colorSchemeManager.getMenuBackground());
            }

            VectorChildFinder plusVector = new VectorChildFinder(this, R.drawable.add_icon, addClientButton);
            VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
            plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
            plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
            addClientButton.invalidate();

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
        });
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
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getNormalTextColor()), 0, s.length(), 0);
            teamSelectorPopup.getMenu().add(1, counter, counter, s);
            counter++;
        }
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
                        apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", currentScopeFilter.getIdValue());
                    }
                    else {
                        parentLoader.setVisibility(View.VISIBLE);
                        if(currentScopeFilter != null) {
                            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", currentScopeFilter.getIdValue());
                        }
                        else {
                            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + agent.getAgent_id());
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
                            apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), currentScopeFilter.getIdValue(), currentMarketStatusFilter.getKey() != null ? currentMarketStatusFilter.getKey() : "", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                        else {
                            apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), "a" + getAgent().getAgent_id(), currentMarketStatusFilter.getKey() != null ? currentMarketStatusFilter.getKey() : "", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                    }
                    else {
                        if(currentScopeFilter != null) {
                            apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), currentScopeFilter.getIdValue(),"", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                        else {
                            apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), "a" + getAgent().getAgent_id(),"", "", 1, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
                        }
                    }
                    apiManager.getMarketStatus(this, agent.getAgent_id(), getSelectedTeamMarketId());
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
                case R.id.cancelButton:
                    navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
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

        apiManager.sendAsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject, getSelectedTeamMarketId());
    }

    private void sendTeamSwapApiCalls(TeamObject team) {
        teamSwap = true;
        apiManager.getTeamParams(this, dataController.getAgent().getAgent_id(), team.getId());
        apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), team.getId(), getSelectedTeamMarketId());
        String dashboardType = "agent";
        if(!isAgentDashboard) {
            dashboardType = "team";
        }
        apiManager.getTileSetup(ParentActivity.this, dataController.getAgent().getAgent_id(), getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType);
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

    private void navigateToScoreboard(boolean b) {
        this.runOnUiThread(() -> {
            if(scopeFinished && tileTemplateFinished && marketStatusFinished) {
                if(getCurrentScopeFilter() != null) {
                    actionBarManager.setToTitleBar(getCurrentScopeFilter().getName(), true);
                    navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class, getCurrentScopeFilter().getName());
                }
                else {
                    actionBarManager.setToTitleBar("", true);
                    navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class, "");
                }
                scopeFinished = false;
                tileTemplateFinished = false;
                marketStatusFinished = false;
                teamSwap = false;
                noNavigation = false;
            }

        });
//        clientFinished = false;
//        goalsFinished = false;
//        settingsFinished = false;
        teamParamFinished = false;
//        colorSchemeFinished = false;
//        labelsFinished = false;
        noNavigation = false;
        activitySettingsParamFinished = false;
        teamsFinished = false;
    }

//    private void navigateToScoreboard() {
        // TODO: I think this is deprecated and we don't need it anymore. Commenting to see
//        if(teamsFinished && clientFinished && goalsFinished && settingsFinished && teamParamFinished && colorSchemeFinished && labelsFinished && activitySettingsParamFinished && noNavigation && !adminTransferring && tileTemplateFinished && scopeFinished && marketStatusFinished) {
//            this.runOnUiThread(() -> navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class, getCurrentScopeFilter().getName()));
//            clientFinished = false;
//            goalsFinished = false;
//            settingsFinished = false;
//            teamParamFinished = false;
//            colorSchemeFinished = false;
//            labelsFinished = false;
//            noNavigation = true;
//            activitySettingsParamFinished = false;
//            tileTemplateFinished = false;
//            teamsFinished = false;
//        }
//        else {
//            if(adminTransferring) {
//                if(clientFinished && goalsFinished && activitySettingsParamFinished && settingsFinished && teamParamFinished && noNavigation && tileTemplateFinished) {
//                    clientFinished = false;
//                    goalsFinished = false;
//                    settingsFinished = false;
//                    teamParamFinished = false;
//                    colorSchemeFinished = false;
//                    labelsFinished = false;
//                    noNavigation = true;
//                    activitySettingsParamFinished = false;
//                    tileTemplateFinished = false;
//
//                    switch (navigationManager.getCurrentFragment()) {
//                        case "Scoreboard":
//                            noNavigation = false;
//                            adminTransferring = false;
//                            System.out.println(navigationManager.getCurrentFragment());
//                            this.runOnUiThread(() -> {
//                                if(tileDebug) {
//                                    navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class, getCurrentScopeFilter().getName());
//                                }
//                                else {
//                                    if(isRecruiting()) {
//                                        navigationManager.clearStackReplaceFragment(RecruitingScoreboardFragment.class);
//                                    }
//                                    else {
//                                        navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
//                                    }
//                                }
//                            });
//                            break;
//                        case "Report":
//                            noNavigation = false;
//                            adminTransferring = false;
//                            System.out.println(navigationManager.getCurrentFragment());
//                            this.runOnUiThread(() -> navigationManager.clearStackReplaceFragment(ReportFragment.class));
//                            break;
//                        case "Record":
//                            noNavigation = false;
//                            adminTransferring = false;
//                            System.out.println(navigationManager.getCurrentFragment());
//                            this.runOnUiThread(() -> navigationManager.clearStackReplaceFragment(RecordFragment.class));
//                            break;
//                        case "Leaderboard":
//                            noNavigation = false;
//                            adminTransferring = false;
//                            System.out.println(navigationManager.getCurrentFragment());
//                            this.runOnUiThread(() -> navigationManager.clearStackReplaceFragment(LeaderboardFragment.class));
//                            break;
//                        case "More":
//                            noNavigation = false;
//                            adminTransferring = false;
//                            System.out.println(navigationManager.getCurrentFragment());
//                            this.runOnUiThread(() -> {
//                                navigationManager.clearStackReplaceFragment(MoreFragment.class);
//                                parentLoader.setVisibility(View.INVISIBLE);
//                            });
//                            break;
//                    }
//                    System.out.println(navigationManager.getCurrentFragment());
//                }
//            }
//        }
//    }

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

    public void showToast(final CharSequence msg) {
        // TODO: This feels like a util
        this.runOnUiThread(() -> {
            Toast toast = Toast.makeText(ParentActivity.this, msg,Toast.LENGTH_SHORT);
            View view = toast.getView();
            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorCorporateOrange));
            view.setBackgroundResource(R.color.colorCorporateOrange);
            text.setPadding(20, 8, 20, 8);
            toast.show();
        });
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(teamSwap) {
            swappingTeamData(returnObject, returnType);
        }
        else if(returnType == ApiReturnTypes.GET_TILES) {
            try {
                String tileString = ((Response) returnObject).body().string();
                tileTemplate =  new JSONObject(tileString);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            tileTemplateFinished = true;
            navigateToScoreboard(true);
        }
        else if(returnType == ApiReturnTypes.GET_TEAM_CLIENT_TILES) {
            try {
                String tileString = ((Response) returnObject).body().string();
                JSONObject newClientTiles = new JSONObject(tileString);
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
                    Log.e("TYPE", "ee");
                }
                else {
                    //overwrite tiles
                    clientTiles = newClientTiles;
                }

                clientTilesFinished = true;
                if(marketStatusFinished) {
                    if(getCurrentScopeFilter() != null) {
                        actionBarManager.setToFilterBar(getCurrentScopeFilter().getName());
                        navigationManager.clearStackReplaceFragment(ClientTileFragment.class, getCurrentScopeFilter().getName());
                    }
                    else {
                        actionBarManager.setToFilterBar("");
                        navigationManager.clearStackReplaceFragment(ClientTileFragment.class, "");
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
        else if(returnType == ApiReturnTypes.GET_MARKET_STATUS) {
            try {
                String tileString = ((Response) returnObject).body().string();

                JSONObject marketStatusObject = new JSONObject(tileString);
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
                navigateToScoreboard(false);
                if(clientTilesFinished) {
                    if(getCurrentScopeFilter() != null) {
                        actionBarManager.setToFilterBar(getCurrentScopeFilter().getName());
                        navigationManager.clearStackReplaceFragment(ClientTileFragment.class, getCurrentScopeFilter().getName());
                    }
                    else {
                        actionBarManager.setToFilterBar("");
                        navigationManager.clearStackReplaceFragment(ClientTileFragment.class, "");
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
        else if(returnType == ApiReturnTypes.UPDATE_ACTIVITIES) {
            dataController.clearUpdatedRecords();
        }
        else if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            AsyncActivitySettingsJsonObject settingsObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncActivitySettingsJsonObject.class);
            AsyncActivitySettingsObject[] settings = settingsObject.getRecord_activities();

            dataController.setActivitiesSelected(settings);
            activitySettingsParamFinished = true;
//            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_AGENT_GOALS) {
            // TODO: I think this is deprecated. Leaving a breakpoint to see if it ever shows up
            // It does show up but I don't think I need it to. Will have to check back.
            AsyncGoalsJsonObject goals = gson.fromJson(((Response) returnObject).body().charStream(), AsyncGoalsJsonObject.class);
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject, isRecruiting());
//            goalsFinished = true;
//            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_SETTINGS) {
            try {
                String settingsString = ((Response) returnObject).body().string();
                JSONObject settingsJson = new JSONObject(settingsString);
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
                    createNotificationAlarm(hour, minute, null); //sets the actual alarm with correct times from user settings
                }
                // TODO: Don't need to check if teamsFinished anymore I think
                if(teamsFinished) {
                    //TODO: Probably don't need either of these
//                    apiManager.getColorScheme(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), dataController.getColorSchemeId());
//                    colorSchemeFinished = true;
                    apiManager.getLabels(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
                }
//                navigateToScoreboard();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
        else if(returnType == ApiReturnTypes.GET_TEAMS) {
            AsyncTeamsJsonObject teamsObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncTeamsJsonObject.class);
            dataController.setTeamsObject(ParentActivity.this, teamsObject);
            this.runOnUiThread(() -> {
                actionBarManager.initTeamBar();
                initTeamSelectorPopup();
                if(dataController.getTeamsObject().size() > 0) {
                    dataController.setSelectedTeamObject(dataController.getTeamsObject().get(0));
                    dataController.setMessageCenterVisible(true);
                    // TODO: I don't need/want the team params to be a race condition
                    apiManager.getTeamParams(ParentActivity.this, agent.getAgent_id(), dataController.getSelectedTeamObject().getId());
                    //TODO: Probably don't need to getClients now
//                    apiManager.getClients(ParentActivity.this, agent.getAgent_id(), getSelectedTeamMarketId());
//                    clientFinished = true;
                    SaveSharedPreference.setTeam(ParentActivity.this, dataController.getSelectedTeamObject().getId() + "");
//                    colorSchemeFinished = true;
//                    labelsFinished = true;
                }
                else {
                    //TODO: Probably don't need to getClients now
//                    apiManager.getClients(ParentActivity.this, agent.getAgent_id(), getSelectedTeamMarketId());
//                    clientFinished = true;
                    dataController.setMessageCenterVisible(false);
                    teamParamFinished = true;
                    dataController.setSlackInfo(null);
                }
                teamsFinished = true;
                scopeFinished = false;
                apiManager.getScope(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());

                apiManager.getMarketStatus(this, agent.getAgent_id(), getSelectedTeamMarketId());

                //TODO: I don't think I need goals anymore, that's passed in with the tiles I think Update: 8/30/20 I no longer think that's true but I can probably get it later now.
                apiManager.getAgentGoals(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
                apiManager.getSettings(ParentActivity.this, agent.getAgent_id());
                //TODO: Could probably get activity settings later (record or settings page). Or maybe never as of 12/22/20
                apiManager.getActivitySettings(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), getSelectedTeamMarketId());
//                apiManager.getTeamAgents(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
                apiManager.getTileSetup(ParentActivity.this, dataController.getAgent().getAgent_id(), getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + agent.getAgent_id());
            });
        }
        else if(returnType == ApiReturnTypes.GET_SCOPE) {
            try {
                String tileString = ((Response) returnObject).body().string();
                scopeBarList = new ArrayList<>();
                JSONObject scopes = new JSONObject(tileString);
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
                            break;
                        }
                        else {
                            scopeBarList.add(new ScopeBarModel(currentAgent.getString("display_name"), "a" + currentAgent.getString("agent_id")));
                        }
                    }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            scopeFinished = true;
            navigateToScoreboard(true);
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
//            navigateToScoreboard();
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
            MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService(apiManager, dataController.getAgent(), this.getApplicationContext(), currentDevice);

            if(firebaseDeviceId.equals("")) {
                myFirebaseMessagingService.initFirebase();
            }
            else {
                myFirebaseMessagingService.refreshToken();
            }
        }
        else if(returnType == ApiReturnTypes.GET_COLOR_SCHEME) {
            // TODO: I think this is deprecated. Leaving a breakpoint to see if it ever shows up
            AsyncTeamColorSchemeObject colorJson = gson.fromJson(((Response) returnObject).body().charStream(), AsyncTeamColorSchemeObject.class);
            TeamColorSchemeObject[] colorScheme = colorJson.getTheme();
            colorSchemeManager.setColorScheme(colorScheme, dataController.getColorSchemeId());
            setActivityColors();
//                navigationManager.getActionBarManager().updateColorSchemeManager(colorSchemeManager);
//                colorSchemeFinished = true;
            SaveSharedPreference.setLogo(this, colorSchemeManager.getLogo() == null ? "" : colorSchemeManager.getLogo());
//                navigateToScoreboard();

        }
        else if(returnType == ApiReturnTypes.GET_LABELS) {
            // TODO: I think this is deprecated. Leaving a breakpoint to see if it ever shows up
            AsyncLabelsJsonObject labelObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncLabelsJsonObject.class);
            HashMap<String, String> labels = labelObject.getMarket();
            dataController.setLabels(labels);
//            labelsFinished = true;
//            navigateToScoreboard();
        }
        else if(returnType == ApiReturnTypes.GET_AGENT) {
            // TODO: I think this is deprecated. Leaving a breakpoint to see if it ever shows up
            boolean adminTransferring = true;
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
//            goalsFinished = true;
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
//            colorSchemeFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_LABELS) {
            AsyncLabelsJsonObject labelObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncLabelsJsonObject.class);
            HashMap<String, String> labels = labelObject.getMarket();
            dataController.setLabels(labels);
//            labelsFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_CLIENTS) {
            AsyncClientJsonObject clientObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncClientJsonObject.class);
            dataController.setClientListObject(clientObject, isRecruiting());
//            clientFinished = true;
        }
        else if(asyncReturnType == ApiReturnTypes.GET_TILES) {
            try {
                String tileString = ((Response) returnObject).body().string();
                tileTemplate =  new JSONObject(tileString);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            tileTemplateFinished = true;
        }
        this.runOnUiThread(() -> executeTeamSwap());
}

    public void createNotificationAlarm(int currentSelectedHour, int currentSelectedMinute, PendingIntent pendingIntent) {
        // TODO: This feels like a util
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
    }

    public void zoomImageFromThumb(View convertView, final View thumbView, Bitmap bmp) {
        // TODO: This feels like a util
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
            expanded.setOnClickListener(view -> unzoomImageFromThumbnail());
        }
    }

    public void unzoomImageFromThumbnail() {
        // TODO: This feels like a util
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
                apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), currentScopeFilter.getIdValue(), currentMarketStatusFilter.getKey(), clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
            else {
                apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), selectedContextId, currentMarketStatusFilter.getKey(), clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
        }
        else {
            if(currentScopeFilter != null) {
                apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), currentScopeFilter.getIdValue(), "", clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
            else {
                apiManager.getTeamClients(this, selectedContextId, getSelectedTeamId(), "a" + getAgent().getAgent_id(), "", clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
            }
        }
    }

    public void resetClientTilesPresetFilter(JSONObject filters, int page) {
        parentLoader.setVisibility(View.VISIBLE);
        apiManager.getTeamClientsPresetFilter(this, agent.getAgent_id(), getSelectedTeamId(), filters, page);
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
            apiManager.getScope(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
            apiManager.getMarketStatus(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId());
        }

        if(currentScopeFilter != null) {
            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", currentScopeFilter.getIdValue());
        }
        else {
            apiManager.getTileSetup(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + agent.getAgent_id());
        }
    }


    public void updateColorScheme(ColorSchemeManager colorSchemeManager) {
        this.colorSchemeManager = colorSchemeManager;
        initTeamSelectorPopup();
        setActivityColors();
        navigationManager.updateColorSchemeManager(colorSchemeManager);
//        navigationManager.getActionBarManager().updateColorSchemeManager(colorSchemeManager);
    }

    // GETTERS AND SETTERS
    // TODO: Almost every single one of these should be moved to the dataController
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

    public int getSelectedTeamId() {
        return dataController.getSelectedTeamObject().getId();
    }

    public int getSelectedTeamMarketId() {
        return dataController.getSelectedTeamObject().getMarket_id();
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

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public ColorSchemeManager getColorSchemeManager() {
        return colorSchemeManager;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setColorSchemeManager(ColorSchemeManager colorSchemeManager) {
        this.colorSchemeManager = colorSchemeManager;
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

    public String getPushNotificationPushId() {
        return pushNotificationPushId;
    }

    public void setShouldDisplayPushNotification(boolean b) {
        shouldDisplayPushNotification = b;
    }

    public boolean isTeamSwapFinished() {
        return !teamSwap;
    }

    public boolean isRecruiting() {
        return getSelectedTeamMarketId() == 2;
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

    public DateManager getDateManager() {
        return dateManager;
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

    public TeamObject getCurrentTeam() {
        return dataController.getSelectedTeamObject();
    }

}

