package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.RecordListAdapter;
import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.models.Metric;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, RecordEventHandler, AsyncServerEventListener {


    private ListView mListView;
    int selectedYear, selectedMonth, selectedDay;
    List<Metric> metricList;
    ParentActivity parentActivity;
    Calendar calendar = Calendar.getInstance();
    ProgressBar loader;

    public RecordFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
//        initializeListView(dataController.updateRecordMetrics());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        calendar = Calendar.getInstance();
        Date d = calendar.getTime();
        new AsyncActivities(this, parentActivity.getAgentInfo().getAgent_id(), d, d).execute();
        loader = view.findViewById(R.id.recordLoader);
        loader.setVisibility(View.VISIBLE);

        initializeCalendarHandler();
        TextView save = parentActivity.findViewById(R.id.saveButton);
        save.setOnClickListener(this);
    }

    private void initializeCalendarHandler() {

        final ImageView calendarLauncher = getView().findViewById(R.id.calender_date_picker);
        final TextView dateDisplay = getView().findViewById(R.id.record_date);

        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        updateDisplayDate(selectedYear, selectedMonth, selectedDay);

        calendarLauncher.setOnClickListener(this);
        dateDisplay.setOnClickListener(this);
    }

    private void updateDisplayDate(int year, int month, int day) {
        selectedYear = year;
        selectedMonth = month;
        selectedDay = day;

        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;

        try {
            d = formatter.parse(formatDate);
            Calendar updatedTime = Calendar.getInstance();
            updatedTime.setTime(d);

            TextView dateDisplay = getView().findViewById(R.id.record_date);
            dateDisplay.setText(sdf.format(updatedTime.getTime()));
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error parsing selected date", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void initializeListView(List<Metric> metricList) {
        mListView = getView().findViewById(R.id.record_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        RecordListAdapter adapter = new RecordListAdapter(getContext(), metricList, this);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//        Metric metric = (Metric) adapterView.getItemAtPosition(position);
////        Toast.makeText(getContext(), String.valueOf(metric.getCurrentNum() + ":" + id), Toast.LENGTH_SHORT).show();
//        Log.e("INCOMING NUMBER", String.valueOf(id));
//        metric.setCurrentNum((int) id);
//        if(id == 0 && metric.getCurrentNum() > 0) {
//            if(recordMetric(metric)){
//                metric.setCurrentNum(metric.getCurrentNum() - 1);
//            }
//        }
//        else if(id == 1){
//            if(recordMetric(metric)){
//                metric.setCurrentNum(metric.getCurrentNum() + 1);
//            }
//        }
    }

    private boolean recordMetric() {
        //open clients view to select contact to add this metric
        //need to have a method return true if metric is saved successfully then set to recordSaved to return to successfully increment number
        parentActivity.stackReplaceFragment(ClientListFragment.class);
        parentActivity.swapToClientListBar();

        return parentActivity.isRecordSaved();//returns the value set by saveButton within Client
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if(year != selectedYear || month != selectedMonth || day != selectedDay) {
                    updateDisplayDate(year, month, day);
                    updateRecordInfo();
                }
                else {
                    Toast.makeText(getContext(), "You have selected the same day", Toast.LENGTH_SHORT).show();
                }

            }
        }, selectedYear, selectedMonth, selectedDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    private void updateRecordInfo() {
        String formattedMonth = String.valueOf(selectedMonth + 1);
        if(selectedMonth < 10) {
            formattedMonth = "0" + formattedMonth;
        }
        String formattedDay = String.valueOf(selectedDay);
        if(selectedDay < 10) {
            formattedDay = "0" + formattedDay;
        }

        String formattedDate = selectedYear + "-" + formattedMonth + "-" + formattedDay;
        parentActivity.updateSelectedRecordDate(formattedDate);
        new AsyncActivities(this, parentActivity.getAgentInfo().getAgent_id(), formattedDate, formattedDate).execute();
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calender_date_picker:
            case R.id.record_date:
                showDatePickerDialog();
                break;
            case R.id.saveButton:
                updateRecords();
                saveRecords();
                break;
            default:
                break;
        }
    }

    private void updateRecords() {
        // TODO: 4/30/2018 set activities object model with current stats
    }

    private void saveRecords() {
        if(parentActivity.getUpdatedRecords().size() > 0) {
            parentActivity.updateRecordedActivities();
        }
        Toast.makeText(parentActivity, "Record Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNumberChanged(Metric metric, int newNum) {
                metric.setCurrentNum(newNum);
                parentActivity.setRecordUpdated(metric);
    }

    @Override
    public void onClientDirectorClicked(Metric metric) {
        switch(metric.getTitle()) {
            case "1st Time Appts":
                break;
            case "Buyer Signed":
                recordMetric();
                break;
            case "Seller Signed":
                recordMetric();
                break;
            case "Buyer Under Contract":
                break;
            case "Seller Under Contract":
                break;
            case "Closed":
                break;
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
                    metricList = parentActivity.getActivitiesObject();
                    initializeListView(metricList);
                }
            });
        }

    }

    @Override
    public void onEventFailed(Object o, String s) {

    }
}
