package co.sisu.mobile.models;

import java.util.LinkedHashMap;

/**
 * Created by Brady Groharing on 4/29/2018.
 */

public class AsyncLeadSourcesJsonObject {

    LinkedHashMap lead_sources;
    String server_time;
    String status;
    String status_code;
    int count;

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

    public LinkedHashMap getLead_sources() {
        return lead_sources;
    }

    public void setLead_sources(LinkedHashMap lead_sources) {
        this.lead_sources = lead_sources;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
