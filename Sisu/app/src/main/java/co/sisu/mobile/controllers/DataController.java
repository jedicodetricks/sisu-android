package co.sisu.mobile.controllers;

import android.media.Image;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.DataStore;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.MorePageContainer;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DataController {
    private List<Metric> metrics = new ArrayList<>();
    private List<MorePageContainer> morePage = new ArrayList<>();

    public DataController(){
        initializeData();
    }

    public void initializeData(){
        DataStore ds = DataStore.getInstance();
        initializeMetrics();
        initializeMorePageObject();
        ds.setData(metrics);
    }
    //this is for testing
    public void initializeMetrics(){
        metrics.add(new Metric("Contacts",5, 7, R.drawable.contact_icon));//add each metric section here
        metrics.add(new Metric("Appointments",3, 80,  R.drawable.appointment_icon));
        metrics.add(new Metric("BB Signed",5, 10,  R.drawable.signed_icon));
        metrics.add(new Metric("Listings Taken",70, 70,  R.drawable.listing_icon));
        metrics.add(new Metric("Under Contract",27, 70,  R.drawable.contract_icon));
        metrics.add(new Metric("Closed",17, 70,  R.drawable.closed_icon));
    }

    public void initializeMorePageObject() {
        morePage.add(new MorePageContainer("Teams", "Configure team settings, invites, challenges", R.drawable.contact_icon_active));
        morePage.add(new MorePageContainer("Clients", "Modify your pipeline", R.drawable.sisu_mark));
        morePage.add(new MorePageContainer("My Profile", "Setup", R.drawable.sisu_mark));
        morePage.add(new MorePageContainer("Setup", "Set goals, edit activities, record settings", R.drawable.sisu_mark));
        morePage.add(new MorePageContainer("Settings", "Application settings", R.drawable.sisu_mark));
        morePage.add(new MorePageContainer("Feedback", "Provide Feedback", R.drawable.sisu_mark));
        morePage.add(new MorePageContainer("Logout", "", R.drawable.sisu_mark));
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public List<MorePageContainer> getMorePageContainer() { return morePage; }
}
