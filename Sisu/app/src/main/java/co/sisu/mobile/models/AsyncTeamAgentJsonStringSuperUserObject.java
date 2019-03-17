package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 * This class only exists because Rick is sending down either a boolean or a byte to indicate is_super_user
 */

public class AsyncTeamAgentJsonStringSuperUserObject {
    AgentModelStringSuperUser[] agents;
    String server_time;
    String status;
    String status_code;

    public AgentModelStringSuperUser[] getAgents() {
        return agents;
    }

    public void setAgents(AgentModelStringSuperUser[] agent) {
        this.agents = agent;
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

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }
}
