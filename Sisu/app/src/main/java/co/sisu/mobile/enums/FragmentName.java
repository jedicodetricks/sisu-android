package co.sisu.mobile.enums;

public enum FragmentName {
    DASHBOARD("ScoreboardTileFragment"),
    CLIENTS("ClientTileFragment"),
    RECORD("RecordFragment"),
    LEADERBOARD("LeaderboardFragment"),
    MORE("MoreFragment");

    public final String label;

    FragmentName(String label) {
        this.label = label;
    }
}
