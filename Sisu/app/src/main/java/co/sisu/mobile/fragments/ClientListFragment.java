package co.sisu.mobile.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ClientListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ClientMessagingEvent;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncClientJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.Metric;
import okhttp3.Response;

public class ClientListFragment extends Fragment implements android.support.v7.widget.SearchView.OnQueryTextListener, View.OnClickListener, AsyncServerEventListener, TabLayout.OnTabSelectedListener, ClientMessagingEvent, DragListView.DragListListener, AdapterView.OnItemClickListener {

//    private DragListView mListView;
    private ListView mListView;
    private String searchText = "";
    private android.support.v7.widget.SearchView clientSearch;
    private TextView total;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ColorSchemeManager colorSchemeManager;
    private ProgressBar loader;
    private List<ClientObject> currentList = new ArrayList<>();
    private TabLayout tabLayout;
    private static String selectedTab = "pipeline";
    private TextView addButton, editListButton, priorityLabel;
    private ConstraintLayout contentView;
    private boolean editMode = false;
    private int priorityPosition = 0;
    private int pipelinePosition = 0;
    private RelativeLayout divider;

    public ClientListFragment() {
        // Required empty public constructor
    }

    public static ClientListFragment newInstance(String tab) {
        ClientListFragment c = new ClientListFragment();
        selectedTab = tab;
        return c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_clients, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        loader = view.findViewById(R.id.clientLoader);
        initListView();
        initSearchBar();
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        AgentModel agent = dataController.getAgent();
        initializeTabView();
        apiManager.getClients(this, agent.getAgent_id(), parentActivity.getSelectedTeamMarketId());
        view.clearFocus();
        selectTab(selectedTab);
        loader.setVisibility(View.VISIBLE);
        initAddButton();
        loadColorScheme();

    }

    private void loadColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.feedbackFragmentParentLayout);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

        tabLayout.setTabTextColors(colorSchemeManager.getMenuText(), colorSchemeManager.getMenuSelectedText());
        tabLayout.setSelectedTabIndicatorColor(colorSchemeManager.getSegmentSelected());
        total.setTextColor(colorSchemeManager.getDarkerTextColor());
        divider.setBackgroundColor(colorSchemeManager.getLine());
        priorityLabel.setTextColor(colorSchemeManager.getDarkerTextColor());


        // TODO: 9/25/2018  
        //THIS IS THE SEARCH BAR COLORING AND MAY NEED TO CHANGE WHEN WE CONFIRM DB COLORS FOR THEMES
        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            clientSearch.setBackgroundColor(colorSchemeManager.getProgressBackground());
            android.support.v7.widget.SearchView.SearchAutoComplete search = clientSearch.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            search.setTextColor(colorSchemeManager.getDarkerTextColor());
            search.setHighlightColor(colorSchemeManager.getProgressBackground());
            search.setHintTextColor(colorSchemeManager.getProgressBackground());
        } else {
            clientSearch.setBackgroundColor(colorSchemeManager.getButtonBorder());
            android.support.v7.widget.SearchView.SearchAutoComplete search = clientSearch.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            search.setTextColor(colorSchemeManager.getDarkerTextColor());
            search.setHighlightColor(colorSchemeManager.getButtonBorder());
            search.setHintTextColor(colorSchemeManager.getButtonBorder());
        }

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

    private void initAddButton() {
        priorityLabel = parentActivity.findViewById(R.id.priorityLabel);
        addButton = parentActivity.findViewById(R.id.addClientButton);
        editListButton = parentActivity.findViewById(R.id.editClientListButton);

        if(addButton != null) {
            addButton.setOnClickListener(this);
        }

        if(editListButton != null) {
            editListButton.setVisibility(View.GONE);
            editListButton.setOnClickListener(this);
        }

    }

    private void initSearchBar() {
        clientSearch = getView().findViewById(R.id.searchClient);
        clientSearch.setOnQueryTextListener(this);
        clientSearch.onActionViewExpanded();
    }


    private void initializeTabView() {
        tabLayout = getView().findViewById(R.id.tabHost);
        tabLayout.addOnTabSelectedListener(this);
        for(int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setText(parentActivity.localizeLabel((String)tabLayout.getTabAt(i).getText()));
            if(parentActivity.isRecruiting()) {
                switch(i) {
                    case 0:
                        // Pipeline
                        break;
                    case 1:
                        tabLayout.getTabAt(i).setText("1st Appts");
                        break;
                    case 2:
                        tabLayout.getTabAt(i).setText("Recruits");
                        break;
                    case 3:
                        tabLayout.getTabAt(i).setText("All");
                        break;
                    case 4:
                        // Lost
                        break;
                }
            }
        }
        total = getView().findViewById(R.id.total);
    }


    private void initListView() {
        mListView = getView().findViewById(R.id.clientListView);
//        mListView.setDragListListener(this);
//        mListView.setLayoutManager(new LinearLayoutManager(parentActivity));
//        mListView.getRecyclerView().setVerticalScrollBarEnabled(true);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);
        mListView.setOnItemClickListener(this);
        total = getView().findViewById(R.id.total);
        divider = getView().findViewById(R.id.divider);

    }

    private void fillListViewWithData(List<ClientObject> metricList) {
        ArrayList mItemArray = new ArrayList<>();
        if(getContext() != null) {

            ArrayList priorityArray = new ArrayList<>();
            ArrayList commonArray = new ArrayList<>();

            for(ClientObject clientObject : metricList) {
                if(clientObject.getIs_priority() == 0) {
                    commonArray.add(clientObject);
                }
                else {
                    priorityArray.add(clientObject);
                }
            }

            int counter = 0;
//            mItemArray.add(new Pair<>((long) counter, "Priority"));
//            priorityPosition = counter;
//            counter++;
            for(int i = 0; i < priorityArray.size(); i++) {
                mItemArray.add(priorityArray.get(i));
                counter++;
            }

//            mItemArray.add(new Pair<>((long) counter, "Pipeline"));
//            pipelinePosition = counter;
            counter++;
            for(int i = 0; i < commonArray.size(); i++) {
                mItemArray.add(commonArray.get(i));
                counter++;
            }
            ClientListAdapter adapter = new ClientListAdapter(getContext(), mItemArray, this, colorSchemeManager, parentActivity.isRecruiting());
            mListView.setAdapter(adapter);

//            ClientItemAdapter clientItemAdapter = new ClientItemAdapter(mItemArray, R.layout.list_item, R.id.client_list_thumbnail, false, this);
//
//            mListView.setDragEnabled(false);
//            mListView.setAdapter(clientItemAdapter, true);
//            mListView.setCanDragHorizontally(false);
////            mListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.list_item));
//            mListView.setCustomDragItem(null);

        }

    }

    private void searchClients() {
        List<ClientObject> sortedList = new ArrayList<>();
        for (ClientObject co : currentList) {
            String name = (co.getFirst_name() + " " + co.getLast_name()).toLowerCase();
            if(name.contains(searchText.toLowerCase())) {
                sortedList.add(co);
            }
            fillListViewWithData(sortedList);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchText = newText;
        searchClients();
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                parentActivity.onBackPressed();
                break;
            case R.id.addClientButton:
                navigationManager.stackReplaceFragment(ClientManageFragment.class);
                break;
            case R.id.searchClient:
                break;
//            case R.id.editClientListButton:
//                if(!editMode) {
//                    changeToEditList(currentList);
//                    editMode = true;
//                }
//                else {
//                    fillListViewWithData(currentList);
//                    editMode = false;
//                }
//                break;
        }
    }

    private void changeToEditList(List<ClientObject> metricList) {
//        ArrayList mItemArray = new ArrayList<>();
//        if(getContext() != null) {
//            ArrayList priorityArray = new ArrayList<>();
//            ArrayList commonArray = new ArrayList<>();
//
//            for(ClientObject clientObject : metricList) {
//                if(clientObject.getIs_priority().equals("0")) {
//                    commonArray.add(clientObject);
//                }
//                else {
//                    priorityArray.add(clientObject);
//                }
//            }
//            int counter = 0;
////            mItemArray.add(new Pair<>((long) counter, "Priority"));
////            priorityPosition = counter;
////            counter++;
//            for(int i = 0; i < priorityArray.size(); i++) {
//                mItemArray.add(new Pair<>((long) counter, priorityArray.get(i)));
//                counter++;
//            }
//
//            mItemArray.add(new Pair<>((long) counter, "Pipeline"));
//            pipelinePosition = counter;
//            counter++;
//            for(int i = 0; i < commonArray.size(); i++) {
//                mItemArray.add(new Pair<>((long) counter, commonArray.get(i)));
//                counter++;
//            }
////            ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList, this);
////            mListView.setAdapter(adapter);
//
//            ClientItemAdapter clientItemAdapter = new ClientItemAdapter(mItemArray, R.layout.edit_list_item, R.id.editButton, false, this);
//            mListView.setDragEnabled(true);
//            mListView.setAdapter(clientItemAdapter, true);
//            mListView.setCanDragHorizontally(false);
////            mListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.list_item));
//            mListView.setCustomDragItem(null);
//
////            mListView.setOnItemClickListener(this);
//        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
//        if (asyncReturnType.equals("Add Notes")) {
//
//        }
//        else {
//            dataController.setClientListObject(returnObject);
//
//            parentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loader.setVisibility(View.GONE);
//                    selectTab(selectedTab);
//                    fillListViewWithData(currentList);
//                }
//            });
//        }

    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_CLIENTS) {
            AsyncClientJsonObject clientObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncClientJsonObject.class);
            dataController.setClientListObject(clientObject, parentActivity.isRecruiting());

            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    selectTab(selectedTab);
                    fillListViewWithData(currentList);
                }
            });
        }
        else if(returnType == ApiReturnTypes.CREATE_NOTE) {}
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

    private int calculateTotalCommission() {
        int totalValue = 0;
        for(int i = 0; i < currentList.size(); i++) {
            try {
                if(parentActivity.isRecruiting()) {
                    ClientObject iterClient = currentList.get(i);
                    totalValue += Double.parseDouble(iterClient.getGross_commission_amt() != null ? iterClient.getGross_commission_amt() : "0");
                }
                else {
                    ClientObject iterClient = currentList.get(i);
                    totalValue += Double.parseDouble(iterClient.getCommission_amt() != null ? iterClient.getCommission_amt() : "0");
                }
            } catch (NumberFormatException nfe) {
                totalValue += 0;
            }

        }
        return totalValue;
    }

    private void selectTab(String selectedTab) {
        if(parentActivity.isRecruiting()) {
            total.setText("Total GCI: $");
        }
        else {
            total.setText("Total: $");
        }
        if(selectedTab != null) {
            switch (selectedTab.toLowerCase()) {
                case "pipeline":
                    tabLayout.getTabAt(0).select();
                    currentList = dataController.getPipelineList();
                    break;
                case "signed":
                case "1st Appts":
                    tabLayout.getTabAt(1).select();
                    currentList = dataController.getSignedList();
                    break;
                case "contract":
                case "recruits":
                    tabLayout.getTabAt(2).select();
                    currentList = dataController.getContractList();
                    break;
                case "closed":
                case "all":
                    tabLayout.getTabAt(3).select();
                    currentList = dataController.getClosedList();
                    break;
                case "archived":
                case "lost":
                    tabLayout.getTabAt(4).select();
                    currentList = dataController.getArchivedList();
                    break;
                default:
                    tabLayout.getTabAt(0).select();
                    currentList = dataController.getPipelineList();
                    break;
            }
        }
        else {
            tabLayout.getTabAt(0).select();
            currentList = dataController.getPipelineList();
        }

        fillListViewWithData(currentList);
        total.append("" + calculateTotalCommission());
        clientSearch.clearFocus();
        Collections.sort(currentList);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        total.setText("Total: $");
        if(mListView != null) {
//            mListView.setAdapter(null);
        }
        switch (tab.getPosition()) {
            case 0:
                currentList = dataController.getPipelineList();
                selectedTab = "pipeline";
                break;
            case 1:
                currentList = dataController.getSignedList();
                selectedTab = "signed";
                break;
            case 2:
                currentList = dataController.getContractList();
                selectedTab = "contract";
                break;
            case 3:
                currentList = dataController.getClosedList();
                selectedTab = "closed";
                break;
            case 4:
                currentList = dataController.getArchivedList();
                selectedTab = "archived";
                break;
        }
        fillListViewWithData(currentList);
        searchClients();
        total.append("" + calculateTotalCommission());
        clientSearch.clearFocus();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    private void addOneToContacts() {
        Metric contactMetric = dataController.getContactsMetric();
        contactMetric.setCurrentNum(contactMetric.getCurrentNum() + 1);
        dataController.setRecordUpdated(contactMetric);
        parentActivity.updateRecordedActivities();
        parentActivity.showToast("+1 to your contacts");
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

    @Override
    public void onItemDragStarted(int position) {
//        parentActivity.showToast("Start - position: " + position);
    }

    @Override
    public void onItemDragging(int itemPosition, float x, float y) {

    }

    @Override
    public void onItemDragEnded(int fromPosition, int toPosition) {
        if (fromPosition != toPosition) {
            Log.e("TO POSITION", String.valueOf(toPosition));
            Log.e("PIPELINE", String.valueOf(pipelinePosition));
            if(toPosition < pipelinePosition) {
                parentActivity.showToast("PRIORITY: " + toPosition);
                pipelinePosition++;
            }
            else if(toPosition > pipelinePosition) {
                parentActivity.showToast("PIPELINE: " + toPosition);
                pipelinePosition--;
            }
            else if(toPosition == pipelinePosition) {
                parentActivity.showToast("THIS SHOULD TOGGLE: " + toPosition);
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ClientObject selectedClient = (ClientObject) parent.getItemAtPosition(position);
        parentActivity.setSelectedClient(selectedClient);
        navigationManager.stackReplaceFragment(ClientManageFragment.class);
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            dragView.findViewById(R.id.item_layout).setBackgroundColor(dragView.getResources().getColor(R.color.colorCorporateOrange));
        }
    }
}


