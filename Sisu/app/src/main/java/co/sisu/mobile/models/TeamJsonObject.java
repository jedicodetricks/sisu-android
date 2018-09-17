package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 4/4/18.
 */

public class TeamJsonObject {
    String created_ts;
    String name;
    String original_team_id;
    String status;
    String team_id;
    String updated_ts;
    String market_id;

    public String getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(String created_ts) {
        this.created_ts = created_ts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginal_team_id() {
        return original_team_id;
    }

    public void setOriginal_team_id(String original_team_id) {
        this.original_team_id = original_team_id;
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

    public String getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(String updated_ts) {
        this.updated_ts = updated_ts;
    }

    public String getMarket_id() {
        return market_id;
    }

    public void setMarket_id(String market_id) {
        this.market_id = market_id;
    }
}
