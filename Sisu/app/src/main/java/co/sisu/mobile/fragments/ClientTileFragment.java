package co.sisu.mobile.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ClientMessagingEvent;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FilterObject;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.ScopeBarModel;
import okhttp3.Response;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class ClientTileFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener, PopupMenu.OnMenuItemClickListener, SearchView.OnQueryTextListener, ClientMessagingEvent {
    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private ProgressBar loader;
    private LayoutInflater inflater;

    private int numOfRows = 1;
    private TextView scopeSelectorText, marketStatusFilterText, saveButtonFilterText;
    private PopupMenu scopePopup, marketStatusPopup, filterPopup;
    private int selectedYear = 0;
    private int selectedMonth = 0;
    private int selectedDay = 0;
    private boolean beginDateSelected = false;
    private boolean endDateSelected = false;
    private String dashboardType = "agent";
    private android.support.v7.widget.SearchView clientSearch;
    private ConstraintLayout paginateInfo;
    private JSONObject paginateObject;
    private String count;
    private ScrollView tileScrollView;
    private boolean updatingClients = false;
    private ImageView addButton;
    private List<FilterObject> agentFilters = new ArrayList();
    private Boolean filterMenuPrepared = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        addButton = parentActivity.findViewById(R.id.addView);
        this.inflater = inflater;
        JSONObject tileTemplate = parentActivity.getClientTiles();

        try {
            if(tileTemplate.has("pagination")) {
                paginateObject = tileTemplate.getJSONObject("pagination");
            }

            if(tileTemplate.has("count")) {
                count = tileTemplate.getString("count");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return createFullView(container, tileTemplate);
    }

    public void teamSwap() {
        parentActivity.resetDashboardTiles(false);
    }

    @SuppressLint("ResourceType")
    private View createFullView(ViewGroup container, JSONObject tileTemplate) {
        loader.setVisibility(View.VISIBLE);
        JSONArray tile_rows = null;

        RelativeLayout parentRelativeLayout;
        View parentLayout = inflater.inflate(R.layout.activity_client_tile_parentlayout, container, false);
        tileScrollView = parentLayout.findViewById(R.id.tileScrollView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            tileScrollView.getViewTreeObserver()
                    .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            if (!tileScrollView.canScrollVertically(1)) {
                                // bottom of scroll view
                                if(!updatingClients) {
                                    updatingClients = true;
                                    try {
                                        if(paginateObject.getBoolean("has_next") == true) {
                                            //GO GET THE NEXT SET OF CLIENTS
                                            int currentPage = paginateObject.getInt("page");
                                            if(parentActivity.getSelectedFilter() == null) {
                                                parentActivity.resetClientTiles("", currentPage + 1);
                                            }
                                            else {
                                                parentActivity.resetClientTilesPresetFilter(parentActivity.getSelectedFilter().getFilters(), currentPage + 1);
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }
                    });
        }
        clientSearch = parentLayout.findViewById(R.id.clientTileSearch);
        clientSearch.setId(1);
        if (tileTemplate != null) {
            colorSchemeManager = parentActivity.getColorSchemeManager();
            paginateInfo = parentActivity.findViewById(R.id.paginateInfo);
            paginateInfo.setVisibility(View.VISIBLE);
            paginateInfo.setBackgroundColor(colorSchemeManager.getAppBackground());
            LayerDrawable borderDrawable = getBorders(
                    colorSchemeManager.getAppBackground(), // Background color
                    Color.GRAY, // Border color
                    0, // Left border in pixels
                    5, // Top border in pixels
                    0, // Right border in pixels
                    0 // Bottom border in pixels
            );
            paginateInfo.setBackground(borderDrawable);

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
//        Log.e("ROW OBJECT", String.valueOf(rowObject));
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
            case "mediam": //I don't know if Rick is still sending this typo down
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
        if(parentActivity.getCurrentScopeFilter() != null) {
            scopeSelectorText.setText(parentActivity.getCurrentScopeFilter().getName());
        }
        scopeSelectorText.setOnClickListener(this);
        scopeSelectorText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        scopeSelectorText.setTextColor(colorSchemeManager.getLighterTextColor());

        marketStatusFilterText = view.findViewById(R.id.contextFilterLeft);
        marketStatusFilterText.setText(parentActivity.getCurrentMarketStatusFilter().getLabel());
        marketStatusFilterText.setOnClickListener(this);
        marketStatusFilterText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        marketStatusFilterText.setTextColor(colorSchemeManager.getLighterTextColor());

        clientSearch.setBackgroundColor(colorSchemeManager.getAppBackground());
        android.support.v7.widget.SearchView.SearchAutoComplete search = clientSearch.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        search.setTextColor(colorSchemeManager.getLighterTextColor());
        search.setHighlightColor(colorSchemeManager.getAppBackground());
        search.setHintTextColor(colorSchemeManager.getLighterTextColor());

        clientSearch.setOnQueryTextListener(this);

        TextView paginationText = paginateInfo.findViewById(R.id.paginateText);
        try {
            paginationText.setText("Showing: 1 to " + count + " of " + paginateObject.getString("total") + " entities");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveButtonFilterText = parentActivity.findViewById(R.id.saveButton);
        if(saveButtonFilterText != null) {
            saveButtonFilterText.setOnClickListener(this);
        }
        initScopePopupMenu();
        initMarketStatusPopupMenu();
        addButton.bringToFront();
    }

    private void initScopePopupMenu() {
        scopePopup = new PopupMenu(getContext(), scopeSelectorText);

        scopePopup.setOnMenuItemClickListener(item -> {
            ScopeBarModel selectedScope = parentActivity.getScopeBarList().get(item.getItemId());
            if(selectedScope.getName().equalsIgnoreCase("-- Groups --") || selectedScope.getName().equalsIgnoreCase("-- Agents --")) {
                // DO NOTHING
                scopePopup.dismiss();
            }
            else {
                scopePopup.dismiss();
                parentActivity.setScopeFilter(selectedScope);
                parentActivity.resetClientTiles("", 1);
                parentActivity.setSelectedFilter(null);
            }
            return false;
        });
//        List<String> timelineArray = initSpinnerArray();
        int counter = 0;
        for(ScopeBarModel scope : parentActivity.getScopeBarList()) {
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
            parentActivity.resetClientTiles("", 1);
            parentActivity.setSelectedFilter(null);
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

    private void initFilterPopupMenu() {
        filterPopup = new PopupMenu(getContext(), saveButtonFilterText);

        filterPopup.setOnMenuItemClickListener(item -> {
            parentActivity.setSelectedFilter(agentFilters.get(item.getItemId()));
            parentActivity.resetClientTilesPresetFilter(parentActivity.getSelectedFilter().getFilters(), 1);
            scopePopup.dismiss();
            return false;
        });

        int counter = 0;
        for(FilterObject currentFilter : agentFilters) {
            SpannableString s = new SpannableString(currentFilter.getFilterName());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterTextColor()), 0, s.length(), 0);
            filterPopup.getMenu().add(1, counter, counter, s);
            counter++;
        }

        filterMenuPrepared = true;
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
        ImageView phoneImage = rowView.findViewById(R.id.centerButton);
        ImageView emailImage = rowView.findViewById(R.id.rightButton);
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
            textImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTextClicked(clientObject.getMobile_phone(), clientObject);
                }
            });
            phoneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPhoneClicked(clientObject.getMobile_phone() != null ? clientObject.getMobile_phone() : clientObject.getHome_phone(), clientObject);
                }
            });

        }

        if(clientObject.getEmail() == null || clientObject.getEmail().equals("")) {
            emailImage.setVisibility(View.INVISIBLE);
        } else {
            emailImage.setVisibility(View.VISIBLE);
            emailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEmailClicked(clientObject.getEmail(), clientObject);
                }
            });

        }

//        drawable = parentActivity.getResources().getDrawable(R.drawable.more_icon_active).mutate();
//        drawable.setColorFilter(colorSchemeManager.getMenuIcon(), PorterDuff.Mode.SRC_ATOP);
//        moreButton.setImageDrawable(drawable);

        ImageView thumbnail = rowView.findViewById(R.id.client_list_thumbnail);
        if(clientObject.getIs_locked() == 1) {
            if(clientObject.getType_id().equalsIgnoreCase("b")) {
                Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.lock_icon).mutate();
                drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
                thumbnail.setImageDrawable(drawable);
            } else {
                Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.lock_icon).mutate();
                drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange), PorterDuff.Mode.SRC_ATOP);
                thumbnail.setImageDrawable(drawable);
            }
        }
        else {
            if(clientObject.getType_id().equalsIgnoreCase("b")) {
                Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.seller_icon_active).mutate();
                drawable.setColorFilter(ContextCompat.getColor(parentActivity, R.color.colorYellow), PorterDuff.Mode.SRC_ATOP);
                thumbnail.setImageDrawable(drawable);
            } else {
                thumbnail.setImageResource(R.drawable.seller_icon_active);
            }
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
                        paginateInfo.setVisibility(View.GONE);
                        navigationManager.stackReplaceFragment(ClientListFragment.class);
                        break;
                    case "scoreboard":
                        paginateInfo.setVisibility(View.GONE);
                        navigationManager.stackReplaceFragment(ScoreboardFragment.class);
                        break;
                    case "record":
                        paginateInfo.setVisibility(View.GONE);
                        navigationManager.stackReplaceFragment(RecordFragment.class);
                        break;
                    case "report":
                        paginateInfo.setVisibility(View.GONE);
                        navigationManager.stackReplaceFragment(ReportFragment.class);
                        break;
                    case "leaderboard":
                        paginateInfo.setVisibility(View.GONE);
                        navigationManager.stackReplaceFragment(LeaderboardFragment.class);
                        break;
                    case "more":
                        paginateInfo.setVisibility(View.GONE);
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
//                parentActivity.getNavigationManager().toggleTeamDrawer();
//                loader.setVisibility(View.VISIBLE);
//                apiManager.getTileSetup(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), selectedStartTime, selectedEndTime, dashboardType);
                break;
            case R.id.saveButton:
                agentFilters = new ArrayList<>();
                filterMenuPrepared = false;
                apiManager.getAgentFilters(this, parentActivity.getAgent().getAgent_id(), parentActivity.getSelectedTeamId());
                while(!filterMenuPrepared) {
                    // Just wait here for the async to finish
                }
                filterPopup.show();
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
        if(returnType == ApiReturnTypes.GET_AGENT_FILTERS) {
            String tileString = null;
            try {
                tileString = ((Response) returnObject).body().string();
                JSONObject responseJson = new JSONObject(tileString);
                JSONArray filtersArray = responseJson.getJSONArray("filters");
                //
                for(int i = 0; i < filtersArray.length(); i++) {
                    JSONObject filtersObject = (JSONObject) filtersArray.get(i);
                    JSONObject filters = filtersObject.getJSONObject("filters");
                    String filtersString = filters.toString();
                    agentFilters.add(new FilterObject(filtersObject.getString("filter_name"), filters));
                }
                //

                initFilterPopupMenu();
//                JSONObject filtersObject = (JSONObject) filtersArray.get(0);
//
//                JSONObject filters = filtersObject.getJSONObject("filters");
//                Iterator<String> keys = filters.keys();
//
//                while(keys.hasNext()) {
//                    String key = keys.next();
//                    // do something with jsonObject here
//                    JSONObject currentFilter = filters.getJSONObject(key);
//                    String garbo = "";
//                }
                String garbo = "";

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_AGENT_FILTERS) {
            Log.e("Error!", "error");
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        parentActivity.resetClientTiles(query, 1);
        parentActivity.setSelectedFilter(null);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onPhoneClicked(String number, ClientObject client) {
        addOneToContacts();
        apiManager.addNote(this, dataController.getAgent().getAgent_id(), client.getClient_id(), number, "PHONE");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    @Override
    public void onTextClicked(String number, ClientObject client) {
        addOneToContacts();
        apiManager.addNote(this, dataController.getAgent().getAgent_id(), client.getClient_id(), number, "TEXTM");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + number));
        startActivity(intent);
    }

    @Override
    public void onEmailClicked(String email, ClientObject client) {
        addOneToContacts();
        apiManager.addNote(this, dataController.getAgent().getAgent_id(), client.getClient_id(), email, "EMAIL");
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        startActivity(intent);
    }

    @Override
    public void onItemClicked(ClientObject selectedClient) {
        parentActivity.setSelectedClient(selectedClient);
        navigationManager.stackReplaceFragment(ClientManageFragment.class);
    }

    private void addOneToContacts() {
        Metric contactMetric = dataController.getContactsMetric();
        if(contactMetric != null) {
            contactMetric.setCurrentNum(contactMetric.getCurrentNum() + 1);
            dataController.setRecordUpdated(contactMetric);
            parentActivity.updateRecordedActivities();
            parentActivity.showToast("+1 to your contacts");
        }
    }

}
