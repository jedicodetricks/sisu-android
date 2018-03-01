package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.sisu.mobile.LeaderboardFragment;
import co.sisu.mobile.R;
import co.sisu.mobile.ScoreboardFragment;
import co.sisu.mobile.ReportFragment;
import co.sisu.mobile.RecordFragment;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        getSupportActionBar().setElevation(0);
        TextView pageTitle = findViewById(R.id.action_bar_title);
        pageTitle.setText("More");
        initializeButtons();
        navigateToScoreboard();
    }

    private void navigateToScoreboard() {
        resetToolbarImages("scoreboard");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.your_placeholder, new ScoreboardFragment());
        // or ft.add(R.id.your_placeholder, new FooFragment());
        // Complete the changes added above
        ft.commit();
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
        scoreBoardButton.setImageResource(R.drawable.home_orange);

        ImageView reportButton = findViewById(R.id.reportView);
        reportButton.setImageResource(R.drawable.reports_grey);

        ImageView recordButton = findViewById(R.id.recordView);
        recordButton.setImageResource(R.drawable.record_grey);

        ImageView leaderBoardButton = findViewById(R.id.leaderBoardView);
        leaderBoardButton.setImageResource(R.drawable.leaderboard_grey);

        ImageView moreButton = findViewById(R.id.moreView);
        moreButton.setImageResource(R.drawable.more_grey);

        switch (inputActivity) {
            case "scoreboard":
                scoreBoardButton.setImageResource(R.drawable.sisu_icon_orange);
                break;
            case "report":
                reportButton.setImageResource(R.drawable.sisu_icon_orange);
                break;
            case "record":
                recordButton.setImageResource(R.drawable.sisu_icon_orange);
                break;
            case "leaderboard":
                leaderBoardButton.setImageResource(R.drawable.sisu_icon_orange);
                break;
            case "more":
                moreButton.setImageResource(R.drawable.sisu_icon_orange);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.action_bar_home:
                //do stuff
                break;
            case R.id.scoreboardView:
                resetToolbarImages("scoreboard");
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.your_placeholder, new ScoreboardFragment());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                break;
            case R.id.reportView:
                resetToolbarImages("report");
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.your_placeholder, new ReportFragment());
                ft.commit();
                break;
            case R.id.recordView:
                resetToolbarImages("record");
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.your_placeholder, new RecordFragment());
                ft.commit();
                break;
            case R.id.leaderBoardView:
                resetToolbarImages("leaderboard");
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.your_placeholder, new LeaderboardFragment());
                ft.commit();
                break;
            case R.id.moreView:
                resetToolbarImages("more");
                //do stuff
                break;
            case R.id.addView:
                //do stuff
                //open floating menu
                break;
            default:
                //do stuff
                break;
        }
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
