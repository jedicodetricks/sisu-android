package co.sisu.mobile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import co.sisu.mobile.adapters.ReportListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.Metric;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {

    private ListView mListView;
    DataController dataController = new DataController();

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeListView();
    }

    private void initializeListView() {

        mListView = (ListView) getView().findViewById(R.id.report_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        final List<Metric> metricList = dataController.getMetrics();

        ReportListAdapter adapter = new ReportListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);
    }
}
