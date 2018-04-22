package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public class UpdateAgentGoalsObject {
    String goal_id;
    String value;

    public UpdateAgentGoalsObject(String goal_id, String value) {
        this.goal_id = goal_id;
        this.value = value;
    }

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
