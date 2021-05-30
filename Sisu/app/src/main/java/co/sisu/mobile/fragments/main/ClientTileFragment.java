package co.sisu.mobile.fragments.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ClientMessagingEvent;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.fragments.ClientManageFragment;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FilterObject;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.utils.TileCreationHelper;
import co.sisu.mobile.utils.Utils;
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
    private ActionBarManager actionBarManager;
    private Utils utils;
    private TileCreationHelper tileCreationHelper;
    private ProgressBar loader;
    private LayoutInflater inflater;
    private int numOfRows = 1;
    private TextView scopeSelectorText, marketStatusFilterText, saveButtonFilterText;
    private PopupMenu scopePopup, marketStatusPopup, filterPopup;
    private android.support.v7.widget.SearchView clientSearch;
    private ConstraintLayout paginateInfo;
    private JSONObject paginateObject;
    private String count;
    private ScrollView tileScrollView;
    private boolean updatingClients = false;
    private ImageView addButton;
    private List<FilterObject> agentFilters = new ArrayList<>();
    private Boolean filterMenuPrepared = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        assert parentActivity != null;
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        tileCreationHelper = parentActivity.getTileCreationHelper();
        loader = parentActivity.findViewById(R.id.parentLoader);
        addButton = parentActivity.findViewById(R.id.addView);
        actionBarManager = parentActivity.getActionBarManager();
        utils = parentActivity.getUtils();
        actionBarManager.setToFilterBar(parentActivity.getCurrentScopeFilter().getName());
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

    @NonNull
    @SuppressLint("ResourceType")
    private View createFullView(ViewGroup container, JSONObject tileTemplate) {
        loader.setVisibility(View.VISIBLE);
        JSONArray tile_rows = null;

        RelativeLayout parentRelativeLayout;
        View parentLayout = inflater.inflate(R.layout.activity_client_tile_parentlayout, container, false);
        tileScrollView = parentLayout.findViewById(R.id.tileScrollView);

        tileScrollView.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if (!tileScrollView.canScrollVertically(1)) {
                        // bottom of scroll view
                        if(!updatingClients) {
                            updatingClients = true;
                            try {
                                if(paginateObject.getBoolean("has_next")) {
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
                });
        clientSearch = parentLayout.findViewById(R.id.clientTileSearch);
        clientSearch.setId(1);
        if (tileTemplate != null) {
            colorSchemeManager = parentActivity.getColorSchemeManager();
            paginateInfo = parentActivity.findViewById(R.id.paginateInfo);
            paginateInfo.setVisibility(View.VISIBLE);
            paginateInfo.setBackgroundColor(colorSchemeManager.getAppBackground());
            LayerDrawable borderDrawable = tileCreationHelper.getBorders(
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
            //

            try {
                tile_rows = tileTemplate.getJSONArray("tile_rows");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (tile_rows != null) {
                Log.e("NUM OF TILE ROWS", String.valueOf(tile_rows.length()));
                for(int i = 0; i < tile_rows.length(); i++) {
                    try {
                        HorizontalScrollView horizontalScrollView = tileCreationHelper.createRowFromJSON(tile_rows.getJSONObject(i), container, false, 250, inflater, this, this);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        scopeSelectorText = view.findViewById(R.id.contextFilterRight);
        if(parentActivity.getCurrentScopeFilter() != null) {
            scopeSelectorText.setText(parentActivity.getCurrentScopeFilter().getName());
            actionBarManager.setToFilterBar(parentActivity.getCurrentScopeFilter().getName());
        }
        else {
            actionBarManager.setToFilterBar("");
        }
        scopeSelectorText.setOnClickListener(this);
        scopeSelectorText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        scopeSelectorText.setTextColor(colorSchemeManager.getLighterText());

        marketStatusFilterText = view.findViewById(R.id.contextFilterLeft);
        marketStatusFilterText.setText(parentActivity.getCurrentMarketStatusFilter().getLabel());
        marketStatusFilterText.setOnClickListener(this);
        marketStatusFilterText.setBackgroundColor(colorSchemeManager.getButtonBackground());
        marketStatusFilterText.setTextColor(colorSchemeManager.getLighterText());

        clientSearch.setBackgroundColor(colorSchemeManager.getAppBackground());
        android.support.v7.widget.SearchView.SearchAutoComplete search = clientSearch.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        search.setTextColor(colorSchemeManager.getLighterText());
        search.setHighlightColor(colorSchemeManager.getAppBackground());
        search.setHintTextColor(colorSchemeManager.getLighterText());

        clientSearch.setOnQueryTextListener(this);

        TextView paginationText = paginateInfo.findViewById(R.id.paginateText);
        try {
            paginationText.setText(String.format("Showing: 1 to %s of %s entities", count, paginateObject.getString("total")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveButtonFilterText = parentActivity.findViewById(R.id.saveButton);
        if(saveButtonFilterText != null) {
            saveButtonFilterText.setOnClickListener(this);
        }
        initScopePopupMenu(view);
        initMarketStatusPopupMenu(view);
        addButton.bringToFront();
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
                parentActivity.resetClientTiles("", 1);
                parentActivity.setSelectedFilter(null);
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

    private void initMarketStatusPopupMenu(@NonNull View view) {
        marketStatusPopup = new PopupMenu(view.getContext(), marketStatusFilterText);

        marketStatusPopup.setOnMenuItemClickListener(item -> {
            MarketStatusModel selectedMarketStatus = parentActivity.getMarketStatuses().get(item.getItemId());

            scopePopup.dismiss();
            parentActivity.setCurrentMarketStatusFilter(selectedMarketStatus);
            parentActivity.resetClientTiles("", 1);
            parentActivity.setSelectedFilter(null);
            return false;
        });

        int counter = 0;
        for(MarketStatusModel marketStatusModel : parentActivity.getMarketStatuses()) {
            SpannableString s = new SpannableString(marketStatusModel.getLabel());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterText()), 0, s.length(), 0);
            marketStatusPopup.getMenu().add(1, counter, counter, s);
            counter++;
        }
    }

    private void initFilterPopupMenu() {
        filterPopup = new PopupMenu(parentActivity, saveButtonFilterText);

        filterPopup.setOnMenuItemClickListener(item -> {
            parentActivity.setSelectedFilter(agentFilters.get(item.getItemId()));
            parentActivity.resetClientTilesPresetFilter(parentActivity.getSelectedFilter().getFilters(), 1);
            scopePopup.dismiss();
            return false;
        });

        int counter = 0;
        for(FilterObject currentFilter : agentFilters) {
            SpannableString s = new SpannableString(currentFilter.getFilterName());
            s.setSpan(new ForegroundColorSpan(colorSchemeManager.getLighterText()), 0, s.length(), 0);
            filterPopup.getMenu().add(1, counter, counter, s);
            counter++;
        }

        filterMenuPrepared = true;
    }

    public void teamSwap() {
        parentActivity.resetDashboardTiles(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contextFilterRight:
                scopePopup.show();
                break;
            case R.id.contextFilterLeft:
                marketStatusPopup.show();
                break;
            case R.id.saveButton:
                agentFilters = new ArrayList<>();
                filterMenuPrepared = false;
                // TODO: We should probably do this is another spot.
                apiManager.getAgentFilters(this, parentActivity.getAgent().getAgent_id(), dataController.getCurrentSelectedTeamId());
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
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.GET_AGENT_FILTERS) {
            String tileString;
            try {
                tileString = ((Response) returnObject).body().string();
                JSONObject responseJson = new JSONObject(tileString);
                JSONArray filtersArray = responseJson.getJSONArray("filters");
                //
                for(int i = 0; i < filtersArray.length(); i++) {
                    JSONObject filtersObject = (JSONObject) filtersArray.get(i);
                    JSONObject filters = filtersObject.getJSONObject("filters");
                    agentFilters.add(new FilterObject(filtersObject.getString("filter_name"), filters));
                }

                initFilterPopupMenu();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_TILES) {
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
        if(returnType == ApiReturnType.GET_AGENT_FILTERS) {
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
        // TODO: This feels like a util or at least a dataController thing
        Metric contactMetric = dataController.getContactsMetric();
        if(contactMetric != null) {
            contactMetric.setCurrentNum(contactMetric.getCurrentNum() + 1);
            dataController.setRecordUpdated(contactMetric);
            parentActivity.updateRecordedActivities();
            utils.showToast("+1 to your contacts", parentActivity, colorSchemeManager);
        }
    }

}
