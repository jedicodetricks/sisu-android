package co.sisu.mobile.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.LeaderboardAgentModel;
import co.sisu.mobile.models.LeaderboardListObject;
import okhttp3.Response;

public class LeaderboardViewModel extends ViewModel implements AsyncServerEventListener {
    private MutableLiveData<List<LeaderboardListObject>> leaderboardScopeData = new MutableLiveData<>();
    private MutableLiveData<List<LeaderboardAgentModel>> leaderboardAgentData = new MutableLiveData<>();
    private String leaderboardTitle = "";

    public MutableLiveData<List<LeaderboardListObject>> getLeaderboardScopeData() {
        return leaderboardScopeData;
    }

    public List<LeaderboardListObject> getLeaderboardScopeDataValue() {
        return leaderboardScopeData.getValue();
    }

    public MutableLiveData<List<LeaderboardAgentModel>> getLeaderboardAgentData() {
        return leaderboardAgentData;
    }

    public List<LeaderboardAgentModel> getLeaderboardAgentDataValue() {
        return leaderboardAgentData.getValue();
    }

    public String getLeaderboardTitle() {
        return leaderboardTitle;
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
        if(returnType == ApiReturnType.GET_LEADERBOARD_LIST) {
            try {
                JSONObject leaderListObject = new JSONObject(returnString);
                JSONObject leaderboards = leaderListObject.getJSONObject("leaderboards");
                List<LeaderboardListObject> newLeaderboardScope = new ArrayList<>();
                Iterator<String> keys = leaderboards.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    JSONObject currentListObject = leaderboards.getJSONObject(key);
                    newLeaderboardScope.add(new LeaderboardListObject(key, currentListObject.getString("label"), currentListObject.getString("metric"), currentListObject.getString("type")));
                }
                if (newLeaderboardScope.size() > 0) {
                    Collections.sort(newLeaderboardScope, (object1, object2) -> object1.getLabel().compareTo(object2.getLabel()));
                }
                leaderboardScopeData.postValue(newLeaderboardScope);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_LEADERBOARD) {
            try {
                JSONObject leaderboardObject = new JSONObject(returnString);
                JSONArray leaderboardReportData = leaderboardObject.getJSONArray("report_data");
                leaderboardTitle = leaderboardObject.getString("title");
                List<LeaderboardAgentModel> allLeaderboardAgents = new ArrayList<>();
                for(int i = 0; i < leaderboardReportData.length(); i++) {
                    JSONObject currentLeaderboardAgent = leaderboardReportData.getJSONObject(i);
                    allLeaderboardAgents.add(new LeaderboardAgentModel(currentLeaderboardAgent.getString("agent_id"), currentLeaderboardAgent.getString("agent_name"), currentLeaderboardAgent.getString("item_count"), currentLeaderboardAgent.getString("place"), currentLeaderboardAgent.getString("profile_image_path")));
                }
                leaderboardAgentData.postValue(allLeaderboardAgents);
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
}
