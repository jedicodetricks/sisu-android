package co.sisu.mobile.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.Metric;

// TODO: 2/20/2018 remove Toasts with links/buttons when proper functionality replaces them

public class ScoreboardActivity extends AppCompatActivity implements View.OnClickListener {

    DataController dataController = new DataController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        getSupportActionBar().setElevation(0);
        TextView pageTitle = findViewById(R.id.action_bar_title);
        pageTitle.setText("Scoreboard");
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
            case R.id.scoreboardView:
                //do stuff
                navigatePage(ScoreboardActivity.class);
                break;
            case R.id.reportView:
                //do stuff
                navigatePage(ReportActivity.class);
                break;
            case R.id.recordView:
                //do stuff
                navigatePage(RecordActivity.class);
                break;
            case R.id.leaderBoardView:
                //do stuff
                navigatePage(LeaderBoardActivity.class);
                break;
            case R.id.moreView:
                //do stuff
                navigatePage(ParentActivity.class);
                break;
//            case R.id.addView:
//                //do stuff
//                //open floating menu
//                showToast("Add Button is clicked");
//                break;
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
//                showToast("Spinner1: position=" + position + " id=" + id);
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

        ImageView addButton = findViewById(R.id.addView);
        addButton.setOnClickListener(this);
    }

    private void createAndAnimateProgressBars(){
        final int ANIMATION_DURATION = 2500; // 2500ms = 2,5s
        final List<Metric> metricList = dataController.getMetrics();

        Metric contactsMetric = metricList.get(0);
        CircularProgressBar contactsProgress = findViewById(R.id.contactsProgress);
        contactsProgress.setColor(ContextCompat.getColor(this, contactsMetric.getColor()));
        contactsProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        contactsProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        contactsProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        contactsProgress.setProgressWithAnimation(contactsMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView contactsCurrentNumber = findViewById(R.id.contactsCurrentNumber);
        TextView contactsGoalNumber = findViewById(R.id.contactsGoalNumber);
        contactsCurrentNumber.setText(String.valueOf(contactsMetric.getCurrentNum()));
        contactsGoalNumber.setText(String.valueOf(contactsMetric.getGoalNum()));

        Metric appointmentsMetric = metricList.get(1);
        CircularProgressBar appointmentsProgress = findViewById(R.id.appointmentsProgress);
        appointmentsProgress.setColor(ContextCompat.getColor(this, appointmentsMetric.getColor()));
        appointmentsProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        appointmentsProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        appointmentsProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        appointmentsProgress.setProgressWithAnimation(appointmentsMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView appointmentsCurrentNumber = findViewById(R.id.appointmentsCurrentNumber);
        TextView appointmentsGoalNumber = findViewById(R.id.appointmentsGoalNumber);
        appointmentsCurrentNumber.setText(String.valueOf(appointmentsMetric.getCurrentNum()));
        appointmentsGoalNumber.setText(String.valueOf(appointmentsMetric.getGoalNum()));

        Metric bbSignedMetric = metricList.get(2);
        CircularProgressBar bbSignedProgress = findViewById(R.id.bbSignedProgress);
        bbSignedProgress.setColor(ContextCompat.getColor(this, bbSignedMetric.getColor()));
        bbSignedProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        bbSignedProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        bbSignedProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        bbSignedProgress.setProgressWithAnimation(bbSignedMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView bbSignedCurrentNumber = findViewById(R.id.bbsignedCurrentNumber);
        TextView bbSignedGoalNumber = findViewById(R.id.bbsignedGoalNumber);
        bbSignedCurrentNumber.setText(String.valueOf(bbSignedMetric.getCurrentNum()));
        bbSignedGoalNumber.setText(String.valueOf(bbSignedMetric.getGoalNum()));

        Metric listingsTakenMetric = metricList.get(3);
        CircularProgressBar listingsTakenProgress = findViewById(R.id.listingsTakenProgress);
        listingsTakenProgress.setColor(ContextCompat.getColor(this, listingsTakenMetric.getColor()));
        listingsTakenProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        listingsTakenProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        listingsTakenProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        listingsTakenProgress.setProgressWithAnimation(listingsTakenMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView listingsTakenCurrentNumber = findViewById(R.id.listingsTakenCurrentNumber);
        TextView listingsTakenGoalNumber = findViewById(R.id.listingsTakenGoalNumber);
        listingsTakenCurrentNumber.setText(String.valueOf(listingsTakenMetric.getCurrentNum()));
        listingsTakenGoalNumber.setText(String.valueOf(listingsTakenMetric.getGoalNum()));

        Metric underContractMetric = metricList.get(4);
        CircularProgressBar underContractProgress = findViewById(R.id.underContractProgress);
        underContractProgress.setColor(ContextCompat.getColor(this, underContractMetric.getColor()));
        underContractProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        underContractProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        underContractProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        underContractProgress.setProgressWithAnimation(underContractMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView underContractCurrentNumber = findViewById(R.id.underContractCurrentNumber);
        TextView underContractGoalNumber = findViewById(R.id.underContactGoalNumber);
        underContractCurrentNumber.setText(String.valueOf(underContractMetric.getCurrentNum()));
        underContractGoalNumber.setText(String.valueOf(underContractMetric.getGoalNum()));

        Metric closedMetric = metricList.get(5);
        CircularProgressBar closedProgress = findViewById(R.id.closedProgress);
        closedProgress.setColor(ContextCompat.getColor(this, closedMetric.getColor()));
        closedProgress.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        closedProgress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        closedProgress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        closedProgress.setProgressWithAnimation(closedMetric.getPercentComplete(), ANIMATION_DURATION);
        TextView closedCurrentNumber = findViewById(R.id.closedCurrentNumber);
        TextView closedGoalNumber = findViewById(R.id.closedGoalNumber);
        closedCurrentNumber.setText(String.valueOf(closedMetric.getCurrentNum()));
        closedGoalNumber.setText(String.valueOf(closedMetric.getGoalNum()));

    }

    private void navigatePage(Class c){
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
