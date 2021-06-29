package co.sisu.mobile.fragments.main;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.LeaderboardListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DateManager;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.LeaderboardAgentModel;
import co.sisu.mobile.models.LeaderboardListObject;
import co.sisu.mobile.utils.Utils;
import co.sisu.mobile.viewModels.GlobalDataViewModel;
import co.sisu.mobile.viewModels.LeaderboardViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment implements AsyncServerEventListener, View.OnClickListener, DatePickerDialog.OnDateSetListener, PopupMenu.OnMenuItemClickListener {

    private LeaderboardListAdapter listAdapter;
    private ListView leaderboardList;
    private ProgressBar loader;
    private ParentActivity parentActivity;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private ActionBarManager actionBarManager;
    private DateManager dateManager;
    private Utils utils;
    private HashMap<String, LeaderboardAgentModel> agents = new HashMap<>();
    private TextView dateSelectorBeginDateText, dateSelectorEndDateText, dateSelectorDateText, scopeSelectorText;
    private GlobalDataViewModel globalDataViewModel;
    private LeaderboardViewModel leaderboardViewModel;
    private PopupMenu scopePopup, dateSelectorPopup;
    private boolean beginDateSelected = false;
    private boolean endDateSelected = false;
    private ImageLoader imageLoader;

    // TODO: This currently isn't setting the color scheme
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        actionBarManager = parentActivity.getActionBarManager();
        utils = parentActivity.getUtils();
        dateManager = new DateManager();
        globalDataViewModel = parentActivity.getGlobalDataViewModel();
        actionBarManager.setToTitleBar("Leaderboards", false);
        loader = parentActivity.findViewById(R.id.parentLoader);
        leaderboardList = view.findViewById(R.id.leaderboardList);
        imageLoader = parentActivity.getImageLoaderInstance();
        loader.setVisibility(View.VISIBLE);
        initDateSelectorAndPopup(view);
        initListeners(view);
        apiManager.getLeaderboardScopeList(leaderboardViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId());
//        setColorScheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "LeaderboardFragment");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "ParentActivity");
        FirebaseAnalytics.getInstance(parentActivity).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void initListeners(View view) {
        leaderboardViewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        leaderboardViewModel.getLeaderboardScopeData().observe(getViewLifecycleOwner(), newLeaderboardScopeData -> {
            initScopePopupMenu(view, newLeaderboardScopeData);
        });

        leaderboardViewModel.getLeaderboardAgentData().observe(getViewLifecycleOwner(), this::displayListData);
    }

    private void initScopePopupMenu(@NonNull View view, @NonNull List<LeaderboardListObject> newLeaderboardScopeData) {
        scopePopup = new PopupMenu(view.getContext(), scopeSelectorText);

        scopePopup.setOnMenuItemClickListener(item -> {
            LeaderboardListObject selectedScope = leaderboardViewModel.getLeaderboardScopeDataValue().get(item.getItemId());
            scopePopup.dismiss();
            apiManager.getLeaderboardList(leaderboardViewModel, globalDataViewModel.getAgentValue().getAgent_id(), globalDataViewModel.getSelectedTeamValue().getId(), dateManager.getFormattedStartTime(), dateManager.getFormattedEndTime(), selectedScope.getKey());

            return false;
        });

        int counter = 0;
        for(LeaderboardListObject scope : newLeaderboardScopeData) {
            SpannableString s = new SpannableString(scope.getLabel());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterText()), 0, s.length(), 0);
            scopePopup.getMenu().add(1, counter, counter, s);
            counter++;
        }
        loader.setVisibility(View.INVISIBLE);
    }

    private void initDateSelectorAndPopup(@NonNull View view) {
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

        dateSelectorPopup = new PopupMenu(view.getContext(), dateSelectorDateText);

        dateSelectorPopup.setOnMenuItemClickListener(this);
        List<String> timelineArray = initSpinnerArray();
        int counter = 0;
        for(String timePeriod : timelineArray) {
            SpannableString s = new SpannableString(timePeriod);
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterText()), 0, s.length(), 0);
            dateSelectorPopup.getMenu().add(1, counter, counter, s);
            counter++;
        }
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

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.leaderboard_parent);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());


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

//        DrawableCompat.setTintList(DrawableCompat.wrap(leaderboardToggle.getThumbDrawable()), new ColorStateList(states, thumbColors));
//        DrawableCompat.setTintList(DrawableCompat.wrap(leaderboardToggle.getTrackDrawable()), new ColorStateList(states, trackColors));

        Drawable drawable = getResources().getDrawable(R.drawable.appointment_icon).mutate();
        drawable.setColorFilter(colorSchemeManager.getIconSelected(), PorterDuff.Mode.SRC_ATOP);
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

    private void displayListData(List<LeaderboardAgentModel> newLeaderboardAgentData) {
        parentActivity.runOnUiThread(() -> {
            listAdapter = new LeaderboardListAdapter(parentActivity, newLeaderboardAgentData, parentActivity, apiManager, globalDataViewModel.getAgentValue().getAgent_id(), imageLoader);
            View header = getLayoutInflater().inflate(R.layout.tile_normal_layout, null);
            TextView headerText = header.findViewById(R.id.normalTileHeader);
            headerText.setTextSize(R.dimen.text_size_larger);
            TextView footerText = header.findViewById(R.id.normalTileFooter);
            footerText.setVisibility(View.GONE);
            headerText.setText(leaderboardViewModel.getLeaderboardTitle());
            leaderboardList.addHeaderView(header);

            leaderboardList.setAdapter(listAdapter);
            leaderboardList.invalidateViews();
            loader.setVisibility(View.INVISIBLE);
        });

    }

    @Override
    public void onEventCompleted(Object returnObject, @NonNull String asyncReturnType) {

    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {

    }

    @Override
    public void onEventFailed(Object o, String s) {
    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateSelectorDate:
                if(dateSelectorPopup != null) {
                    dateSelectorPopup.show();
                }
                else {
                    utils.showToast("Currently retrieving data. Please try again in a moment", parentActivity);
                }
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
                if(scopePopup != null) {
                    scopePopup.show();
                }
                else {
                    utils.showToast("Currently retrieving data. Please try again in a moment", parentActivity);
                }
                break;
            default:
                break;
        }
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

//        loader.setVisibility(View.VISIBLE);
        // TODO: Go and get the correct leaderboard (I don't think I need to. I think only the scope can trigger the api call)
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

        return false;
    }

}
