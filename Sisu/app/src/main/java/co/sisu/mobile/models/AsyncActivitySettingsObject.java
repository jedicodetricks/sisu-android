package co.sisu.mobile.models;

public class AsyncActivitySettingsObject {

    private String activity_type;
    private String display_locked;
    private long display_order;
    private String name;
    private boolean value;

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

    public void setDisplay_order(long display_order) {
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
