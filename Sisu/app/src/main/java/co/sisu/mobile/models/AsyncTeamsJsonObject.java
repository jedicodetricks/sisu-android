package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 4/4/18.
 */

public class AsyncTeamsJsonObject {
    String count;
    String server_time;
    String status;
    String status_code;
    TeamJsonObject[] teams;


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
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

    public TeamJsonObject[] getTeams() {
        return teams;
    }

    public void setTeams(TeamJsonObject[] teams) {
        this.teams = teams;
    }
}
