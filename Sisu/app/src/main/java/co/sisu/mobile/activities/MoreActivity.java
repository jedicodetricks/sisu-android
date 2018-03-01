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

import co.sisu.mobile.R;
import co.sisu.mobile.TestComponentFragment;
import co.sisu.mobile.TestFragment;
import co.sisu.mobile.TestFragment2;

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


//        TestFragment test = new TestFragment();
//        test.setArguments(getIntent().getExtras());
//        getFragmentManager().beginTransaction().add(R.id.your_placeholder, test).commit();

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

//        ImageView addButton = findViewById(R.id.addView);
//        addButton.setOnClickListener(this);
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
        leaderBoardButton.setImageResource(R.drawable.more_grey);

        switch (inputActivity) {
            case "scoreboard":
                scoreBoardButton.setImageResource(R.drawable.sisu_icon_orange);
                showToast("scoreboard Button is clicked");
                break;
            case "report":
                reportButton.setImageResource(R.drawable.sisu_icon_orange);
                showToast("report Button is clicked");
                break;
            case "record":
                recordButton.setImageResource(R.drawable.sisu_icon_orange);
                showToast("record Button is clicked");
                break;
            case "leaderboard":
                leaderBoardButton.setImageResource(R.drawable.sisu_icon_orange);
                showToast("leaderboard Button is clicked");
                break;
            case "more":
                moreButton.setImageResource(R.drawable.sisu_icon_orange);
                showToast("more Button is clicked");
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
                //do stuff
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.your_placeholder, new TestComponentFragment());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                break;
            case R.id.reportView:
                resetToolbarImages("report");
                //do stuff
                // Begin the transaction
                ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.your_placeholder, new TestFragment());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                break;
            case R.id.recordView:
                resetToolbarImages("record");
                //do stuff
                ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.your_placeholder, new TestFragment2());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                break;
            case R.id.leaderBoardView:
                resetToolbarImages("leaderboard");
                //do stuff
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
