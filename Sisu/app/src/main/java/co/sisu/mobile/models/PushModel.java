package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 10/15/2018.
 */

public class PushModel {
    private String body;
    private String created_ts;
    private String current_attempt;
    private String device_id;
    private String external_id;
    private String max_retries;
    private String push_id;
    private String send_time;
    private String status;
    private String timezone;
    private String title;
    private String updated_ts;
    private String user_data;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(String created_ts) {
        this.created_ts = created_ts;
    }

    public String getCurrent_attempt() {
        return current_attempt;
    }

    public void setCurrent_attempt(String current_attempt) {
        this.current_attempt = current_attempt;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public String getMax_retries() {
        return max_retries;
    }

    public void setMax_retries(String max_retries) {
        this.max_retries = max_retries;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(String updated_ts) {
        this.updated_ts = updated_ts;
    }

    public String getUser_data() {
        return user_data;
    }

    public void setUser_data(String user_data) {
        this.user_data = user_data;
    }
}
