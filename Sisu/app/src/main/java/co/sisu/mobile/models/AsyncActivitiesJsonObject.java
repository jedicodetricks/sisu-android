package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncActivitiesJsonObject {
    ActivitiesModel[] activities;
    ActivitiesCounterModel[] counters;
    String end_date;
    String server_time;
    String start_date;
    String status;
    String status_code;

    public ActivitiesModel[] getActivities() {
        return activities;
    }

    public void setActivities(ActivitiesModel[] activities) {
        this.activities = activities;
    }

    public ActivitiesCounterModel[] getCounters() {
        return counters;
    }

    public void setCounters(ActivitiesCounterModel[] counters) {
        this.counters = counters;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
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
}
