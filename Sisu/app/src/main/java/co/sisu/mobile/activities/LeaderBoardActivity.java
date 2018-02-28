package co.sisu.mobile.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.LeaderboardListExpandableAdapter;

public class LeaderBoardActivity extends AppCompatActivity {

    LeaderboardListExpandableAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.teamExpandable);

        // preparing list data
        prepareListData();

        listAdapter = new LeaderboardListExpandableAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
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
