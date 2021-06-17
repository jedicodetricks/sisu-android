package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AsyncAgentJsonObject {
    AgentModel agent;
    String server_time;
    String status;
    String status_code;

    public AsyncAgentJsonObject(AsyncAgentJsonStringSuperUserObject stringSuperUserObject) {
        this.agent = new AgentModel(stringSuperUserObject.getAgent());
    }

    public AgentModel getAgent() {
        return agent;
    }

    public void setAgent(AgentModel agent) {
        this.agent = agent;
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
