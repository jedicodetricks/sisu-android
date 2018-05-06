package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class AgentGoalsObject {
    String agent_id;
    String goal_id;
    String name;
    String value;

    public AgentGoalsObject(String agent_id, String goal_id, String name, String value) {
        this.agent_id = agent_id;
        this.goal_id = goal_id;
        this.name = name;
        this.value = value;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
