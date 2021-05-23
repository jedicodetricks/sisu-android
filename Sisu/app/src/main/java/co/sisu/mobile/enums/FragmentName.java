package co.sisu.mobile.enums;

public enum FragmentName {
    DASHBOARD("ScoreboardTileFragment"),
    CLIENTS("ClientTileFragment");

    public final String label;

    FragmentName(String label) {
        this.label = label;
    }
}
