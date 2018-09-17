package co.sisu.mobile.models;

import java.util.Date;

/**
 * Created by Brady Groharing on 4/17/2018.
 */

public class UpdateActivitiesModel {

    String date;
    String activity_type;
    int count;
    int agent_id;
    int weight;

    public UpdateActivitiesModel(String date, String activity_type, int count, int agent_id) {
        this.date = date;
        this.activity_type = activity_type;
        this.count = count;
        this.agent_id = agent_id;
    }

    public UpdateActivitiesModel(String date, String activity_type, int count, int agent_id, int weight) {
        this.date = date;
        this.activity_type = activity_type;
        this.count = count;
        this.agent_id = agent_id;
        this.weight = weight;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(int agent_id) {
        this.agent_id = agent_id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
