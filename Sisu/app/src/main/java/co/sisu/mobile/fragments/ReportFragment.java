package co.sisu.mobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ReportListAdapter;
import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.Metric;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment implements AsyncServerEventListener {

    private ListView mListView;
    ParentActivity parentActivity;
    int selectedStartYear = 0;
    int selectedStartMonth = 0;
    int selectedStartDay = 0;
    int selectedEndYear = 0;
    int selectedEndMonth = 0;
    int selectedEndDay = 0;
    Calendar calendar = Calendar.getInstance();
    ProgressBar loader;
    String timeline = "day";
    Spinner spinner;

    public ReportFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
//        initializeListView(dataController.updateRecordMetrics());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        loader = parentActivity.findViewById(R.id.parentLoader);
        initializeListView();
        initializeTimelineSelector();
        spinner.setSelection(4);
    }

    private void initializeTimelineSelector() {
        spinner = getView().findViewById(R.id.reportsTimelineSelector);
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
                        timeline = "day";
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case 1:
                        //Last Week
                        timeline = "week";
                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        calendar.add(Calendar.DAY_OF_WEEK, 6);
                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case 2:
                        //This Week
                        timeline = "week";
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        calendar.add(Calendar.DAY_OF_WEEK, 6);
                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case 3:
                        //Last Month
                        timeline = "month";
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
                        timeline = "month";
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 5:
                        //Last year
                        timeline = "year";
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
                        timeline = "year";
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
                new AsyncActivities(ReportFragment.this, parentActivity.getAgentInfo().getAgent_id(), formattedStartTime, formattedEndTime, parentActivity.getJwtObject()).execute();
                //will need to refresh page with fresh data based on api call here determined by timeline value selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private int calculateProgressOnTrack(Metric metric) {
        int positionPercent = 0; //will determine blue
        int goalNum = metric.getGoalNum(); //monthly goal
        switch (timeline) {
            case "day":
                goalNum = metric.getDailyGoalNum();
                break;
            case "week":
                goalNum = metric.getWeeklyGoalNum();
                break;
            case "month":
                goalNum = metric.getGoalNum();
                break;
            case "year":
                goalNum = metric.getYearlyGoalNum();
                break;
        }

        int dayDifference = selectedEndDay - selectedStartDay;

        if(timeline.equalsIgnoreCase("week")) { //week
            int dayOfWeek = Calendar.DAY_OF_WEEK;
            int weekDifference = 7 - dayOfWeek;
//            goalNum = goalNum / calendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getCurrentNum() * weekDifference >= goalNum) {
                positionPercent = metric.getPercentComplete(timeline) + 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        } else if(timeline.equalsIgnoreCase("month")) { //month
            int dayOfMonth = Calendar.DAY_OF_MONTH;
            int monthDifference = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - dayOfMonth;
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getCurrentNum() * monthDifference >= goalNum) {
                positionPercent = metric.getPercentComplete(timeline) + 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        } else if(timeline.equalsIgnoreCase("year")) { //year
//            goalNum = goalNum * 12; //annual goal
            int dayOfYear = Calendar.DAY_OF_YEAR;
            int yearDifference = calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - dayOfYear;
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getCurrentNum() * yearDifference >= goalNum) {
                positionPercent = metric.getPercentComplete(timeline) + 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        }
        return positionPercent;
    }

    private List<Metric> initializeMetrics() {
        List<Metric> activities = parentActivity.getActivitiesObject();

        return activities;
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

    private void calculateProgressColor(Metric metric, int positionPercent) {
        if(getContext() != null) {
            if (metric.getPercentComplete(timeline) < positionPercent) {
                metric.setColor(ContextCompat.getColor(getContext(),R.color.colorMoonBlue));
            } else if (metric.getPercentComplete(timeline) > positionPercent && metric.getPercentComplete(timeline) <= 99 ) {
                metric.setColor(ContextCompat.getColor(getContext(),R.color.colorYellow));
            } else if (metric.getPercentComplete(timeline) > 99){
                metric.setColor(ContextCompat.getColor(getContext(),R.color.colorCorporateOrange));
            }
        }

    }

    private void initializeListView() {

        mListView = getView().findViewById(R.id.report_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);
    }

    private void setData(List<Metric> metricList) {
        if(getContext() != null) {
            for (int i = 0; i < metricList.size(); i++) {
                calculateProgressColor(metricList.get(i), calculateProgressOnTrack(metricList.get(i)));
            }
            ReportListAdapter adapter = new ReportListAdapter(getContext(), metricList, timeline);
            mListView.setAdapter(adapter);
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
                    setData(parentActivity.getActivitiesObject());
                }
            });
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }
}
