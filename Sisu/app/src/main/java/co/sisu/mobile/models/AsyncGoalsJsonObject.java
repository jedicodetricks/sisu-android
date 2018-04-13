package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 4/13/18.
 */

public class AsyncGoalsJsonObject {
    AgentGoalsObject[] agent_goals;
    String server_time;
    String status;
    String status_goals;
    SystemGoalsObject[] system_goals;
    //There is also a teams goals object but I don't know what that looks like

    public AgentGoalsObject[] getAgent_goals() {
        return agent_goals;
    }

    public void setAgent_goals(AgentGoalsObject[] agent_goals) {
        this.agent_goals = agent_goals;
    }

    public String getServer_time() {
        return server_time;
    }

    public void setServer_time(String server_time) {
        this.server_time = server_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_goals() {
        return status_goals;
    }

    public void setStatus_goals(String status_goals) {
        this.status_goals = status_goals;
    }

    public SystemGoalsObject[] getSystem_goals() {
        return system_goals;
    }

    public void setSystem_goals(SystemGoalsObject[] system_goals) {
        this.system_goals = system_goals;
    }

    public AgentGoalsObject[] getGoalsObjects() {
        return agent_goals;
    }

    public void setGoalsObjects(AgentGoalsObject[] agentGoalsObjects) {
        this.agent_goals = agentGoalsObjects;
    }
}
