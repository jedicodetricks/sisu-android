package co.sisu.mobile.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnType;
import co.sisu.mobile.fragments.main.ClientTileFragment;
import okhttp3.Response;

public class ClientTilesViewModel extends ViewModel implements AsyncServerEventListener {
    private final MutableLiveData<JSONObject> clientTiles = new MutableLiveData<>();

    public void setClientTiles(JSONObject newTiles) {
        clientTiles.setValue(newTiles);
    }

    public LiveData<JSONObject> getClientTiles() {
        return clientTiles;
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
                JSONObject pagination = null;
                try {
                    pagination = newClientTiles.getJSONObject("pagination");
                    if (pagination.getInt("page") > 1) {
                        //append tiles
                        JSONArray currentClientTiles = newClientTiles.getJSONArray("tile_rows");
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
                        clientTiles.postValue(newClientTiles);
                    } else {
                        //overwrite tiles
//                        clientTiles = newClientTiles;
                        clientTiles.postValue(newClientTiles);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
