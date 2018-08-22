package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 7/31/18.
 */

public class AsyncTeamColorSchemeObject {

    String server_time;
    String status;
    int status_code;
    TeamColorSchemeObject[] theme;

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

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public TeamColorSchemeObject[] getTheme() {
        return theme;
    }

    public void setTheme(TeamColorSchemeObject[] theme) {
        this.theme = theme;
    }
}
