package co.sisu.mobile.fragments;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ClientListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.ClientObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientsFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, TabHost.OnTabChangeListener, View.OnClickListener {

    private ListView mListView;
    DataController dataController;
    List<ClientObject> metricList;
    TabHost host;
    String searchText = "";

    public ClientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataController = new DataController(getContext());
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_clients, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        metricList = dataController.getClientObject();
        initializeTabView();
        initializePipelineList(metricList);
        initializeSignedList(metricList);
        initializeContractList(metricList);
        initializeClosedList(metricList);
        initializeArchivedList(metricList);
        initSearchBar();
        initActionBar();
    }


    private void initActionBar() {
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        activity.getSupportActionBar().setCustomView(R.layout.action_bar_add_client_layout);
//        View view = activity.getSupportActionBar().getCustomView();
//
//        TextView cancelButton = view.findViewById(R.id.cancelButton);
//        cancelButton.setOnClickListener(this);
    }

    private void initSearchBar() {
        SearchView clientSearch = getView().findViewById(R.id.clientSeachbar);
        clientSearch.setOnQueryTextListener(this);
    }


    private void initializeTabView() {
        int numOfTabs = 5;
        // create the TabHost that will contain the Tabs
        host = getView().findViewById(R.id.tabHost);
        host.setOnTabChangedListener(this);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Pipeline");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Pipeline");
        host.addTab(spec);


        //Tab 2
        spec = host.newTabSpec("Signed");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Signed");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Contract");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Contract");
        host.addTab(spec);

        //Tab 4
        spec = host.newTabSpec("Closed");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Closed");
        host.addTab(spec);

        //Tab 5
        spec = host.newTabSpec("Archived");
        spec.setContent(R.id.tab5);
        spec.setIndicator("Archived");
        host.addTab(spec);

        for(int i = 0; i < numOfTabs; i++) {
            TextView x = host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);

            x.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_smaller));
        }
    }

    private void initializePipelineList(List<ClientObject> metricList) {
        mListView = getView().findViewById(R.id.pipeline_list);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    private void initializeSignedList(List<ClientObject> metricList) {
        mListView = getView().findViewById(R.id.signed_list);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    private void initializeContractList(List<ClientObject> metricList) {
        mListView = getView().findViewById(R.id.contract_list);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    private void initializeClosedList(List<ClientObject> metricList) {
        mListView = getView().findViewById(R.id.closed_list);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    private void initializeArchivedList(List<ClientObject> metricList) {
        mListView = getView().findViewById(R.id.archived_list);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    private void searchClients() {
        Log.d("Selected", String.valueOf(host.getCurrentTab()));
        Log.d("Searching", searchText);
        List<ClientObject> sortedList = new ArrayList<>();
        switch(host.getCurrentTab()) {
            case 0:
//                for (ClientObject co : metricList) {
//                    if(co.getName().contains(searchText)) {
//                        sortedList.add(co);
//                    }
//                    initializePipelineList(sortedList);
//                }
                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
    public void onTabChanged(String tabId) {
        searchClients();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                getActivity().onBackPressed();
                break;
        }
    }
}
