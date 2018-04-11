package co.sisu.mobile.fragments;


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
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.List;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ClientListAdapter;
import co.sisu.mobile.api.AsyncClients;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncClientJsonObject;
import co.sisu.mobile.models.ClientObject;

public class ClientsFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, View.OnClickListener, AsyncServerEventListener, TabLayout.OnTabSelectedListener {

    private ListView mListView;
//    DataController dataController;
    List<ClientObject> pipelineList, signedList, contractList, closedList, archivedList;
    String searchText = "";
    SearchView clientSearch;
    ParentActivity parentActivity;
    ProgressBar loader;

    public ClientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        dataController = new DataController(getContext());
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_clients, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pipelineList = new ArrayList<>();
        signedList = new ArrayList<>();
        contractList = new ArrayList<>();
        closedList = new ArrayList<>();
        archivedList = new ArrayList<>();
        loader = view.findViewById(R.id.clientLoader);
        initSearchBar();
        parentActivity = (ParentActivity) getActivity();
        AgentModel agent = parentActivity.getAgentInfo();
        Log.e("AGENT", agent.getAgent_id());
        initializeTabView();
        new AsyncClients(this, agent.getAgent_id()).execute();
        view.clearFocus();
        loader.setVisibility(View.VISIBLE);
    }

    private void initSearchBar() {
        clientSearch = getView().findViewById(R.id.searchClient);
        clientSearch.setOnQueryTextListener(this);
        clientSearch.onActionViewExpanded();
    }


    private void initializeTabView() {
        TabLayout tabLayout = getView().findViewById(R.id.tabHost);
        tabLayout.addOnTabSelectedListener(this);
    }


    private void initListView(List<ClientObject> metricList) {
        mListView = getView().findViewById(R.id.clientListView);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }


    private void searchClients() {
//        Log.d("Selected", String.valueOf(host.getCurrentTab()));
//        Log.d("Searching", searchText);
//        List<ClientObject> sortedList = new ArrayList<>();
//        switch(host.getCurrentTab()) {
//            case 0:
////                for (ClientObject co : metricList) {
////                    if(co.getName().contains(searchText)) {
////                        sortedList.add(co);
////                    }
////                    initializePipelineList(sortedList);
////                }
//                break;
//            case 1:
//
//                break;
//            case 2:
//
//                break;
//            case 3:
//
//                break;
//            case 4:
//
//                break;
//        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                getActivity().onBackPressed();
                break;
            case R.id.searchClient:
                break;
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        AsyncClientJsonObject clientParentObject = (AsyncClientJsonObject) returnObject;
        ClientObject[] clientObject = clientParentObject.getClients();

        for(int i = 0; i < clientObject.length; i++) {
            ClientObject co = clientObject[i];
//            Log.e("CLIENT " + i, "First name: " + clientObject[i].getFirst_name());
            if(co.getStatus().equalsIgnoreCase("D")) {
                //Archived List
                archivedList.add(co);
            }
            else if(co.getClosed_dt() != null) {
                //Closed List
                closedList.add(co);
            }
            else if(co.getPaid_dt() != null) {
                //Contract List
                contractList.add(co);
            }
            else if(co.getSigned_dt() != null) {
                //Signed List
                signedList.add(co);
            }
            else {
                //Pipeline List
                pipelineList.add(co);
            }
        }
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loader.setVisibility(View.GONE);
                initListView(pipelineList);
            }
        });
    }

    @Override
    public void onEventFailed() {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch ((String) tab.getText()) {
            case "Pipeline":
                initListView(pipelineList);
                break;
            case "Signed":
                initListView(signedList);
                break;
            case "Contract":
                initListView(contractList);
                break;
            case "Closed":
                initListView(closedList);
                break;
            case "Archived":
                initListView(archivedList);
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}
}


//if underlineSegment.selectedSegmentIndex == PipelineType.pipeline.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.signed_dt == nil) }
//        clientArray  = clientArray.filter { ($0.uc_dt == nil) }
//        clientArray  = clientArray.filter { ($0.closed_dt == nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.signed.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.signed_dt != nil) }
//        clientArray  = clientArray.filter { ($0.uc_dt == nil) }
//        clientArray  = clientArray.filter { ($0.closed_dt == nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.contract.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.uc_dt != nil) }
//        clientArray  = clientArray.filter { ($0.closed_dt == nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.closed.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.closed_dt != nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.archive.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "D") }
//        }
