package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public class AsyncUpdateAgentGoalsJsonObject {
    UpdateAgentGoalsObject[] goals;

    public UpdateAgentGoalsObject[] getGoals() {
        return goals;
    }

    public void setGoals(UpdateAgentGoalsObject[] goals) {
        this.goals = goals;
    }
}
