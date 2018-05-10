package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ActivityListAdapter;
import co.sisu.mobile.api.AsyncActivitySettings;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncUpdateActivitySettings;
import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.SettingsObject;
import co.sisu.mobile.models.UpdateSettingsObject;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivitySettingsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AsyncServerEventListener {

    private ListView mListView;
    ParentActivity parentActivity;
    List<SelectedActivities> selectedActivities;
    ProgressBar loader;

    public ActivitySettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View toReturn = inflater.inflate(R.layout.activity_activities_settings, container, false);
        return toReturn;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        loader = view.findViewById(R.id.activitySettingsLoader);
        initializeButtons();
//        initializeListView();
        loader.setVisibility(View.VISIBLE);
        new AsyncActivitySettings(this, parentActivity.getAgentInfo().getAgent_id(), parentActivity.getJwtObject()).execute();
    }

    private void setupFieldsWithData() {
        Log.e("SETUP WITH DATA", "YEP");
        HashMap<String, SelectedActivities> activitiesSelected = parentActivity.getActivitiesSelected();
        selectedActivities = new ArrayList<>();

        for ( String key : activitiesSelected.keySet() ) {
            SelectedActivities selectedActivity = activitiesSelected.get(key);
            Log.e(selectedActivity.getName(), selectedActivity.getValue());

            if(selectedActivity.getName() != null) {
                selectedActivities.add(selectedActivity);
            }
        }
    }

    private void initializeButtons() {
        TextView saveButton = parentActivity.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
    }

    private void initializeListView() {
        mListView = getView().findViewById(R.id.activity_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

//        final List<SelectedActivities> activitiesContainerList = parentActivity.getActivitiesObject();

        ActivityListAdapter adapter = new ActivityListAdapter(getContext(), selectedActivities);
        mListView.setAdapter(adapter);

//        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //ActivitiesPageContainer value = (ActivitiesPageContainer) parent.getItemAtPosition(position);

//        switch() {
//            default:
//                break;
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton://notify of success update api
                saveSettings();
                parentActivity.stackReplaceFragment(MoreFragment.class);
                parentActivity.swapToBacktionBar("More", null);
                break;
        }
    }

    private void updateCurrentSettings() {

    }

    private void saveSettings() {
        new AsyncUpdateActivitySettings(this, createUpdateObject(selectedActivities), parentActivity.getJwtObject()).execute();
    }

    private AsyncUpdateSettingsJsonObject createUpdateObject(List<SelectedActivities> selectedActivities) {
        String valueString = "{";
        int counter = 0;
        for(SelectedActivities activity : selectedActivities) {
            valueString += "\"" + activity.getType() + "\"" + ":" + activity.getValue();
            if(counter < selectedActivities.size() - 1) {
                valueString += ",";
            }
            counter++;
        }
        valueString += "}";
        List<UpdateSettingsObject> list = new ArrayList<>();
        list.add(new UpdateSettingsObject("record_activities", valueString, 7));
        AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject = new AsyncUpdateSettingsJsonObject(2, Integer.valueOf(parentActivity.getAgentInfo().getAgent_id()), list);
        return asyncUpdateSettingsJsonObject;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Activity Settings")) {
            AsyncActivitySettingsJsonObject settingsJson = (AsyncActivitySettingsJsonObject) returnObject;
            SettingsObject settings = settingsJson.getParameter();
            parentActivity.setActivitiesSelected(settings);
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    setupFieldsWithData();
                    initializeListView();
                }
            });
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }
}
