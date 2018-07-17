package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 7/17/18.
 */

public class NotesObject {
    String client_id;
    String created_ts;
    String id;
    String log_type_id;
    String note;
    String status;
    String updated_ts;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(String created_ts) {
        this.created_ts = created_ts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLog_type_id() {
        return log_type_id;
    }

    public void setLog_type_id(String log_type_id) {
        this.log_type_id = log_type_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(String updated_ts) {
        this.updated_ts = updated_ts;
    }
}
