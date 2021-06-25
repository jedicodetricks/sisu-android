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
import java.util.Iterator;
import java.util.List;

import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.fragments.main.ClientTileFragment;
import co.sisu.mobile.models.FilterObject;
import okhttp3.Response;

public class ClientTilesViewModel extends ViewModel implements AsyncServerEventListener {
    private final MutableLiveData<JSONObject> clientTiles = new MutableLiveData<>();
    private final MutableLiveData<List<FilterObject>> clientFilters = new MutableLiveData<>();

    public void setClientTiles(JSONObject newTiles) {
        clientTiles.setValue(newTiles);
    }

    public void setClientFilters(List<FilterObject> newFilters) {
        clientFilters.setValue(newFilters);
    }

    public LiveData<JSONObject> getClientTiles() {
        return clientTiles;
    }

    public LiveData<List<FilterObject>> getClientFilters() {
        return clientFilters;
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {}

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnType returnType) {
        String returnString = null;
        try {
            returnString = ((Response) returnObject).body().string();
            if (returnString == null) {
                // TODO: Should probably throw a custom error here.
                throw new IOException("ReturnString broken for returnType: " + returnType.name());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (returnType == ApiReturnType.GET_TEAM_CLIENT_TILES) {
            try {
                JSONObject newClientTiles = new JSONObject(returnString);
                JSONObject pagination;
                try {
                    pagination = newClientTiles.getJSONObject("pagination");
                    if (pagination.getInt("page") > 1) {
                        //append tiles
                        JSONArray currentClientTiles = clientTiles.getValue().getJSONArray("tile_rows");
                        JSONArray clientTilesToAppend = newClientTiles.getJSONArray("tile_rows");

                        for (int i = 0; i < clientTilesToAppend.length(); i++) {
                            JSONObject tileObject = clientTilesToAppend.getJSONObject(i);
                            JSONArray currentTiles = tileObject.getJSONArray("tiles");
                            JSONObject tile = currentTiles.getJSONObject(0);
                            if (tile.has("type")) {
                                String type = tile.getString("type");
                                switch (type) {
                                    case "clientList":
                                        currentClientTiles.put(tileObject);
                                        break;
                                    case "smallHeader":
                                        break;
                                    default:
                                        Log.e("TYPE", type);
                                        break;
                                }
                            }
                        }
                        newClientTiles.put("tile_rows", currentClientTiles);
                        newClientTiles.put("pagination", pagination);
                        newClientTiles.put("count", currentClientTiles.length());
                    } else {
                        //overwrite tiles
//                        clientTiles = newClientTiles;
                    }
                    clientTiles.postValue(newClientTiles);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(returnType == ApiReturnType.GET_AGENT_FILTERS) {
            try {
                JSONObject responseJson = new JSONObject(returnString);
                JSONArray filtersArray = responseJson.getJSONArray("filters");
                List<FilterObject> agentFiltersList = new ArrayList<>();
                //
                for(int i = 0; i < filtersArray.length(); i++) {
                    JSONObject filtersObject = (JSONObject) filtersArray.get(i);
                    JSONObject filters = filtersObject.getJSONObject("filters");
                    Iterator<String> keys = filters.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        try {
                            JSONObject value = filters.getJSONObject(key);
                            if(value.length() > 0) {
                                agentFiltersList.add(new FilterObject(key, value));
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
                if(agentFiltersList.size() == 0) {
                    agentFiltersList.add(new FilterObject("No Filters Configured", null));
                }
                clientFilters.postValue(agentFiltersList);

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
