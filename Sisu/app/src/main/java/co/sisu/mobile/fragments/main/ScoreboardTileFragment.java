package co.sisu.mobile.fragments.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.ViewModelProvider;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.NotificationActivity;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.DateManager;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.utils.TileCreationHelper;
import co.sisu.mobile.viewModels.DashboardViewModel;
import co.sisu.mobile.viewModels.GlobalDataViewModel;
import okhttp3.Response;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ScoreboardTileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener {

    private ParentActivity parentActivity;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private DateManager dateManager;
    private ActionBarManager actionBarManager;
    private TileCreationHelper tileCreationHelper;
    private ProgressBar loader;
    private LayoutInflater inflater;
    private int numOfRows = 1;

    private TextView dateSelectorBeginDateText, dateSelectorEndDateText, dateSelectorDateText, scopeSelectorText;
    private PopupMenu popup;
    private boolean beginDateSelected = false;
    private boolean endDateSelected = false;

    private PopupMenu scopePopup;

    private DashboardViewModel dashboardTilesViewModel;
    private GlobalDataViewModel globalDataViewModel;
    private View parentLayout;
    private ViewGroup container;

    // TODO: The tile tapping no longer works
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        assert parentActivity != null;
        this.apiManager = parentActivity.getApiManager();
        this.dateManager = parentActivity.getDateManager();
        this.actionBarManager = parentActivity.getActionBarManager();
        this.tileCreationHelper = parentActivity.getTileCreationHelper();
        this.globalDataViewModel = parentActivity.getGlobalDataViewModel();
        loader = parentActivity.findViewById(R.id.parentLoader);
        this.inflater = inflater;
        this.container = container;
        initListeners();
        parentLayout = inflater.inflate(R.layout.activity_tile_template_test_parentlayout, container, false);
        apiManager.getTileSetup(dashboardTilesViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", "a" + globalDataViewModel.getAgentValue().getAgent_id());
        return parentLayout;
    }

    public void initListeners() {
        dashboardTilesViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardTilesViewModel.getDashboardTiles().observe(getViewLifecycleOwner(), dashboardTiles -> {
            parentLayout = createFullView(container, dashboardTiles);
            finishSetup();
        });

        globalDataViewModel.getSelectedTeam().observe(getViewLifecycleOwner(), newSelectedTeam -> {
            loader.setVisibility(View.VISIBLE);
            apiManager.getTeamParams(globalDataViewModel, globalDataViewModel.getAgentValue().getAgent_id(), newSelectedTeam.getId());
            apiManager.getTileSetup(dashboardTilesViewModel, globalDataViewModel.getAgentValue().getAgent_id(), newSelectedTeam.getId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent");
            apiManager.getLabels(globalDataViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId());
        });
    }

    @NonNull
    @SuppressLint("ResourceType")
    private View createFullView(ViewGroup container, JSONObject tileTemplate) {
        loader.setVisibility(View.VISIBLE);
        JSONArray tile_rows = null;

        RelativeLayout tileRelativeLayout;
        RelativeLayout radioGroupLayout;
//        View parentLayout = inflater.inflate(R.layout.activity_tile_template_test_parentlayout, container, false);
            try {
                radioGroupLayout = parentLayout.findViewById(R.id.tileDashboardDateSelector);
                radioGroupLayout.setId(1);
            } catch(Exception e) {
                // This just means that it's a redraw and we don't care if it's wrong (I think)
            }

        if (tileTemplate != null) {
            try {
                colorSchemeManager = new ColorSchemeManager(tileTemplate.getJSONObject("theme"), parentActivity);
                parentActivity.updateColorScheme(colorSchemeManager);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Create the parent layout that all the rows will go in
            parentLayout.setBackgroundColor(colorSchemeManager.getAppBackground());
            tileRelativeLayout = parentLayout.findViewById(R.id.tileRelativeLayout);
            tileRelativeLayout.removeAllViewsInLayout();

            initDateSelector(parentLayout);
            initPopupMenu(parentLayout);
            //

            try {
                tile_rows = tileTemplate.getJSONArray("tile_rows");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (tile_rows != null) {
                for(int i = 1; i < tile_rows.length(); i++) {
                    try {
                        HorizontalScrollView horizontalScrollView = tileCreationHelper.createRowFromJSON(tile_rows.getJSONObject(i), container, false, 300, inflater, dashboardTilesViewModel, null);
                        if(horizontalScrollView != null) {
                            // Add one here to account for the radioGroup's ID.
                            horizontalScrollView.setId(numOfRows + 1);
                            RelativeLayout.LayoutParams horizontalParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            horizontalParam.addRule(RelativeLayout.BELOW, numOfRows);

                            tileRelativeLayout.addView(horizontalScrollView, horizontalParam);
                            numOfRows++;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        loader.setVisibility(View.INVISIBLE);
        return parentLayout;
    }

    public void finishSetup() {
        if(parentActivity.getCurrentScopeFilter() != null) {
            actionBarManager.setToTitleBar(parentActivity.getCurrentScopeFilter().getName(), true);
        }
        else {
            actionBarManager.setToTitleBar("", true);
        }
        parentActivity.findViewById(R.id.addView).setVisibility(View.VISIBLE);

        if(parentActivity.shouldDisplayPushNotification()) {
            parentActivity.setShouldDisplayPushNotification(false);
            String title = parentActivity.getPushNotificationTitle();
            String body = parentActivity.getPushNotificationBody();
            String is_html = parentActivity.getPushNotificationIsHTML();
//            String pushId = parentActivity.getPushNotificationPushId();
            if(is_html != null && is_html.equals("true")) {
                //TODO: This will have to make an api call with pushId
            }
            else {
                Intent intent = new Intent(parentActivity, NotificationActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("body", body);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        initScopePopupMenu(parentLayout);
        loader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("On Pause", "On Pause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("On Resume", "On Resume");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "DashboardTileFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        parentActivity.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("On Start", "On Start");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("On Stop", "On Stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("On Destroy", "On Destroy");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("On Create", "On Create");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("On Destroy View", "On Destroy View");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("On Save Instance State", "On Save Instance State");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e("On Restore Instance State", "On Restore Instance State");
    }

    private void initScopePopupMenu(@NonNull View view) {
        scopePopup = new PopupMenu(view.getContext(), scopeSelectorText);

        scopePopup.setOnMenuItemClickListener(item -> {
            ScopeBarModel selectedScope = globalDataViewModel.getScopeDataValue().get(item.getItemId());
            if(selectedScope.getName().equalsIgnoreCase("-- Groups --") || selectedScope.getName().equalsIgnoreCase("-- Agents --")) {
                // DO NOTHING
                scopePopup.dismiss();
            }
            else {
                scopePopup.dismiss();
                parentActivity.setScopeFilter(selectedScope);
                apiManager.getTileSetup(dashboardTilesViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), "agent", selectedScope.getIdValue());
            }
            return false;
        });

        int counter = 0;
        for(ScopeBarModel scope : globalDataViewModel.getScopeDataValue()) {
            SpannableString s = new SpannableString(scope.getName());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterText()), 0, s.length(), 0);
            scopePopup.getMenu().add(1, counter, counter, s);
            counter++;
        }
    }

    private void initPopupMenu(@NonNull View view) {
        popup = new PopupMenu(view.getContext(), dateSelectorDateText);

        popup.setOnMenuItemClickListener(this);
        List<String> timelineArray = initSpinnerArray();
        int counter = 0;
        for(String timePeriod : timelineArray) {
            SpannableString s = new SpannableString(timePeriod);
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterText()), 0, s.length(), 0);
            popup.getMenu().add(1, counter, counter, s);
            counter++;
        }
    }

    private void initDateSelector(@NonNull View view) {
        dateSelectorDateText = view.findViewById(R.id.dateSelectorDate);
        dateSelectorDateText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        dateSelectorDateText.setTextColor(colorSchemeManager.getLighterText());

        dateSelectorBeginDateText = view.findViewById(R.id.dateSelectorBeginDate);
        dateSelectorBeginDateText.setText(dateManager.getFormattedStartTime());
        dateSelectorBeginDateText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        dateSelectorBeginDateText.setTextColor(colorSchemeManager.getLighterText());

        dateSelectorEndDateText = view.findViewById(R.id.dateSelectorEndDate);
        dateSelectorEndDateText.setText(dateManager.getFormattedEndTime());
        dateSelectorEndDateText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        dateSelectorEndDateText.setTextColor(colorSchemeManager.getLighterText());

        scopeSelectorText = view.findViewById(R.id.scopeSelector);
        scopeSelectorText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        scopeSelectorText.setTextColor(colorSchemeManager.getLighterText());

        dateSelectorDateText.setOnClickListener(this);
        dateSelectorBeginDateText.setOnClickListener(this);
        dateSelectorEndDateText.setOnClickListener(this);
        scopeSelectorText.setOnClickListener(this);
    }

    @NonNull
    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Yesterday");
        spinnerArray.add("Today");
        spinnerArray.add("Last Week");
        spinnerArray.add("This Week");
        spinnerArray.add(dateManager.getLastMonth());
        spinnerArray.add(dateManager.getThisMonth());
        spinnerArray.add(dateManager.getLastYear());
        spinnerArray.add(dateManager.getThisYear());
        return spinnerArray;
    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.dateSelectorDate:
                popup.show();
                break;
            case R.id.dateSelectorBeginDate:
                beginDateSelected = true;
                endDateSelected = false;
                new SpinnerDatePickerDialogBuilder()
                        .context(getContext())
                        .callback(this)
                        .spinnerTheme(android.R.style.Theme_Holo_Dialog)
                        .showTitle(false)
                        .defaultDate(dateManager.getSelectedStartYear(), dateManager.getSelectedStartMonth(), dateManager.getSelectedStartDay())
                        .minDate(1990, 0, 1)
                        .build()
                        .show();
                break;
            case R.id.dateSelectorEndDate:
                beginDateSelected = false;
                endDateSelected = true;
                new SpinnerDatePickerDialogBuilder()
                        .context(getContext())
                        .callback(this)
                        .spinnerTheme(android.R.style.Theme_Holo_Dialog)
                        .showTitle(false)
                        .defaultDate(dateManager.getSelectedEndYear(), dateManager.getSelectedEndMonth(), dateManager.getSelectedEndDay())
                        .minDate(1990, 0, 1)
                        .build()
                        .show();
                break;
            case R.id.scopeSelector:
                scopePopup.show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                //Yesterday
                dateManager.setToYesterday();
                break;
            case 1:
                //Today
                dateManager.setToToday();
                break;
            case 2:
                //Last Week
                dateManager.setToLastWeek();
                break;
            case 3:
                //This Week
                dateManager.setToThisWeek();
                break;
            case 4:
                //Last Month
                dateManager.setToLastMonth();
                break;
            case 5:
                //This Month
                dateManager.setToThisMonth();
                break;
            case 6:
                //Last year
                dateManager.setToLastYear();
                break;
            case 7:
                //This year
                dateManager.setToThisYear();
                break;
            default:
                return false;
        }

        dateSelectorBeginDateText.setText(dateManager.getFormattedStartTime());
        dateSelectorEndDateText.setText(dateManager.getFormattedEndTime());
        loader.setVisibility(View.VISIBLE);
        if(parentActivity.getCurrentScopeFilter() != null) {
            apiManager.getTileSetup(dashboardTilesViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(dashboardTilesViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), "a" + globalDataViewModel.getAgentValue().getAgent_id());
        }

        return false;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        if(beginDateSelected) {
            dateManager.setSelectedStartTime(year, monthOfYear, dayOfMonth);
            dateSelectorBeginDateText.setText(dateManager.getFormattedStartTime());
        }
        else if(endDateSelected) {
            dateManager.setSelectedEndTime(year, monthOfYear, dayOfMonth);
            dateSelectorEndDateText.setText(dateManager.getFormattedEndTime());
        }

        beginDateSelected = false;
        endDateSelected = false;

        loader.setVisibility(View.VISIBLE);
        if(parentActivity.getCurrentScopeFilter() != null) {
            apiManager.getTileSetup(dashboardTilesViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(dashboardTilesViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(),"a" + globalDataViewModel.getAgentValue().getAgent_id());
        }
    }
}
