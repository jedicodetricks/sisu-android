package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncActivitySettingsJsonObject {

    AsyncActivitySettingsObject[] record_activities;
    String server_time;
    String status;
    String status_code;
    int count;

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public AsyncActivitySettingsObject[] getRecord_activities() {
        return record_activities;
    }

    public void setRecord_activities(AsyncActivitySettingsObject[] record_activities) {
        this.record_activities = record_activities;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
