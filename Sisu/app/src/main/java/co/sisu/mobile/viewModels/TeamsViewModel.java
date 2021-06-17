package co.sisu.mobile.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.models.TeamObject;
import okhttp3.Response;

public class TeamsViewModel extends ViewModel implements AsyncServerEventListener {
    private final MutableLiveData<List<TeamObject>> allTeams = new MutableLiveData<>();

    public void select(List<TeamObject> item) {
        allTeams.setValue(item);
    }

    public LiveData<List<TeamObject>> getTeamsObject() {
        return allTeams;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        if(returnType == ApiReturnType.GET_TEAMS) {
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
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnType returnType) {

    }
}
