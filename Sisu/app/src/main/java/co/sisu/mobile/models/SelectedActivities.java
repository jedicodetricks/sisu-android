package co.sisu.mobile.models;


/**
 * Created by Brady Groharing on 4/28/2018.
 */

public class SelectedActivities {
    boolean value;
    String name;
    String type;

    public SelectedActivities(boolean value, String type) {
        this.value = value;
        this.type = type;
    }

    public SelectedActivities(boolean value, String type, String name) {
        this.value = value;
        this.type = type;
        this.name = name;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
