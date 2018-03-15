package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamObject {

    private String name;
    private int id;
    private int color;
    private String teamLetter;

    public TeamObject(String name, int id, int color) {
        this.name = name;
        this.id = id;
        this.color = color;
        this.teamLetter = name.charAt(0) + "";
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
}
