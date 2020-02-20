package co.sisu.mobile.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.ScopeBarModel;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ClientTileFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener, PopupMenu.OnMenuItemClickListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private ProgressBar loader;
    private LayoutInflater inflater;

    private int numOfRows = 1;
    private TextView scopeSelectorText, marketStatusFilterText;
    private PopupMenu scopePopup, marketStatusPopup;
    private int selectedYear = 0;
    private int selectedMonth = 0;
    private int selectedDay = 0;
    private boolean beginDateSelected = false;
    private boolean endDateSelected = false;
    private String dashboardType = "agent";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        this.inflater = inflater;
        JSONObject tileTemplate = parentActivity.getClientTiles();

        return createFullView(container, tileTemplate);
    }

    public void teamSwap() {
//        createAndAnimateProgressBars(dataController.updateScoreboardTimeline());
//        loader.setVisibility(View.VISIBLE);
//        apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), selectedStartTime, selectedEndTime, dashboardType);
//        parentActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                setupUiVisuals();
//            }
//        });
    }

    @SuppressLint("ResourceType")
    private View createFullView(ViewGroup container, JSONObject tileTemplate) {
        loader.setVisibility(View.VISIBLE);
        JSONArray tile_rows = null;

        RelativeLayout parentRelativeLayout;
        View parentLayout = inflater.inflate(R.layout.activity_client_tile_parentlayout, container, false);
        SearchView searchLayout = parentLayout.findViewById(R.id.searchClient);
        searchLayout.setId(1);
        if (tileTemplate != null) {
            colorSchemeManager = parentActivity.getColorSchemeManager();
            // Create the parent layout that all the rows will go in
            parentLayout.setBackgroundColor(colorSchemeManager.getAppBackground());
            parentRelativeLayout = parentLayout.findViewById(R.id.tileRelativeLayout);
//            initContextFilterSelector(parentLayout);
//            initTimelineSelector(parentLayout);
//            initDateSelector(parentLayout);
//            initPopupMenu();
//            initializeCalendarHandler();
//            initDashboardTypeSelector(parentLayout);
            //

            try {
                tile_rows = tileTemplate.getJSONArray("tile_rows");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("NUM OF TILE ROWS", String.valueOf(tile_rows.length()));
            for(int i = 0; i < tile_rows.length(); i++) {
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

    @SuppressLint("ResourceType")
    private HorizontalScrollView createRowFromJSON(JSONObject rowObject, ViewGroup container, Boolean isLeaderboardObject) {
        Log.e("ROW OBJECT", String.valueOf(rowObject));
        try {
            JSONArray rowTiles = rowObject.getJSONArray("tiles");
            Double height = rowObject.getDouble("rowheight");
//            Double innerGap = rowObject.getDouble("innerGap");
            Boolean disabled = rowObject.getBoolean("disabled");
//            Boolean square = rowObject.getBoolean("square");
            Integer maxTiles = rowObject.getInt("max_tiles");

            int correctedHeight = height.intValue();
            List<View> rowViews = new ArrayList<>();

            for(int i = 0; i < rowTiles.length(); i++) {
                JSONObject tileObject = rowTiles.getJSONObject(i);
                String type = tileObject.getString("type");

                switch (type) {
                    case "clientList":
                        correctedHeight = height.intValue() + 150;
                        View v = createClientView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "smallHeader":
                        correctedHeight = height.intValue() + 225;
                        v = createSmallHeaderView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    default:
                        Log.e("TYPE", type);
                        break;
                }
            }

            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);

            View view = null;

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
                Double height = rowObject.getDouble("rowheight");

                HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height.intValue());
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
        float returnSize;
        switch(size) {
            case "small":
                returnSize = getResources().getDimension(R.dimen.font_small);
                break;
            case "medium":
            case "mediam":
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        scopeSelectorText = view.findViewById(R.id.contextFilterRight);
        scopeSelectorText.setText(parentActivity.getCurrentScopeFilter().getName());
        scopeSelectorText.setOnClickListener(this);
        scopeSelectorText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        scopeSelectorText.setTextColor(colorSchemeManager.getLighterTextColor());

        marketStatusFilterText = view.findViewById(R.id.contextFilterLeft);
        marketStatusFilterText.setText(parentActivity.getCurrentMarketStatusFilter().getLabel());
        marketStatusFilterText.setOnClickListener(this);
        marketStatusFilterText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        marketStatusFilterText.setTextColor(colorSchemeManager.getLighterTextColor());

        initScopePopupMenu();
        initMarketStatusPopupMenu();
    }

    private void initScopePopupMenu() {
        scopePopup = new PopupMenu(getContext(), scopeSelectorText);

        scopePopup.setOnMenuItemClickListener(item -> {
            ScopeBarModel selectedScope = parentActivity.getScopeBarAgents().get(item.getItemId());
            if(selectedScope.getName().equalsIgnoreCase("-- Groups --") || selectedScope.getName().equalsIgnoreCase("-- Agents --")) {
                // DO NOTHING
                scopePopup.dismiss();
            }
            else {
                scopePopup.dismiss();
                parentActivity.setScopeFilter(selectedScope);
                parentActivity.resetClientTiles();
            }
            return false;
        });
//        List<String> timelineArray = initSpinnerArray();
        int counter = 0;
        for(ScopeBarModel scope : parentActivity.getScopeBarAgents()) {
            SpannableString s = new SpannableString(scope.getName());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterTextColor()), 0, s.length(), 0);

            scopePopup.getMenu().add(1, counter, counter, s);

            counter++;
        }
    }

    private void initMarketStatusPopupMenu() {
        marketStatusPopup = new PopupMenu(getContext(), marketStatusFilterText);

        marketStatusPopup.setOnMenuItemClickListener(item -> {
            MarketStatusModel selectedMarketStatus = parentActivity.getMarketStatuses().get(item.getItemId());

            scopePopup.dismiss();
            parentActivity.setCurrentMarketStatusFilter(selectedMarketStatus);
            parentActivity.resetClientTiles();
            return false;
        });
//        List<String> timelineArray = initSpinnerArray();
        int counter = 0;
        for(MarketStatusModel marketStatusModel : parentActivity.getMarketStatuses()) {
            SpannableString s = new SpannableString(marketStatusModel.getLabel());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterTextColor()), 0, s.length(), 0);
            marketStatusPopup.getMenu().add(1, counter, counter, s);
            counter++;
        }

    }

    private View createClientView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = null;
        boolean isSideView = false;

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

        String tileColor = tileObject.getString("tile_color");
        Boolean rounded = tileObject.getBoolean("rounded");

        String border = "";
        if(tileObject.has("border")) {
            border = tileObject.getString("border");
        }


        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(Color.parseColor(tileColor));
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

        return rowView;
    }

    private View createSmallHeaderView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = null;
        if(tileObject.has("side")) {
            if(tileObject.getBoolean("side") == true) {
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

        Boolean rounded = false;
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

        // TODO: I am setting this to true because IOS is making assumptions
        rounded = true;
        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(Color.parseColor(assignedTileColor));
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
                        navigationManager.stackReplaceFragment(ScoreboardFragment.class);
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

    private void progressOnClick(String clickDestination) {
        switch (clickDestination) {
            case "clients":
                navigationManager.stackReplaceFragment(ClientListFragment.class);
                break;
            case "scoreboard":
                navigationManager.stackReplaceFragment(ScoreboardFragment.class);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dashboardTypeTileLeftLayout:
//                toggleDashboardTypeSelector(true);
                break;
            case R.id.dashboardTypeTileRightLayout:
//                toggleDashboardTypeSelector(false);
                break;
            case R.id.contextFilterRight:
                scopePopup.show();
                break;
            case R.id.contextFilterLeft:
                marketStatusPopup.show();
                break;
            case R.id.scopeSelector:
                parentActivity.getNavigationManager().toggleTeamDrawer();
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
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }
}