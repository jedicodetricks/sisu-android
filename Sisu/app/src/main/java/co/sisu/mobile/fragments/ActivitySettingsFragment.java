package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.woxthebox.draglistview.DragListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ActivityListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ActionBarManager;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.UpdateActivitiesModel;
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
    private ActionBarManager actionBarManager;
    private AsyncActivitySettingsObject[] selectedActivities;
//    private AsyncActivitySettingsObject[] currentActivitiesSorting;
    private JSONArray currentActivitySettings;
    private List<SelectedActivities> currentActivitySorting;
    private ProgressBar loader;
    private ArrayList mItemArray = new ArrayList<>();
    private boolean editMode = false;
    private TextView saveButton, title;
    private ImageView editButton;
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
        actionBarManager = parentActivity.getActionBarManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        loader.setVisibility(View.VISIBLE);
        initializeButtons();
        initializeListView();
        initializeFields();
        setColorScheme();
        apiManager.getActivitySettings(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedTeamId(), parentActivity.getSelectedTeamMarketId());
    }

    private void initializeFields() {
        title = getView().findViewById(R.id.activity_settings_title);
    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.activitySettingsParentLayout);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

        title.setTextColor(colorSchemeManager.getDarkerTextColor());
    }

    private void initializeButtons() {
        saveButton = parentActivity.findViewById(R.id.saveButton);
        if(saveButton != null) {
            saveButton.setOnClickListener(this);
        }
        editButton = parentActivity.findViewById(R.id.actionBarActionImage);
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



    private void fillListViewWithData(JSONArray selectedActivities) {
        int counter = 0;
        mItemArray = new ArrayList<>();
        currentActivitySorting = new ArrayList<>();
        if(getContext() != null) {
            for(int i = 0; i < selectedActivities.length(); i++) {
                try {
                    JSONObject currentSetting = selectedActivities.getJSONObject(i);
                    String activityType = null;
                    Boolean value = null;
                    String name = null;

                    if(currentSetting.has("activity_type")) {
                        activityType = currentSetting.getString("activity_type");
                    }
                    if(currentSetting.has("value")) {
                        value = currentSetting.getBoolean("value");
                    }
                    if(currentSetting.has("name")) {
                        name = currentSetting.getString("name");
                    }

                    if(!activityType.equalsIgnoreCase("CONTA")) {
                        SelectedActivities activity = new SelectedActivities(value, activityType, name);
                        currentActivitySorting.add(activity);
                        mItemArray.add(new Pair<>((long) counter, activity));
                        counter++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        ActivityListAdapter activityListAdapter;

        if(!editMode) {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_activity_list, R.id.activity_list_title, false, colorSchemeManager, false, this);
            mListView.setDragEnabled(false);
        }
        else {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_edit_activity_list, R.id.editButton, false, colorSchemeManager, true, this);
            mListView.setDragEnabled(true);
        }
        mListView.setAdapter(activityListAdapter, false);
        mListView.setCanDragHorizontally(false);
        mListView.setCustomDragItem(null);

    }

    private void fillListViewWithData(List<SelectedActivities> newActivitySorting) {
        int counter = 0;
        mItemArray = new ArrayList<>();
        currentActivitySorting = new ArrayList<>();
        if(getContext() != null) {
            for(SelectedActivities currentActivity: newActivitySorting) {
                if(!currentActivity.getType().equalsIgnoreCase("CONTA")) {
                    SelectedActivities activity = new SelectedActivities(currentActivity.getValue(), currentActivity.getType(), currentActivity.getName());
                    currentActivitySorting.add(activity);
                    mItemArray.add(new Pair<>((long) counter, activity));
                    counter++;
                }
            }
        }

        ActivityListAdapter activityListAdapter;

        if(!editMode) {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_activity_list, R.id.activity_list_title, false, colorSchemeManager, false, this);
            mListView.setDragEnabled(false);
        }
        else {
            activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_edit_activity_list, R.id.editButton, false, colorSchemeManager, true, this);
            mListView.setDragEnabled(true);
        }
        mListView.setAdapter(activityListAdapter, false);
        mListView.setCanDragHorizontally(false);
        mListView.setCustomDragItem(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveButton:
                editMode = !editMode;
                updateRecordedActivities();
                actionBarManager.setToEditBar("Record Settings");
                fillListViewWithData(currentActivitySorting);
                break;
            case R.id.actionBarActionImage:
                editMode = !editMode;
                actionBarManager.setToSaveBar("Record Settings");
                saveButton.setText("Done");
                fillListViewWithData(currentActivitySorting);
                break;
        }
    }

    public void updateRecordedActivities() {
        List<UpdateActivitiesModel> updateActivitiesModels = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        AsyncUpdateActivitiesJsonObject activitiesJsonObject = new AsyncUpdateActivitiesJsonObject();

        UpdateActivitiesModel[] array = new UpdateActivitiesModel[updateActivitiesModels.size()];
        updateActivitiesModels.toArray(array);

        activitiesJsonObject.setActivities(array);
        apiManager.sendAsyncUpdateActivitySettings(this, dataController.getAgent().getAgent_id(), createUpdateObject(currentActivitySorting), parentActivity.getSelectedTeamId(), parentActivity.getSelectedTeamMarketId());
    }


    private String createUpdateObject(List<SelectedActivities> selectedActivities) {
        // TODO: This could be a lot cleaner. Should just use JsonArray and all that. Could probably move it to the apiManager
        String valueString = "{\"record_activities\":[";
        int counter = 0;
        for(SelectedActivities setting: selectedActivities) {
            valueString += "{\"activity_type\":\"" + setting.getType() + "\", \"value\":" + setting.getValue();
            if(counter < selectedActivities.size() - 1) {
                valueString += "},";
            }
            else {
                valueString += "}";
            }
            counter++;
        }
        valueString += "]}";
        return valueString;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_ACTIVITY_SETTINGS) {
            try {
                String activitySettingsString = ((Response) returnObject).body().string();
                ((Response) returnObject).close();
                JSONObject activitySettingsJson = new JSONObject(activitySettingsString);
                JSONArray activitySettings = activitySettingsJson.getJSONArray("record_activities");
                dataController.setActivitySettingsNew(activitySettings);
                currentActivitySettings = dataController.getActivitySettingsNew();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            parentActivity.runOnUiThread(() -> {
                fillListViewWithData(currentActivitySettings);
                loader.setVisibility(View.GONE);
            });
        }
        else if(returnType == ApiReturnTypes.UPDATE_ACTIVITY_SETTINGS) {
            try {
                String activitySettingsString = ((Response) returnObject).body().string();
                ((Response) returnObject).close();
                JSONObject activitySettingsJson = new JSONObject(activitySettingsString);
                int bogus = 1;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(editMode) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        settingsSaveComplete = true;
                        editMode = false;
                        saveButton.setText("Save");
                        editButton.setVisibility(View.VISIBLE);
                        fillListViewWithData(dataController.getActivitySettingsNew());
                    }
                });

            }
            else {
                parentActivity.showToast("Activity updates saved");
                actionBarManager.setToEditBar("Record Settings");
            }
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {}

    @Override
    public void onItemDragStarted(int position) {}

    @Override
    public void onItemDragging(int itemPosition, float x, float y) {}

    @Override
    public void onItemDragEnded(int fromPosition, int toPosition) {
        SelectedActivities fromActivity = currentActivitySorting.get(fromPosition);
        currentActivitySorting.remove(fromPosition);
        currentActivitySorting.add(toPosition, fromActivity);
    }

    public void onCheckChanged() {
        updateRecordedActivities();
    }
}
