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
    private ProgressBar loader;
    private LayoutInflater inflater;
    private int numOfRows = 1;
    private boolean isAgentDashboard;

    private ConstraintLayout leftLayout, rightLayout;
    private TextView dateSelectorBeginDateText, dateSelectorEndDateText, dateSelectorDateText, scopeSelectorText;
    private PopupMenu popup;
    private int selectedYear = 0;
    private int selectedMonth = 0;
    private int selectedDay = 0;
    private boolean beginDateSelected = false;
    private boolean endDateSelected = false;
    private String dashboardType = "agent";
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
        loader = parentActivity.findViewById(R.id.parentLoader);
        this.inflater = inflater;
        this.isAgentDashboard = parentActivity.isAgentDashboard();
        JSONObject tileTemplate = parentActivity.getTileTemplate();

        return createFullView(container, tileTemplate);
    }

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
            initCalendarHandler();
            //

            try {
                tile_rows = tileTemplate.getJSONArray("tile_rows");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("NUM OF TILE ROWS", String.valueOf(tile_rows.length()));
            for(int i = 1; i < tile_rows.length(); i++) {
                try {
                    HorizontalScrollView horizontalScrollView = createRowFromJSON(tile_rows.getJSONObject(i), container, false);
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

    private void initScopePopupMenu(View view) {
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

    private void initPopupMenu(View view) {
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

    private void initCalendarHandler() {
        // TODO: this should probably just be handled in the DateManager
        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    private void initDateSelector(View view) {
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

    private List<String> initSpinnerArray() {
        // TODO: This should probably juse be handled in the DateManager
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

        return spinnerArray;
    }

    public void teamSwap() {
        loader.setVisibility(View.VISIBLE);
        parentActivity.resetDashboardTiles(false);
    }

    private HorizontalScrollView createRowFromJSON(JSONObject rowObject, ViewGroup container, Boolean isLeaderboardObject) {
        // TODO: We can probably move this method into a util since I assume there is overlap.
//        Log.e("ROW OBJECT", String.valueOf(rowObject));
        try {
            JSONArray rowTiles = rowObject.getJSONArray("tiles");
            double height = rowObject.getDouble("rowheight");
//            Double innerGap = rowObject.getDouble("innerGap");
            Boolean disabled = rowObject.getBoolean("disabled");
//            Boolean square = rowObject.getBoolean("square");
            int maxTiles = rowObject.getInt("max_tiles");

            int correctedHeight = (int) height + 300;
            List<View> rowViews = new ArrayList<>();

            for(int i = 0; i < rowTiles.length(); i++) {
                JSONObject tileObject = rowTiles.getJSONObject(i);
                String type = tileObject.getString("type");

                switch (type) {
                    case "normal":
                        boolean side = false;
                        if(tileObject.has("side")) {
                            side = tileObject.getBoolean("side");
                        }
                        if(side) {
                            correctedHeight = (int) height + 150;
                        }
                        View v = createNormalView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "smallHeader":
                        side = false;
                        if(tileObject.has("side")) {
                            side = tileObject.getBoolean("side");
                        }
                        if(side) {
                            correctedHeight = (int) height + 150;
                        }
                        v = createSmallHeaderView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "largeHeader":
                        correctedHeight = (int) height + 150;
                        v = createSmallHeaderView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "progress":
                        v = createProgressView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "fullText":
                        v = createFullTextView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "activity":
                        v = createActivityView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "ratioDiamond":
                        correctedHeight = (int) height + 400;
                        v = createRatioDiamondView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "legend":
                        v = createLegendView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "ratio":
                        correctedHeight = (int) height + 100;
                        v = createRatioView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    default:
                        Log.e("TYPE", type);
                        break;
                }
            }

            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);

            View view;

            if(rowViews.size() > maxTiles) {
                view = inflater.inflate(R.layout.activity_tile_template_test, container, false);
                RelativeLayout relativeLayout = view.findViewById(R.id.tileRelativeLayout);


                // Starting this at 50 so that it doesn't get confused with other IDs.
                int viewCounter = 50;
                for(View v: rowViews) {
                    v.setId(viewCounter);
                    RelativeLayout.LayoutParams childRelativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, correctedHeight);
//                    childRelativeParams.setMargins(2, 2, 2, 2);
                    if(viewCounter > 50) {
                        childRelativeParams.addRule(RelativeLayout.RIGHT_OF, viewCounter - 1);
                    }
                    relativeLayout.addView(v, childRelativeParams);
                    viewCounter++;
                }
                horizontalScrollView.addView(relativeLayout);
            }
            else {
                view = inflater.inflate(R.layout.activity_tile_template_linear_test, container, false);
                LinearLayout linearLayout = view.findViewById(R.id.tileLinearLayout);
                LinearLayout.LayoutParams textviewparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, correctedHeight, 1);
                textviewparam.setMargins(2, 2, 2, 2);

                for(View v: rowViews) {
                    linearLayout.addView(v, textviewparam);
                }
                horizontalScrollView.addView(linearLayout);
            }

//            view.setLayoutParams(relativeParams);

            return horizontalScrollView;

        } catch (JSONException e) {
            // That means this is probably a spacer
            try {
                double height = rowObject.getDouble("rowheight");

                HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) height);
                View view = inflater.inflate(R.layout.activity_tile_template_linear_test, container, false);
                view.setLayoutParams(relativeParams);
                horizontalScrollView.addView(view);
                return horizontalScrollView;
            } catch (JSONException e1) {
                // If we get here, we've really screwed it up.
                e1.printStackTrace();
            }

        }

        return null;

    }

    private float getTextViewSizing(String size) {
        // TODO: This feels like a util
        float returnSize;
        switch(size) {
            case "small":
                returnSize = getResources().getDimension(R.dimen.font_small);
                break;
            case "medium":
                returnSize = getResources().getDimension(R.dimen.font_large);
                break;
            case "large":
                returnSize = getResources().getDimension(R.dimen.font_larger);
                break;
            default:
                returnSize = getResources().getDimension(R.dimen.font_mega);
                Log.e("TEXTVIEW SIZE", "Error setting TextView Size: " + size);
                break;
        }

        return returnSize;
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

    private View createLegendView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_legend_layout, row, false);
        ImageView noPace = rowView.findViewById(R.id.legendTileNoPaceCircle);
        ImageView onPace = rowView.findViewById(R.id.legendTilePaceCircle);
        ImageView onGoal = rowView.findViewById(R.id.legendTileGoalCircle);

        try {
            noPace.setColorFilter(Color.parseColor(tileObject.getString("progress_offtrack")), PorterDuff.Mode.SRC_ATOP);
            onPace.setColorFilter(Color.parseColor(tileObject.getString("progress_ontrack")), PorterDuff.Mode.SRC_ATOP);
            onGoal.setColorFilter(Color.parseColor(tileObject.getString("progress_complete")), PorterDuff.Mode.SRC_ATOP);
        } catch(IllegalArgumentException e) {
            noPace.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
            onPace.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange), PorterDuff.Mode.SRC_ATOP);
            onGoal.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        }

        return rowView;
    }

    private View createRatioView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_ratio_layout, row, false);
        String titleString = tileObject.getString("header");
        TextView titleText = rowView.findViewById(R.id.ratioTileTitle);

        String headerColor = tileObject.getString("header_text_color");
        String headerSize = tileObject.getString("font_header");
        titleText.setText(titleString);
        titleText.setTextColor(Color.parseColor(headerColor));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));
        titleText.setGravity(View.TEXT_ALIGNMENT_CENTER);

        String tileColor = tileObject.getString("tile_color");
        boolean rounded = tileObject.getBoolean("rounded");

        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }

        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(Color.parseColor(tileColor));
            rowView.setBackground(ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            int topBorder = 0;
            int leftBorder = 0;
            int rightBorder = 0;
            int bottomBorder = 0;

            switch (border) {
                case "all":
                    topBorder = 5;
                    leftBorder = 5;
                    rightBorder = 5;
                    bottomBorder = 5;
                    break;
                case "top":
                    topBorder = 5;
                    break;
                case "left":
                    leftBorder = 5;
                    break;
                case "right":
                    rightBorder = 5;
                    break;
                case "bottom":
                    bottomBorder = 5;
                    break;
            }

            LayerDrawable borderDrawable = getBorders(
                    Color.parseColor(tileColor), // Background color
                    Color.GRAY, // Border color
                    leftBorder, // Left border in pixels
                    topBorder, // Top border in pixels
                    rightBorder, // Right border in pixels
                    bottomBorder // Bottom border in pixels
            );
            rowView.setBackground(borderDrawable);
        }

        return rowView;
    }

    private View createNormalView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView;
        boolean isSideView = false;
        if(tileObject.has("side")) {
            if(tileObject.getBoolean("side")) {
                rowView = inflater.inflate(R.layout.tile_normal_side_layout, row, false);
                isSideView = true;
            }
            else {
                rowView = inflater.inflate(R.layout.tile_normal_layout, row, false);
            }
        }
        else {
            rowView = inflater.inflate(R.layout.tile_normal_layout, row, false);
        }
        String headerText = tileObject.getString("header");
        String footerText = tileObject.getString("value");
        String headerColor = tileObject.getString("header_text_color");
        String footerColor = tileObject.getString("footer_text_color");
        String headerSize = tileObject.getString("font_header");
        String footerSize = tileObject.getString("font_footer");
        JSONObject progressBar = null;
        if(tileObject.has("progress_bar")) {
            progressBar = tileObject.getJSONObject("progress_bar");
        }

        TextView header = rowView.findViewById(R.id.normalTileHeader);
        TextView footer = rowView.findViewById(R.id.normalTileFooter);
        ProgressBar progress = rowView.findViewById(R.id.normalTileProgressBar);

        header.setText(headerText);
        header.setTextColor(Color.parseColor(headerColor));
        if(!isSideView) {
            if(headerText.length() > 15) {
                header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing("small"));
            }
            else {
                header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));
            }
        }

        footer.setText(footerText);
        footer.setTextColor(Color.parseColor(footerColor));
        footer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(footerSize));
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);

        if(progress != null && progressBar != null) {
            double completedPercent = 0.0;
            if(progressBar.has("completed")) {
                completedPercent = progressBar.getDouble("completed");
            }
            String progressColor = progressBar.getString("progress_color");
            progress.setProgress((int) completedPercent);
            try {
                progress.setProgressTintList(ColorStateList.valueOf(Color.parseColor(progressColor)));
//                progress.getProgressDrawable().setColorFilter(Color.parseColor(progressColor), PorterDuff.Mode.SRC_IN);

            } catch (IllegalArgumentException e) {
                progress.getProgressDrawable().setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange), PorterDuff.Mode.SRC_IN);
            }
        }

        String tileColor = tileObject.getString("tile_color");
        boolean rounded = tileObject.getBoolean("rounded");

        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }

        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(Color.parseColor(tileColor));
            rowView.setBackground(ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            int topBorder = 0;
            int leftBorder = 0;
            int rightBorder = 0;
            int bottomBorder = 0;

            switch (border) {
                case "all":
                    topBorder = 5;
                    leftBorder = 5;
                    rightBorder = 5;
                    bottomBorder = 5;
                    break;
                case "top":
                    topBorder = 5;
                    break;
                case "left":
                    leftBorder = 5;
                    break;
                case "right":
                    rightBorder = 5;
                    break;
                case "bottom":
                    bottomBorder = 5;
                    break;
            }

            LayerDrawable borderDrawable = getBorders(
                    Color.parseColor(tileColor), // Background color
                    Color.GRAY, // Border color
                    leftBorder, // Left border in pixels
                    topBorder, // Top border in pixels
                    rightBorder, // Right border in pixels
                    bottomBorder // Bottom border in pixels
            );
            rowView.setBackground(borderDrawable);
        }

        return rowView;
    }

    private View createSmallHeaderView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView;
        if(tileObject.has("side")) {
            if(tileObject.getBoolean("side")) {
                rowView = inflater.inflate(R.layout.tile_smallheader_side_layout, row, false);
            }
            else {
                rowView = inflater.inflate(R.layout.tile_smallheader_layout, row, false);
            }
        }
        else {
            rowView = inflater.inflate(R.layout.tile_smallheader_layout, row, false);
        }

        ConstraintLayout parentLayout = rowView.findViewById(R.id.smallHeaderTileParent);

        boolean rounded = false;
        String headerText = tileObject.getString("header");
        String footerText = tileObject.getString("value");
        if(tileObject.has("rounded")) {
            try {
                rounded = tileObject.getBoolean("rounded");
            } catch (Exception e) {
                // If we throw this it means he passed us null for rounded... probably
            }
        }
        String headerColor = tileObject.getString("header_text_color");
        String footerColor = tileObject.getString("footer_text_color");
        String headerSize = tileObject.getString("font_header");
        String footerSize = tileObject.getString("font_footer");
        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }

        Object tileColor = tileObject.get("tile_color");

        TextView header = rowView.findViewById(R.id.smallHeaderTileHeader);
        TextView footer = rowView.findViewById(R.id.smallHeaderTileFooter);
        header.setText(headerText);
        header.setTextColor(Color.parseColor(headerColor));
        header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        footer.setText(footerText);
        footer.setTextColor(Color.parseColor(footerColor));
        footer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(footerSize));
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);
//        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        String assignedTileColor = "#FFFFFF";
        if(tileColor instanceof String) {
            assignedTileColor = (String) tileColor;
        }
        else if(tileColor instanceof JSONObject) {
            assignedTileColor = "#FFF000";
        }

        if(rounded) {
            // I'm doing this try catch because apparently there is some bad color data coming in sometimes
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners);
            try {
                roundedCorners.setColor(Color.parseColor(assignedTileColor));
                rowView.setBackground(ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners));
            } catch (Exception e) {
                roundedCorners.setColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
                rowView.setBackground(ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners));
            }

        }
        else {
            int topBorder = 0;
            int leftBorder = 0;
            int rightBorder = 0;
            int bottomBorder = 0;

            switch (border) {
                case "all":
                    topBorder = 5;
                    leftBorder = 5;
                    rightBorder = 5;
                    bottomBorder = 5;
                    break;
                case "top":
                    topBorder = 5;
                    break;
                case "left":
                    leftBorder = 5;
                    break;
                case "right":
                    rightBorder = 5;
                    break;
                case "bottom":
                    bottomBorder = 5;
                    break;
            }

            LayerDrawable borderDrawable = getBorders(
                    Color.parseColor(assignedTileColor), // Background color
                    Color.GRAY, // Border color
                    leftBorder, // Left border in pixels
                    topBorder, // Top border in pixels
                    rightBorder, // Right border in pixels
                    bottomBorder // Bottom border in pixels
            );
            rowView.setBackground(borderDrawable);
        }

        if(tileObject.has("tap")) {
            final String clickDestination = tileObject.getString("tap");
            parentLayout.setOnClickListener(view -> {
                switch (clickDestination) {
                    case "clients":
                        navigationManager.stackReplaceFragment(ClientListFragment.class);
                        break;
                    case "scoreboard":
                        if(parentActivity.getCurrentScopeFilter() != null) {
                            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, parentActivity.getCurrentScopeFilter().getIdValue());
                        }
                        else {
                            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, "a" + parentActivity.getAgent().getAgent_id());
                        }
                        break;
                    case "record":
                        navigationManager.stackReplaceFragment(RecordFragment.class);
                        break;
                    case "report":
                        navigationManager.stackReplaceFragment(ReportFragment.class);
                        break;
                    case "leaderboard":
                        navigationManager.stackReplaceFragment(LeaderboardFragment.class);
                        break;
                    case "more":
                        navigationManager.stackReplaceFragment(MoreFragment.class);
                        break;
                }
            });
        }

        return rowView;
    }

    private View createActivityView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_activity_layout, row, false);
        ConstraintLayout parentLayout = rowView.findViewById(R.id.activityTileParent);
        TextView header = rowView.findViewById(R.id.activityTileHeader);
        TextView count = rowView.findViewById(R.id.activityTileCount);
        TextView unit = rowView.findViewById(R.id.activityTileUnit);

        String headerText = tileObject.getString("header");
        String countText = tileObject.getString("value");
        String unitText = tileObject.getString("units");
        boolean rounded = tileObject.getBoolean("rounded");
        String headerColor = tileObject.getString("header_text_color");
        String headerSize = tileObject.getString("font_header");
        String headerAlignment = tileObject.getString("header_alignment");

        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }
        String tileColor = tileObject.getString("tile_color");

        header.setText(headerText);
        header.setTextColor(Color.parseColor(headerColor));
        header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        count.setText(countText);
        count.setTextColor(Color.parseColor(headerColor));
        count.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        unit.setText(unitText);
        unit.setTextColor(Color.parseColor(headerColor));
        unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(Color.parseColor(tileColor));
            rowView.setBackground(ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            int topBorder = 0;
            int leftBorder = 0;
            int rightBorder = 0;
            int bottomBorder = 0;

            switch (border) {
                case "all":
                    topBorder = 5;
                    leftBorder = 5;
                    rightBorder = 5;
                    bottomBorder = 5;
                    break;
                case "top":
                    topBorder = 5;
                    break;
                case "left":
                    leftBorder = 5;
                    break;
                case "right":
                    rightBorder = 5;
                    break;
                case "bottom":
                    bottomBorder = 5;
                    break;
            }

            LayerDrawable borderDrawable = getBorders(
                    Color.parseColor(tileColor), // Background color
                    Color.GRAY, // Border color
                    leftBorder, // Left border in pixels
                    topBorder, // Top border in pixels
                    rightBorder, // Right border in pixels
                    bottomBorder // Bottom border in pixels
            );
            rowView.setBackground(borderDrawable);
        }

        if(tileObject.has("tap")) {
            final String clickDestination = tileObject.getString("tap");
            parentLayout.setOnClickListener(view -> {
                switch (clickDestination) {
                    case "clients":
                        navigationManager.stackReplaceFragment(ClientListFragment.class);
                        break;
                    case "scoreboard":
                        if(parentActivity.getCurrentScopeFilter() != null) {
                            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, parentActivity.getCurrentScopeFilter().getIdValue());
                        }
                        else {
                            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, "a" + parentActivity.getAgent().getAgent_id());
                        }
                        break;
                    case "record":
                        navigationManager.stackReplaceFragment(RecordFragment.class);
                        break;
                    case "report":
                        navigationManager.stackReplaceFragment(ReportFragment.class);
                        break;
                    case "leaderboard":
                        navigationManager.stackReplaceFragment(LeaderboardFragment.class);
                        break;
                    case "more":
                        navigationManager.stackReplaceFragment(MoreFragment.class);
                        break;
                }
            });
        }

        return rowView;
    }

    private View createRatioDiamondView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_ratio_diamond_layout, row, false);
        ImageView diamond = rowView.findViewById(R.id.tileRatioDiamond);
        View centerLine = rowView.findViewById(R.id.tileCenterLine);
//        View verticalLine = rowView.findViewById(R.id.tileVerticalLine);
        TextView ratioText = rowView.findViewById(R.id.tileRatioText);
        TextView leftText = rowView.findViewById(R.id.tileRatioLeftText);
        TextView rightText = rowView.findViewById(R.id.tileRatioRightText);

        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }
        String tileColor = tileObject.getString("tile_color");
        String diamondColor = tileObject.getString("diamond_color");
        String leftHeader = tileObject.getString("left_header");
        String rightHeader = tileObject.getString("right_header");
        String header = tileObject.getString("header");
        String headerColor = tileObject.getString("header_text_color");
        String headerSize = tileObject.getString("font_header");

        leftText.setText(leftHeader);
        leftText.setTextColor(Color.parseColor(headerColor));
        leftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        rightText.setText(rightHeader);
        rightText.setTextColor(Color.parseColor(headerColor));
        rightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        ratioText.setText(header);
        ratioText.setTextColor(Color.parseColor(headerColor));
        ratioText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(row.getContext(), R.drawable.shape_diamond);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(diamondColor));

        diamond.setImageDrawable(wrappedDrawable);
        centerLine.setBackgroundColor(Color.parseColor(diamondColor));
//        verticalLine.setBackgroundColor(Color.parseColor(diamondColor));

        centerLine.setZ(1);
//        verticalLine.setZ(2);
        diamond.setZ(3);
        ratioText.setZ(4);

        int topBorder = 0;
        int leftBorder = 0;
        int rightBorder = 0;
        int bottomBorder = 0;

        switch (border) {
            case "all":
                topBorder = 5;
                leftBorder = 5;
                rightBorder = 5;
                bottomBorder = 5;
                break;
            case "top":
                topBorder = 5;
                break;
            case "left":
                leftBorder = 5;
                break;
            case "right":
                rightBorder = 5;
                break;
            case "bottom":
                bottomBorder = 5;
                break;
        }

        LayerDrawable borderDrawable = getBorders(
                Color.parseColor(tileColor), // Background color
                Color.GRAY, // Border color
                leftBorder, // Left border in pixels
                topBorder, // Top border in pixels
                rightBorder, // Right border in pixels
                bottomBorder // Bottom border in pixels
        );
        rowView.setBackground(borderDrawable);


        return rowView;
    }

    private View createProgressView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_progress_layout, row, false);
        ConstraintLayout constraintLayout = rowView.findViewById(R.id.progressTileLayout);
        String title = tileObject.getString("under_title");

        double currentProgress = tileObject.getDouble("current");
        double maxProgress = tileObject.getDouble("max");
        String progressColor = tileObject.getString("color");
        boolean rounded = tileObject.getBoolean("rounded");
        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }
        String tileColor = tileObject.getString("tile_color");
        double pacer = tileObject.getDouble("pacer") - 90;

        CircularProgressBar progress = rowView.findViewById(R.id.progressTileProgressBar);
        CircularProgressBar progressMark = rowView.findViewById(R.id.progressTileProgressMark);
        progressMark.setStartAngle((int) pacer);
        progressMark.setColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        progressMark.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progressMark.setProgressWithAnimation(1, 0);

        TextView titleView = rowView.findViewById(R.id.progressTileHeader);
        titleView.setText(title);
        if(title.length() > 15) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing("small"));
        }
        else {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing("medium"));
        }
        TextView currentProgressText = rowView.findViewById(R.id.progressTileCurrentNumber);
        currentProgressText.setText((int) currentProgress + "");
        TextView goalProgressText = rowView.findViewById(R.id.progressTileGoalNumber);
        goalProgressText.setText((int) maxProgress + "");
        try {
            progress.setColor(Color.parseColor(progressColor));
        } catch(IllegalArgumentException e) {
            progress.setColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
        }
        progress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        double percentCompleted = getPercentComplete(currentProgress, maxProgress);
        progress.setProgressWithAnimation((float) percentCompleted, ANIMATION_DURATION);
        progress.setProgress((float) percentCompleted);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        rowView.setClipToOutline(true);

        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
            rowView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            int topBorder = 0;
            int leftBorder = 0;
            int rightBorder = 0;
            int bottomBorder = 0;

            switch (border) {
                case "all":
                    topBorder = 5;
                    leftBorder = 5;
                    rightBorder = 5;
                    bottomBorder = 5;
                    break;
                case "top":
                    topBorder = 5;
                    break;
                case "left":
                    leftBorder = 5;
                    break;
                case "right":
                    rightBorder = 5;
                    break;
                case "bottom":
                    bottomBorder = 5;
                    break;
            }

            LayerDrawable borderDrawable = getBorders(
                    Color.parseColor(tileColor), // Background color
                    Color.GRAY, // Border color
                    leftBorder, // Left border in pixels
                    topBorder, // Top border in pixels
                    rightBorder, // Right border in pixels
                    bottomBorder // Bottom border in pixels
            );

            rowView.setBackground(borderDrawable);
        }

        if(tileObject.has("tap")) {
            if(tileObject.has("tap_client_filter")) {
                final String tapClientFilter = tileObject.getString("tap_client_filter");
                final String clickDestination = tileObject.getString("tap");
                constraintLayout.setOnClickListener(view -> progressOnClick(clickDestination, tapClientFilter));

                progressMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressOnClick(clickDestination, tapClientFilter);
                    }
                });
            }
            else {
                final String clickDestination = tileObject.getString("tap");
                constraintLayout.setOnClickListener(view -> progressOnClick(clickDestination, ""));

                progressMark.setOnClickListener(view -> progressOnClick(clickDestination, ""));
            }

        }

        return rowView;
    }

    private void progressOnClick(String clickDestination, String marketStatusKey) {
        switch (clickDestination) {
            case "clients":
                if(!marketStatusKey.equals("")) {
                    MarketStatusModel selectedMarketStatus = new MarketStatusModel(marketStatusKey, "", true);

                    parentActivity.setCurrentMarketStatusFilter(selectedMarketStatus);
                    parentActivity.resetClientTiles("", 1);
                    parentActivity.setSelectedFilter(null);
                }
                else {
                    parentActivity.resetClientTiles("", 1);
                }
                break;
            case "scoreboard":
                if(parentActivity.getCurrentScopeFilter() != null) {
                    apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, parentActivity.getCurrentScopeFilter().getIdValue());
                }
                else {
                    apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, "a" + parentActivity.getAgent().getAgent_id());
                }
                break;
            case "record":
                navigationManager.stackReplaceFragment(RecordFragment.class);
                break;
            case "report":
                navigationManager.stackReplaceFragment(ReportFragment.class);
                break;
            case "leaderboard":
                navigationManager.stackReplaceFragment(LeaderboardFragment.class);
                break;
            case "more":
                navigationManager.stackReplaceFragment(MoreFragment.class);
                break;
        }
    }

    private View createFullTextView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_fulltext_side_layout, row, false);
        TextView leftText = rowView.findViewById(R.id.fullTextTileHeader);


        String headerText = tileObject.getString("header");
        boolean rounded = tileObject.getBoolean("rounded");
        String headerColor = tileObject.getString("header_text_color");
        String headerSize = tileObject.getString("font_header");
        String headerAlignment = tileObject.getString("header_alignment");

        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }
        String tileColor = tileObject.getString("tile_color");

        leftText.setText(headerText);
        leftText.setTextColor(Color.parseColor(headerColor));
        leftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        switch (headerAlignment) {
            case "left":
                leftText.setGravity(View.TEXT_ALIGNMENT_VIEW_START);
                break;
            case "center":
                leftText.setGravity(View.TEXT_ALIGNMENT_CENTER);
                break;
            case "right":
                leftText.setGravity(View.TEXT_ALIGNMENT_VIEW_END);
                break;
        }

        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(ContextCompat.getColor(row.getContext(), R.color.colorAlmostBlack));
            rowView.setBackground(ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            int topBorder = 0;
            int leftBorder = 0;
            int rightBorder = 0;
            int bottomBorder = 0;

            switch (border) {
                case "all":
                    topBorder = 5;
                    leftBorder = 5;
                    rightBorder = 5;
                    bottomBorder = 5;
                    break;
                case "top":
                    topBorder = 5;
                    break;
                case "left":
                    leftBorder = 5;
                    break;
                case "right":
                    rightBorder = 5;
                    break;
                case "bottom":
                    bottomBorder = 5;
                    break;
            }

            LayerDrawable borderDrawable = getBorders(
                    Color.parseColor(tileColor), // Background color
                    Color.GRAY, // Border color
                    leftBorder, // Left border in pixels
                    topBorder, // Top border in pixels
                    rightBorder, // Right border in pixels
                    bottomBorder // Bottom border in pixels
            );
            rowView.setBackground(borderDrawable);
        }
        return rowView;
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
            dashboardType = "agent";
            if(!isAgentClicked) {
                dashboardType = "team";
            }
            loader.setVisibility(View.VISIBLE);
            if(parentActivity.getCurrentScopeFilter() != null) {
                apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, parentActivity.getCurrentScopeFilter().getIdValue());
            }
            else {
                apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, "a" + parentActivity.getAgent().getAgent_id());
            }
        }
    }

    private Date getDateFromFormattedTime(String formattedTime) {
        // TODO: This is a DateManager Util
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = formatter.parse(formattedTime);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPercentComplete(double currentNum, double goalNum){
        // TODO: This feels like a util
        if(goalNum == 0) {
            if(currentNum > 0) {
                return 100;
            }
            else {
                return 0;
            }
        }

        return (int) ((currentNum/goalNum) * 100);
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
                        .defaultDate(selectedYear, selectedMonth, selectedDay)
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
                        .defaultDate(selectedYear, selectedMonth, selectedDay)
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
                    navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class, parentActivity.getCurrentScopeFilter().getName());
                }
                else {
                    navigationManager.clearStackReplaceFragment(ScoreboardTileFragment.class, "a" + parentActivity.getAgent().getAgent_id());
                }
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
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, "a" + parentActivity.getAgent().getAgent_id());
        }

        return false;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        // TODO: I bet all of this logic could just be passed into the DateManager
        monthOfYear = monthOfYear + 1;
        String formattedMonth = String.valueOf(monthOfYear);
        String formattedDay = String.valueOf(dayOfMonth);

        if(dayOfMonth < 10) {
            formattedDay = "0" + dayOfMonth;
        }

        if(monthOfYear < 10) {
            formattedMonth = "0" + monthOfYear;
        }

        if(beginDateSelected) {
            dateSelectorBeginDateText.setText(year + "-" + formattedMonth + "-" + formattedDay);
            dateManager.setSelectedStartTime(getDateFromFormattedTime(year + "-" + formattedMonth + "-" + formattedDay));
            dateManager.setFormattedStartTime(year + "-" + formattedMonth + "-" + formattedDay);
        }
        else if(endDateSelected) {
            dateSelectorEndDateText.setText(year + "-" + formattedMonth + "-" + formattedDay);
            dateManager.setSelectedEndTime(getDateFromFormattedTime(year + "-" + formattedMonth + "-" + formattedDay));
            dateManager.setFormattedEndTime(year + "-" + formattedMonth + "-" + formattedDay);
        }

        beginDateSelected = false;
        endDateSelected = false;

        loader.setVisibility(View.VISIBLE);
        if(parentActivity.getCurrentScopeFilter() != null) {
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType, parentActivity.getCurrentScopeFilter().getIdValue());
        }
        else {
            apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), dashboardType,"a" + parentActivity.getAgent().getAgent_id());
        }
    }
}
