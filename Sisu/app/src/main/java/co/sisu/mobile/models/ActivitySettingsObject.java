package co.sisu.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivitySettingsObject {

    private String activity_type;
    private boolean display_locked;
    private int display_order;
    private String name;
    private int team_id;
    private boolean user_input_restricted;
    private boolean value;

    public ActivitySettingsObject(JSONObject currentActivitySetting) {
        try {
            this.activity_type = currentActivitySetting.getString("activity_type");
            // TODO: There is an attribute called challenge_category_id that I'm not using
            this.display_locked = currentActivitySetting.getBoolean("display_locked");
            this.display_order = currentActivitySetting.getInt("display_order");
            this.name = currentActivitySetting.getString("name");
            this.team_id = currentActivitySetting.getInt("team_id");
            this.user_input_restricted = currentActivitySetting.getBoolean("user_input_restricted");
            this.value = currentActivitySetting.getBoolean("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public boolean getDisplay_locked() {
        return display_locked;
    }

    public void setDisplay_locked(boolean display_locked) {
        this.display_locked = display_locked;
    }

    public long getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(int display_order) {
        this.display_order = display_order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isDisplay_locked() {
        return display_locked;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public boolean isUser_input_restricted() {
        return user_input_restricted;
    }

    public void setUser_input_restricted(boolean user_input_restricted) {
        this.user_input_restricted = user_input_restricted;
    }

    public boolean isValue() {
        return value;
    }
}
