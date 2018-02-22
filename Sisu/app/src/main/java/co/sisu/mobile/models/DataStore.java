package co.sisu.mobile.models;

import java.util.List;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DataStore {
    private List<Metric> data;
    private static DataStore ds = null;

    protected DataStore(){

    }

    public static DataStore getInstance(){
        if(ds == null){
            ds = new DataStore();
        }
        return ds;
    }

    public List<Metric> getData(){
        return data;
    }

    public void setData(List<Metric> data){
        this.data = data;
    }

}
