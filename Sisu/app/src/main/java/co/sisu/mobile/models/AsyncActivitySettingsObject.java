package co.sisu.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

public class AsyncActivitySettingsObject {

    private String activity_type;
    private String display_locked;
    private int display_order;
    private String name;
    private boolean value;

    public AsyncActivitySettingsObject(JSONObject currentActivitySetting) {
        try {
            this.activity_type = currentActivitySetting.getString("activity_type");
            // TODO: Why am I not using this as a boolean but as a string?!
            this.display_locked = String.valueOf(currentActivitySetting.getBoolean("display_locked"));
            this.display_order = currentActivitySetting.getInt("display_order");
            this.name = currentActivitySetting.getString("name");
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

    public String getDisplay_locked() {
        return display_locked;
    }

    public void setDisplay_locked(String display_locked) {
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
}
