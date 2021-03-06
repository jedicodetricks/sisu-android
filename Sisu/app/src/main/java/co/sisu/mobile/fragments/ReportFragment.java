package co.sisu.mobile.fragments;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.DropdownAdapter;
import co.sisu.mobile.adapters.ReportListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.DateManager;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.Metric;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment implements AsyncServerEventListener {

    private ListView mListView;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private DateManager dateManager;
//    private int selectedStartYear = 0;
//    private int selectedStartMonth = 0;
//    private int selectedStartDay = 0;
//    private int selectedEndYear = 0;
//    private int selectedEndMonth = 0;
//    private int selectedEndDay = 0;
//    private Calendar calendar = Calendar.getInstance();
    private ProgressBar loader;
    private Spinner spinner;
    private boolean pastTimeline;
//    private String formattedStartTime;
//    private String formattedEndTime;

    public ReportFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
//        loader.setVisibility(View.VISIBLE);
//        apiManager.sendAsyncActivities(ReportFragment.this, dataController.getAgent().getAgent_id(), formattedStartTime, formattedEndTime, dataController.getCurrentSelectedTeamMarketId());
//        setColorScheme();
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
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        dateManager = parentActivity.getDateManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        initializeListView();
        initializeTimelineSelector();
        spinner.setSelection(dateManager.getTimelineSelection());
        setColorScheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "ReportFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        FirebaseAnalytics.getInstance(parentActivity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void setColorScheme() {
        spinner.setPopupBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
        spinner.getBackground().setColorFilter(colorSchemeManager.getIconSelected(), PorterDuff.Mode.SRC_ATOP);

        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark));
            loader.getIndeterminateDrawable().setBounds(bounds);
        } else {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            loader.getIndeterminateDrawable().setBounds(bounds);
        }
    }

    private void initializeTimelineSelector() {
        spinner = getView().findViewById(R.id.reportsTimelineSelector);
        List<String> spinnerArray = initSpinnerArray();
        DropdownAdapter adapter = new DropdownAdapter(getContext(), R.layout.spinner_item, spinnerArray, colorSchemeManager);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                calendar = Calendar.getInstance();
                loader.setVisibility(View.VISIBLE);

                switch (position) {
                    case 0:
                        //Yesterday
                        dateManager.setToYesterday();
                        pastTimeline = true;

//                        parentActivity.setTimeline("day");
//                        parentActivity.setTimelineSelection(0);
//                        calendar.add(Calendar.DAY_OF_MONTH, -1);
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);
//
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                    case 1:
                        //Today
                        dateManager.setToToday();
                        pastTimeline = false;

//                        parentActivity.setTimeline("day");
//                        parentActivity.setTimelineSelection(1);
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);
//
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        break;
                    case 2:
                        //Last Week
                        dateManager.setToLastWeek();
                        pastTimeline = true;

//                        parentActivity.setTimeline("week");
//                        parentActivity.setTimelineSelection(2);
//                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
//                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
//
//                        calendar.add(Calendar.DAY_OF_WEEK, 6);
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                        break;
                    case 3:
                        //This Week
                        dateManager.setToThisWeek();
                        pastTimeline = false;

//                        parentActivity.setTimeline("week");
//                        parentActivity.setTimelineSelection(3);
//                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
//
//                        calendar.add(Calendar.DAY_OF_WEEK, 6);
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                        break;
                    case 4:
                        //Last Month
                        dateManager.setToLastMonth();
                        pastTimeline = true;

//                        parentActivity.setTimeline("month");
//                        parentActivity.setTimelineSelection(4);
//                        calendar.add(Calendar.MONTH, -1);
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedStartDay = 1;
//
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 5:
                        //This Month
                        dateManager.setToThisMonth();
                        pastTimeline = false;

//                        parentActivity.setTimeline("month");
//                        parentActivity.setTimelineSelection(5);
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedStartDay = 1;
//
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
//                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 6:
                        //Last year
                        dateManager.setToLastYear();
                        pastTimeline = true;

//                        parentActivity.setTimeline("year");
//                        parentActivity.setTimelineSelection(6);
//                        calendar.add(Calendar.YEAR, -1);
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = 1;
//                        selectedStartDay = 1;
//
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = 12;
//                        selectedEndDay = 31;
                        break;
                    case 7:
                        //This year
                        dateManager.setToThisYear();
                        pastTimeline = false;

//                        parentActivity.setTimeline("year");
//                        parentActivity.setTimelineSelection(7);
//                        selectedStartYear = calendar.get(Calendar.YEAR);
//                        selectedStartMonth = 1;
//                        selectedStartDay = 1;
//
//                        selectedEndYear = calendar.get(Calendar.YEAR);
//                        selectedEndMonth = 12;
//                        selectedEndDay = 31;
                        break;
                }
//                String formattedStartMonth = String.valueOf(selectedStartMonth);
//                String formattedEndMonth = String.valueOf(selectedEndMonth);
//                String formattedStartDay = String.valueOf(selectedStartDay);
//                String formattedEndDay = String.valueOf(selectedEndDay);
//
//                if(selectedStartDay < 10) {
//                    formattedStartDay = "0" + selectedStartDay;
//                }
//
//                if(selectedEndDay < 10) {
//                    formattedEndDay = "0" + selectedEndDay;
//                }
//
//                if(selectedStartMonth < 10) {
//                    formattedStartMonth = "0" + selectedStartMonth;
//                }
//
//                if(selectedEndMonth < 10) {
//                    formattedEndMonth = "0" + selectedEndMonth;
//                }
//
//                formattedStartTime = selectedStartYear + "-" + formattedStartMonth + "-" + formattedStartDay;
//                formattedEndTime = selectedEndYear + "-" + formattedEndMonth + "-" + formattedEndDay;
                apiManager.sendAsyncActivities(ReportFragment.this, dataController.getAgent().getAgent_id(), dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime(), dataController.getCurrentSelectedTeamMarketId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private int calculateProgressOnTrack(Metric metric) {
        int positionPercent = 0; //will determine blue
        double goalNum = metric.getGoalNum(); //monthly goal
        Calendar calendar = Calendar.getInstance();
        switch (dateManager.getTimeline()) {
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

        if(dateManager.getTimeline().equalsIgnoreCase("week")) { //week
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            positionPercent = (int) (((double)dayOfWeek / (double)calendar.getActualMaximum(Calendar.DAY_OF_WEEK)) * 100);
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getPercentComplete(dateManager.getTimeline()) >= positionPercent) {
                positionPercent = metric.getPercentComplete(dateManager.getTimeline()) - 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        } else if(dateManager.getTimeline().equalsIgnoreCase("month")) { //month
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            positionPercent = (int) (((double)dayOfMonth / (double)calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) * 100);
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getPercentComplete(dateManager.getTimeline()) >= positionPercent) {
                positionPercent = metric.getPercentComplete(dateManager.getTimeline()) - 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        } else if(dateManager.getTimeline().equalsIgnoreCase("year")) { //year
//            goalNum = goalNum * 12; //annual goal
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            positionPercent = (int) (((double)dayOfYear / (double)calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) * 100);
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getPercentComplete(dateManager.getTimeline()) >= positionPercent) {
                positionPercent = metric.getPercentComplete(dateManager.getTimeline()) - 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        }
        return positionPercent;
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Yesterday");
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
            if(pastTimeline) {
                if(metric.getPercentComplete(dateManager.getTimeline()) < 100) {
                    metric.setColor(ContextCompat.getColor(getContext(),R.color.sisuBlue));
                }
                else {
                    metric.setColor(ContextCompat.getColor(getContext(),R.color.sisuOrange));
                }
            }
            else {
                if (metric.getPercentComplete(dateManager.getTimeline()) < positionPercent) {
                    metric.setColor(ContextCompat.getColor(getContext(), R.color.sisuBlue));
                } else if (metric.getPercentComplete(dateManager.getTimeline()) > 99) {
                    metric.setColor(ContextCompat.getColor(getContext(), R.color.sisuOrange));
                } else {
                    metric.setColor(ContextCompat.getColor(getContext(), R.color.sisuYellow));
                }
            }
        }

    }

    private void initializeListView() {
        mListView = getView().findViewById(R.id.report_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);
    }

    private void setData(List<Metric> metricList) {
        List<Metric> prunedList = new ArrayList<>();

        for(Metric metric: metricList) {
            metric.setTitle(parentActivity.localizeLabel(metric.getTitle()));
            if(metric.getCurrentNum() > 0 || partOfImportantList(metric)) {
//                metric.setTitle(dataController.localizeLabel(metric.getTitle()));
                prunedList.add(metric);
            }
        }

        if(getContext() != null) {
            for (int i = 0; i < metricList.size(); i++) {
                calculateProgressColor(metricList.get(i), calculateProgressOnTrack(metricList.get(i)));
            }
            ReportListAdapter adapter = new ReportListAdapter(getContext(), prunedList, dateManager.getTimeline(), colorSchemeManager, dataController.getFirstOtherActivity());
            mListView.setAdapter(adapter);

        }

    }

    private boolean partOfImportantList(Metric metric) {
        switch (metric.getType()) {
            case "CONTA":
            case "BAPPT":
            case "SAPPT":
            case "BSGND":
            case "SSGND":
            case "BUNDC":
            case "SUNDC":
            case "BCLSD":
            case "SCLSD":
            case "SGND":
            case "1TAPT":
            case "CLSD":
            case "UCNTR":
            case "LSTT":
            case "BBSGD":
                return true;
        }

        return false;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.GET_ACTIVITIES) {
            AsyncActivitiesJsonObject activitiesObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitiesJsonObject.class);
            dataController.setActivitiesObject(activitiesObject, parentActivity.isRecruiting());
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    setData(dataController.getActivitiesObject());
                }
            });
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}
