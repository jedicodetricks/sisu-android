package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class AgentGoalsObject {
    private String display_order;
    private String goal_id;
    private String label;
    private String value;
    private String monthly;

    public AgentGoalsObject(String display_order, String goal_id, String label, String value, String monthly) {
        this.display_order = display_order;
        this.goal_id = goal_id;
        this.label = label;
        this.value = value;
        this.monthly = monthly;
    }

    public String getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(String display_order) {
        this.display_order = display_order;
    }

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMonthly() {
        return monthly;
    }

    public void setMonthly(String monthly) {
        this.monthly = monthly;
    }
}
