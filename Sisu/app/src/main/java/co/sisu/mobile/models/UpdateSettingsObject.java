package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/23/2018.
 */

public class UpdateSettingsObject {
    String name;
    String value;
    int parameter_type_id;

    public UpdateSettingsObject(String name, String value, int parameter_type_id) {
        this.name = name;
        this.value = value;
        this.parameter_type_id = parameter_type_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getParameter_type_id() {
        return parameter_type_id;
    }

    public void setParameter_type_id(int parameter_type_id) {
        this.parameter_type_id = parameter_type_id;
    }
}
