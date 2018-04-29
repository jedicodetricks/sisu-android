package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncActivitySettingsJsonObject {

    SettingsObject parameter;
    String server_time;
    String status;
    String status_code;

    public SettingsObject getParameter() {
        return parameter;
    }

    public void setParameter(SettingsObject parameter) {
        this.parameter = parameter;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }
}
