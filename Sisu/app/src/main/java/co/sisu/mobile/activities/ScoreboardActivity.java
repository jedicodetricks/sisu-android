package co.sisu.mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import co.sisu.mobile.R;

/**
 * Created by bradygroharing on 2/13/18.
 */
// TODO: 2/20/2018 remove Toasts with links/buttons when proper functionality replaces them  

public class ScoreboardActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        getSupportActionBar().setElevation(0);
        initializeButtons();
        createAndAnimateProgressBars();
        initializeTimelineSelector();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.action_bar_home:
                //do stuff
                showToast("Home Button is clicked");
                break;
            case R.id.action_bar_menu:
                //do stuff
                //open floating slide menu from right
                showToast("Menu Button is clicked");
                break;
            case R.id.scoreboardView:
                //do stuff
                showToast("Scoreboard Button is clicked");
                navigatePage(ScoreboardActivity.class);
                break;
            case R.id.reportView:
                //do stuff
                showToast("Report Button is clicked");
                navigatePage(ReportActivity.class);
                break;
            case R.id.recordView:
                //do stuff
                showToast("Record Button is clicked");
                navigatePage(RecordActivity.class);
                break;
            case R.id.leaderBoardView:
                //do stuff
                //navigatePage(LeaderBoardActivity.class);
                showToast("LeaderBoard Button is clicked");
                navigatePage(LeaderBoardActivity.class);
                break;
            case R.id.moreView:
                //do stuff
                //open floating menu
                showToast("More Button is clicked");
                break;
            case R.id.addView:
                //do stuff
                //open floating menu
                showToast("Add Button is clicked");
                break;
            default:
                //do stuff
                break;
        }
    }

    private void initializeTimelineSelector() {
        Spinner spinner = findViewById(R.id.timelineSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timeline_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {
                showToast("Spinner1: position=" + position + " id=" + id);
                //will need to refresh page with fresh data based on api call here determined by timeline value selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private void initializeButtons(){
        View view = getSupportActionBar().getCustomView();

        ImageButton homeButton= (ImageButton)view.findViewById(R.id.action_bar_home);
        homeButton.setOnClickListener(this);

        ImageButton menuButton= (ImageButton)view.findViewById(R.id.action_bar_menu);
        menuButton.setOnClickListener(this);

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

        ImageView addButton = findViewById(R.id.addView);
        addButton.setOnClickListener(this);
    }

    private void createAndAnimateProgressBars(){
        CircularProgressBar appointmentsProgress = findViewById(R.id.appointmentsProgress);
        appointmentsProgress.setColor(ContextCompat.getColor(this, R.color.colorMoonBlue));
        appointmentsProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        appointmentsProgress.setProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        appointmentsProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        int animationDuration = 2500; // 2500ms = 2,5s
        appointmentsProgress.setProgressWithAnimation(10, animationDuration); // Default duration = 1500ms

        CircularProgressBar contactsProgress = findViewById(R.id.contactsProgress);
        contactsProgress.setColor(ContextCompat.getColor(this, R.color.colorMoonBlue));
        contactsProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        contactsProgress.setProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        contactsProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        contactsProgress.setProgressWithAnimation(30, animationDuration); // Default duration = 1500ms

        CircularProgressBar bbSignedProgress = findViewById(R.id.bbSignedProgress);
        bbSignedProgress.setColor(ContextCompat.getColor(this, R.color.colorMoonBlue));
        bbSignedProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        bbSignedProgress.setProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        bbSignedProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        bbSignedProgress.setProgressWithAnimation(50, animationDuration); // Default duration = 1500ms

        CircularProgressBar listingsTakenProgress = findViewById(R.id.listingsTakenProgress);
        listingsTakenProgress.setColor(ContextCompat.getColor(this, R.color.colorMoonBlue));
        listingsTakenProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        listingsTakenProgress.setProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        listingsTakenProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        listingsTakenProgress.setProgressWithAnimation(70, animationDuration); // Default duration = 1500ms

        CircularProgressBar underContractProgress = findViewById(R.id.underContractProgress);
        underContractProgress.setColor(ContextCompat.getColor(this, R.color.colorMoonBlue));
        underContractProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        underContractProgress.setProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        underContractProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        underContractProgress.setProgressWithAnimation(90, animationDuration); // Default duration = 1500ms

        CircularProgressBar closedProgress = findViewById(R.id.closedProgress);
        closedProgress.setColor(ContextCompat.getColor(this, R.color.colorMoonBlue));
        closedProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        closedProgress.setProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        closedProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        closedProgress.setProgressWithAnimation(100, animationDuration); // Default duration = 1500ms
    }

    private void navigatePage(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
