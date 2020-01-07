package co.sisu.mobile.models;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bradygroharing on 4/17/18.
 */

public class ParameterObject {
    String external_id;
    String external_type_id;
    String name;
    String parameter_id;
    String parameter_type_id;
    String status;
    String value;

    public ParameterObject(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.status = jsonObject.getString("status");;
            this.value = jsonObject.getString("value");;
            this.parameter_type_id = jsonObject.getString("parameter_type_id");;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ParameterObject(String name, String status, String value, String parameter_type_id) {
        this.name = name;
        this.status = status;
        this.value = value;
        this.parameter_type_id = parameter_type_id;
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public String getExternal_type_id() {
        return external_type_id;
    }

    public void setExternal_type_id(String external_type_id) {
        this.external_type_id = external_type_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter_id() {
        return parameter_id;
    }

    public void setParameter_id(String parameter_id) {
        this.parameter_id = parameter_id;
    }

    public String getParameter_type_id() {
        return parameter_type_id;
    }

    public void setParameter_type_id(String parameter_type_id) {
        this.parameter_type_id = parameter_type_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
