package co.sisu.mobile.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import co.sisu.mobile.adapters.TeamBarAdapter;
import co.sisu.mobile.api.AsyncAgentGoals;
import co.sisu.mobile.api.AsyncClients;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncSettings;
import co.sisu.mobile.api.AsyncTeams;
import co.sisu.mobile.api.AsyncUpdateActivities;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.fragments.ClientListFragment;
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
import co.sisu.mobile.system.SaveSharedPreference;

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
    private boolean activeAddClientBar = false;
    private String addClientChild = "";
    ProgressBar parentLoader;
    int backPressed;

    String currentSelectedRecordDate = "";
    private boolean clientFinished = false;
    private boolean goalsFinished = false;
    private boolean settingsFinished = false;
    private JWTObject jwtObject;
    private String timeline = "month";
    private int timelineSelection = 4;

    public boolean isRecordSaved() {
        return recordSaved;
    }

    public void setRecordSaved(boolean recordSaved) {
        showToast("Parent Saved");
        this.recordSaved = recordSaved;
    }

    boolean recordSaved = false;
    int selectedTeam = 0;
    ActionBar bar;
    AgentModel agent;
    ErrorMessageFragment errorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataController = new DataController();
        agent = getIntent().getParcelableExtra("Agent");
        dataController.setAgent(agent);
        errorFragment = new ErrorMessageFragment();
        setContentView(R.layout.activity_parent);
        parentLoader = findViewById(R.id.parentLoader);
//        parentLoader.setVisibility(View.VISIBLE);
        bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setDisplayShowCustomEnabled(true);
        View customView = getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        bar.setCustomView(customView);
        Toolbar parent =(Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0,0);
        parent.setPaddingRelative(0,0,0,0);
        drawerLayout = findViewById(R.id.drawer_layout);
        backPressed = 0;

        initializeActionBar();
        getSupportActionBar().setElevation(0);
        getJwtFromPrefs();

        pageTitle.setText("Scoreboard");
        fragmentTag = "Scoreboard";
        initializeButtons();
        new AsyncTeams(this, agent.getAgent_id(), null).execute();
        new AsyncClients(this, agent.getAgent_id(), null).execute();
//        parentLoader.setVisibility(View.VISIBLE);
    }



    public void initializeActionBar() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        bar.setCustomView(R.layout.action_bar_layout);
        pageTitle = findViewById(R.id.action_bar_title);
        teamLetter = findViewById(R.id.team_letter);
        teamBlock = findViewById(R.id.action_bar_home);

        View view = getSupportActionBar().getCustomView();
        if(fragmentTag != null) {
            pageTitle.setText(fragmentTag);
        }
        View homeButton= view.findViewById(R.id.action_bar_home);
        homeButton.setOnClickListener(this);

        if(teamsList != null && pageTitle.getText().toString().equals("Leaderboard")) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            teamBlock.setBackgroundColor(teamsList.get(selectedTeam).getColor());
            teamLetter.setText(teamsList.get(selectedTeam).getTeamLetter().toUpperCase());
            teamLetter.setBackgroundColor(teamsList.get(selectedTeam).getColor());
        }
        else {
            teamBlock.setVisibility(View.GONE);
            teamLetter.setVisibility(View.GONE);
        }
    }

    private void initializeTeamBar(List<TeamObject> teamsList) {
        if(teamsList.size() > 0) {
            ListView mListView = findViewById(R.id.navViewList);
            mListView.setDivider(null);
            mListView.setDividerHeight(30);

            this.teamsList = teamsList;

            TeamBarAdapter adapter = new TeamBarAdapter(getBaseContext(), teamsList);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(this);

            teamBlock.setBackgroundColor(teamsList.get(0).getColor());
            teamLetter.setText(teamsList.get(0).getTeamLetter().toUpperCase());
            teamLetter.setBackgroundColor(teamsList.get(0).getColor());
        }

    }

    private void navigateToScoreboard() {
        if(clientFinished && goalsFinished && settingsFinished) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    parentLoader.setVisibility(View.GONE);
                    resetToolbarImages("scoreboard");
                    replaceFragment(ScoreboardFragment.class);
                }
            });
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
        addClientChild = "";
        FragmentManager fm = this.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

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
                initializeActionBar();
                replaceFragment(ScoreboardFragment.class);
                break;
            case R.id.reportView:
                resetToolbarImages("report");
                pageTitle.setText("Report");
                fragmentTag = "Report";
                initializeActionBar();
                replaceFragment(ReportFragment.class);
                break;
            case R.id.recordView:
                resetToolbarImages("record");
                pageTitle.setText("Record");
                fragmentTag = "Record";
                initializeActionBar();
                swapToBacktionBar(fragmentTag, null);
                replaceFragment(RecordFragment.class);
                break;
            case R.id.leaderBoardView:
                resetToolbarImages("leaderboard");
                pageTitle.setText("Leaderboard");
                fragmentTag = "Leaderboard";
                initializeActionBar();
                replaceFragment(LeaderboardFragment.class);
                break;
            case R.id.moreView:
                resetToolbarImages("more");
                pageTitle.setText("More");
                fragmentTag = "More";
                initializeActionBar();
                replaceFragment(MoreFragment.class);
                break;
            case R.id.cancelButton:
                resetToolbarImages("scoreboard");
                pageTitle.setText("Scoreboard");
                fragmentTag = "Scoreboard";
                initializeActionBar();
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

        new AsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject, getJwtObject()).execute();
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
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void navigateToClientList(String tab, String child) {
        Fragment fragment = null;
        try {
            fragment = ClientListFragment.newInstance(tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        swapToClientListBar(child);
        FragmentManager fragmentManager = getSupportFragmentManager();
//        Log.e("BACK STACK", String.valueOf(fragmentManager.getBackStackEntryCount()));
//        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
//            Log.e("BACKSTACK " + i, String.valueOf(fragmentManager.getBackStackEntryAt(i)));
//        }
        if(child !=  null) {
            fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
        }
        else {
            fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
        }
        resetToolbarImages("more");
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

    public void popStackReplaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commit();
    }



    public void swapToBacktionBar(final String titleString, final String child) {
        //Get it?! Back action... Backtion!
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetAllActionBars();
                if(child != null) {
                    addClientChild = child;
                }
                activeBacktionBar = true;
                getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
                backtionTitle = findViewById(R.id.actionBarTitle);
                if(titleString == null) {
                    String displayName = "";
                    if(selectedClient.getFirst_name() != null) {
                        displayName += selectedClient.getFirst_name() + " ";
                    }
                    if(selectedClient.getLast_name() != null) {
                        displayName += selectedClient.getLast_name();
                    }
                    backtionTitle.setText(displayName);
                } else {
                    backtionTitle.setText(titleString);
                }
            }
        });

    }

    public void swapToTitleBar(final String titleString) {
        resetAllActionBars();
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

    public void swapToClientListBar(String child) {
        if(child != null) {
            addClientChild = child;
        }
        resetAllActionBars();
        activeClientListBar = true;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setCustomView(R.layout.action_bar_clients_layout);
            }
        });
    }

    private void resetAllActionBars() {
         activeBacktionBar = false;
         activeClientListBar = false;
         activeTitleBar = false;
         activeAddClientBar = false;
    }

    public AgentModel getAgentInfo() {
        return agent;
    }

    public int getSelectedTeamId() {
        int teamId = -1;
        if(teamsList != null) {
            teamId = teamsList.get(selectedTeam).getId();

        }
        return teamId;
    }

    public void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragManager = getSupportFragmentManager();
        if(fragManager.getBackStackEntryCount() < 1 && backPressed < 1) { //needs if statement checking if on root fragment, app is always on root activity.. need fragment management
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.sisu_mark)
                    .setTitle("Closing Sisu")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .setOnKeyListener( new Dialog.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                finish();
                                dialog.dismiss();
                            }
                            return true;
                        }
                    })
                    .show();
        } else {
            if(activeBacktionBar) {
                activeBacktionBar = false;
                if(addClientChild.equals("client")) {
                    swapToClientListBar(null);
                }
                else {
                    initializeActionBar();
                }
            }
            else if(activeClientListBar) {
                activeClientListBar = false;

                if(addClientChild.equals("record")) {
                    swapToBacktionBar("Record", null);
                }
                else {
                    initializeActionBar();
                }
            }
            else if(activeTitleBar) {
                activeTitleBar = false;
                initializeActionBar();
            }
            else if(activeAddClientBar) {
                activeAddClientBar = false;
                if(addClientChild.equals("client")) {
                    swapToClientListBar(null);
                }
                else {
                    initializeActionBar();
                }
            }
            super.onBackPressed();
        }
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
                    initializeTeamBar(dataController.getTeamsObject());
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
        replaceFragment(ErrorMessageFragment.class);
//        if(asyncReturnType.equals("Goals")) {
//            new AsyncAgentGoals(ParentActivity.this, agent.getAgent_id(), getSelectedTeamId()).execute();
//        }
    }

    public void setActivitiesObject(Object returnObject) {
        dataController.setActivitiesObject(returnObject);
    }

    public void setScoreboardActivities(Object returnObject) {
        dataController.setScoreboardActivities(returnObject);
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

    public HashMap<String, SelectedActivities> getActivitiesSelected() {
        return dataController.getActivitiesSelected();
    }

    public void setActivitiesSelected(SettingsObject activitiesSelected) {
        dataController.setActivitiesSelected(activitiesSelected);
    }

    public List<Metric> getUpdatedRecords() {
        return dataController.getUpdatedRecords();
    }

    public void swapToAddClientBar(String child) {
        resetAllActionBars();
        activeAddClientBar = true;
        addClientChild = child;
        getSupportActionBar().setCustomView(R.layout.action_bar_add_client_layout);
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

    public void getJwtFromPrefs() {
        jwtObject = new JWTObject(SaveSharedPreference.getJWT(this), SaveSharedPreference.getClientTimestamp(this), SaveSharedPreference.getTransId(this));
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
}
