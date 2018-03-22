package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.sisu.mobile.adapters.MoreListAdapter;
import co.sisu.mobile.adapters.TeamBarAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.R;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.RecordFragment;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    TextView pageTitle, teamLetter;
    View teamBlock;
    DrawerLayout drawerLayout;
    DataController dataController;
    private String fragmentTag = "Scoreboard";
    List<TeamObject> teamsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        getSupportActionBar().setElevation(0);
        pageTitle = findViewById(R.id.action_bar_title);
        teamLetter = findViewById(R.id.team_letter);
        teamBlock = findViewById(R.id.action_bar_home);
        pageTitle.setText("Scoreboard");
        drawerLayout = findViewById(R.id.drawer_layout);
        dataController = new DataController(this);
        initializeButtons();
        initializeTeamBar();
        navigateToScoreboard();
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
        View view = getSupportActionBar().getCustomView();

        View homeButton= view.findViewById(R.id.action_bar_home);
        homeButton.setOnClickListener(this);

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
//        showToast(String.valueOf(team.getId()));
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.your_placeholder);

//        showToast(String.valueOf(f.getTag()));
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
        // Get information based on team id
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
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
    }

    public void swapToBacktionBar() {
        //Get it?! Back action... Backtion!

        getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}
