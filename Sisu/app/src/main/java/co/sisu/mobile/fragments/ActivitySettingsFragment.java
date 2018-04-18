package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ActivityListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.Metric;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivitySettingsFragment extends Fragment implements AdapterView.OnItemClickListener{

    private ListView mListView;
    DataController dataController;
    ParentActivity activity;
    public ActivitySettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataController = new DataController();
        View toReturn = inflater.inflate(R.layout.activity_activities_settings, container, false);
        return toReturn;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity = (ParentActivity) getActivity();
        initializeListView();
        inititializeButtons();
    }

    private void inititializeButtons() {

    }

    private void initializeListView() {

        mListView = getView().findViewById(R.id.activity_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        final List<Metric> activitiesContainerList = dataController.getActivitiesObject();

        ActivityListAdapter adapter = new ActivityListAdapter(getContext(), activitiesContainerList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //ActivitiesPageContainer value = (ActivitiesPageContainer) parent.getItemAtPosition(position);

//        switch() {
//            default:
//                break;
//        }
    }
}
