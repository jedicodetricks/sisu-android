package co.sisu.mobile.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamObject {

    private String name;
    private int id;
    private int color;
    private String teamLetter;
    private int market_id;
    private String icon;
    private String logo;
    private String role;

    public TeamObject(String name, int id, int color, int market_id, String role) {
        this.name = name;
        this.id = id;
        this.color = color;
        this.teamLetter = name.charAt(0) + "";
        this.market_id = market_id;
        this.role = role;
    }

    public TeamObject(JSONObject currentTeam) {
        // TODO: This object has a ton more in it that I'm not using
        try {
            this.name = currentTeam.getString("name");
            this.teamLetter = name.charAt(0) + "";
            this.id = currentTeam.getInt("team_id");
            this.market_id = currentTeam.getInt("market_id");
            this.role = currentTeam.getString("role");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public String getTeamLetter() {
        return teamLetter;
    }

    public int getMarket_id() {
        return market_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getRole() {
        return role;
    }

}
