package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 7/17/18.
 */

public class AsyncNotesJsonObject {
    NotesObject[] client_logs;
    String server_time;
    String status;
    String status_code;

    public NotesObject[] getClient_logs() {
        return client_logs;
    }

    public void setClient_logs(NotesObject[] client_logs) {
        this.client_logs = client_logs;
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
