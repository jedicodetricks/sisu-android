package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.LeaderboardListExpandableAdapter;
import co.sisu.mobile.api.AsyncLeaderboardStats;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.AsyncLeaderboardJsonObject;
import co.sisu.mobile.models.LeaderboardItemsObject;
import co.sisu.mobile.models.LeaderboardObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment implements AsyncServerEventListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    LeaderboardListExpandableAdapter listAdapter;
    ExpandableListView expListView;
    List<LeaderboardObject> listDataHeader;
    HashMap<LeaderboardObject, List<LeaderboardItemsObject>> listDataChild;
    ProgressBar loader;
    Calendar calendar = Calendar.getInstance();
    ParentActivity parentActivity;
    Switch leaderboardToggle;
    int selectedYear = 0;
    int selectedMonth = 0;
    int selectedDay = 0;
    int[] teamColors = {R.color.colorCorporateGrey, R.color.colorAlmostBlack};
    private int colorCounter = 0;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        parentActivity = (ParentActivity) getActivity();
        loader = view.findViewById(R.id.leaderboardLoader);
        expListView = view.findViewById(R.id.teamExpandable);
        expListView.setGroupIndicator(null);
        initToggle();
        loader.setVisibility(View.VISIBLE);
        getLeaderboard(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        initializeCalendarHandler();
    }

    private void initToggle() {
        leaderboardToggle = getView().findViewById(R.id.leaderboardToggle);
        leaderboardToggle.setOnCheckedChangeListener(this);
    }

    private void initializeCalendarHandler() {

        final ImageView calendarLauncher = getView().findViewById(R.id.leaderboard_calender_date_picker);
        final TextView dateDisplay = getView().findViewById(R.id.leaderboard_date);

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
//        selectedDay = day;

        Date d;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");

        if(leaderboardToggle.isChecked()) {
            //Year Selected
            sdf = new SimpleDateFormat("yyyy");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;

        try {
            d = formatter.parse(formatDate);
            Calendar updatedTime = Calendar.getInstance();
            updatedTime.setTime(d);

            TextView dateDisplay = getView().findViewById(R.id.leaderboard_date);
            dateDisplay.setText(sdf.format(updatedTime.getTime()));
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error parsing selected date", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                if(year != selectedYear || month != selectedMonth || day != selectedDay) {
                    updateDisplayDate(year, month, day);
                    getLeaderboard(selectedYear, selectedMonth + 1);
                }
                else {
                    Toast.makeText(getContext(), "You have selected the same time period", Toast.LENGTH_SHORT).show();
                }

            }
        }, selectedYear, selectedMonth, selectedDay);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if(leaderboardToggle.isChecked()) {
            //Year selected
            (dialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
            (dialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("month", "id", "android")).setVisibility(View.GONE);
        }
        else {
            (dialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        }
        dialog.show();
    }

    private void getLeaderboard(int year, int month) {
        if(parentActivity.getSelectedTeamId() == -1) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                }
            });
        }
        else {
            loader.setVisibility(View.VISIBLE);
            listAdapter = null;
            expListView.setAdapter(listAdapter);

            String formattedYear = String.valueOf(year);
            String formattedMonth =  "";
            String formattedTeamId = String.valueOf(parentActivity.getSelectedTeamId());
            if(month != 0) {
                formattedMonth = String.valueOf(month);
            }
            if(leaderboardToggle.isChecked()) {
                //Year selected
                new AsyncLeaderboardStats(this, formattedTeamId, formattedYear, "").execute();
            }
            else {
                new AsyncLeaderboardStats(this, formattedTeamId, formattedYear, formattedMonth).execute();
            }
        }

    }

    public void teamSwap() {
        listAdapter = null;
        expListView.setAdapter(listAdapter);
        getLeaderboard(selectedYear, selectedMonth);
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
//        spinnerArray.add("Today");
//        spinnerArray.add("Last Week");
//        spinnerArray.add("This Week");

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

    private void prepareListData(LeaderboardObject[] leaderBoardSections) {
        listDataHeader = new ArrayList<LeaderboardObject>();
        listDataChild = new HashMap<LeaderboardObject, List<LeaderboardItemsObject>>();
        colorCounter = 0;

        for(int i = 0; i < leaderBoardSections.length; i++) {
            leaderBoardSections[i].setColor(teamColors[colorCounter]);
            listDataHeader.add(leaderBoardSections[i]);
            List<LeaderboardItemsObject> leaderboardItems = new ArrayList<>();
            for(int j = 0; j < leaderBoardSections[i].getLeaderboardItemsObject().length; j++) {
                leaderboardItems.add(leaderBoardSections[i].getLeaderboardItemsObject()[j]);
            }
            listDataChild.put(listDataHeader.get(i), leaderboardItems);
            if(colorCounter == teamColors.length - 1) {
                colorCounter = 0;
            }
            else {
                colorCounter++;
            }
        }
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter = new LeaderboardListExpandableAdapter(getContext(), listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);
            }
        });
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        AsyncLeaderboardJsonObject leaderboardJsonObject = (AsyncLeaderboardJsonObject) returnObject;
        LeaderboardObject[] leaderBoardSections = leaderboardJsonObject.getLeaderboardObject();
        prepareListData(leaderBoardSections);

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onEventFailed(Object o, String s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leaderboard_calender_date_picker:
            case R.id.leaderboard_date:
                showDatePickerDialog();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateDisplayDate(selectedYear, selectedMonth, selectedDay);
        getLeaderboard(selectedYear, selectedMonth + 1);
    }
}
