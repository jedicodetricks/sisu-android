package co.sisu.mobile.models;

import java.util.List;

/**
 * Created by Brady Groharing on 4/23/2018.
 */

public class AsyncUpdateSettingsJsonObject {
    int type;
    int id;
    List<UpdateSettingsObject> parameters;

    public AsyncUpdateSettingsJsonObject(int type, int id, List<UpdateSettingsObject> parameters) {
        this.type = type;
        this.id = id;
        this.parameters = parameters;
    }

    public AsyncUpdateSettingsJsonObject() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<UpdateSettingsObject> getParameters() {
        return parameters;
    }

    public void setParameters(List<UpdateSettingsObject> parameters) {
        this.parameters = parameters;
    }
}
