package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.ReportListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.Metric;


public class ReportActivity extends AppCompatActivity {

    private ListView mListView;
    DataController dataController = new DataController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        initializeListView();
    }

    private void initializeListView() {

        mListView = (ListView) findViewById(R.id.report_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        final List<Metric> metricList = dataController.getMetrics();

        ReportListAdapter adapter = new ReportListAdapter(this, metricList);
        mListView.setAdapter(adapter);
    }

}
