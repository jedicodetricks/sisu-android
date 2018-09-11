package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.woxthebox.draglistview.DragListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ActivityListAdapter;
import co.sisu.mobile.adapters.ClientItemAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.UpdateSettingsObject;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivitySettingsFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, AsyncServerEventListener, DragListView.DragListListener {

    private DragListView mListView;
    private ParentActivity parentActivity;
    private NavigationManager  navigationManager;
    private ApiManager apiManager;
    private DataController dataController;
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
        loader = view.findViewById(R.id.activitySettingsLoader);
        initializeButtons();
        initializeListView();
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
        mListView.setDragListListener(this);
        mListView.setLayoutManager(new LinearLayoutManager(parentActivity));
        mListView.getRecyclerView().setVerticalScrollBarEnabled(true);
    }

    private void fillListViewWithData(HashMap<String, SelectedActivities> selectedActivities) {
        ArrayList mItemArray = new ArrayList<>();
        int counter = 0;
        if(getContext() != null) {

            for ( String key : selectedActivities.keySet() ) {
                SelectedActivities value = selectedActivities.get(key);
                mItemArray.add(new Pair<>((long) counter, value));
                counter++;
//            jsonString += "\"" + key + "\":\"" + value + "\"";
//            if(counter < changedFields.size() - 1) {
//                jsonString += ",";
//            }
//            counter++;
            }
//
//            ArrayList priorityArray = new ArrayList<>();
//            ArrayList commonArray = new ArrayList<>();
//
//            for(ClientObject clientObject : metricList) {
//                if(clientObject.getIs_priority().equals("0")) {
//                    commonArray.add(clientObject);
//                }
//                else {
//                    priorityArray.add(clientObject);
//                }
//            }
//
//            int counter = 0;
////            mItemArray.add(new Pair<>((long) counter, "Priority"));
////            priorityPosition = counter;
////            counter++;
//            for(int i = 0; i < priorityArray.size(); i++) {
//                mItemArray.add(new Pair<>((long) counter, priorityArray.get(i)));
//                counter++;
//            }
//
//            mItemArray.add(new Pair<>((long) counter, "Pipeline"));
//            pipelinePosition = counter;
//            counter++;
//            for(int i = 0; i < commonArray.size(); i++) {
//                mItemArray.add(new Pair<>((long) counter, commonArray.get(i)));
//                counter++;
//            }
//
//
//
////            ClientListAdapter adapter = new ClientListAdapter(getContext(), metricList, this);
////            mListView.setAdapter(adapter);
//
//            ClientItemAdapter clientItemAdapter = new ClientItemAdapter(mItemArray, R.layout.list_item, R.id.client_list_thumbnail, false, this);
//            mListView.setDragEnabled(false);
//            mListView.setAdapter(clientItemAdapter, true);
//            mListView.setCanDragHorizontally(false);
////            mListView.setCustomDragItem(new MyDragItem(getContext(), R.layout.list_item));
//            mListView.setCustomDragItem(null);
//
////            mListView.setOnItemClickListener(this);
        }
        ActivityListAdapter activityListAdapter = new ActivityListAdapter(mItemArray, R.layout.adapter_activity_list, R.id.activity_list_title, false);
        mListView.setAdapter(activityListAdapter, true);
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
                saveSettings();
                break;
        }
    }

    private void updateCurrentSettings() {

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
                    fillListViewWithData(dataController.getSelectedActivitiesList());
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

    @Override
    public void onItemDragStarted(int position) {

    }

    @Override
    public void onItemDragging(int itemPosition, float x, float y) {

    }

    @Override
    public void onItemDragEnded(int fromPosition, int toPosition) {

    }
}
