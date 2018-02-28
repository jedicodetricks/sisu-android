package co.sisu.mobile.controllers;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.DataStore;
import co.sisu.mobile.models.Metric;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DataController {
    private List<Metric> metrics = new ArrayList<>();

    public DataController(){
        initializeData();
    }

    public void initializeData(){
        DataStore ds = DataStore.getInstance();
        initializeMetrics();
        ds.setData(metrics);
    }
    //this is for testing
    public void initializeMetrics(){
        metrics.add(new Metric("Contacts",5, 7, R.drawable.contact_icon));//add each metric section here
        metrics.add(new Metric("Appointments",3, 80,  R.drawable.appointment_icon));
        metrics.add(new Metric("BB Signed",5, 10,  R.drawable.signed_icon));
        metrics.add(new Metric("Listings Taken",70, 70,  R.drawable.signed_icon));
        metrics.add(new Metric("Under Contract",27, 70,  R.drawable.under_contract_icon));
        metrics.add(new Metric("Closed",17, 70,  R.drawable.closed_icon));
    }

    public List<Metric> getMetrics() {
        return metrics;
    }
}
