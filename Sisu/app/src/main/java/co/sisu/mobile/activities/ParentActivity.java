package co.sisu.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import co.sisu.mobile.adapters.TeamBarAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.R;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.RecordFragment;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView pageTitle, teamLetter;
    View teamBlock;
    DrawerLayout drawerLayout;
    DataController dataController;
    private String fragmentTag;
    List<TeamObject> teamsList;
    boolean activeBacktionBar = false;
    boolean activeClientBar = false;
    int selectedTeam = 0;
    ActionBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        bar = getSupportActionBar();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        initializeActionBar();
        getSupportActionBar().setElevation(0);

        pageTitle.setText("Scoreboard");
        fragmentTag = "Scoreboard";
        drawerLayout = findViewById(R.id.drawer_layout);
        dataController = new DataController(this);
        initializeButtons();
        initializeTeamBar();

        navigateToScoreboard();
    }

    public void initializeActionBar() {
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

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
            pageTitle.setText("More");
        }
    }

    private void initializeTeamBar() {
        ListView mListView = findViewById(R.id.navViewList);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        teamsList = dataController.getTeams();

        TeamBarAdapter adapter = new TeamBarAdapter(getBaseContext(), teamsList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);

        teamBlock.setBackgroundColor(teamsList.get(0).getColor());
        teamLetter.setText(teamsList.get(0).getTeamLetter());
        teamLetter.setBackgroundColor(teamsList.get(0).getColor());
    }

    private void navigateToScoreboard() {
        resetToolbarImages("scoreboard");
        replaceFragment(ScoreboardFragment.class);
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
                showToast("CANCEL BUTTON");
            default:
                break;
        }
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

    public void swapToBacktionBar() {
        //Get it?! Back action... Backtion!
        activeBacktionBar = true;
        getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
    }

    public void swapToClientBar() {
        activeClientBar = true;
        bar.setCustomView(R.layout.action_bar_clients_layout);
        View view = getSupportActionBar().getCustomView();

        TextView cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);
    }

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
        else if(activeClientBar) {
            activeClientBar = false;
            initializeActionBar();
        }
        super.onBackPressed();
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
