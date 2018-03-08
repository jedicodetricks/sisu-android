package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.R;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.RecordFragment;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener {

    TextView pageTitle;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        getSupportActionBar().setElevation(0);
        pageTitle = findViewById(R.id.action_bar_title);
        pageTitle.setText("Scoreboard");
        drawerLayout = findViewById(R.id.drawer_layout);
        initializeButtons();
        navigateToScoreboard();
    }

    private void navigateToScoreboard() {
        resetToolbarImages("scoreboard");
        replaceFragment(ScoreboardFragment.class);
    }

    private void initializeButtons(){
        View view = getSupportActionBar().getCustomView();

        ImageButton homeButton= view.findViewById(R.id.action_bar_home);
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
                replaceFragment(ScoreboardFragment.class);
                break;
            case R.id.reportView:
                resetToolbarImages("report");
                pageTitle.setText("Report");
                replaceFragment(ReportFragment.class);
                break;
            case R.id.recordView:
                resetToolbarImages("record");
                pageTitle.setText("Record");
                replaceFragment(RecordFragment.class);
                break;
            case R.id.leaderBoardView:
                resetToolbarImages("leaderboard");
                pageTitle.setText("Leaderboard");
                replaceFragment(LeaderboardFragment.class);
                break;
            case R.id.moreView:
                resetToolbarImages("more");
                pageTitle.setText("More");
                replaceFragment(MoreFragment.class);
                break;
            default:
                break;
        }
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
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment)
                .commit();
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
