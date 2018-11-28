package co.sisu.mobile.models;

import java.util.List;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncLeaderboardJsonObject {
    String date;
    List<LeaderboardObject> leaderboards;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<LeaderboardObject> getLeaderboardObject() {
        return leaderboards;
    }

    public void setLeaderboardObject(List<LeaderboardObject> leaderboardObject) {
        this.leaderboards = leaderboardObject;
    }
}
