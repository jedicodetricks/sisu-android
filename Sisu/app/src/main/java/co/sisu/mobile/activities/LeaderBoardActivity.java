package co.sisu.mobile.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.TeamsListExpandableAdapter;

public class LeaderBoardActivity extends AppCompatActivity {

    TeamsListExpandableAdapter listAdapter;
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

        listAdapter = new TeamsListExpandableAdapter(this, listDataHeader, listDataChild);

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
        List<String> top250 = new ArrayList<String>();
        top250.add("Linus Torvald");
        top250.add("Steve Jobs");
        top250.add("Bill Gates");
        top250.add("Elon Musk");
        top250.add("Richard Branson");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("Linus Torvald");
        nowShowing.add("Steve Jobs");
        nowShowing.add("Bill Gates");
        nowShowing.add("Elon Musk");
        nowShowing.add("Richard Branson");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("Linus Torvald");
        comingSoon.add("Steve Jobs");
        comingSoon.add("Bill Gates");
        comingSoon.add("Elon Musk");
        comingSoon.add("Richard Branson");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }
}
