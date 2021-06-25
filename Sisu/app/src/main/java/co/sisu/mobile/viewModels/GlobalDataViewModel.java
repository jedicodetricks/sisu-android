package co.sisu.mobile.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.models.MarketStatusModel;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.models.TeamObject;
import okhttp3.Response;

public class GlobalDataViewModel extends ViewModel implements AsyncServerEventListener {
    private MutableLiveData<List<TeamObject>> allTeams = new MutableLiveData<>();
    private MutableLiveData<TeamObject> selectedTeam = new MutableLiveData<>();
    private MutableLiveData<String> slackUrl = new MutableLiveData<>("");
    private MutableLiveData<AgentModel> agentData = new MutableLiveData<>();
    private MutableLiveData<List<ParameterObject>> settingsData = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, String>> labelsData = new MutableLiveData<>();
    private MutableLiveData<FirebaseDeviceObject> currentFirebaseDeviceData = new MutableLiveData<>();
    private MutableLiveData<String> currentFirebaseDeviceIdData = new MutableLiveData<>("");
    private MutableLiveData<List<ScopeBarModel>> scopeData = new MutableLiveData<>();
    private MutableLiveData<List<MarketStatusModel>> marketStatusData = new MutableLiveData<>();


    public void setAllTeams(List<TeamObject> item) {
        allTeams.setValue(item);
    }

    public LiveData<List<TeamObject>> getTeamsObject() {
        return allTeams;
    }

    public void setSelectedTeam(TeamObject newSelectedTeam) {
        selectedTeam.setValue(newSelectedTeam);
    }

    public LiveData<TeamObject> getSelectedTeam() {
        return selectedTeam;
    }

    public TeamObject getSelectedTeamValue() {
        return selectedTeam.getValue();
    }

    public LiveData<AgentModel> getAgentData() {
        return agentData;
    }

    public AgentModel getAgentValue() {
        return agentData.getValue();
    }

    public void setAgentData(AgentModel agentData) {
        this.agentData.setValue(agentData);
    }

    public LiveData<List<ParameterObject>> getSettingsData() {
        return settingsData;
    }

    public LiveData<HashMap<String, String>> getLabelsData() {
        return labelsData;
    }

    public LiveData<FirebaseDeviceObject> getCurrentFirebaseDeviceData() {
        return currentFirebaseDeviceData;
    }

    public MutableLiveData<String> getCurrentFirebaseDeviceIdData() {
        return currentFirebaseDeviceIdData;
    }

    public void setCurrentFirebaseDeviceIdData(String currentFirebaseDeviceIdData) {
        this.currentFirebaseDeviceIdData.setValue(currentFirebaseDeviceIdData);
    }

    public MutableLiveData<List<ScopeBarModel>> getScopeData() {
        return scopeData;
    }

    public List<ScopeBarModel> getScopeDataValue() {
        return scopeData.getValue();
    }

    public MutableLiveData<List<MarketStatusModel>> getMarketStatusData() {
        return marketStatusData;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        String returnString = null;
        try {
            returnString = ((Response) returnObject).body().string();
            if(returnString == null) {
                // TODO: Should probably throw a custom error here.
                throw new IOException("ReturnString broken for returnType: " + returnType.name());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(returnType == ApiReturnType.GET_TEAMS) {
            try {
                JSONObject teamsObject = new JSONObject(returnString);
                JSONArray teamsArray = teamsObject.getJSONArray("teams");
                List<TeamObject> allTeamsObject = new ArrayList<>();
                for(int i = 0; i < teamsArray.length(); i++) {
                    try {
                        JSONObject currentTeam = teamsArray.getJSONObject(i);
                        allTeamsObject.add(new TeamObject(currentTeam));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                allTeams.postValue(allTeamsObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_TEAM_PARAMS) {
            // TODO: We'll need to change how we use the slackInfo from DataController since it's here now.
            try {
                JSONObject teamParamsObject = new JSONObject(returnString);
                if(teamParamsObject.getString("status_code").equals("-1")) {
                    slackUrl.postValue("");
                }
                else {
                    ParameterObject params = new ParameterObject(teamParamsObject.getJSONObject("parameter"));
                    slackUrl.postValue(params.getValue());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_FIREBASE_DEVICES) {
            try {
                JSONObject firebaseObject = new JSONObject(returnString);
                JSONArray devices = firebaseObject.getJSONArray("devices");
                // TODO: Probably have to set this in the parent activity and then get it in here.
//                String firebaseDeviceId = SaveSharedPreference.getFirebaseDeviceId(this);
                String firebaseDeviceId = currentFirebaseDeviceIdData.getValue();
                for(int i = 0; i < devices.length(); i++) {
                    FirebaseDeviceObject currentDevice = new FirebaseDeviceObject(devices.getJSONObject(i));
                    if(currentDevice.getDevice_id() != null && currentDevice.getDevice_id().equals(firebaseDeviceId)) {
                        Log.e("Current Device", currentDevice.getDevice_id());
                        currentFirebaseDeviceData.postValue(currentDevice);
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_SCOPE) {
            try {
                List<ScopeBarModel> scopeBarList = new ArrayList<>();
                JSONObject scopes = new JSONObject(returnString);
                JSONObject allScopes = scopes.getJSONObject("scopes");

                if(allScopes.has("team")) {
                    JSONObject scopeTeam = allScopes.getJSONObject("team");
                    scopeBarList.add(new ScopeBarModel(scopeTeam.getString("display_name"), "t" + scopeTeam.getString("team_id")));
                }
                if(allScopes.has("groups")) {
                    JSONArray scopeGroups = allScopes.getJSONArray("groups");
                    scopeBarList.add(new ScopeBarModel("-- Groups --", "Groups"));

                    for(int i = 0; i < scopeGroups.length(); i++) {
                        JSONObject currentGroup = (JSONObject) scopeGroups.get(i);
                        scopeBarList.add(new ScopeBarModel(currentGroup.getString("display_name"), "g" + currentGroup.getString("group_id")));
                    }
                }
                if(allScopes.has("agents")) {
                    JSONArray scopeAgents = allScopes.getJSONArray("agents");
                    scopeBarList.add(new ScopeBarModel("-- Agents --", "Groups"));

                    for(int i = 0; i < scopeAgents.length(); i++) {
                        JSONObject currentAgent = (JSONObject) scopeAgents.get(i);
                        if(currentAgent.getString("agent_id").equalsIgnoreCase(getAgentData().getValue().getAgent_id())) {
                            ScopeBarModel agentScope = new ScopeBarModel(currentAgent.getString("display_name"), "a" + currentAgent.getString("agent_id"));
                            scopeBarList.add(0, agentScope);
                        }
                        else {
                            scopeBarList.add(new ScopeBarModel(currentAgent.getString("display_name"), "a" + currentAgent.getString("agent_id")));
                        }
                    }
                }

                scopeData.postValue(scopeBarList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_MARKET_STATUS) {
            try {
                JSONObject marketStatusObject = new JSONObject(returnString);
                try {
                    JSONArray marketStatuses = marketStatusObject.getJSONArray("client_status");
                    List<MarketStatusModel> marketStatusBar = new ArrayList<>();
                    for(int k = 0; k < marketStatuses.length(); k++) {
                        JSONObject currentMarketStatus = (JSONObject) marketStatuses.get(k);
                        MarketStatusModel currentModel = new MarketStatusModel(currentMarketStatus.getString("key"), currentMarketStatus.getString("label"), currentMarketStatus.getBoolean("select"));
                        marketStatusBar.add(currentModel);
//                        if(currentModel.getKey().equalsIgnoreCase("")) {
//                            currentMarketStatusFilter = currentModel;
//                        }
                    }
                    marketStatusData.postValue(marketStatusBar);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                marketStatusFinished = true;
//                navigateToScoreboard();
//                if(clientTilesFinished) {
//                    if(getCurrentScopeFilter() != null) {
//                        actionBarManager.setToFilterBar(getCurrentScopeFilter().getName());
//                    }
//                    else {
//                        actionBarManager.setToFilterBar("");
//                    }
//                    navigationManager.clearStackReplaceFragment(ClientTileFragment.class);
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_SETTINGS) {
            try {
                JSONObject settingsJson = new JSONObject(returnString);
                JSONArray settings = settingsJson.getJSONArray("parameters");
                List<ParameterObject> newSettingsObject = createSettingsObject(settings);
                settingsData.postValue(newSettingsObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(returnType == ApiReturnType.GET_LABELS) {
            try {
                JSONObject labelsObject = new JSONObject(returnString);
                JSONObject marketLabels = labelsObject.getJSONObject("market");
                HashMap<String, String> labels = new LinkedHashMap<>();
                Iterator<String> keys = marketLabels.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    String value = marketLabels.getString(key);
                    labels.put(key, value);
                }
                labelsData.postValue(labels);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }

    //region Settings Creation
    public List<ParameterObject> createSettingsObject(JSONArray settings) {
        List<String> existingSettings = new ArrayList<>();
        List<ParameterObject> newSettings = new ArrayList<>();

        for(int i = 0; i < settings.length(); i++) {
            JSONObject s;
            try {
                s = settings.getJSONObject(i);
                switch (s.getString("name")) {
                    case "local_timezone":
                        existingSettings.add("local_timezone");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "daily_reminder_time":
                        existingSettings.add("daily_reminder_time");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "lights":
                        existingSettings.add("lights");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "daily_reminder":
                        existingSettings.add("daily_reminder");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "record_activities":
                        existingSettings.add("record_activities");
                        newSettings.add(new ParameterObject(s));
                        break;
                    default:
                        newSettings.add(new ParameterObject(s));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(!existingSettings.contains("local_timezone")) {
            newSettings.add(getDefaultLocalTimezone());
        }

        if(!existingSettings.contains("daily_reminder_time")) {
            newSettings.add(getDefaultDailyReminderTime());
        }

        if(!existingSettings.contains("daily_reminder")) {
            newSettings.add(getDefaultDailyReminder());
        }

        if(!existingSettings.contains("record_activities")) {
            newSettings.add(getDefaultRecordActivities());
        }

        if(!existingSettings.contains("lights")) {
            newSettings.add(getDefaultLights());
        }

        return newSettings;
    }

    private ParameterObject getDefaultLocalTimezone() {
        return new ParameterObject("local_timezone", "N", "America/Denver", "0");
    }

    private ParameterObject getDefaultDailyReminderTime() {
        return new ParameterObject("daily_reminder_time", "N", "17:00", "5");
    }

    private ParameterObject getDefaultDailyReminder() {
        return new ParameterObject("daily_reminder", "N", "1", "3");
    }

    private ParameterObject getDefaultRecordActivities() {
        return new ParameterObject("record_activities", "N", "{\"THANX\":1,\"APPTT\":1,\"SHWNG\":1,\"REFFR\":1,\"REFFC\":1,\"ADDDB\":1,\"5STAR\":1,\"EXERS\":1,\"PCMAS\":1,\"OPENH\":1,\"APPTS\":1,\"HOURP\":1,\"DIALS\":1,\"BSHNG\":1,\"MEDIT\":1}", "7");
    }

    private ParameterObject getDefaultLights() {
        return new ParameterObject("lights", "N", "0", "5");
    }

    //endregion Settings Creation

}
