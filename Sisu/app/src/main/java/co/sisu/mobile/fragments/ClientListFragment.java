package co.sisu.mobile.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ClientListAdapter;
import co.sisu.mobile.api.AsyncClients;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ClientMessagingEvent;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.ClientObject;

public class ClientListFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, View.OnClickListener, AsyncServerEventListener, TabLayout.OnTabSelectedListener, ClientMessagingEvent {

    private ListView mListView;
    String searchText = "";
    SearchView clientSearch;
    ParentActivity parentActivity;
    ProgressBar loader;
    List<ClientObject> currentList = new ArrayList<>();
    TabLayout tabLayout;
    static String selectedTab = "";

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
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_clients, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        loader = view.findViewById(R.id.clientLoader);
        initSearchBar();
        parentActivity = (ParentActivity) getActivity();
        AgentModel agent = parentActivity.getAgentInfo();
        initializeTabView();
        new AsyncClients(this, agent.getAgent_id()).execute();
        view.clearFocus();
        selectTab();
        loader.setVisibility(View.VISIBLE);
    }

    private void initSearchBar() {
        clientSearch = getView().findViewById(R.id.searchClient);
        clientSearch.setOnQueryTextListener(this);
        clientSearch.onActionViewExpanded();
    }


    private void initializeTabView() {
        tabLayout = getView().findViewById(R.id.tabHost);
        tabLayout.addOnTabSelectedListener(this);
    }


    private void initListView(List<ClientObject> metricList) {
        mListView = getView().findViewById(R.id.clientListView);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList, this);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    private void searchClients() {
        List<ClientObject> sortedList = new ArrayList<>();
        for (ClientObject co : currentList) {
            String name = (co.getFirst_name() + " " + co.getLast_name()).toLowerCase();
            if(name.contains(searchText.toLowerCase())) {
                sortedList.add(co);
            }
            initListView(sortedList);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ClientObject selectedClient = (ClientObject) parent.getItemAtPosition(position);
        parentActivity.setSelectedClient(selectedClient);
        parentActivity.stackReplaceFragment(ClientEditFragment.class);
        parentActivity.swapToBacktionBar(null);
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
                getActivity().onBackPressed();
                break;
            case R.id.addButton:
                //navigate to addClient
                break;
            case R.id.searchClient:
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        parentActivity.setClientsObject(returnObject);

        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
                currentList = parentActivity.getPipelineList();
                initListView(currentList);
            }
        });
    }

    @Override
    public void onEventFailed(Object o, String s) {

    }

    private void selectTab() {
        switch (selectedTab.toLowerCase()) {
            case "pipeline":
                tabLayout.getTabAt(0).select();
                break;
            case "signed":
                tabLayout.getTabAt(1).select();
                break;
            case "contract":
                tabLayout.getTabAt(2).select();
                break;
            case "closed":
                tabLayout.getTabAt(3).select();
                break;
            case "archived":
                tabLayout.getTabAt(4).select();
                break;
            default:
                tabLayout.getTabAt(0).select();
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mListView.setAdapter(null);
        switch ((String) tab.getText()) {
            case "Pipeline":
                currentList = parentActivity.getPipelineList();
                break;
            case "Signed":
                currentList = parentActivity.getSignedList();
                break;
            case "Under Contract":
                currentList = parentActivity.getContractList();
                break;
            case "Closed":
                currentList = parentActivity.getClosedList();
                break;
            case "Archived":
                currentList = parentActivity.getArchivedList();
                break;
        }
        initListView(currentList);
        searchClients();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}

    @Override
    public void onPhoneClicked(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    @Override
    public void onTextClicked(String number) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + number));
        startActivity(intent);
    }

    @Override
    public void onEmailClicked(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        startActivity(intent);
    }
}
