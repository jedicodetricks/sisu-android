package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/17/2018.
 */

public class AsyncUpdateActivitiesJsonObject {
    UpdateActivitiesModel[] activities;

    public UpdateActivitiesModel[] getActivities() {
        return activities;
    }

    public void setActivities(UpdateActivitiesModel[] activities) {
        this.activities = activities;
    }
}
