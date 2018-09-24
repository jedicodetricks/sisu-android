package co.sisu.mobile.utils;

import java.util.Comparator;

import co.sisu.mobile.models.LeaderboardObject;

public class LeaderboardComparator implements Comparator<LeaderboardObject> {

    @Override
    public int compare(LeaderboardObject o1, LeaderboardObject o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
