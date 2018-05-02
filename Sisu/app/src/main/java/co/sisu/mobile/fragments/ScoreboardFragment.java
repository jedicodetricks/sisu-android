package co.sisu.mobile.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    int selectedStartYear = 0;
    int selectedStartMonth = 0;
    int selectedStartDay = 0;
    int selectedEndYear = 0;
    int selectedEndMonth = 0;
    int selectedEndDay = 0;
    Calendar calendar = Calendar.getInstance();
    ProgressBar loader;

    private CircularProgressBar contactsProgress, contactsProgressMark, appointmentsProgress, appointmentsProgressMark, bbSignedProgress, bbSignedProgressMark,
            listingsTakenProgress, listingsTakenProgressMark, underContractProgress, underContractProgressMark, closedProgress, closedProgressMark;

    private TextView contactsCurrentNumber, contactsGoalNumber, appointmentsCurrentNumber, appointmentsGoalNumber, bbSignedCurrentNumber, bbSignedGoalNumber,
            listingsTakenCurrentNumber, listingsTakenGoalNumber, underContractCurrentNumber, underContractGoalNumber, closedCurrentNumber, closedGoalNumber;

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
        loader = view.findViewById(R.id.scoreboardLoader);

        initializeTimelineSelector();
        initializeButtons();
        initProgressBars();

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
                calendar = Calendar.getInstance();
                loader.setVisibility(View.VISIBLE);

                switch (position) {
                    case 0:
                        //Today
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case 1:
                        //Last Week
                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        calendar.add(Calendar.DAY_OF_WEEK, 7);
                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case 2:
                        //This Week
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);
                        calendar.add(Calendar.DAY_OF_WEEK, 7);

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case 3:
                        //Last Month
                        calendar.add(Calendar.MONTH, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 4:
                        //This Month
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 5:
                        //Last year
                        calendar.add(Calendar.YEAR, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = 12;
                        selectedEndDay = 31;
                        break;
                    case 6:
                        //This year
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = 12;
                        selectedEndDay = 31;
                        break;
                }

                String formattedStartMonth = String.valueOf(selectedStartMonth);
                String formattedEndMonth = String.valueOf(selectedEndMonth);
                String formattedStartDay = String.valueOf(selectedStartDay);
                String formattedEndDay = String.valueOf(selectedEndDay);

                if(selectedStartDay < 10) {
                    formattedStartDay = "0" + selectedStartDay;
                }

                if(selectedEndDay < 10) {
                    formattedEndDay = "0" + selectedEndDay;
                }

                if(selectedStartMonth < 10) {
                    formattedStartMonth = "0" + selectedStartMonth;
                }

                if(selectedEndMonth < 10) {
                    formattedEndMonth = "0" + selectedEndMonth;
                }



                String formattedStartTime = selectedStartYear + "-" + formattedStartMonth + "-" + formattedStartDay;
                String formattedEndTime = selectedEndYear + "-" + formattedEndMonth + "-" + formattedEndDay;
                new AsyncActivities(ScoreboardFragment.this, parentActivity.getAgentInfo().getAgent_id(), formattedStartTime, formattedEndTime).execute();

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
        sdf = new SimpleDateFormat("yyyy");
        String thisYear = sdf.format(calendar.getTime());

        calendar.add(Calendar.YEAR, -1);
        String lastYear = sdf.format(calendar.getTime());
        spinnerArray.add(lastYear);
        spinnerArray.add(thisYear);
//        spinnerArray.add("All Records");

        return spinnerArray;
    }

    private void initializeButtons(){
        ImageView addButton = getView().findViewById(R.id.addView);
        addButton.setOnClickListener(this);

        CircularProgressBar contact = getView().findViewById(R.id.contactsProgressMark);
        contact.setOnClickListener(this);

        CircularProgressBar appointments = getView().findViewById(R.id.appointmentsProgressMark);
        appointments.setOnClickListener(this);

        CircularProgressBar signed = getView().findViewById(R.id.bbSignedProgressMark);
        signed.setOnClickListener(this);

        CircularProgressBar listing = getView().findViewById(R.id.listingsTakenProgressMark);
        listing.setOnClickListener(this);

        CircularProgressBar underContract = getView().findViewById(R.id.underContractProgressMark);
        underContract.setOnClickListener(this);

        CircularProgressBar closed = getView().findViewById(R.id.closedProgressMark);
        closed.setOnClickListener(this);
    }

    private void initProgressBars() {
        contactsProgress = getView().findViewById(R.id.contactsProgress);
        contactsProgressMark = getView().findViewById(R.id.contactsProgressMark);
        contactsCurrentNumber = getView().findViewById(R.id.contactsCurrentNumber);
        contactsGoalNumber = getView().findViewById(R.id.contactsGoalNumber);

        appointmentsProgress = getView().findViewById(R.id.appointmentsProgress);
        appointmentsProgressMark = getView().findViewById(R.id.appointmentsProgressMark);
        appointmentsCurrentNumber = getView().findViewById(R.id.appointmentsCurrentNumber);
        appointmentsGoalNumber = getView().findViewById(R.id.appointmentsGoalNumber);

        bbSignedProgress = getView().findViewById(R.id.bbSignedProgress);
        bbSignedProgressMark = getView().findViewById(R.id.bbSignedProgressMark);
        bbSignedCurrentNumber = getView().findViewById(R.id.bbsignedCurrentNumber);
        bbSignedGoalNumber = getView().findViewById(R.id.bbsignedGoalNumber);

        listingsTakenProgress = getView().findViewById(R.id.listingsTakenProgress);
        listingsTakenProgressMark = getView().findViewById(R.id.listingsTakenProgressMark);
        listingsTakenCurrentNumber = getView().findViewById(R.id.listingsTakenCurrentNumber);
        listingsTakenGoalNumber = getView().findViewById(R.id.listingsTakenGoalNumber);

        underContractProgress = getView().findViewById(R.id.underContractProgress);
        underContractProgressMark = getView().findViewById(R.id.underContractProgressMark);
        underContractCurrentNumber = getView().findViewById(R.id.underContractCurrentNumber);
        underContractGoalNumber = getView().findViewById(R.id.underContactGoalNumber);

        closedProgress = getView().findViewById(R.id.closedProgress);
        closedProgressMark = getView().findViewById(R.id.closedProgressMark);
        closedCurrentNumber = getView().findViewById(R.id.closedCurrentNumber);
        closedGoalNumber = getView().findViewById(R.id.closedGoalNumber);
    }

    private void animateProgressBars(List<Metric> metricList){

        for(int i = 0; i < metricList.size(); i++) {

            switch(metricList.get(i).getTitle()) {
                case "Contacts":
                    Metric contactsMetric = metricList.get(i);
                    setupProgressBar(contactsMetric, contactsProgress, contactsProgressMark, contactsCurrentNumber, contactsGoalNumber);
                    break;

                case "1st Time Appts":
                    Metric appointmentsMetric = metricList.get(i);
                    setupProgressBar(appointmentsMetric, appointmentsProgress, appointmentsProgressMark, appointmentsCurrentNumber, appointmentsGoalNumber);
                    break;

                case "Buyer Signed":
                    Metric bbSignedMetric = metricList.get(i);
                    setupProgressBar(bbSignedMetric, bbSignedProgress, bbSignedProgressMark, bbSignedCurrentNumber, bbSignedGoalNumber);
                    break;

                case "Open Houses":
                    Metric listingsTakenMetric = metricList.get(i);
                    setupProgressBar(listingsTakenMetric, listingsTakenProgress, listingsTakenProgressMark, listingsTakenCurrentNumber, listingsTakenGoalNumber);
                    break;

                case "Buyer Under Contract":
                    Metric underContractMetric = metricList.get(i);
                    setupProgressBar(underContractMetric, underContractProgress, underContractProgressMark, underContractCurrentNumber, underContractGoalNumber);
                    break;

                case "Buyer Closed":
                    Metric closedMetric = metricList.get(i);
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
                launchAddClient();
                break;
            case R.id.contactsProgressMark:
                navigateToClientList("pipeline");
                break;
            case R.id.appointmentsProgressMark:
                navigateToClientList("pipeline");
                break;
            case R.id.bbSignedProgressMark:
                navigateToClientList("signed");
                break;
            case R.id.listingsTakenProgressMark:
                navigateToClientList("signed");
                break;
            case R.id.underContractProgressMark:
                navigateToClientList("contract");
                break;
            case R.id.closedProgressMark:
                navigateToClientList("closed");
                break;
            default:
                break;
        }
    }

    private void navigateToClientList(String tabName){
        parentActivity.navigateToClientList(tabName);
    }

    private void launchAddClient() {
        Intent intent = new Intent(getContext(), AddClientActivity.class);
        intent.putExtra("Agent", parentActivity.getAgentInfo());
        startActivity(intent);
    }

    private void showToast(CharSequence msg){
        if(getContext() != null) {
            Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Activities")) {
            parentActivity.setActivitiesObject(returnObject);
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    animateProgressBars(parentActivity.getScoreboardObject());
                }
            });
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }
}
