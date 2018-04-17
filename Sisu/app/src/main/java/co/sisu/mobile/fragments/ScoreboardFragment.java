package co.sisu.mobile.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.AddClientActivity;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.utils.CircularProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    ParentActivity parentActivity;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
//        createAndAnimateProgressBars(dataController.updateScoreboardTimeline());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_scoreboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();

        initializeTimelineSelector();
        initializeButton();
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        new AsyncActivities(this, parentActivity.getAgentInfo().getAgent_id(), d, d).execute();

    }

    private void initializeTimelineSelector() {
        Spinner spinner = getView().findViewById(R.id.timelineSelector);
        List<String> spinnerArray = initSpinnerArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.spinner_item,
                spinnerArray
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                showToast("Spinner1: position=" + position + " id=" + id);
//                createAndAnimateProgressBars(dataController.updateScoreboardTimeline());
                //will need to refresh page with fresh data based on api call here determined by timeline value selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Today");
        spinnerArray.add("Last Week");
        spinnerArray.add("This Week");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");

        String thisMonth = sdf.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String lastMonth = sdf.format(calendar.getTime());
        spinnerArray.add(lastMonth);
        spinnerArray.add(thisMonth);

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("YYYY");
        String thisYear = sdf.format(calendar.getTime());

        calendar.add(Calendar.YEAR, -1);
        String lastYear = sdf.format(calendar.getTime());
        spinnerArray.add(lastYear);
        spinnerArray.add(thisYear);
        spinnerArray.add("All Records");

        return spinnerArray;
    }

    private void initializeButton(){
        ImageView addButton = getView().findViewById(R.id.addView);
        addButton.setOnClickListener(this);
    }

    private void createAndAnimateProgressBars(List<Metric> metricList){

        for(int i = 0; i < metricList.size(); i++) {

            switch(metricList.get(i).getTitle()) {
                case "Contacts":
                    Metric contactsMetric = metricList.get(i);
                    CircularProgressBar contactsProgress = getView().findViewById(R.id.contactsProgress);
                    CircularProgressBar contactsProgressMark = getView().findViewById(R.id.contactsProgressMark);
                    TextView contactsCurrentNumber = getView().findViewById(R.id.contactsCurrentNumber);
                    TextView contactsGoalNumber = getView().findViewById(R.id.contactsGoalNumber);
                    setupProgressBar(contactsMetric, contactsProgress, contactsProgressMark, contactsCurrentNumber, contactsGoalNumber);

                    break;

                case "Appointments":
                    Metric appointmentsMetric = metricList.get(i);
                    CircularProgressBar appointmentsProgress = getView().findViewById(R.id.appointmentsProgress);
                    CircularProgressBar appointmentsProgressMark = getView().findViewById(R.id.appointmentsProgressMark);
                    TextView appointmentsCurrentNumber = getView().findViewById(R.id.appointmentsCurrentNumber);
                    TextView appointmentsGoalNumber = getView().findViewById(R.id.appointmentsGoalNumber);
                    setupProgressBar(appointmentsMetric, appointmentsProgress, appointmentsProgressMark, appointmentsCurrentNumber, appointmentsGoalNumber);

                    break;

                case "Buyer Signed":
                    Metric bbSignedMetric = metricList.get(i);
                    CircularProgressBar bbSignedProgress = getView().findViewById(R.id.bbSignedProgress);
                    CircularProgressBar bbSignedProgressMark = getView().findViewById(R.id.bbSignedProgressMark);
                    TextView bbSignedCurrentNumber = getView().findViewById(R.id.bbsignedCurrentNumber);
                    TextView bbSignedGoalNumber = getView().findViewById(R.id.bbsignedGoalNumber);
                    setupProgressBar(bbSignedMetric, bbSignedProgress, bbSignedProgressMark, bbSignedCurrentNumber, bbSignedGoalNumber);

                    break;

                case "Open Houses":
                    Metric listingsTakenMetric = metricList.get(i);
                    CircularProgressBar listingsTakenProgress = getView().findViewById(R.id.listingsTakenProgress);
                    CircularProgressBar listingsTakenProgressMark = getView().findViewById(R.id.listingsTakenProgressMark);
                    TextView listingsTakenCurrentNumber = getView().findViewById(R.id.listingsTakenCurrentNumber);
                    TextView listingsTakenGoalNumber = getView().findViewById(R.id.listingsTakenGoalNumber);
                    setupProgressBar(listingsTakenMetric, listingsTakenProgress, listingsTakenProgressMark, listingsTakenCurrentNumber, listingsTakenGoalNumber);

                    break;

                case "Buyer Under Contract":
                    Metric underContractMetric = metricList.get(i);
                    CircularProgressBar underContractProgress = getView().findViewById(R.id.underContractProgress);
                    CircularProgressBar underContractProgressMark = getView().findViewById(R.id.underContractProgressMark);
                    TextView underContractCurrentNumber = getView().findViewById(R.id.underContractCurrentNumber);
                    TextView underContractGoalNumber = getView().findViewById(R.id.underContactGoalNumber);
                    setupProgressBar(underContractMetric, underContractProgress, underContractProgressMark, underContractCurrentNumber, underContractGoalNumber);

                    break;

                case "Buyer Closed":
                    Metric closedMetric = metricList.get(i);
                    CircularProgressBar closedProgress = getView().findViewById(R.id.closedProgress);
                    CircularProgressBar closedProgressMark = getView().findViewById(R.id.closedProgressMark);
                    TextView closedCurrentNumber = getView().findViewById(R.id.closedCurrentNumber);
                    TextView closedGoalNumber = getView().findViewById(R.id.closedGoalNumber);
                    setupProgressBar(closedMetric, closedProgress, closedProgressMark, closedCurrentNumber, closedGoalNumber);

                    break;
            }
        }

    }

    public void setupProgressBar(Metric metric, CircularProgressBar progress, CircularProgressBar progressMark, TextView currentNumber, TextView goalNumber) {
        final int ANIMATION_DURATION = 1500; // Time in millis
        final int PROGRESS_MARK = calculateProgressMarkPosition(metric);
        calculateProgressColor(metric, PROGRESS_MARK);
        Context context = getContext();
        progress.setColor(metric.getColor());
        progress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
        progress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setProgressWithAnimation(metric.getPercentComplete(), ANIMATION_DURATION);
        currentNumber.setText(String.valueOf(metric.getCurrentNum()));
        goalNumber.setText(String.valueOf(metric.getGoalNum()));
        progressMark.setStartAngle(PROGRESS_MARK);
        progressMark.setColor(ContextCompat.getColor(context, R.color.colorWhite));
        progressMark.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progressMark.setProgressWithAnimation(1, 0);
    }

    private int calculateProgressMarkPosition(Metric metric) {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int position = -90;
        for(int i = 1; i <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            position += 12;
            if(i == currentDay) {
                break;
            }
        }
        return position;
    }

    private void calculateProgressColor(Metric metric, int position) {
        position += 90;
        int positionPercent = (int) (((double)position/(double)360) * 100);
        Context context = getContext();
        if (metric.getPercentComplete() < positionPercent) {
            metric.setColor(ContextCompat.getColor(context,R.color.colorMoonBlue));
        } else if (metric.getPercentComplete() == positionPercent) {
            metric.setColor(ContextCompat.getColor(context,R.color.colorYellow));
        } else {
            metric.setColor(ContextCompat.getColor(context,R.color.colorCorporateOrange));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addView:
                navigatePage(AddClientActivity.class);
                break;
            default:
                break;
        }
    }

    private void navigatePage(Class c){
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }

    private void showToast(CharSequence msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Activities")) {
            parentActivity.setActivitiesObject(returnObject);
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createAndAnimateProgressBars(parentActivity.getActivitiesObject());
                }
            });
        }
    }

    @Override
    public void onEventFailed() {

    }
}
