package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class MarketStatusModel {
    private String key;
    private String label;
    private boolean select;

    public MarketStatusModel(String key, String label, boolean select) {
        this.key = key;
        this.label = label;
        this.select = select;
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

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
