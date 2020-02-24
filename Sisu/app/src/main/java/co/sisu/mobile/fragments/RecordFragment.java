package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.RecordListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.Metric;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener, RecordEventHandler, AsyncServerEventListener, PopupMenu.OnMenuItemClickListener {


    private ListView mListView;
    private int selectedYear, selectedMonth, selectedDay;
    private List<Metric> metricList;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private Calendar calendar = Calendar.getInstance();
    private ProgressBar loader;
    private TextView dateDisplay, otherLabel, leftSelector, rightSelector;
    private ImageView calendarLauncher;
    private AsyncActivitySettingsObject[] allActivities;
    private TableLayout activitiesTable;
    private LayoutInflater inflater;
    private PopupMenu dateSelectorPopup;
    private int selectedStartYear = 0;
    private int selectedStartMonth = 0;
    private int selectedStartDay = 0;
    private int selectedEndYear = 0;
    private int selectedEndMonth = 0;
    private int selectedEndDay = 0;
    private String formattedStartTime;
    private String formattedEndTime;
    private Date selectedStartTime;
    private Date selectedEndTime;

    public RecordFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
        loader.setVisibility(View.VISIBLE);
        Date d = calendar.getTime();
        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), d, d, parentActivity.getSelectedTeamMarketId());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        return inflater.inflate(R.layout.activity_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        calendar = Calendar.getInstance();
        apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), parentActivity.getSelectedTeamMarketId());

//        Date d = calendar.getTime();
//        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), d, d, parentActivity.getSelectedTeamMarketId());
        loader = parentActivity.findViewById(R.id.parentLoader);
        loader.setVisibility(View.VISIBLE);

//        initializeCalendarHandler();
        TextView save = parentActivity.findViewById(R.id.saveButton);
        if(save != null) {
            save.setOnClickListener(this);
        }
        initDateSelectors(view);

        setColorScheme();
//        activitiesTable = view.findViewById(R.id.activitiesTable);

    }

    private void initDateSelectors(View view) {
        leftSelector = view.findViewById(R.id.miniDateSelectorDate);
        leftSelector.setOnClickListener(this);
        rightSelector = view.findViewById(R.id.miniDateSelectorDateFormat);
        rightSelector.setOnClickListener(this);

        dateSelectorPopup = new PopupMenu(getContext(), leftSelector);

        dateSelectorPopup.setOnMenuItemClickListener(this);
        List<String> timelineArray = initSpinnerArray();
        int counter = 0;
        for(String timePeriod : timelineArray) {
            SpannableString s = new SpannableString(timePeriod);
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterTextColor()), 0, s.length(), 0);

            dateSelectorPopup.getMenu().add(1, counter, counter, s);

            counter++;
        }

        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        updateDisplayDate(selectedYear, selectedMonth, selectedDay);
    }

    private void setLabels() {
        for(Metric metric: metricList) {
            metric.setTitle(parentActivity.localizeLabel(metric.getTitle()));
        }
    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.record_list_parent_layout);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

//        dateDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());

//        Drawable drawable = getResources().getDrawable(R.drawable.appointment_icon).mutate();
//        drawable.setColorFilter(colorSchemeManager.getIconActive(), PorterDuff.Mode.SRC_ATOP);
//        calendarLauncher.setImageDrawable(drawable);

        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark));
            loader.getIndeterminateDrawable().setBounds(bounds);
        } else {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            loader.getIndeterminateDrawable().setBounds(bounds);
        }

        leftSelector.setBackgroundColor(colorSchemeManager.getButtonBackground());
        leftSelector.setTextColor(colorSchemeManager.getLighterTextColor());

        rightSelector.setBackgroundColor(colorSchemeManager.getButtonBackground());
        rightSelector.setTextColor(colorSchemeManager.getLighterTextColor());
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Yesterday");
        spinnerArray.add("Today");

        return spinnerArray;
    }

//    private void initializeCalendarHandler() {
//        calendarLauncher = getView().findViewById(R.id.calender_date_picker);
//        dateDisplay = getView().findViewById(R.id.record_date);
//
//        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
//        selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
//        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
//
//        updateDisplayDate(selectedYear, selectedMonth, selectedDay);
//
//        calendarLauncher.setOnClickListener(this);
//        dateDisplay.setOnClickListener(this);
//
//    }

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

            rightSelector.setText(sdf.format(updatedTime.getTime()));
        } catch (ParseException e) {
            parentActivity.showToast("Error parsing selected date");
            e.printStackTrace();
        }
    }

    private void initializeListView(List<Metric> metricList) {
        if(getView() != null) {
            mListView = getView().findViewById(R.id.record_list_view);
            mListView.setDivider(null);
            mListView.setDividerHeight(30);

            RecordListAdapter adapter = new RecordListAdapter(getContext(), metricList, this, colorSchemeManager, dataController.getFirstOtherActivity(), parentActivity);
            mListView.setAdapter(adapter);
        }

    }

    private void initTableView(List<Metric> metricList) {
        if(getView() != null) {
            TableRow currentRow = null;
            for(int i = 0; i < metricList.size(); i++) {
                Metric currentMetric = metricList.get(i);

                if(i % 2 == 0) {
                    TableRow tr = new TableRow(getContext());
                    tr.setLayoutParams(new RelativeLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    View rowView = inflater.inflate(R.layout.adapter_record_table_row, tr, false);
                    TextView title = rowView.findViewById(R.id.recordTitle);
                    currentRow = tr;
                    title.setText(currentMetric.getTitle());
                    tr.addView(rowView);
                }
                else {
                    View rowView = inflater.inflate(R.layout.adapter_record_table_row, currentRow, false);
                    TextView title = rowView.findViewById(R.id.recordTitle);
                    title.setText(currentMetric.getTitle());
                    currentRow.addView(rowView);
                    activitiesTable.addView(currentRow);
                }
//                TableRow tr = new TableRow(getContext());
//                Button b = new Button(getContext());
//                b.setText("Dynamic Button");
//
//                Button c = new Button(getContext());
//                c.setText("Dynamic Button");
////            b.setLayoutParams(new RelativeLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//                /* Add Button to row. */
//                tr.addView(b);
//                tr.addView(c);
//                activitiesTable.addView(tr);
            }
            /* Create a Button to be the row-content. */


            /* Add row to TableLayout. */
            //tr.setBackgroundResource(R.drawable.sf_gradient_03);

        }

    }

    private void recordMetric(String tab) {
        String switchTab = "";
        switch(tab) {
            case "appts":
                switchTab = "pipeline";
                break;
            case "signed":
                switchTab = "pipeline";
                break;
            case "contract":
                switchTab= "signed";
                break;
            case "closed":
                switchTab = "contract";
                break;
        }
        navigationManager.navigateToClientList(switchTab);
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                updateDisplayDate(year, month, day);
                updateRecordInfo();
            }
        }, selectedYear, selectedMonth, selectedDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateRecordInfo() {
        String formattedMonth = String.valueOf(selectedMonth + 1);
        if(selectedMonth + 1 < 10) {
            formattedMonth = "0" + formattedMonth;
        }
        String formattedDay = String.valueOf(selectedDay);
        if(selectedDay < 10) {
            formattedDay = "0" + formattedDay;
        }

        String formattedDate = selectedYear + "-" + formattedMonth + "-" + formattedDay;
        parentActivity.updateSelectedRecordDate(formattedDate);
        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), formattedDate, formattedDate, parentActivity.getSelectedTeamMarketId());
        loader.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.miniDateSelectorDateFormat:
                showDatePickerDialog();
                break;
            case R.id.miniDateSelectorDate:
                dateSelectorPopup.show();
                break;
            case R.id.saveButton:
                if(parentActivity.isAdminMode()) {
                    popAdminConfirmDialog();
                }
                else {
                    saveRecords();
                }
                break;
            default:
                break;
        }
    }

    private void saveRecords() {
        if(dataController.getUpdatedRecords().size() > 0) {
            parentActivity.updateRecordedActivities();
            parentActivity.showToast("Records Saved");
        }
        else {
            parentActivity.showToast("There are no changes to save");
        }
    }

    private void popAdminConfirmDialog() {
        String message = "Admin. Do you want to save?";
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
        builder.setMessage(message)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveRecords();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dataController.clearUpdatedRecords();
                    }
                });
        builder.create();
        builder.show();
    }

    @Override
    public void onNumberChanged(Metric metric, int newNum) {
        metric.setCurrentNum(newNum);
        dataController.setRecordUpdated(metric);
    }

    @Override
    public void onClientDirectorClicked(Metric metric) {
        switch(metric.getType()) {
            case "1TAPT":
                recordMetric("appts");
                break;
            case "SGND":
                recordMetric("signed");
                break;
            case "UCNTR":
                recordMetric("contract");
                break;
            case "CLSD":
                recordMetric("closed");
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_ACTIVITIES) {
            AsyncActivitiesJsonObject activitiesObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitiesJsonObject.class);
            dataController.setActivitiesObject(activitiesObject, parentActivity.isRecruiting());
            dataController.setRecordActivities(activitiesObject, parentActivity.isRecruiting());
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    metricList = dataController.getRecordActivities();
                    setLabels();
                    initializeListView(metricList);
//                    initTableView(metricList);
                }
            });
//            apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), parentActivity.getSelectedTeamMarketId());
        }
        else if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            AsyncActivitySettingsJsonObject settingsJson = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitySettingsJsonObject.class);
            AsyncActivitySettingsObject[] settings = settingsJson.getRecord_activities();
            dataController.setActivitiesSelected(settings);

//            currentActivitiesSorting = dataController.getActivitiesSelected();
//            parentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loader.setVisibility(View.GONE);
//                    metricList = dataController.getRecordActivities();
//                    setLabels();
//                    initializeListView(metricList);
////                    setupFieldsWithData();
////                    fillListViewWithData(dataController.getActivitiesSelected());
//                }
//            });
            Date d = calendar.getTime();
            apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), d, d, parentActivity.getSelectedTeamMarketId());
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {}

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        calendar = Calendar.getInstance();
        switch (item.getItemId()) {
            case 0:
                //Yesterday
                parentActivity.setTimeline("day");
                parentActivity.setTimelineSelection(0);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                selectedStartYear = calendar.get(Calendar.YEAR);
                selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                selectedEndYear = calendar.get(Calendar.YEAR);
                selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            case 1:
                //Today
                parentActivity.setTimeline("day");
                parentActivity.setTimelineSelection(1);

                selectedStartYear = calendar.get(Calendar.YEAR);
                selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                selectedEndYear = calendar.get(Calendar.YEAR);
                selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            default:
                return false;
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

        formattedStartTime = selectedStartYear + "-" + formattedStartMonth + "-" + formattedStartDay;
        formattedEndTime = selectedEndYear + "-" + formattedEndMonth + "-" + formattedEndDay;
        selectedStartTime = getDateFromFormattedTime(formattedStartTime);
        selectedEndTime = getDateFromFormattedTime(formattedEndTime);

        parentActivity.setFormattedStartTime(formattedStartTime);
        parentActivity.setFormattedEndTime(formattedEndTime);

        rightSelector.setText(formattedStartTime);
        loader.setVisibility(View.VISIBLE);

        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), selectedStartTime, selectedStartTime, parentActivity.getSelectedTeamMarketId());

        return false;
    }

    private Date getDateFromFormattedTime(String formattedTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = formatter.parse(formattedTime);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
