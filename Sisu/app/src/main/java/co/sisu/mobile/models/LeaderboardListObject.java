package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class LeaderboardListObject {
    private String label, metric, type, key;

    public LeaderboardListObject(String key, String label, String metric, String type) {
        this.key = key;
        this.label = label;
        this.metric = metric;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
