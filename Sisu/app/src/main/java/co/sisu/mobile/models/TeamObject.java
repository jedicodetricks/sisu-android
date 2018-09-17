package co.sisu.mobile.models;

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

    public TeamObject(String name, int id, int color, int market_id) {
        this.name = name;
        this.id = id;
        this.color = color;
        this.teamLetter = name.charAt(0) + "";
        this.market_id = market_id;
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
}
