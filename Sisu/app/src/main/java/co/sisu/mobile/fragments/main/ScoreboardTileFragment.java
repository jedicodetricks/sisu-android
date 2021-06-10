package co.sisu.mobile.fragments.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.PopupMenu;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
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
import okhttp3.Response;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ScoreboardTileFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener, PopupMenu.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener {

    private ParentActivity parentActivity;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private DateManager dateManager;
    private ActionBarManager actionBarManager;
    private TileCreationHelper tileCreationHelper;
    private DataController dataController;
    private ProgressBar loader;
    private LayoutInflater inflater;
    private int numOfRows = 1;

    private TextView dateSelectorBeginDateText, dateSelectorEndDateText, dateSelectorDateText, scopeSelectorText;
    private PopupMenu popup;
    private boolean beginDateSelected = false;
    private boolean endDateSelected = false;

    private PopupMenu scopePopup;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        assert parentActivity != null;
        this.navigationManager = parentActivity.getNavigationManager();
        this.apiManager = parentActivity.getApiManager();
        this.dateManager = parentActivity.getDateManager();
        this.actionBarManager = parentActivity.getActionBarManager();
        this.tileCreationHelper = parentActivity.getTileCreationHelper();
        this.dataController = parentActivity.getDataController();
        loader = parentActivity.findViewById(R.id.parentLoader);
        this.inflater = inflater;
        JSONObject tileTemplate = parentActivity.getTileTemplate();

        return createFullView(container, tileTemplate);
    }

    @NonNull
    @SuppressLint("ResourceType")
    private View createFullView(ViewGroup container, JSONObject tileTemplate) {
        loader.setVisibility(View.VISIBLE);
        JSONArray tile_rows = null;

        RelativeLayout parentRelativeLayout;
        View parentLayout = inflater.inflate(R.layout.activity_tile_template_test_parentlayout, container, false);
        RelativeLayout upperRelativeLayout = parentLayout.findViewById(R.id.tileDashboardDateSelector);
        upperRelativeLayout.setId(1);
        if (tileTemplate != null) {
            try {
                colorSchemeManager = new ColorSchemeManager(tileTemplate.getJSONObject("theme"), parentActivity);
                parentActivity.updateColorScheme(colorSchemeManager);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Create the parent layout that all the rows will go in
            parentLayout.setBackgroundColor(colorSchemeManager.getAppBackground());
            parentRelativeLayout = parentLayout.findViewById(R.id.tileRelativeLayout);
//            initTimelineSelector(parentLayout);
            initDateSelector(parentLayout);
            initPopupMenu(parentLayout);
            //

            try {
                tile_rows = tileTemplate.getJSONArray("tile_rows");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (tile_rows != null) {
                Log.e("NUM OF TILE ROWS", String.valueOf(tile_rows.length()));
                for(int i = 1; i < tile_rows.length(); i++) {
                    try {
                        HorizontalScrollView horizontalScrollView = tileCreationHelper.createRowFromJSON(tile_rows.getJSONObject(i), container, false, 300, inflater, this, null);
                        if(horizontalScrollView != null) {
                            // Add one here to account for the spinner's ID.
                            horizontalScrollView.setId(numOfRows + 1);
                            RelativeLayout.LayoutParams horizontalParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            horizontalParam.addRule(RelativeLayout.BELOW, numOfRows);

                            parentRelativeLayout.addView(horizontalScrollView, horizontalParam);
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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
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
        initScopePopupMenu(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("On Pause", "on pause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "DashboardTileFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        parentActivity.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void initScopePopupMenu(@NonNull View view) {
        scopePopup = new PopupMenu(view.getContext(), scopeSelectorText);

        scopePopup.setOnMenuItemClickListener(item -> {
            ScopeBarModel selectedScope = parentActivity.getScopeBarList().get(item.getItemId());
            if(selectedScope.getName().equalsIgnoreCase("-- Groups --") || selectedScope.getName().equalsIgnoreCase("-- Agents --")) {
                // DO NOTHING
                scopePopup.dismiss();
            }
            else {
                scopePopup.dismiss();
                parentActivity.setScopeFilter(selectedScope);
                parentActivity.resetDashboardTiles(true);
            }
            return false;
        });

        int counter = 0;
        for(ScopeBarModel scope : parentActivity.getScopeBarList()) {
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

    public void teamSwap() {
        loader.setVisibility(View.VISIBLE);
        parentActivity.resetDashboardTiles(false);
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
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.GET_TILES) {
            try {
                String tileString = ((Response) returnObject).body().string();
                parentActivity.setTileTemplate(new JSONObject(tileString));
                if(parentActivity.getCurrentScopeFilter() != null) {
                    actionBarManager.setToTitleBar(parentActivity.getCurrentScopeFilter().getName(), true);
                }
                else {
                    actionBarManager.setToTitleBar("a" + parentActivity.getAgent().getAgent_id(), true);
                }
                navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

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
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), "a" + parentActivity.getAgent().getAgent_id());
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
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(),"a" + parentActivity.getAgent().getAgent_id());
        }
    }
}
