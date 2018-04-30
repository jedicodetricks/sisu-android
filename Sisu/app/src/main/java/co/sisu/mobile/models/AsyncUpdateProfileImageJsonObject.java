package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncUpdateProfileImageJsonObject {
    String data;
    String external_id;
    String type;
    String format;

    public AsyncUpdateProfileImageJsonObject(String data, String external_id, String type, String format) {
        this.data = data;
        this.external_id = external_id;
        this.type = type;
        this.format = format;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
