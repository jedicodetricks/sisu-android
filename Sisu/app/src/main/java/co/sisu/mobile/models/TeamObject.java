package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamObject {

    private String name;
    private int id;

    public TeamObject(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
