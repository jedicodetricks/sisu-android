package co.sisu.mobile.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.UpdateSettingsObject;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivitySettingsFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private ListView mListView;
    private ParentActivity parentActivity;
    private NavigationManager  navigationManager;
    private ApiManager apiManager;
    private DataController dataController;
    private ColorSchemeManager colorSchemeManager;
    private List<SelectedActivities> selectedActivities;
    private ProgressBar loader;

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
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        loader = view.findViewById(R.id.activitySettingsLoader);
        initializeButtons();
//        initializeListView();
        loader.setVisibility(View.VISIBLE);
        apiManager.sendAsyncActivitySettings(this, dataController.getAgent().getAgent_id());
    }

    private void setupFieldsWithData() {
        HashMap<String, SelectedActivities> activitiesSelected = dataController.getActivitiesSelected();
        selectedActivities = new ArrayList<>();

        for ( String key : activitiesSelected.keySet() ) {
            SelectedActivities selectedActivity = activitiesSelected.get(key);

            if(selectedActivity.getName() != null) {
                selectedActivities.add(selectedActivity);
            }
        }
    }

    private void initializeButtons() {
        TextView saveButton = parentActivity.findViewById(R.id.saveButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(this);
        }
    }

    private void initializeListView() {
        mListView = getView().findViewById(R.id.activity_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);


        ActivityListAdapter adapter = new ActivityListAdapter(getContext(), selectedActivities, colorSchemeManager);
        mListView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton://notify of success update api
                saveSettings();
                break;
        }
    }


    private void saveSettings() {
        apiManager.sendAsyncUpdateActivitySettings(this, dataController.getAgent().getAgent_id(), createUpdateObject(selectedActivities));

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
        AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject = new AsyncUpdateSettingsJsonObject(2, Integer.valueOf(dataController.getAgent().getAgent_id()), list);
        return asyncUpdateSettingsJsonObject;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Activity Settings")) {
            AsyncParameterJsonObject settingsJson = (AsyncParameterJsonObject) returnObject;
            ParameterObject settings = settingsJson.getParameter();
            dataController.setActivitiesSelected(settings);

            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    setupFieldsWithData();
                    initializeListView();
                }
            });
        }
        else if(asyncReturnType.equals("Update Settings")) {
            navigationManager.clearStackReplaceFragment(MoreFragment.class);
            parentActivity.showToast("Activity updates saved");

        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }
}
