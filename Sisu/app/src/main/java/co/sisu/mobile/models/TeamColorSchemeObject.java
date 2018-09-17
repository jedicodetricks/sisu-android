package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 7/31/18.
 */

public class TeamColorSchemeObject {
    private String created_ts;
    private String lighter;
    private String name;
    private String status;
    private String team_id;
    private String theme_data;
    private String theme_id;
    private String updated_ts;


    public TeamColorSchemeObject(String created_ts, String lighter, String name, String status, String team_id, String theme_data, String theme_id, String updated_ts) {
        this.created_ts = created_ts;
        this.lighter = lighter;
        this.name = name;
        this.status = status;
        this.team_id = team_id;
        this.theme_data = theme_data;
        this.theme_id = theme_id;
        this.updated_ts = updated_ts;
    }

    public String getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(String created_ts) {
        this.created_ts = created_ts;
    }

    public String getLighter() {
        return lighter;
    }

    public void setLighter(String lighter) {
        this.lighter = lighter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getTheme_data() {
        return theme_data;
    }

    public void setTheme_data(String theme_data) {
        this.theme_data = theme_data;
    }

    public String getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(String theme_id) {
        this.theme_id = theme_id;
    }

    public String getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(String updated_ts) {
        this.updated_ts = updated_ts;
    }
}
