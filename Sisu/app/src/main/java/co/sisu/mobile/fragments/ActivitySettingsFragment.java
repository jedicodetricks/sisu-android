package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.woxthebox.draglistview.DragListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import co.sisu.mobile.ApiReturnTypes;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ActivityListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AsyncActivitySettingsJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.UpdateActivitiesModel;
import co.sisu.mobile.models.UpdateSettingsObject;
import okhttp3.Response;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivitySettingsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AsyncServerEventListener, DragListView.DragListListener {

    private DragListView mListView;
    private ParentActivity parentActivity;
    private NavigationManager  navigationManager;
    private ApiManager apiManager;
    private DataController dataController;
    private ColorSchemeManager colorSchemeManager;
    private AsyncActivitySettingsObject[] selectedActivities;
    private AsyncActivitySettingsObject[] currentActivitiesSorting;
    private ProgressBar loader;
    private ArrayList mItemArray = new ArrayList<>();
    private boolean editMode = false;
    private TextView saveButton, editButton, title;
    private boolean activitySaveComplete = false;
    private boolean settingsSaveComplete = false;

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
        loader = parentActivity.findViewById(R.id.parentLoader);
        initializeButtons();
        initializeListView();
        initializeFields();
        loader.setVisibility(View.VISIBLE);
        apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId());
        setColorScheme();
    }

    private void initializeFields() {
        title = getView().findViewById(R.id.activity_settings_title);
    }

    private void setColorScheme() {
        title.setTextColor(colorSchemeManager.getDarkerTextColor());
    }

    private void setupFieldsWithData() {
        selectedActivities = dataController.getActivitiesSelected();
//        selectedActivities = new ArrayList<>();

//        for ( String key : activitiesSelected.keySet() ) {
//            SelectedActivities selectedActivity = activitiesSelected.get(key);
//
//            if(selectedActivity.getName() != null) {
//                selectedActivities.add(selectedActivity);
//            }
//        }
    }

    private void initializeButtons() {
        saveButton = parentActivity.findViewById(R.id.saveButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(this);
        }
        editButton = parentActivity.findViewById(R.id.editButton);
        if(editButton != null) {
            editButton.setOnClickListener(this);
        }
    }

    private void initializeListView() {
        mListView = getView().findViewById(R.id.activity_list_view);
        mListView.setDragListListener(this);
        mListView.setLayoutManager(new LinearLayoutManager(parentActivity));
        mListView.getRecyclerView().setVerticalScrollBarEnabled(true);
    }



    private void fillListViewWithData(AsyncActivitySettingsObject[] selectedActivities) {
        int counter = 0;
        mItemArray = new ArrayList<>();
        if(getContext() != null) {
            for (AsyncActivitySettingsObject setting : selectedActivities) {
                SelectedActivities value = new SelectedActivities(setting.getValue(), setting.getActivity_type(), setting.getName());
                mItemArray.add(new Pair<>((long) counter, value));
                counter++;
            }
        }

        ActivityListAdapter activityListAdapter;

        if(!editMode) {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_activity_list, R.id.activity_list_title, false, colorSchemeManager, false);
            mListView.setDragEnabled(false);
        }
        else {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_edit_activity_list, R.id.editButton, false, colorSchemeManager, true);
            mListView.setDragEnabled(true);
        }
        mListView.setAdapter(activityListAdapter, false);
        mListView.setCanDragHorizontally(false);
        mListView.setCustomDragItem(null);

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
                if(editMode) {
                    //This would save the editing of priority
                    dataController.sortSelectedActivities(currentActivitiesSorting);
                    saveSorting();
                }
                else {
                    saveSettings();
                }
                break;
            case R.id.editButton:
                editMode = !editMode;
                if(editMode) {
                    saveButton.setText("Done");
                    editButton.setVisibility(View.INVISIBLE);
                }
                else {
                    saveButton.setText("Save");
                }
                fillListViewWithData(dataController.getActivitiesSelected());
                break;
        }
    }


    private void saveSorting() {
        List<Metric> allActivities = dataController.getMasterActivitiesObject();
        int weightCounter = 0;

        for(AsyncActivitySettingsObject s : currentActivitiesSorting) {
            for(Metric m : allActivities) {
//                if(m.getWeight() < 80) {
                if(m.getType().equalsIgnoreCase(s.getActivity_type())) {
                    m.setWeight(currentActivitiesSorting.length - weightCounter);
                    weightCounter++;
                    break;
                }
//                }
            }
        }
        updateRecordedActivities(allActivities);
    }

    public void updateRecordedActivities(List<Metric> updatedRecords) {
//        List<Metric> updatedRecords = dataController.getUpdatedRecords();
        List<UpdateActivitiesModel> updateActivitiesModels = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        AsyncUpdateActivitiesJsonObject activitiesJsonObject = new AsyncUpdateActivitiesJsonObject();
        for(Metric m : updatedRecords) {
            switch(m.getType()) {
                case "BSGND":
                case "SSGND":
                case "BUNDC":
                case "SUNDC":
                case "BCLSD":
                case "SCLSD":
                case "BAPPT":
                case "SAPPT":
                case "CONTA":
                    break;
                default:
                    updateActivitiesModels.add(new UpdateActivitiesModel(formatter.format(d), m.getType(), m.getCurrentNum(), Integer.valueOf(dataController.getAgent().getAgent_id()), m.getWeight()));
                    break;
            }
        }
        UpdateActivitiesModel[] array = new UpdateActivitiesModel[updateActivitiesModels.size()];
        updateActivitiesModels.toArray(array);

        activitiesJsonObject.setActivities(array);
        dataController.setActivitiesSelected(currentActivitiesSorting);
        apiManager.sendAsyncUpdateActivitySettings(this, dataController.getAgent().getAgent_id(), createUpdateObject(currentActivitiesSorting), parentActivity.getSelectedTeamMarketId());
    }

    private void saveSettings() {
        List<AsyncActivitySettingsObject> updatedSettings = new ArrayList<>();
        AsyncActivitySettingsObject[] currentActivitySettings = dataController.getActivitiesSelected();
        for(int i = 0; i < mItemArray.size(); i++) {
            Pair<Long, SelectedActivities> currentPair = (Pair<Long, SelectedActivities>) mItemArray.get(i);
            SelectedActivities selectedActivity = currentPair.second;
            for(AsyncActivitySettingsObject setting : currentActivitySettings) {
                if(selectedActivity.getType().equalsIgnoreCase(setting.getActivity_type())) {
                    setting.setValue(selectedActivity.getValue());
                }
            }
        }
        dataController.setActivitiesSelected(currentActivitySettings);
        apiManager.sendAsyncUpdateActivitySettings(this, dataController.getAgent().getAgent_id(), createUpdateObject(dataController.getActivitiesSelected()), parentActivity.getSelectedTeamMarketId());
    }

    private String createUpdateObject(AsyncActivitySettingsObject[] selectedActivities) {
        String valueString = "{\"record_activities\":[";
        int counter = 0;
        for(AsyncActivitySettingsObject setting: selectedActivities) {
            valueString += "{\"activity_type\":\"" + setting.getActivity_type() + "\", \"value\":" + setting.getValue();
            if(counter < selectedActivities.length - 1) {
                valueString += "},";
            }
            else {
                valueString += "}";
            }
            counter++;
        }
        valueString += "]}";
//        List<UpdateSettingsObject> list = new ArrayList<>();
//        list.add(new UpdateSettingsObject("record_activities", valueString));
//        AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject = new AsyncUpdateSettingsJsonObject(Integer.valueOf(dataController.getAgent().getAgent_id()), list);
        return valueString;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
//        if(asyncReturnType.equals("Activity Settings")) {
//            AsyncParameterJsonObject settings = gson.fromJson(responseBody, AsyncParameterJsonObject.class);
//            AsyncParameterJsonObject settingsJson = (AsyncParameterJsonObject) returnObject;
//            ParameterObject settings = settingsJson.getParameter();
//            dataController.setActivitiesSelected(settings);
//            currentActivitiesSorting = setupCurrentSorting(dataController.getActivitiesSelected());
//            parentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loader.setVisibility(View.GONE);
//                    setupFieldsWithData();
//                    fillListViewWithData(dataController.getActivitiesSelected());
//                }
//            });
//        }

        if(asyncReturnType.equals("Update Settings")) {
            if(editMode) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        settingsSaveComplete = true;
//                        if(activitySaveComplete) {
                        editMode = false;
                        saveButton.setText("Save");
                        editButton.setVisibility(View.VISIBLE);
                        fillListViewWithData(dataController.getActivitiesSelected());
//                        }

                    }
                });

            }
            else {
                navigationManager.clearStackReplaceFragment(MoreFragment.class);
                parentActivity.showToast("Activity updates saved");
            }
        }
        else if(asyncReturnType.equals("Update Activities")) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activitySaveComplete = true;
                    if(settingsSaveComplete) {
                        editMode = false;
                        saveButton.setText("Save");
                        editButton.setVisibility(View.VISIBLE);
                        fillListViewWithData(dataController.getActivitiesSelected());
                    }

                }
            });
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            AsyncActivitySettingsJsonObject settingsJson = (AsyncActivitySettingsJsonObject) returnObject;
            AsyncActivitySettingsObject[] settings = settingsJson.getRecord_activities();
            dataController.setActivitiesSelected(settings);
            currentActivitiesSorting = dataController.getActivitiesSelected();
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    setupFieldsWithData();
                    fillListViewWithData(dataController.getActivitiesSelected());
                }
            });
        }
    }

    private List<String> setupCurrentSorting(LinkedHashMap<String, SelectedActivities> activitiesSelected) {
        List<String> currentSorting = new ArrayList<>();
        for (String key : activitiesSelected.keySet()) {
            currentSorting.add(key);
        }
        return currentSorting;
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public void onItemDragStarted(int position) {

    }

    @Override
    public void onItemDragging(int itemPosition, float x, float y) {

    }

    @Override
    public void onItemDragEnded(int fromPosition, int toPosition) {
        AsyncActivitySettingsObject fromActivity = currentActivitiesSorting[fromPosition];
        sortActivities(fromActivity, fromPosition, toPosition);
    }

    private void sortActivities(AsyncActivitySettingsObject fromActivity, int fromPosition, int toPosition) {
        List<AsyncActivitySettingsObject> newSorting = new ArrayList<>();
        if(fromPosition > toPosition) {
            for(int i = 0; i < toPosition; i++) {
                if(i != fromPosition) {
                    newSorting.add(currentActivitiesSorting[i]);
                }
            }
            newSorting.add(fromActivity);
            for(int i = toPosition; i < currentActivitiesSorting.length; i++) {
                if(i != fromPosition) {
                    newSorting.add(currentActivitiesSorting[i]);
                }
            }
        }
        else if(toPosition > fromPosition) {
            for(int i = 0; i <= toPosition; i++) {
                if(i != fromPosition) {
                    newSorting.add(currentActivitiesSorting[i]);
                }
            }
            newSorting.add(fromActivity);
            for(int i = toPosition + 1; i < currentActivitiesSorting.length; i++) {
                newSorting.add(currentActivitiesSorting[i]);
            }
        }
        else {
            for(int i = 0; i < currentActivitiesSorting.length; i++) {
                newSorting.add(currentActivitiesSorting[i]);
            }
        }
        currentActivitiesSorting = newSorting.toArray(new AsyncActivitySettingsObject[0]);
        setupLocalWeighting(currentActivitiesSorting);
    }

    private void setupLocalWeighting(AsyncActivitySettingsObject[] currentActivitiesSorting) {

        for(int i = 0; i < currentActivitiesSorting.length; i++) {
            currentActivitiesSorting[i].setDisplay_order(currentActivitiesSorting.length - i);
        }
    }
}
