package co.sisu.mobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.LeaderboardListExpandableAdapter;
import co.sisu.mobile.api.AsyncLeaderboardStats;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.models.AsyncLeaderboardJsonObject;
import co.sisu.mobile.models.LeaderboardItemsObject;
import co.sisu.mobile.models.LeaderboardObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment implements AsyncServerEventListener {

    LeaderboardListExpandableAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_leaderboard, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        expListView = view.findViewById(R.id.teamExpandable);
        expListView.setGroupIndicator(null);
        prepareListData();
        listAdapter = new LeaderboardListExpandableAdapter(getContext(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        initializeTimelineSelector();
        new AsyncLeaderboardStats(this, "429", "2018", "04").execute();
    }

    private void initializeTimelineSelector() {
        Spinner spinner = getView().findViewById(R.id.reportsTimelineSelector);
        List<String> spinnerArray = initSpinnerArray();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.spinner_item,
                spinnerArray
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                initializeListView(dataController.updateRecordMetrics());
                //will need to refresh page with fresh data based on api call here determined by timeline value selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
//        spinnerArray.add("Today");
//        spinnerArray.add("Last Week");
//        spinnerArray.add("This Week");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");

        String thisMonth = sdf.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String lastMonth = sdf.format(calendar.getTime());
        spinnerArray.add(lastMonth);
        spinnerArray.add(thisMonth);

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("YYYY");
        String thisYear = sdf.format(calendar.getTime());

        calendar.add(Calendar.YEAR, -1);
        String lastYear = sdf.format(calendar.getTime());
        spinnerArray.add(lastYear);
        spinnerArray.add(thisYear);
//        spinnerArray.add("All Records");

        return spinnerArray;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Overall Leaderboard");
        listDataHeader.add("Under Contract");
        listDataHeader.add("Closed");

        // Adding child data

        List<String> overallLeaderboard = new ArrayList<String>();
        overallLeaderboard.add("Linus Torvald");
        overallLeaderboard.add("Steve Jobs");
        overallLeaderboard.add("Bill Gates");
        overallLeaderboard.add("Elon Musk");
        overallLeaderboard.add("Richard Branson");

        List<String> underContract = new ArrayList<String>();
        underContract.add("Linus Torvald");
        underContract.add("Steve Jobs");
        underContract.add("Bill Gates");
        underContract.add("Elon Musk");
        underContract.add("Richard Branson");

        List<String> closed = new ArrayList<String>();
        closed.add("Linus Torvald");
        closed.add("Steve Jobs");
        closed.add("Bill Gates");
        closed.add("Elon Musk");
        closed.add("Richard Branson");

        listDataChild.put(listDataHeader.get(0), overallLeaderboard); // Header, Child data
        listDataChild.put(listDataHeader.get(1), underContract);
        listDataChild.put(listDataHeader.get(2), closed);
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        AsyncLeaderboardJsonObject leaderboardJsonObject = (AsyncLeaderboardJsonObject) returnObject;
        LeaderboardObject[] leaderBoardSections = leaderboardJsonObject.getLeaderboardObject();
        Log.e("Leaderboard Section", leaderBoardSections[0].getActivity_type());

        for(int i = 0; i < leaderBoardSections.length; i++) {
            LeaderboardItemsObject[] leaderboardItem = leaderBoardSections[i].getLeaderboardItemsObject();

            Log.e("Leaderboard Item", leaderboardItem[i].getLabel());
        }
    }

    @Override
    public void onEventFailed() {

    }
}
