package co.sisu.mobile.models;

import android.graphics.Bitmap;

/**
 * Created by bradygroharing on 7/20/18.
 */

public class LeaderboardAgentModel {
    private String agent_id;
    private String label;
    private String place;
    private String profile;
    private String value;
    private Bitmap bitmap;

    public LeaderboardAgentModel(String agent_id, String label, String place, String profile, String value) {
        this.agent_id = agent_id;
        this.label = label;
        this.place = place;
        this.profile = profile;
        this.value = value;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

