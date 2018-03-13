package co.sisu.mobile.controllers;

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
    private boolean metricOneCurrent = true;
    private List<Metric> metrics1 = new ArrayList<>();
    private List<Metric> metrics2 = new ArrayList<>();

    private List<MorePageContainer> morePage = new ArrayList<>();

    public DataController(){
        initializeData();
    }

    public void initializeData(){
        DataStore ds = DataStore.getInstance();
        initializeMetricsOne();
        initliazeMetricsTwo();
        initializeMorePageObject();
        ds.setData(metrics1);
    }
    //this is for testing
    public void initializeMetricsOne(){
        metrics1.add(new Metric("Contacts",5, 7, R.drawable.contact_icon, R.color.colorYellow));//add each metric section here
        metrics1.add(new Metric("Appointments",3, 80,  R.drawable.appointment_icon, R.color.colorMoonBlue));
        metrics1.add(new Metric("BB Signed",5, 10,  R.drawable.signed_icon, R.color.colorYellow));
        metrics1.add(new Metric("Listings Taken",70, 70,  R.drawable.listing_icon, R.color.colorCorporateOrange));
        metrics1.add(new Metric("Under Contract",27, 70,  R.drawable.contract_icon, R.color.colorMoonBlue));
        metrics1.add(new Metric("Closed",17, 70,  R.drawable.closed_icon, R.color.colorMoonBlue));
    }

    public void initliazeMetricsTwo() {
        metrics2.add(new Metric("Contacts",7, 7, R.drawable.contact_icon, R.color.colorYellow));//add each metric section here
        metrics2.add(new Metric("Appointments",80, 80,  R.drawable.appointment_icon, R.color.colorMoonBlue));
        metrics2.add(new Metric("BB Signed",10, 10,  R.drawable.signed_icon, R.color.colorYellow));
        metrics2.add(new Metric("Listings Taken",0, 70,  R.drawable.listing_icon, R.color.colorCorporateOrange));
        metrics2.add(new Metric("Under Contract",70, 70,  R.drawable.contract_icon, R.color.colorMoonBlue));
        metrics2.add(new Metric("Closed",70, 70,  R.drawable.closed_icon, R.color.colorMoonBlue));
    }

    public void initializeMorePageObject() {
        morePage.add(new MorePageContainer("Teams", "Configure team settings, invites, challenges", R.drawable.team_icon_active));
        morePage.add(new MorePageContainer("Clients", "Modify your pipeline", R.drawable.clients_icon_active));
        morePage.add(new MorePageContainer("My Profile", "Setup", R.drawable.client_icon_active));
        morePage.add(new MorePageContainer("Setup", "Set goals, edit activities, record settings", R.drawable.setup_icon_active));
        morePage.add(new MorePageContainer("Settings", "Application settings", R.drawable.settings_icon_active));
        morePage.add(new MorePageContainer("Feedback", "Provide Feedback", R.drawable.feedback_icon_active));
        morePage.add(new MorePageContainer("Logout", "", R.drawable.logout_icon_active));
    }

    public List<Metric> getMetrics() {
        return metrics1;
    }

    public List<MorePageContainer> getMorePageContainer() { return morePage; }

    public List<Metric> updateScoreboardTimeline() {

        if(metricOneCurrent) {
            metricOneCurrent = !metricOneCurrent;
            return metrics2;
        }
        else {
            metricOneCurrent = !metricOneCurrent;
            return metrics1;
        }
    }
}
