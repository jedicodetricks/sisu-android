package co.sisu.mobile.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import okhttp3.Response;

public class DashboardTilesViewModel extends ViewModel implements AsyncServerEventListener {
    private final MutableLiveData<JSONObject> dashboardTiles = new MutableLiveData<>();

    public void setDashboardTiles(JSONObject newTiles) {
        dashboardTiles.setValue(newTiles);
    }

    public LiveData<JSONObject> getDashboardTiles() {
        return dashboardTiles;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

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
        if(returnType == ApiReturnType.GET_TILES) {
            try {
                JSONObject updatedTiles =  new JSONObject(returnString);
                dashboardTiles.postValue(updatedTiles);
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
