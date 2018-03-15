package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 3/15/2018.
 */

public class ClientObject {

    private String name;
    private String price;

    public ClientObject(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
