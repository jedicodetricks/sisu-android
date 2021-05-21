package co.sisu.mobile.fragments.main;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.DateManager;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.fragments.ClientManageFragment;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.DoubleMetric;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.oldFragments.TransactionFragment;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener, RecordEventHandler, AsyncServerEventListener, PopupMenu.OnMenuItemClickListener {


    private ListView mListView;
    private List<Metric> metricList;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private ActionBarManager actionBarManager;
    private DateManager dateManager;
    private Calendar calendar = Calendar.getInstance();
    private ProgressBar loader;
    private TextView dateDisplay, otherLabel, leftSelector, rightSelector, transactionLabel, activitiesLabel;
    private ImageView addTransactionButton, appointmentSetButton, appointmentMetButton, signedButton, underContractButton, closedButton;
    private AsyncActivitySettingsObject[] allActivities;
    private TableLayout activitiesTable;
    private LayoutInflater inflater;
    private PopupMenu dateSelectorPopup;
    private final int smallerTitleSize = 18;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        return inflater.inflate(R.layout.activity_record, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.view = view;
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        dateManager = parentActivity.getDateManager();
        actionBarManager = parentActivity.getActionBarManager();
        actionBarManager.setToSaveBar("Record");
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
        initTransactionButtons(view);
        initLabels(view);
        setTransactionSectionVisible(view);
        setColorScheme();
//        activitiesTable = view.findViewById(R.id.activitiesTable);

    }

    private void initLabels(View view) {
        transactionLabel = view.findViewById(R.id.recordTransactionTitleView);
        activitiesLabel = view.findViewById(R.id.recordTitleView);
    }

    private void initTransactionButtons(View view){
        addTransactionButton = view.findViewById(R.id.addTransactionButton);
        appointmentSetButton = view.findViewById(R.id.appointmentSetButton);
        appointmentMetButton = view.findViewById(R.id.appointmentMetButton);
        signedButton = view.findViewById(R.id.signedButton);
        underContractButton = view.findViewById(R.id.underContractButton);
        closedButton = view.findViewById(R.id.closedButton);

        addTransactionButton.setOnClickListener(this);
        appointmentSetButton.setOnClickListener(this);
        appointmentMetButton.setOnClickListener(this);
        signedButton.setOnClickListener(this);
        underContractButton.setOnClickListener(this);
        closedButton.setOnClickListener(this);
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
        dateManager.setRecordDateToToday();

        updateDisplayDate(dateManager.getRecordYear(), dateManager.getRecordMonth() - 1, dateManager.getRecordDay());
    }

    private void initListView(List<Metric> metricList) {
        List<DoubleMetric> doubleMetricList = new ArrayList<>();
        for(int i = 0; i < metricList.size(); i++) {
            if(i % 2 == 0) {
                if(i + 1 >= metricList.size()) {
                    doubleMetricList.add(new DoubleMetric(metricList.get(i), null));
                }
                else {
                    doubleMetricList.add(new DoubleMetric(metricList.get(i), metricList.get(i + 1)));
                }
            }
        }

        RelativeLayout parentRelativeLayout = view.findViewById(R.id.record_activities_list_parent);
        parentRelativeLayout.removeAllViews();
        int numOfRows = 0;
        for(int i = 0; i < doubleMetricList.size(); i++) {
            View view = inflater.inflate(R.layout.adapter_double_record_table_row, parentRelativeLayout, false);
            view = createActivityRowView(view, doubleMetricList.get(i));

            view.setId(numOfRows + 1);
            RelativeLayout.LayoutParams horizontalParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            horizontalParam.addRule(RelativeLayout.BELOW, numOfRows);
            parentRelativeLayout.addView(view, horizontalParam);
            numOfRows++;
        }
    }

    private void setTransactionSectionVisible(View view) {
        if(parentActivity.getSelectedTeamMarketId() != 0) {
            RelativeLayout transactionSection = view.findViewById(R.id.recordTransactionSection);
            transactionSection.setVisibility(View.GONE);
        }
    }

    private void setLabels() {
        for(Metric metric: metricList) {
            metric.setTitle(parentActivity.localizeLabel(metric.getTitle()));
        }
    }

    private void setColorScheme() {
        RelativeLayout layout = getView().findViewById(R.id.record_list_parent_layout);
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

        VectorChildFinder plusVector = new VectorChildFinder(this.getContext(), R.drawable.add_icon, addTransactionButton);
        VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
        plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
        addTransactionButton.invalidate();

        plusVector = new VectorChildFinder(this.getContext(), R.drawable.add_icon, appointmentSetButton);
        plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
        plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
        appointmentSetButton.invalidate();

        plusVector = new VectorChildFinder(this.getContext(), R.drawable.add_icon, appointmentMetButton);
        plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
        plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
        appointmentMetButton.invalidate();

        plusVector = new VectorChildFinder(this.getContext(), R.drawable.add_icon, signedButton);
        plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
        plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
        signedButton.invalidate();

        plusVector = new VectorChildFinder(this.getContext(), R.drawable.add_icon, underContractButton);
        plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
        plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
        underContractButton.invalidate();

        plusVector = new VectorChildFinder(this.getContext(), R.drawable.add_icon, closedButton);
        plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
        plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
        closedButton.invalidate();

        transactionLabel.setBackgroundColor(colorSchemeManager.getPrimaryColor());
        activitiesLabel.setBackgroundColor(colorSchemeManager.getPrimaryColor());
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Yesterday");
        spinnerArray.add("Today");

        return spinnerArray;
    }

    private void updateDisplayDate(int year, int month, int day) {
        // TODO: This feels like a DateManager Util
        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;
        String formattedMonth = String.valueOf(month);
        if(month < 10) {
            formattedMonth = "0" + formattedMonth;
        }
        String formattedDay = String.valueOf(day);
        if(day < 10) {
            formattedDay = "0" + formattedDay;
        }
        String displayDate = year + "-" + formattedMonth + "-" + formattedDay;

        try {
            d = formatter.parse(formatDate);
            Calendar updatedTime = Calendar.getInstance();
            updatedTime.setTime(d);

            rightSelector.setText(displayDate);
        } catch (ParseException e) {
            parentActivity.showToast("Error parsing selected date");
            e.printStackTrace();
        }
    }

    private View createActivityRowView(View rowView, DoubleMetric doubleMetric) {
        final Metric leftMetric = doubleMetric.getLeftMetric();
        final Metric rightMetric = doubleMetric.getRightMetric();

        TextView leftTitleView = rowView.findViewById(R.id.leftRecordTitle);
        leftTitleView.setTextColor(colorSchemeManager.getDarkerTextColor());
        leftTitleView.setText(leftMetric.getTitle());
        if(leftMetric.getTitle().length() >= smallerTitleSize) {
            leftTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smaller));
        }
//        if(leftMetric.getTitle().length() >= smallestTitleSize) {
//            leftTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smallest));
//        }

        // Get the row counter element
        final EditText leftRowCounter = rowView.findViewById(R.id.leftRowCounter);

        ImageView leftMinusButton = rowView.findViewById(R.id.leftMinusButton);
        Drawable minusDrawable = rowView.getResources().getDrawable(R.drawable.minus_icon).mutate();
        minusDrawable.setTint(colorSchemeManager.getPrimaryColor());
        leftMinusButton.setImageDrawable(minusDrawable);

        VectorChildFinder vector = new VectorChildFinder(rowView.getContext(), R.drawable.minus_icon, leftMinusButton);
        for (int i = 0; i < 7; i++) {
            String pathName = "orange_area" + (i + 1);
            VectorDrawableCompat.VFullPath path = vector.findPathByName(pathName);
            path.setFillColor(colorSchemeManager.getPrimaryColor());
            path.setStrokeColor(colorSchemeManager.getPrimaryColor());
        }

        leftMinusButton.invalidate();


        ImageView leftPlusButton = rowView.findViewById(R.id.leftPlusButton);
        VectorChildFinder plusVector = new VectorChildFinder(rowView.getContext(), R.drawable.add_icon, leftPlusButton);
        VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
        plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
        leftPlusButton.invalidate();


        leftMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parentActivity.isTeamSwapFinished()) {
                    int minusOne = leftMetric.getCurrentNum();
                    if(minusOne > 0) {
                        minusOne -= 1;
                    }
                    leftRowCounter.setText(String.valueOf(minusOne));
                }

            }
        });

        leftPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(parentActivity.isTeamSwapFinished()) {
                    int plusOne = leftMetric.getCurrentNum() + 1;
                    leftRowCounter.setText(String.valueOf(plusOne));
                }

            }
        });

        leftRowCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!leftRowCounter.getText().toString().equals("")) {
                    if(Integer.valueOf(leftRowCounter.getText().toString()) != leftMetric.getCurrentNum()) {
                        switch(leftMetric.getType()) {
                            case "1TAPT":
                            case "CLSD":
                            case "UCNTR":
                            case "SGND":
//                                mRecordEventHandler.onClientDirectorClicked(leftMetric);
                                break;
                            default:
                                onNumberChanged(leftMetric, Integer.valueOf(leftRowCounter.getText().toString()));
                                break;
                        }
                    }
                }
            }
        });

        leftRowCounter.setText(String.valueOf(leftMetric.getCurrentNum()));
        leftRowCounter.setTextColor(colorSchemeManager.getDarkerTextColor());


        //EVERYTHING FOR THE RIGHT SIDE

        TextView rightTitleView = rowView.findViewById(R.id.rightRecordTitle);
        rightTitleView.setTextColor(colorSchemeManager.getDarkerTextColor());
        final EditText rightRowCounter = rowView.findViewById(R.id.rightRowCounter);
        ImageView rightMinusButton = rowView.findViewById(R.id.rightMinusButton);
        ImageView rightPlusButton = rowView.findViewById(R.id.rightPlusButton);

        if(rightMetric != null) {
            rightTitleView.setText(rightMetric.getTitle());
            if(rightMetric.getTitle().length() >= smallerTitleSize) {
                rightTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smaller));
            }
//            if(rightMetric.getTitle().length() >= smallestTitleSize) {
//                rightTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smallest));
//            }
            // Get the row counter element

            minusDrawable = rowView.getResources().getDrawable(R.drawable.minus_icon).mutate();
            minusDrawable.setTint(colorSchemeManager.getPrimaryColor());
            rightMinusButton.setImageDrawable(minusDrawable);

            vector = new VectorChildFinder(rowView.getContext(), R.drawable.minus_icon, rightMinusButton);
            for (int i = 0; i < 7; i++) {
                String pathName = "orange_area" + (i + 1);
                VectorDrawableCompat.VFullPath path = vector.findPathByName(pathName);
                path.setFillColor(colorSchemeManager.getPrimaryColor());
                path.setStrokeColor(colorSchemeManager.getPrimaryColor());
            }

            rightMinusButton.invalidate();


            plusVector = new VectorChildFinder(rowView.getContext(), R.drawable.add_icon, rightPlusButton);
            plusPath = plusVector.findPathByName("orange_area");
            plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
            plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
            rightPlusButton.invalidate();


            rightMinusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(parentActivity.isTeamSwapFinished()) {
                        int minusOne = rightMetric.getCurrentNum();
                        if(minusOne > 0) {
                            minusOne -= 1;
                        }
                        rightRowCounter.setText(String.valueOf(minusOne));
                    }

                }
            });

            rightPlusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(parentActivity.isTeamSwapFinished()) {
                        int plusOne = rightMetric.getCurrentNum() + 1;
                        rightRowCounter.setText(String.valueOf(plusOne));
                    }

                }
            });

            rightRowCounter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!rightRowCounter.getText().toString().equals("")) {
                        if(Integer.valueOf(rightRowCounter.getText().toString()) != rightMetric.getCurrentNum()) {
                            switch(rightMetric.getType()) {
                                case "1TAPT":
                                case "CLSD":
                                case "UCNTR":
                                case "SGND":
//                                    mRecordEventHandler.onClientDirectorClicked(rightMetric);
                                    break;
                                default:
                                    onNumberChanged(rightMetric, Integer.valueOf(rightRowCounter.getText().toString()));
                                    break;
                            }
                        }
                    }
                }
            });

            rightRowCounter.setText(String.valueOf(rightMetric.getCurrentNum()));
            rightRowCounter.setTextColor(colorSchemeManager.getDarkerTextColor());
        }
        else {
            //Make all the elements GONE
            rightTitleView.setVisibility(View.GONE);
            rightRowCounter.setVisibility(View.GONE);
            rightMinusButton.setVisibility(View.GONE);
            rightPlusButton.setVisibility(View.GONE);
        }



        return rowView;
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);
                dateManager.setRecordDateToDate(cal);
                updateDisplayDate(year, month, day);
                updateRecordInfo();
            }
        }, dateManager.getRecordYear(), dateManager.getRecordMonth() - 1, dateManager.getRecordDay());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }

    private void updateRecordInfo() {
        // TODO: This feels like a util
        Calendar cal = Calendar.getInstance();
        cal.set(dateManager.getRecordYear(), dateManager.getRecordMonth() - 1, dateManager.getRecordDay());

        dateManager.setRecordDateToDate(cal);
        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), dateManager.getFormattedRecordDate(), dateManager.getFormattedRecordDate(), parentActivity.getSelectedTeamMarketId());
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
            case R.id.addTransactionButton:
                navigationManager.stackReplaceFragment(ClientManageFragment.class);
                break;
            case R.id.appointmentSetButton:
                apiManager.getClientList(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamMarketId(), "appt_set_dt");
                parentActivity.setRecordClientListType("'Appointment Set'");
                break;
            case R.id.appointmentMetButton:
                apiManager.getClientList(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamMarketId(), "appt_dt");
                parentActivity.setRecordClientListType("'Appointment Met'");
                break;
            case R.id.signedButton:
                apiManager.getClientList(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamMarketId(), "signed_dt");
                parentActivity.setRecordClientListType("'Signed'");
                break;
            case R.id.underContractButton:
                apiManager.getClientList(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamMarketId(), "uc_dt");
                parentActivity.setRecordClientListType("'Under Contract'");
                break;
            case R.id.closedButton:
                apiManager.getClientList(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamMarketId(), "closed_dt");
                parentActivity.setRecordClientListType("'Closed'");
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
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_ACTIVITIES) {
            AsyncActivitiesJsonObject activitiesObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitiesJsonObject.class);
            dataController.setActivitiesObject(activitiesObject, parentActivity.isRecruiting());
            dataController.setRecordActivities(activitiesObject, parentActivity.isRecruiting());
            parentActivity.runOnUiThread(() -> {
                loader.setVisibility(View.GONE);
                metricList = dataController.getRecordActivities();
                setLabels();
                initListView(metricList);
//                    initTableView(metricList);
            });
//            apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), parentActivity.getSelectedTeamMarketId());
        }
        else if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            AsyncActivitySettingsJsonObject settingsJson = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitySettingsJsonObject.class);
            AsyncActivitySettingsObject[] settings = settingsJson.getRecord_activities();
            dataController.setActivitiesSelected(settings);

            Date d = calendar.getTime();
            apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), d, d, parentActivity.getSelectedTeamMarketId());
        }
        else if(returnType == ApiReturnTypes.GET_CLIENT_LIST) {
            try {
                String clientString = ((Response) returnObject).body().string();
                JSONObject clientJson = new JSONObject(clientString);
                parentActivity.setRecordClientsList(clientJson);
                navigationManager.stackReplaceFragment(TransactionFragment.class);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {}

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                //Yesterday
                dateManager.setRecordDateToYesterday();
                break;
            case 1:
                //Today
                dateManager.setRecordDateToToday();
                break;
            default:
                return false;
        }

        rightSelector.setText(dateManager.getFormattedRecordDate());
        loader.setVisibility(View.VISIBLE);

        apiManager.sendAsyncActivities(this, dataController.getAgent().getAgent_id(), dateManager.getFormattedRecordDate(), dateManager.getFormattedRecordDate(), parentActivity.getSelectedTeamMarketId());

        return false;
    }
}
