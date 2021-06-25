package co.sisu.mobile.activities;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.sisu.mobile.BuildConfig;
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
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;
import co.sisu.mobile.system.SaveSharedPreference;
import co.sisu.mobile.utils.TileCreationHelper;
import co.sisu.mobile.utils.Utils;
import co.sisu.mobile.viewModels.GlobalDataViewModel;

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
    // TODO: I think this should be getting set somehow. It's always true. Either that or kill it.
    private boolean isAgentDashboard = true;
    private List<ScopeBarModel> scopeBarList = new ArrayList<>();
    private List<MarketStatusModel> marketStatusBar = new ArrayList<>();
    private ScopeBarModel currentScopeFilter = null;
    private MarketStatusModel currentMarketStatusFilter = null;
    private ImageView addClientButton;
    private FilterObject selectedFilter;

    private PopupMenu teamSelectorPopup;
    private String dashboardType = "agent";
    private FirebaseAnalytics mFirebaseAnalytics;
    private GlobalDataViewModel globalDataViewModel;

    // TODO: I added a breakpoint on all the scope and market status filters to see if that race condition is gone. 6/16/21 - I think it is.
    // TODO: There is a bug when you are in the message center and press the plus button, then press back.
    // TODO: I can get rid of the MainActivity and just come straight here with a new Fragment, same with ForgotPasswordActivity
    // TODO: The TeamObject is the most important object and I've got to make sure we have it before I let them navigate around
    // TODO: Probably want to add colorscheme to the viewModel and observe changes.
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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        noNavigation = true;
        initListeners();
        initParentFields();
        initButtons();
        initActionBar();

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
        globalDataViewModel.setAgentData(agent);
        FirebaseCrashlytics.getInstance().setCustomKey("agent_id", agent.getAgent_id());
        FirebaseCrashlytics.getInstance().setUserId(agent.getAgent_id());

        if (BuildConfig.DEBUG) {
            //TODO: Don't release with this uncommented, you fuck face.
            //MOCKING AN AGENT
//            agent.setAgent_id("49201"); // This is a good agent for color checking
//            agent.setAgent_id("54185");
//            dataController.setAgent(agent);
//            globalDataViewModel.setAgentData(agent);
            //
        }
    }

    // region implements all initializers
    private void initListeners() {
        globalDataViewModel = new ViewModelProvider(this).get(GlobalDataViewModel.class);

        globalDataViewModel.getLabelsData().observe(this, newLabelsData -> {
            dataController.setLabels(newLabelsData);
        });

        globalDataViewModel.getSettingsData().observe(this, newSettingsData -> {
            dataController.setSettings(newSettingsData);
            utils.createNotificationAlarmIfActive(newSettingsData, null, this); //sets the actual alarm with correct times from user settings
        });

        globalDataViewModel.getScopeData().observe(this, newScopeData -> {
            scopeBarList = newScopeData;
            if(currentScopeFilter == null) {
                // This would be the agent scope
                currentScopeFilter = scopeBarList.get(0);
            }
            actionBarManager.setTitle(currentScopeFilter.getName());
            scopeFinished = true;
            navigateToScoreboard();
        });

        globalDataViewModel.getMarketStatusData().observe(this, newMarketStatusData -> {
            marketStatusBar = newMarketStatusData;
            for(MarketStatusModel marketStatusModel : newMarketStatusData) {
                if(marketStatusModel.getKey().equalsIgnoreCase("")) {
                    // TODO: CurrentMarketStatusFilter should be in the viewModel
                    currentMarketStatusFilter = marketStatusModel;
                }
            }
            marketStatusFinished = true;
            navigateToScoreboard();
//                    if(clientTilesFinished) {
//                        if(getCurrentScopeFilter() != null) {
//                            actionBarManager.setToFilterBar(getCurrentScopeFilter().getName());
//                        }
//                        else {
//                            actionBarManager.setToFilterBar("");
//                        }
//                        navigationManager.clearStackReplaceFragment(ClientTileFragment.class);
//                    }

        });

        globalDataViewModel.getAgentData().observe(this, newAgentData -> {
            apiManager.getFirebaseDevices(globalDataViewModel, agent.getAgent_id());
            apiManager.getTeams(globalDataViewModel, agent.getAgent_id());
        });

        globalDataViewModel.getTeamsObject().observe(this, allTeams -> {
            // TODO: Gotta kill that function eventually
            dataController.setTeamsObject(allTeams);

            this.runOnUiThread(() -> {
                actionBarManager.initTeamBar(allTeams);
                initTeamSelectorPopup();
                if(allTeams.size() > 0) {
                    // TODO: We should select this based on the sharedPreferences
                    dataController.setSelectedTeamObject(allTeams.get(0));
                    globalDataViewModel.setSelectedTeam(allTeams.get(0));
                    dataController.setMessageCenterVisible(true);
                    FirebaseCrashlytics.getInstance().setCustomKey("team_id", dataController.getCurrentSelectedTeamId());
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
            });

            apiManager.getScope(globalDataViewModel, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
            apiManager.getMarketStatus(globalDataViewModel, agent.getAgent_id(), dataController.getCurrentSelectedTeamMarketId());
            apiManager.getSettings(globalDataViewModel, agent.getAgent_id());
            apiManager.getLabels(globalDataViewModel, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
        });

        globalDataViewModel.setCurrentFirebaseDeviceIdData(SaveSharedPreference.getFirebaseDeviceId(this));
        globalDataViewModel.getCurrentFirebaseDeviceData().observe(this, currentDevice -> {
            MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService(apiManager, dataController.getAgent(), this.getApplicationContext(), currentDevice);

            if(Objects.requireNonNull(globalDataViewModel.getCurrentFirebaseDeviceIdData().getValue()).equals("") || currentDevice == null) {
                myFirebaseMessagingService.initFirebase();
            }
            else {
                myFirebaseMessagingService.refreshToken();
            }
        });
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
//            this.runOnUiThread(() -> parentLoader.setVisibility(View.VISIBLE));
//            sendTeamSwapApiCalls(team);
            globalDataViewModel.setSelectedTeam(team);
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

    // endregion implements all initializers

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
            Rect bounds = parentLoader.getIndeterminateDrawable().getBounds();
            if(colorSchemeManager.getAppBackground() == Color.WHITE) {
                parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark, null));
            } else {
                parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress, null));
            }
            parentLoader.getIndeterminateDrawable().setBounds(bounds);
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
            paginateInfo.setVisibility(View.GONE);
            switch (v.getId()) {
                case R.id.action_bar_home:
                case R.id.team_icon:
                case R.id.team_letter:
                    addClientButton.setVisibility(View.VISIBLE);
                    teamSelectorPopup.show();
                    break;
                case R.id.scoreboardView:
                    parentLoader.setVisibility(View.VISIBLE);
                    addClientButton.setVisibility(View.VISIBLE);
                    scopeFinished = true;
                    marketStatusFinished = true;
                    navigateToScoreboard();
                    break;
                case R.id.reportView:
                    noNavigation = false;
                    parentLoader.setVisibility(View.VISIBLE);
                    addClientButton.setVisibility(View.VISIBLE);
                    navigationManager.clearStackReplaceFragment(ClientTileFragment.class);
                    break;
                case R.id.recordView:
                    parentLoader.setVisibility(View.VISIBLE);
                    noNavigation = false;
                    actionBarManager.setToSaveBar("Record");
                    navigationManager.clearStackReplaceFragment(RecordFragment.class);
                    break;
                case R.id.leaderBoardView:
                    parentLoader.setVisibility(View.VISIBLE);
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

    // TODO: This should be moved into the fragment that owns it
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

    private void navigateToScoreboard() {
        this.runOnUiThread(() -> {
            if(scopeFinished && marketStatusFinished) {
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
        if(returnType == ApiReturnType.UPDATE_ACTIVITIES) {
            dataController.clearUpdatedRecords();
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        Log.e("FAILURE", asyncReturnType);
    }

    @Override
    public void onEventFailed(Object returnObject, @NonNull ApiReturnType returnType) {
        Log.e("FAILURE", returnType.name());
    }

    public void resetClientTiles(String clientSearch, int page) {
        // TODO: Gonna have to rework all of this
//        parentLoader.setVisibility(View.VISIBLE);
//        marketStatusFinished = true;
//        String selectedContextId = agent.getAgent_id();
//        if(currentScopeFilter != null) {
//            if(currentScopeFilter.getIdValue().charAt(0) == 'a') {
//                selectedContextId = currentScopeFilter.getIdValue().substring(1);
//            }
//            actionBarManager.setTitle(currentScopeFilter.getName());
//        }
//        else {
//            Log.e("Garage", "Garbage");
//        }
//
//        if(currentMarketStatusFilter != null) {
//            if(currentScopeFilter != null) {
//                apiManager.getTeamClients(clientTilesViewModel, selectedContextId, dataController.getCurrentSelectedTeamId(), currentScopeFilter.getIdValue(), currentMarketStatusFilter.getKey(), clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
//            }
//            else {
//                apiManager.getTeamClients(clientTilesViewModel, selectedContextId, dataController.getCurrentSelectedTeamId(), selectedContextId, currentMarketStatusFilter.getKey(), clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
//            }
//        }
//        else {
//            if(currentScopeFilter != null) {
//                apiManager.getTeamClients(clientTilesViewModel, selectedContextId, dataController.getCurrentSelectedTeamId(), currentScopeFilter.getIdValue(), "", clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
//            }
//            else {
//                apiManager.getTeamClients(clientTilesViewModel, selectedContextId, dataController.getCurrentSelectedTeamId(), "a" + getAgent().getAgent_id(), "", clientSearch, page, dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime());
//            }
//        }
    }

    public void resetClientTilesPresetFilter(JSONObject filters, int page) {
        parentLoader.setVisibility(View.VISIBLE);
        apiManager.getTeamClientsPresetFilter(this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), filters, page);
    }

    public void resetDashboardTiles(boolean scopeSelected) {
        // TODO: Gonna have to rework all of this
//        parentLoader.setVisibility(View.VISIBLE);
//        if(currentScopeFilter != null) {
//            actionBarManager.setTitle(currentScopeFilter.getName());
//        }
//        else {
//            Log.e("Garbage", "Garbage");
//        }
//        tileTemplateFinished = false;
//        if(scopeSelected) {
//            scopeFinished = true;
//            marketStatusFinished = true;
//        }
//        else {
//            scopeFinished = false;
//            marketStatusFinished = false;
//            apiManager.getScope(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
//            apiManager.getMarketStatus(ParentActivity.this, agent.getAgent_id(), dataController.getCurrentSelectedTeamId());
//        }
//
//        if(currentScopeFilter != null) {
//            apiManager.getTileSetup(dashboardTilesViewModel, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", currentScopeFilter.getIdValue());
//        }
//        else {
//            apiManager.getTileSetup(dashboardTilesViewModel, agent.getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + agent.getAgent_id());
//        }
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

    public GlobalDataViewModel getGlobalDataViewModel() {
        return globalDataViewModel;
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

    public Utils getUtils() {
        return utils;
    }

    public TileCreationHelper getTileCreationHelper() {
        return tileCreationHelper;
    }

    public DateManager getDateManager() {
        return dateManager;
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        return mFirebaseAnalytics;
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

