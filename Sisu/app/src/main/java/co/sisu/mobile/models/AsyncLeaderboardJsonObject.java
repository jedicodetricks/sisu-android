package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncLeaderboardJsonObject {
    String date;
    LeaderboardObject[] leaderboards;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LeaderboardObject[] getLeaderboardObject() {
        return leaderboards;
    }

    public void setLeaderboardObject(LeaderboardObject[] leaderboardObject) {
        this.leaderboards = leaderboardObject;
    }
}
