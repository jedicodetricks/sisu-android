package co.sisu.mobile.models;

import java.util.List;

/**
 * Created by Brady Groharing on 4/23/2018.
 */

public class AsyncUpdateSettingsJsonObject {
    int type;
    int id;
    List<UpdateSettingsObject> record_activities;

    public AsyncUpdateSettingsJsonObject(int type, int id, List<UpdateSettingsObject> record_activities) {
        this.type = type;
        this.id = id;
        this.record_activities = record_activities;
    }

    public AsyncUpdateSettingsJsonObject(int id, List<UpdateSettingsObject> record_activities) {
        this.id = id;
        this.record_activities = record_activities;
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

    public List<UpdateSettingsObject> getRecord_activities() {
        return record_activities;
    }

    public void setRecord_activities(List<UpdateSettingsObject> record_activities) {
        this.record_activities = record_activities;
    }
}
