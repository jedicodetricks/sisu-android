package co.sisu.mobile.models;


/**
 * Created by Brady Groharing on 4/28/2018.
 */

public class SelectedActivities {
    String value;
    String name;
    String type;

    public SelectedActivities(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public SelectedActivities(String value, String type, String name) {
        this.value = value;
        this.type = type;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
