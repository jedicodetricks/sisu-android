package co.sisu.mobile.fragments.main;


import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.LeaderboardListExpandableAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.CacheManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.AsyncLabelsJsonObject;
import co.sisu.mobile.models.AsyncLeaderboardJsonObject;
import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import co.sisu.mobile.models.LeaderboardAgentModel;
import co.sisu.mobile.models.LeaderboardItemsObject;
import co.sisu.mobile.models.LeaderboardObject;
import co.sisu.mobile.models.TeamColorSchemeObject;
import co.sisu.mobile.system.SaveSharedPreference;
import co.sisu.mobile.utils.LeaderboardComparator;
import co.sisu.mobile.utils.Utils;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment implements AsyncServerEventListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener, com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    private LeaderboardListExpandableAdapter listAdapter;
    private ExpandableListView expListView;
    private List<LeaderboardObject> listDataHeader;
    private HashMap<LeaderboardObject, List<LeaderboardItemsObject>> listDataChild;
    private ProgressBar loader;
    private Calendar calendar = Calendar.getInstance();
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private ActionBarManager actionBarManager;
    private CacheManager cacheManager;
    private Utils utils;
    private Switch leaderboardToggle;
    private int selectedYear = 0;
    private int selectedMonth = 0;
    private int selectedDay = 0;
    private int[] teamColors = {R.color.sisuCorporateGrey, R.color.sisuAlmostBlack};
    private int colorCounter = 0;
    private HashMap<String, LeaderboardAgentModel> agents = new HashMap<>();
    private int agentCounter = 0;
    private List<LeaderboardObject> leaderBoardSections;
    private TextView dateDisplay, monthToggle, yearToggle;
    private ImageView calendarLauncher;

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
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        actionBarManager = parentActivity.getActionBarManager();
        cacheManager = parentActivity.getCacheManager();
        utils = parentActivity.getUtils();
        actionBarManager.setToTitleBar("Leaderboards", false);
        loader = parentActivity.findViewById(R.id.parentLoader);
        expListView = view.findViewById(R.id.teamExpandable);
        expListView.setGroupIndicator(null);
        expListView.setDividerHeight(0);
        initToggle();
        loader.setVisibility(View.VISIBLE);
        initFields();
        getLeaderboard(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        initializeCalendarHandler();
        setColorScheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "LeaderboardFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        FirebaseAnalytics.getInstance(parentActivity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void initFields() {
        monthToggle = getView().findViewById(R.id.monthToggleText);
        yearToggle = getView().findViewById(R.id.yearToggleText);

    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.leaderboard_parent);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

        dateDisplay.setTextColor(colorSchemeManager.getDarkerText());
        monthToggle.setTextColor(colorSchemeManager.getDarkerText());
        yearToggle.setTextColor(colorSchemeManager.getDarkerText());

        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getSegmentSelected()
        };

        int[] trackColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getSegmentSelected()
        };

        DrawableCompat.setTintList(DrawableCompat.wrap(leaderboardToggle.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(leaderboardToggle.getTrackDrawable()), new ColorStateList(states, trackColors));

        Drawable drawable = getResources().getDrawable(R.drawable.appointment_icon).mutate();
        drawable.setColorFilter(colorSchemeManager.getIconSelected(), PorterDuff.Mode.SRC_ATOP);
        calendarLauncher.setImageDrawable(drawable);
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

    private void initLeaderBoardImages(LeaderboardAgentModel leaderboardAgentModel) {
        if(leaderboardAgentModel.getProfile() != null) {
            Bitmap bitmap = cacheManager.getBitmapFromMemCache(leaderboardAgentModel.getProfile());
            if(bitmap == null) {
                apiManager.getLeaderboardImage(this, dataController.getAgent().getAgent_id(), leaderboardAgentModel);
            }
            else {
                leaderboardAgentModel.setBitmap(bitmap);
                agentDisplayCounting();
            }
        }
        else {
            agentDisplayCounting();
        }
    }

    private void initToggle() {
        leaderboardToggle = getView().findViewById(R.id.leaderboardToggle);
        leaderboardToggle.setOnCheckedChangeListener(this);
    }

    private void initializeCalendarHandler() {

//        datePicker.date(this);
        calendarLauncher = getView().findViewById(R.id.leaderboard_calender_date_picker);
        dateDisplay = getView().findViewById(R.id.leaderboard_date);

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
            utils.showToast("Error parsing selected date", parentActivity);
            e.printStackTrace();
        }
    }

    private void getLeaderboard(int year, int month) {
        if(dataController.getCurrentSelectedTeamId() == 0) {
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
            String formattedTeamId = String.valueOf(dataController.getCurrentSelectedTeamId());
            if(month != 0) {
                formattedMonth = String.valueOf(month);
            }
            if(leaderboardToggle.isChecked()) {
                //Year selected
                apiManager.getLeaderboardYear(this, dataController.getAgent().getAgent_id(), formattedTeamId, formattedYear);
            }
            else {
                apiManager.getLeaderboardYearAndMonth(this, dataController.getAgent().getAgent_id(), formattedTeamId, formattedYear, formattedMonth);
            }
        }

    }

    public void teamSwap() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter = null;
                expListView.setAdapter(listAdapter);
                getLeaderboard(selectedYear, selectedMonth + 1);
                SaveSharedPreference.setTeam(parentActivity, dataController.getCurrentSelectedTeamId() + "");
            }
        });

    }


    private void prepareListData() {

        if(leaderBoardSections != null) {
            for(int i = 0; i < leaderBoardSections.size(); i++) {
                for(int j = 0; j < leaderBoardSections.get(i).getLeaderboardItemsObject().length; j++) {
                    if(!leaderBoardSections.get(i).getLeaderboardItemsObject()[j].getValue().equals("0")) {

                        LeaderboardItemsObject currentAgent = leaderBoardSections.get(i).getLeaderboardItemsObject()[j];

                        if(!agents.containsKey(currentAgent.getAgent_id())) {
                            agents.put(leaderBoardSections.get(i).getLeaderboardItemsObject()[j].getAgent_id(), new LeaderboardAgentModel(currentAgent.getAgent_id(), currentAgent.getLabel(),
                                    /*Stop trying to delete this line, Brady*/                                               currentAgent.getPlace(), currentAgent.getProfile(), currentAgent.getValue()));
                        }
                    }
                }

                if(colorCounter == teamColors.length - 1) {
                    colorCounter = 0;
                }
                else {
                    colorCounter++;
                }
            }

            if(agents.size() > 0) {
                agentCounter = 0;
                for (HashMap.Entry<String, LeaderboardAgentModel> entry : agents.entrySet())
                {
                    initLeaderBoardImages(entry.getValue());
                }
            }
            else {
                utils.showToast("There is no info to display so far this month", parentActivity);
                displayListData();
            }

        }


    }

    private void displayListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        colorCounter = 0;
        Collections.sort(leaderBoardSections, new LeaderboardComparator());
        int addedSections = 0;

        for(int i = 0; i < leaderBoardSections.size(); i++) {
            leaderBoardSections.get(i).setColor(teamColors[colorCounter]);
            boolean isEmpty = false;
            if(leaderBoardSections.get(i).getLeaderboardItemsObject().length != 0) {
                for(LeaderboardItemsObject leaderboardObject : leaderBoardSections.get(i).getLeaderboardItemsObject()) {
                    if(!leaderboardObject.getValue().equalsIgnoreCase("0")) {
                        isEmpty = false;
                        break;
                    }
                    else {
                        isEmpty = true;
                    }
                }

                if(isEmpty) {
                    continue;
                }
                listDataHeader.add(leaderBoardSections.get(i));
                List<LeaderboardItemsObject> leaderboardItems = new ArrayList<>();

                for(int j = 0; j < leaderBoardSections.get(i).getLeaderboardItemsObject().length; j++) {
                    if(!leaderBoardSections.get(i).getLeaderboardItemsObject()[j].getValue().equals("0")) {
                        LeaderboardItemsObject item = leaderBoardSections.get(i).getLeaderboardItemsObject()[j];
                        LeaderboardAgentModel agent = agents.get(item.getAgent_id());
                        item.setImage(agent.getBitmap());
                        leaderboardItems.add(item);
                    }
                }

                listDataChild.put(listDataHeader.get(addedSections), leaderboardItems);
                if(colorCounter == teamColors.length - 1) {
                    colorCounter = 0;
                }
                else {
                    colorCounter++;
                }
                addedSections++;
            }

        }

        //Collections.sort(listDataHeader, new LeaderboardComparator());


        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listAdapter = new LeaderboardListExpandableAdapter(getContext(), listDataHeader, listDataChild, parentActivity, apiManager, dataController.getAgent().getAgent_id());
                expListView.setAdapter(listAdapter);
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void agentDisplayCounting() {
        if(agentCounter < agents.size() - 1) {
            agentCounter++;
        }
        else {
            displayListData();
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        // TODO: Move these to the new format
        if(asyncReturnType.equals("Leaderboard Image")) {
            //TODO: This one is being used. Transition it
            LeaderboardAgentModel leaderboardAgentModel = (LeaderboardAgentModel) returnObject;
            if (leaderboardAgentModel.getBitmap() != null) {
                cacheManager.addBitmapToMemoryCache(leaderboardAgentModel.getProfile(), leaderboardAgentModel.getBitmap());
            }
            agentDisplayCounting();
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.GET_LEADERBOARDS){
            AsyncLeaderboardJsonObject leaderboardJsonObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncLeaderboardJsonObject.class);
            leaderBoardSections = leaderboardJsonObject.getLeaderboardObject();

            prepareListData();
        }
        else if(returnType == ApiReturnType.GET_COLOR_SCHEME) {
            AsyncTeamColorSchemeObject colorJson = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncTeamColorSchemeObject.class);
            TeamColorSchemeObject[] colorScheme = colorJson.getTheme();
            colorSchemeManager.setColorScheme(colorScheme, parentActivity);
            parentActivity.setActivityColors();
        }
        else if(returnType == ApiReturnType.GET_LABELS) {
            AsyncLabelsJsonObject labelObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncLabelsJsonObject.class);
            HashMap<String, String> labels = labelObject.getMarket();
            dataController.setLabels(labels);
        }
    }

    @Override
    public void onEventFailed(Object o, String s) {
        Log.e("LEADERBOARD", "FAILED");
    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leaderboard_calender_date_picker:
            case R.id.leaderboard_date:
                new SpinnerDatePickerDialogBuilder()
                        .context(getContext())
                        .callback(this)
                        .spinnerTheme(android.R.style.Theme_Holo_Dialog)
                        .showTitle(false)
                        .defaultDate(selectedYear, selectedMonth, selectedDay)
                        .showDaySpinner(false)
                        .minDate(1990, 0, 1)
                        .build()
                        .show();
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateDisplayDate(selectedYear, selectedMonth, selectedDay);
        getLeaderboard(selectedYear, selectedMonth + 1);
    }

    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(year != selectedYear || monthOfYear != selectedMonth || dayOfMonth != selectedDay) {
            if(leaderboardToggle.isChecked() && monthOfYear != selectedMonth) {
                //TODO: Should this just toggle for them and search it? They obviously want to do that in this situation
                utils.showToast("You're in year search mode. Swap to month search to change month selection.", parentActivity);
            }
            else {
                updateDisplayDate(year, monthOfYear, dayOfMonth);
                getLeaderboard(selectedYear, selectedMonth + 1);
            }
        }
        else {
            utils.showToast("You have selected the same time period", parentActivity);
        }
    }
}
