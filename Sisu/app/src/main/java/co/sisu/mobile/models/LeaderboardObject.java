package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class LeaderboardObject {
    String activity_type;
    LeaderboardAgentModel[] items;
    String name;
    int color;

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public LeaderboardAgentModel[] getLeaderboardItemsObject() {
        return items;
    }

    public void setLeaderboardItemsObject(LeaderboardAgentModel[] leaderboardAgentModel) {
        this.items = leaderboardAgentModel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
