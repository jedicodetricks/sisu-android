package co.sisu.mobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.LeaderboardListExpandableAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

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

        expListView = (ExpandableListView) view.findViewById(R.id.teamExpandable);
        prepareListData();
        listAdapter = new LeaderboardListExpandableAdapter(getContext(), listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
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

}
