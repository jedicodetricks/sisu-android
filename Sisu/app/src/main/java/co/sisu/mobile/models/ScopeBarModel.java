package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class ScopeBarModel {
    private String name;
    private String idValue;

    public ScopeBarModel(String name, String idValue) {
        this.name = name;
        this.idValue = idValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }
}
