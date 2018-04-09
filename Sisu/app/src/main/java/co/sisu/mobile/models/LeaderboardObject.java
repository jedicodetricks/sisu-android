package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class LeaderboardObject {
    String activity_type;
    LeaderboardItemsObject[] items;

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public LeaderboardItemsObject[] getLeaderboardItemsObject() {
        return items;
    }

    public void setLeaderboardItemsObject(LeaderboardItemsObject[] leaderboardItemsObject) {
        this.items = leaderboardItemsObject;
    }
}
