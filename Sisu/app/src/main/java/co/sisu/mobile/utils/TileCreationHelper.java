package co.sisu.mobile.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.content.res.AppCompatResources;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ClientMessagingEvent;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.DateManager;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.fragments.ClientManageFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.main.LeaderboardFragment;
import co.sisu.mobile.fragments.main.MoreFragment;
import co.sisu.mobile.fragments.main.RecordFragment;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.MarketStatusModel;

import static android.view.FrameMetrics.ANIMATION_DURATION;

public class TileCreationHelper {

    private ParentActivity parentActivity;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private DateManager dateManager;
    private ColorSchemeManager colorSchemeManager;
    private DataController dataController;
    private Utils utils;

    public TileCreationHelper(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.apiManager = parentActivity.getApiManager();
        this.navigationManager = parentActivity.getNavigationManager();
        this.dateManager = parentActivity.getDateManager();
        this.utils = parentActivity.getUtils();
        this.colorSchemeManager = parentActivity.getColorSchemeManager();
        this.dataController = parentActivity.getDataController();
    }

    @Nullable
    public HorizontalScrollView createRowFromJSON(@NonNull JSONObject rowObject, ViewGroup container, Boolean isLeaderboardObject, int adjustHeightInt, LayoutInflater inflater, AsyncServerEventListener callback, ClientMessagingEvent clientCallback) {
        try {
            JSONArray rowTiles = rowObject.getJSONArray("tiles");
            double height = rowObject.getDouble("rowheight");
//            Double innerGap = rowObject.getDouble("innerGap");
            Boolean disabled = rowObject.getBoolean("disabled");
//            Boolean square = rowObject.getBoolean("square");
            int maxTiles = rowObject.getInt("max_tiles");

            int correctedHeight = (int) height + adjustHeightInt;
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
                        View v = createNormalView(container, tileObject, inflater);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "smallHeader":
                        side = false;
                        if(tileObject.has("side")) {
                            side = tileObject.getBoolean("side");
                        }
                        if(side) {
                            correctedHeight = (int) height + 175;
                        }
                        v = createSmallHeaderView(container, tileObject, inflater, callback);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "largeHeader":
                        correctedHeight = (int) height + 150;
                        v = createSmallHeaderView(container, tileObject, inflater, callback);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "progress":
                        v = createProgressView(container, tileObject, inflater, callback);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "fullText":
                        v = createFullTextView(container, tileObject, inflater);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "activity":
                        v = createActivityView(container, tileObject, inflater, callback);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "ratioDiamond":
                        correctedHeight = (int) height + 400;
                        v = createRatioDiamondView(container, tileObject, inflater);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "legend":
                        v = createLegendView(container, tileObject, inflater);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "ratio":
                        correctedHeight = (int) height + 100;
                        v = createRatioView(container, tileObject, inflater);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "clientList":
                        correctedHeight = (int) height + 150;
                        v = createClientView(container, tileObject, inflater, clientCallback);
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
                LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, correctedHeight, 1);
                textViewParams.setMargins(2, 2, 2, 2);

                for(View v: rowViews) {
                    linearLayout.addView(v, textViewParams);
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

    @NonNull
    public View createRatioDiamondView(ViewGroup row, @NonNull JSONObject tileObject, @NonNull LayoutInflater inflater) throws JSONException {
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
        leftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

        rightText.setText(rightHeader);
        rightText.setTextColor(Color.parseColor(headerColor));
        rightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

        ratioText.setText(header);
        ratioText.setTextColor(Color.parseColor(headerColor));
        ratioText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

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

    @NonNull
    public View createActivityView(ViewGroup row, @NonNull JSONObject tileObject, @NonNull LayoutInflater inflater, AsyncServerEventListener callback) throws JSONException {
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
        header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

        count.setText(countText);
        count.setTextColor(Color.parseColor(headerColor));
        count.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

        unit.setText(unitText);
        unit.setTextColor(Color.parseColor(headerColor));
        unit.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

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
                        // TODO: IMPORTANT! This needs to navigate to the correct page
//                        navigationManager.stackReplaceFragment(ClientListFragment.class);
                        break;
                    case "scoreboard":
                        if(parentActivity.getCurrentScopeFilter() != null) {
                            apiManager.getTileSetup(callback, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
                        }
                        else {
                            apiManager.getTileSetup(callback, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), "a" + parentActivity.getAgent().getAgent_id());
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

    @NonNull
    public View createNormalView(ViewGroup row, @NonNull JSONObject tileObject, LayoutInflater inflater) throws JSONException {
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
                header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing("small", parentActivity));
            }
            else {
                header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));
            }
        }

        footer.setText(footerText);
        footer.setTextColor(Color.parseColor(footerColor));
        footer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(footerSize, parentActivity));
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
                progress.getProgressDrawable().setColorFilter(ContextCompat.getColor(parentActivity, R.color.sisuOrange), PorterDuff.Mode.SRC_IN);
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

    @NonNull
    public View createSmallHeaderView(ViewGroup row, @NonNull JSONObject tileObject, LayoutInflater inflater, AsyncServerEventListener callback) throws JSONException {
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
        header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

        footer.setText(footerText);
        footer.setTextColor(Color.parseColor(footerColor));
        footer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(footerSize, parentActivity));
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);
//        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        String assignedTileColor = "#FFFFFF";
        if(tileColor instanceof String) {
            assignedTileColor = (String) tileColor;
        }
        else if(tileColor instanceof JSONObject) {
            assignedTileColor = "#FFF000";
        }
        // TODO: The ClientTileFragment had to set this to true because IOS was making assumptions. I don't know if I still need to do that.
        if(rounded) {
            // I'm doing this try catch because apparently there is some bad color data coming in sometimes
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners);
            try {
                roundedCorners.setColor(Color.parseColor(assignedTileColor));
                rowView.setBackground(ContextCompat.getDrawable(row.getContext(), R.drawable.shape_rounded_corners));
            } catch (Exception e) {
                roundedCorners.setColor(ContextCompat.getColor(parentActivity, R.color.sisuOrange));
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
                        // TODO: IMPORTANT! This needs to navigate to the correct page
//                        navigationManager.stackReplaceFragment(ClientListFragment.class);
                        break;
                    case "scoreboard":
                        if(parentActivity.getCurrentScopeFilter() != null) {
                            apiManager.getTileSetup(callback, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
                        }
                        else {
                            apiManager.getTileSetup(callback, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), "a" + parentActivity.getAgent().getAgent_id());
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

    public View createProgressView(ViewGroup row, @NonNull JSONObject tileObject, @NonNull LayoutInflater inflater, AsyncServerEventListener callback) throws JSONException {
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
        progressMark.setColor(ContextCompat.getColor(parentActivity, R.color.sisuWhite));
        progressMark.setProgressBarWidth(parentActivity.getResources().getDimension(R.dimen.circularBarWidth));
        progressMark.setProgressWithAnimation(1, 0);

        TextView titleView = rowView.findViewById(R.id.progressTileHeader);
        titleView.setText(title);
        if(title.length() > 15) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing("small", parentActivity));
        }
        else {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing("medium", parentActivity));
        }
        TextView currentProgressText = rowView.findViewById(R.id.progressTileCurrentNumber);
        currentProgressText.setText((int) currentProgress + "");
        TextView goalProgressText = rowView.findViewById(R.id.progressTileGoalNumber);
        goalProgressText.setText((int) maxProgress + "");
        try {
            progress.setColor(Color.parseColor(progressColor));
        } catch(IllegalArgumentException e) {
            progress.setColor(ContextCompat.getColor(parentActivity, R.color.sisuOrange));
        }
        progress.setProgressBarWidth(parentActivity.getResources().getDimension(R.dimen.circularBarWidth));
        progress.setBackgroundProgressBarWidth(parentActivity.getResources().getDimension(R.dimen.circularBarWidth));
        double percentCompleted = utils.getPercentComplete(currentProgress, maxProgress);
        progress.setProgressWithAnimation((float) percentCompleted, ANIMATION_DURATION);
        progress.setProgress((float) percentCompleted);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.sisuLightGrey)));
        rowView.setClipToOutline(true);

        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(parentActivity, R.drawable.shape_rounded_corners);
            roundedCorners.setColor(ContextCompat.getColor(parentActivity, R.color.sisuAlmostBlack));
            rowView.setBackground(ContextCompat.getDrawable(parentActivity, R.drawable.shape_rounded_corners));
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
                constraintLayout.setOnClickListener(view -> progressOnClick(clickDestination, tapClientFilter, callback));

                progressMark.setOnClickListener(view -> progressOnClick(clickDestination, tapClientFilter, callback));
            }
            else {
                final String clickDestination = tileObject.getString("tap");
                constraintLayout.setOnClickListener(view -> progressOnClick(clickDestination, "", callback));

                progressMark.setOnClickListener(view -> progressOnClick(clickDestination, "", callback));
            }

        }

        return rowView;
    }

    @NonNull
    public View createFullTextView(ViewGroup row, @NonNull JSONObject tileObject, @NonNull LayoutInflater inflater) throws JSONException {
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
        leftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));

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
            roundedCorners.setColor(ContextCompat.getColor(row.getContext(), R.color.sisuAlmostBlack));
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

    @NonNull
    public View createLegendView(ViewGroup row, @NonNull JSONObject tileObject, @NonNull LayoutInflater inflater) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_legend_layout, row, false);
        ImageView noPace = rowView.findViewById(R.id.legendTileNoPaceCircle);
        ImageView onPace = rowView.findViewById(R.id.legendTilePaceCircle);
        ImageView onGoal = rowView.findViewById(R.id.legendTileGoalCircle);

        try {
            noPace.setColorFilter(Color.parseColor(tileObject.getString("progress_offtrack")), PorterDuff.Mode.SRC_ATOP);
            onPace.setColorFilter(Color.parseColor(tileObject.getString("progress_ontrack")), PorterDuff.Mode.SRC_ATOP);
            onGoal.setColorFilter(Color.parseColor(tileObject.getString("progress_complete")), PorterDuff.Mode.SRC_ATOP);
        } catch(IllegalArgumentException e) {
            noPace.setColorFilter(ContextCompat.getColor(parentActivity, R.color.sisuYellow), PorterDuff.Mode.SRC_ATOP);
            onPace.setColorFilter(ContextCompat.getColor(parentActivity, R.color.sisuOrange), PorterDuff.Mode.SRC_ATOP);
            onGoal.setColorFilter(ContextCompat.getColor(parentActivity, R.color.sisuWhite), PorterDuff.Mode.SRC_ATOP);
        }

        return rowView;
    }

    public View createRatioView(ViewGroup row, @NonNull JSONObject tileObject, @NonNull LayoutInflater inflater) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_ratio_layout, row, false);
        String titleString = tileObject.getString("header");
        TextView titleText = rowView.findViewById(R.id.ratioTileTitle);

        String headerColor = tileObject.getString("header_text_color");
        String headerSize = tileObject.getString("font_header");
        titleText.setText(titleString);
        titleText.setTextColor(Color.parseColor(headerColor));
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));
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

    public View createClientView(ViewGroup row, JSONObject tileObject, @NonNull LayoutInflater inflater, ClientMessagingEvent clientCallback) throws JSONException {
        View rowView;
        ConstraintLayout paginateInfo = parentActivity.findViewById(R.id.paginateInfo);
        rowView = inflater.inflate(R.layout.adapter_client_list, row, false);

        String headerText = tileObject.getString("header");
        String footerText = tileObject.getString("value");
        String headerColor = tileObject.getString("header_text_color");
        String footerColor = tileObject.getString("footer_text_color");
        String headerSize = tileObject.getString("font_header");
        String footerSize = tileObject.getString("font_footer");

        TextView header = rowView.findViewById(R.id.client_list_title);
        TextView footer = rowView.findViewById(R.id.client_list_subtitle);

        header.setText(headerText);
        header.setTextColor(Color.parseColor(headerColor));
        if(headerText.length() > 15) {
            header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing("small", parentActivity));
        }
        else {
            header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize, parentActivity));
        }

        footer.setText(footerText);
        footer.setTextColor(Color.parseColor(footerColor));
        footer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(footerSize, parentActivity));
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);

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

        rowView.setOnClickListener(view -> {
            try {
                ClientObject selectedClient = new ClientObject(tileObject.getJSONObject("tile_data"));
                parentActivity.setSelectedClient(selectedClient);
                paginateInfo.setVisibility(View.GONE);
                navigationManager.stackReplaceFragment(ClientManageFragment.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        ClientObject clientObject = new ClientObject(tileObject.getJSONObject("tile_data"));
        ImageView textImage = rowView.findViewById(R.id.leftButton);
        Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.text_message_icon_active, null).mutate();
        drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
//        drawable.setColorFilter(ContextCompat.getColor(rowView.getContext(), colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
        textImage.setImageDrawable(drawable);

        ImageView phoneImage = rowView.findViewById(R.id.centerButton);
        drawable = parentActivity.getResources().getDrawable(R.drawable.phone_icon_active, null).mutate();
        drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
//        drawable.setColorFilter(ContextCompat.getColor(rowView.getContext(), colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
        phoneImage.setImageDrawable(drawable);

        ImageView emailImage = rowView.findViewById(R.id.rightButton);
        drawable = parentActivity.getResources().getDrawable(R.drawable.email_icon_active, null).mutate();
        drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
//        drawable.setColorFilter(ContextCompat.getColor(rowView.getContext(), colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
        emailImage.setImageDrawable(drawable);

//
        if(clientObject.getHome_phone() == null || clientObject.getHome_phone().equals("")) {
            phoneImage.setVisibility(View.INVISIBLE);
        }
        else {
            phoneImage.setVisibility(View.VISIBLE);
        }

        if(clientObject.getMobile_phone() == null || clientObject.getMobile_phone().equals("")) {
            textImage.setVisibility(View.INVISIBLE);
        } else {
            phoneImage.setVisibility(View.VISIBLE);
            textImage.setVisibility(View.VISIBLE);
            textImage.setOnClickListener(v -> clientCallback.onTextClicked(clientObject.getMobile_phone(), clientObject));
            phoneImage.setOnClickListener(v -> clientCallback.onPhoneClicked(clientObject.getMobile_phone() != null ? clientObject.getMobile_phone() : clientObject.getHome_phone(), clientObject));

        }

        if(clientObject.getEmail() == null || clientObject.getEmail().equals("")) {
            emailImage.setVisibility(View.INVISIBLE);
        } else {
            emailImage.setVisibility(View.VISIBLE);
            emailImage.setOnClickListener(v -> clientCallback.onEmailClicked(clientObject.getEmail(), clientObject));

        }

        ImageView thumbnail = rowView.findViewById(R.id.client_list_thumbnail);
        if(clientObject.getIs_locked() == 1) {
            if(clientObject.getType_id().equalsIgnoreCase("b")) {
                drawable = parentActivity.getResources().getDrawable(R.drawable.lock_icon).mutate();
                drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.sisuYellow), PorterDuff.Mode.SRC_ATOP);
                thumbnail.setImageDrawable(drawable);
            } else {
                drawable = parentActivity.getResources().getDrawable(R.drawable.lock_icon).mutate();
                drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.sisuOrange), PorterDuff.Mode.SRC_ATOP);
                thumbnail.setImageDrawable(drawable);
            }
        }
        else {
            if(clientObject.getType_id().equalsIgnoreCase("b")) {
                drawable = parentActivity.getResources().getDrawable(R.drawable.seller_icon_active).mutate();
                drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.sisuYellow), PorterDuff.Mode.SRC_ATOP);
                thumbnail.setImageDrawable(drawable);
            } else {
                thumbnail.setImageResource(R.drawable.seller_icon_active);
            }
        }


        return rowView;
    }

    private float getTextViewSizing(@NonNull String size, Context context) {
        float returnSize;
        switch(size) {
            case "small":
                returnSize = context.getResources().getDimension(R.dimen.font_small);
                break;
            case "medium":
            case "mediam":
                returnSize = context.getResources().getDimension(R.dimen.font_large);
                break;
            case "large":
                returnSize = context.getResources().getDimension(R.dimen.font_larger);
                break;
            default:
                returnSize = context.getResources().getDimension(R.dimen.font_mega);
                Log.e("TEXTVIEW SIZE", "Error setting TextView Size: " + size);
                break;
        }

        return returnSize;
    }

    private void progressOnClick(String clickDestination, String marketStatusKey, AsyncServerEventListener callback) {
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
                    apiManager.getTileSetup(callback, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), parentActivity.getCurrentScopeFilter().getIdValue());
                }
                else {
                    apiManager.getTileSetup(callback, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId(), dateManager.getSelectedStartTime(), dateManager.getSelectedEndTime(), parentActivity.getDashboardType(), "a" + parentActivity.getAgent().getAgent_id());
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

    public LayerDrawable getBorders(int bgColor, int borderColor, int left, int top, int right, int bottom){
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
}
