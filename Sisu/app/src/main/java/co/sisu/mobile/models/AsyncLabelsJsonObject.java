package co.sisu.mobile.models;

import java.util.HashMap;

public class AsyncLabelsJsonObject {

    HashMap<String, String> market;
    String server_time;
    String status;
    String status_code;

    public HashMap<String, String> getMarket() {
        return market;
    }

    public void setMarket(HashMap<String, String> labels) {
        this.market = labels;
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
