package co.sisu.mobile.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncAgentGoals;
import co.sisu.mobile.api.AsyncClients;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncSettings;
import co.sisu.mobile.api.AsyncTeams;
import co.sisu.mobile.api.AsyncUpdateActivities;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.fragments.ErrorMessageFragment;
import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.RecordFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.JWTObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.SettingsObject;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncServerEventListener {

    DataController dataController;
    NavigationManager navigationManager;
    ProgressBar parentLoader;

    String currentSelectedRecordDate = "";
    private boolean clientFinished = false;
    private boolean goalsFinished = false;
    private boolean settingsFinished = false;
    private String timeline = "month";
    private int timelineSelection = 4;

    AgentModel agent;
    ErrorMessageFragment errorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        dataController = new DataController();
        navigationManager = new NavigationManager(this);
        agent = getIntent().getParcelableExtra("Agent");
        dataController.setAgent(agent);
        errorFragment = new ErrorMessageFragment();
        parentLoader = findViewById(R.id.parentLoader);

        initializeButtons();
        new AsyncTeams(this, agent.getAgent_id(), null).execute();
        new AsyncClients(this, agent.getAgent_id(), null).execute();
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

        if(dataController.getUpdatedRecords().size() > 0) {
            updateRecordedActivities();
        }

        switch (v.getId()) {
            case R.id.action_bar_home:
                navigationManager.toggleDrawer();
                break;
            case R.id.scoreboardView:
                navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                break;
            case R.id.reportView:
                navigationManager.clearStackReplaceFragment(ReportFragment.class);
                break;
            case R.id.recordView:
                navigationManager.clearStackReplaceFragment(RecordFragment.class);
                break;
            case R.id.leaderBoardView:
                navigationManager.clearStackReplaceFragment(LeaderboardFragment.class);
                break;
            case R.id.moreView:
                navigationManager.clearStackReplaceFragment(MoreFragment.class);
                break;
            case R.id.cancelButton:
                navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
            default:
                break;
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

        new AsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject, getJwtObject()).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TeamObject team = (TeamObject) parent.getItemAtPosition(position);
        navigationManager.updateTeam(team);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.your_placeholder);
        navigationManager.updateSelectedTeam(position);
        switch (f.getTag()) {
            case "Scoreboard":
                ((ScoreboardFragment) f).teamSwap();
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
        }
        navigationManager.closeDrawer();
    }

    private void navigateToScoreboard() {
        if(clientFinished && goalsFinished && settingsFinished) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                }
            });
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
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Teams")) {
            dataController.setTeamsObject(ParentActivity.this, returnObject);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.initializeTeamBar(dataController.getTeamsObject());
                    new AsyncAgentGoals(ParentActivity.this, agent.getAgent_id(), null).execute();
                    new AsyncSettings(ParentActivity.this, agent.getAgent_id(), null).execute();
                }
            });
        }
        else if(asyncReturnType.equals("Goals")) {
            AsyncGoalsJsonObject goals = (AsyncGoalsJsonObject) returnObject;
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject);
            goalsFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Settings")) {
            AsyncSettingsJsonObject settingsJson = (AsyncSettingsJsonObject) returnObject;
            SettingsObject[] settings = settingsJson.getParameters();
            dataController.setSettings(settings);
            settingsFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Update Activities")) {
            clearUpdatedRecords();
        }
        else if(asyncReturnType.equals("Clients")) {
            setClientsObject(returnObject);
            clientFinished = true;
            navigateToScoreboard();
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        Log.e("FAILURE", asyncReturnType);
        errorFragment.setMessage(asyncReturnType + " cause this failure.");
        navigationManager.clearStackReplaceFragment(ErrorMessageFragment.class);

    }


    // GETTERS AND SETTERS

    public void setActivitiesObject(Object returnObject) {
        dataController.setActivitiesObject(returnObject);
    }

    public void setScoreboardActivities(Object returnObject) {
        dataController.setScoreboardActivities(returnObject);
    }

    public List<Metric> getActivitiesObject() {
        return dataController.getActivitiesObject();
    }

    public List<Metric> getScoreboardObject() {
        return dataController.getScoreboardObject();
    }

    public void setClientsObject(Object returnObject) {
        dataController.setClientListObject(returnObject);
    }

    public void setSelectedClient(ClientObject client) {
        navigationManager.setSelectedClient(client);
    }

    public ClientObject getSelectedClient() {
        return navigationManager.getSelectedClient();
    }

    public List<ClientObject> getPipelineList() {
        return dataController.getPipelineList();
    }

    public List<ClientObject> getSignedList() {
        return dataController.getSignedList();
    }

    public List<ClientObject> getContractList() {
        return dataController.getContractList();
    }

    public List<ClientObject> getClosedList() {
        return dataController.getClosedList();
    }

    public List<ClientObject> getArchivedList() {
        return dataController.getArchivedList();
    }

    public void setRecordUpdated(Metric metric) {
        dataController.setRecordUpdated(metric);
    }

    public List<SettingsObject> getSettings() {
        return dataController.getSettings();
    }

    public void setSettings(SettingsObject[] settings) {
        dataController.setSettings(settings);
    }

    public void clearUpdatedRecords() {
        dataController.clearUpdatedRecords();
    }

    public void updateSelectedRecordDate(String formattedDate) {
        this.currentSelectedRecordDate = formattedDate;
    }

    public HashMap<String, SelectedActivities> getActivitiesSelected() {
        return dataController.getActivitiesSelected();
    }

    public void setActivitiesSelected(SettingsObject activitiesSelected) {
        dataController.setActivitiesSelected(activitiesSelected);
    }

    public List<Metric> getUpdatedRecords() {
        return dataController.getUpdatedRecords();
    }

    public void setAgentGoals(AgentGoalsObject[] agentGoalsObject) {
        dataController.setAgentGoals(agentGoalsObject);
    }

    public void setAgentIncomeAndReason(AgentModel agentModel) {
        agent.setDesired_income(agentModel.getDesired_income());
        agent.setVision_statement(agentModel.getVision_statement());
    }

    public void setAgent(AgentModel agentModel) {
        agentModel.setAgentGoalsObject(agent.getAgentGoalsObject());
        this.agent = agentModel;
        dataController.setAgent(agentModel);
    }

    public JWTObject getJwtObject() {
        return null;
    }

    public void setRecordObject(Object returnObject) {
        dataController.setRecordActivities(returnObject);
    }

    public List<Metric> getRecordObject() {
        return dataController.getRecordActivities();
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

    public AgentModel getAgentInfo() {
        return agent;
    }

    public int getSelectedTeamId() {
        int teamId = navigationManager.getSelectedTeamId();
        return teamId;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }
}
