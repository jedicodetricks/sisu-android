package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.TeamBarAdapter;
import co.sisu.mobile.api.AsyncAgentGoals;
import co.sisu.mobile.api.AsyncClients;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncSettings;
import co.sisu.mobile.api.AsyncTeams;
import co.sisu.mobile.api.AsyncUpdateActivities;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.fragments.ClientListFragment;
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
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.SettingsObject;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncServerEventListener {

    TextView pageTitle, teamLetter, backtionTitle, title;
    View teamBlock;
    DrawerLayout drawerLayout;
    DataController dataController;
    ClientObject selectedClient;
    private String fragmentTag;
    List<TeamObject> teamsList;
    boolean activeBacktionBar = false;
    boolean activeClientListBar = false;
    boolean activeTitleBar = false;
    String currentSelectedRecordDate = "";
    private boolean clientFinished = false;
    private boolean goalsFinished = false;

    public boolean isRecordSaved() {
        return recordSaved;
    }

    public void setRecordSaved(boolean recordSaved) {
        Toast.makeText(this, "parent saved", Toast.LENGTH_SHORT).show();
        this.recordSaved = recordSaved;
    }

    boolean recordSaved = false;
    int selectedTeam = 0;
    ActionBar bar;
    AgentModel agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataController = new DataController();
        agent = getIntent().getParcelableExtra("Agent");
        dataController.setAgent(agent);

        setContentView(R.layout.activity_parent);
        bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        bar.setCustomView(customView);
        Toolbar parent =(Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0,0);
        parent.setPaddingRelative(0,0,0,0);
        initializeActionBar();
        getSupportActionBar().setElevation(0);

        pageTitle.setText("Scoreboard");
        fragmentTag = "Scoreboard";
        drawerLayout = findViewById(R.id.drawer_layout);
        initializeButtons();
        new AsyncTeams(this, agent.getAgent_id()).execute();
        new AsyncClients(this, agent.getAgent_id()).execute();
    }



    public void initializeActionBar() {
        bar.setCustomView(R.layout.action_bar_layout);
        pageTitle = findViewById(R.id.action_bar_title);
        teamLetter = findViewById(R.id.team_letter);
        teamBlock = findViewById(R.id.action_bar_home);

        View view = getSupportActionBar().getCustomView();

        View homeButton= view.findViewById(R.id.action_bar_home);
        homeButton.setOnClickListener(this);

        if(teamsList != null) {
            teamBlock.setBackgroundColor(teamsList.get(selectedTeam).getColor());
            teamLetter.setText(teamsList.get(selectedTeam).getTeamLetter());
            teamLetter.setBackgroundColor(teamsList.get(selectedTeam).getColor());
        }
    }

    private void initializeTeamBar(List<TeamObject> teamsList) {
        ListView mListView = findViewById(R.id.navViewList);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        this.teamsList = teamsList;

        TeamBarAdapter adapter = new TeamBarAdapter(getBaseContext(), teamsList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);

        teamBlock.setBackgroundColor(teamsList.get(0).getColor());
        teamLetter.setText(teamsList.get(0).getTeamLetter());
        teamLetter.setBackgroundColor(teamsList.get(0).getColor());
    }

    private void navigateToScoreboard() {
        if(clientFinished && goalsFinished) {
            resetToolbarImages("scoreboard");
            replaceFragment(ScoreboardFragment.class);
        }

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

    private void resetToolbarImages(String inputActivity) {
        ImageView scoreBoardButton = findViewById(R.id.scoreboardView);
        scoreBoardButton.setImageResource(R.drawable.home_icon);

        ImageView reportButton = findViewById(R.id.reportView);
        reportButton.setImageResource(R.drawable.report_icon);

        ImageView recordButton = findViewById(R.id.recordView);
        recordButton.setImageResource(R.drawable.record_icon);

        ImageView leaderBoardButton = findViewById(R.id.leaderBoardView);
        leaderBoardButton.setImageResource(R.drawable.leaderboard_icon);

        ImageView moreButton = findViewById(R.id.moreView);
        moreButton.setImageResource(R.drawable.more_icon);

        switch (inputActivity) {
            case "scoreboard":
                scoreBoardButton.setImageResource(R.drawable.home_icon_active);
                break;
            case "report":
                reportButton.setImageResource(R.drawable.report_icon_active);
                break;
            case "record":
                recordButton.setImageResource(R.drawable.record_icon_active);
                break;
            case "leaderboard":
                leaderBoardButton.setImageResource(R.drawable.leaderboard_icon_active);
                break;
            case "more":
                moreButton.setImageResource(R.drawable.more_icon_active);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        activeBacktionBar = false;
        activeClientListBar = false;
        activeTitleBar = false;
        initializeActionBar();
        if(dataController.getUpdatedRecords().size() > 0) {
            updateRecordedActivities();
        }

        switch (v.getId()) {
            case R.id.action_bar_home:
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.scoreboardView:
                resetToolbarImages("scoreboard");
                pageTitle.setText("Scoreboard");
                fragmentTag = "Scoreboard";
                replaceFragment(ScoreboardFragment.class);
                break;
            case R.id.reportView:
                resetToolbarImages("report");
                pageTitle.setText("Report");
                fragmentTag = "Report";
                replaceFragment(ReportFragment.class);
                break;
            case R.id.recordView:
                resetToolbarImages("record");
                pageTitle.setText("Record");
                fragmentTag = "Record";
                swapToBacktionBar(fragmentTag);
                replaceFragment(RecordFragment.class);
                break;
            case R.id.leaderBoardView:
                resetToolbarImages("leaderboard");
                pageTitle.setText("Leaderboard");
                fragmentTag = "Leaderboard";
                replaceFragment(LeaderboardFragment.class);
                break;
            case R.id.moreView:
                resetToolbarImages("more");
                pageTitle.setText("More");
                fragmentTag = "More";
                replaceFragment(MoreFragment.class);
                break;
            case R.id.cancelButton:
                resetToolbarImages("scoreboard");
                pageTitle.setText("Scoreboard");
                fragmentTag = "Scoreboard";
                replaceFragment(ScoreboardFragment.class);
            case R.id.saveButton:
//                resetToolbarImages("scoreboard");
//                pageTitle.setText("Scoreboard");
//                fragmentTag = "Scoreboard";
//                replaceFragment(ScoreboardFragment.class);
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

        new AsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject).execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TeamObject team = (TeamObject) parent.getItemAtPosition(position);
        teamBlock.setBackgroundColor(team.getColor());
        teamLetter.setText(team.getTeamLetter());
        teamLetter.setBackgroundColor(team.getColor());
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.your_placeholder);
        selectedTeam = position;
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
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void replaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commit();
    }

    public void navigateToClientList(String tab) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) ClientListFragment.newInstance(tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        swapToClientListBar();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commit();
    }

    public void stackReplaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment and adding it to the stack
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
    }

    public void swapToBacktionBar(String titleString) {
        //Get it?! Back action... Backtion!
        activeBacktionBar = true;
        getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
        backtionTitle = findViewById(R.id.actionBarTitle);
        if(titleString == null) {
            backtionTitle.setText(selectedClient.getFirst_name() + " " + selectedClient.getLast_name());
        } else {
            backtionTitle.setText(titleString);
        }
    }

    public void swapToTitleBar(final String titleString) {
        activeTitleBar = true;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setCustomView(R.layout.action_bar_title_layout);
                title = findViewById(R.id.title);
                title.setText(titleString);
            }
        });

    }

    public void swapToClientListBar() {
        activeClientListBar = true;
        getSupportActionBar().setCustomView(R.layout.action_bar_clients_layout);
    }

    public AgentModel getAgentInfo() {
        return agent;
    }

    public int getSelectedTeamId() { return teamsList.get(selectedTeam).getId(); }

    public void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if(activeBacktionBar) {
            activeBacktionBar = false;
            initializeActionBar();
        }
        else if(activeClientListBar) {
            activeClientListBar = false;
            initializeActionBar();
        }
        else if(activeTitleBar) {
            activeTitleBar = false;
            initializeActionBar();
        }
        super.onBackPressed();
    }

    public void showToast(final CharSequence msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ParentActivity.this, msg,Toast.LENGTH_SHORT).show();
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
                    initializeTeamBar(dataController.getTeamsObject());
                    new AsyncAgentGoals(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId()).execute();
                    new AsyncSettings(ParentActivity.this, agent.getAgent_id()).execute();
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
    public void onEventFailed(Object o, String s) {

    }

    public void setActivitiesObject(Object returnObject) {
        dataController.setActivitiesObject(returnObject);
    }

    public List<Metric> getActivitiesObject() {
        return dataController.getActivitiesObject();
    }
    public List<Metric> getScoreboardObject() { return dataController.getScoreboardObject(); }


    public void setClientsObject(Object returnObject) {
        dataController.setClientListObject(returnObject);
    }

    public void setSelectedClient(ClientObject client) {
        selectedClient = client;
    }

    public ClientObject getSelectedClient() {
        return selectedClient;
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

    public void setSpecificGoal(AgentGoalsObject selectedGoal, int value) {
        dataController.setSpecificGoal(selectedGoal, value);
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
}
