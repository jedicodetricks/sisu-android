package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class ActivitiesCounterModel implements Comparable<ActivitiesCounterModel> {
    String activity_type;
    String count;
    String coalesce;
    String name;
    int records;
    int weight;
    double goalNum;

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCoalesce() {
        return coalesce;
    }

    public void setCoalesce(String coalesce) {
        this.coalesce = coalesce;
    }

    public double getGoalNum() {
        return goalNum;
    }

    public void setGoalNum(double goalNum) {
        this.goalNum = goalNum;
    }

    @Override
    public int compareTo(ActivitiesCounterModel other) {
        if(this.getWeight() > other.getWeight())
            return -1;
        else if (this.getWeight() == other.getWeight())
            return 0 ;
        return 1 ;
    }
}
