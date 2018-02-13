package co.sisu.mobile.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAndAnimateProgressBars();
        initializeTimelineSelector();
    }

    private void initializeTimelineSelector() {
        Spinner spinner = (Spinner) findViewById(R.id.timelineSelector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timeline_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    public void createAndAnimateProgressBars(){
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
}
