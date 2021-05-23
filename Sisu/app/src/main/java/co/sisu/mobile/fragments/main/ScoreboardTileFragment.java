package co.sisu.mobile.fragments.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
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
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.oldFragments.ClientListFragment;
import co.sisu.mobile.utils.CircularProgressBar;
import co.sisu.mobile.utils.TileCreationHelper;
import co.sisu.mobile.utils.Utils;
import okhttp3.Response;

import static android.view.FrameMetrics.ANIMATION_DURATION;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ScoreboardTileFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener, PopupMenu.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private DateManager dateManager;
    private ActionBarManager actionBarManager;
    private Utils utils;
    private TileCreationHelper tileCreationHelper;
    private ProgressBar loader;
    private LayoutInflater inflater;
    private int numOfRows = 1;
    private boolean isAgentDashboard;

    private ConstraintLayout leftLayout, rightLayout;
    private TextView dateSelectorBeginDateText, dateSelectorEndDateText, dateSelectorDateText, scopeSelectorText;
    private PopupMenu popup;
    private boolean beginDateSelected = false;
    private boolean endDateSelected = false;

    private PopupMenu scopePopup;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        dateManager = parentActivity.getDateManager();
        actionBarManager = parentActivity.getActionBarManager();
        utils = parentActivity.getUtils();
        tileCreationHelper = parentActivity.getTileCreationHelper();
        loader = parentActivity.findViewById(R.id.parentLoader);
        this.inflater = inflater;
        this.isAgentDashboard = parentActivity.isAgentDashboard();
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
                colorSchemeManager = new ColorSchemeManager(tileTemplate.getJSONObject("theme"));
                parentActivity.setColorSchemeManager(colorSchemeManager);
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
            Log.e("NUM OF TILE ROWS", String.valueOf(tile_rows.length()));
            for(int i = 1; i < tile_rows.length(); i++) {
                try {
                    HorizontalScrollView horizontalScrollView = tileCreationHelper.createRowFromJSON(tile_rows.getJSONObject(i), container, false, inflater, this);
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
        loader.setVisibility(View.INVISIBLE);
        return parentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // TODO: This is throwing a null pointer sometimes apparently.
        actionBarManager.setToTitleBar(parentActivity.getCurrentScopeFilter().getName(), true);
        parentActivity.findViewById(R.id.addView).setVisibility(View.VISIBLE);

        if(parentActivity.shouldDisplayPushNotification()) {
            parentActivity.setShouldDisplayPushNotification(false);
            String title = parentActivity.getPushNotificationTitle();
            String body = parentActivity.getPushNotificationBody();
            String is_html = parentActivity.getPushNotificationIsHTML();
            String pushId = parentActivity.getPushNotificationPushId();
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
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterTextColor()), 0, s.length(), 0);

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
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterTextColor()), 0, s.length(), 0);

            popup.getMenu().add(1, counter, counter, s);

            counter++;
        }
    }

    private void initDateSelector(@NonNull View view) {
        dateSelectorDateText = view.findViewById(R.id.dateSelectorDate);
        dateSelectorDateText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        dateSelectorDateText.setTextColor(colorSchemeManager.getLighterTextColor());

        dateSelectorBeginDateText = view.findViewById(R.id.dateSelectorBeginDate);
        dateSelectorBeginDateText.setText(dateManager.getFormattedStartTime());
        dateSelectorBeginDateText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        dateSelectorBeginDateText.setTextColor(colorSchemeManager.getLighterTextColor());

        dateSelectorEndDateText = view.findViewById(R.id.dateSelectorEndDate);
        dateSelectorEndDateText.setText(dateManager.getFormattedEndTime());
        dateSelectorEndDateText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        dateSelectorEndDateText.setTextColor(colorSchemeManager.getLighterTextColor());

        scopeSelectorText = view.findViewById(R.id.scopeSelector);
        scopeSelectorText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        scopeSelectorText.setTextColor(colorSchemeManager.getLighterTextColor());

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

    protected LayerDrawable getBorders(int bgColor, int borderColor, int left, int top, int right, int bottom){
        // TODO: This feels like a util
        // Initialize new color drawables
        ColorDrawable borderColorDrawable = new ColorDrawable(borderColor);
        ColorDrawable backgroundColorDrawable = new ColorDrawable(bgColor);

        // Initialize a new array of drawable objects
        Drawable[] drawables = new Drawable[]{
                borderColorDrawable,
                backgroundColorDrawable
        };

        // Initialize a new layer drawable instance from drawables array
        LayerDrawable layerDrawable = new LayerDrawable(drawables);

        // Set padding for background color layer
        layerDrawable.setLayerInset(
                1, // Index of the drawable to adjust [background color layer]
                left, // Number of pixels to add to the left bound [left border]
                top, // Number of pixels to add to the top bound [top border]
                right, // Number of pixels to add to the right bound [right border]
                bottom // Number of pixels to add to the bottom bound [bottom border]
        );

        // Finally, return the one or more sided bordered background drawable
        return layerDrawable;
    }


    private void toggleDashboardTypeSelector(boolean isAgentClicked) {
        // TODO: I think this is never used. Adding a breakpoint to see.
        boolean toggled = false;
        LayerDrawable underlineDrawable = getBorders(
                colorSchemeManager.getAppBackground(), // Background color
                Color.GRAY, // Border color
                0, // Left border in pixels
                0, // Top border in pixels
                0, // Right border in pixels
                5 // Bottom border in pixels
        );

        LayerDrawable noUnderlineDrawable = getBorders(
                colorSchemeManager.getAppBackground(), // Background color
                Color.GRAY, // Border color
                0, // Left border in pixels
                0, // Top border in pixels
                0, // Right border in pixels
                0 // Bottom border in pixels
        );

        // TODO: These two layouts could cause an issue. They're null. Does it never go into this block?
        if(isAgentDashboard && !isAgentClicked) {
            leftLayout.setBackground(noUnderlineDrawable);
            rightLayout.setBackground(underlineDrawable);
            toggled = true;
        }
        else if(!isAgentDashboard && isAgentClicked){
            rightLayout.setBackground(noUnderlineDrawable);
            leftLayout.setBackground(underlineDrawable);
            toggled = true;
        }

        if(toggled) {
            isAgentDashboard = !isAgentDashboard;
            parentActivity.setAgentDashboard(isAgentDashboard);
            parentActivity.setDashboardType("agent");
            if(!isAgentClicked) {
                parentActivity.setDashboardType("team");
            }
            loader.setVisibility(View.VISIBLE);
            if(parentActivity.getCurrentScopeFilter() != null) {
                apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
            }
            else {
                apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), "a" + parentActivity.getAgent().getAgent_id());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dashboardTypeTileLeftLayout:
                toggleDashboardTypeSelector(true);
                break;
            case R.id.dashboardTypeTileRightLayout:
                toggleDashboardTypeSelector(false);
                break;
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
                System.out.println("STOP");
                break;
            case R.id.scopeSelector:
                scopePopup.show();
//                parentActivity.getNavigationManager().toggleTeamDrawer();
//                loader.setVisibility(View.VISIBLE);
//                apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), selectedStartTime, selectedEndTime, dashboardType);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

    }

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
//                loader.setVisibility(View.INVISIBLE);
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
    public boolean onMenuItemClick(MenuItem item) {
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
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), "a" + parentActivity.getAgent().getAgent_id());
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
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(),"a" + parentActivity.getAgent().getAgent_id());
        }
    }
}
