package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class ActivitiesCounterModel {
    String activity_type;
    String count;
    String name;

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
}
