package co.sisu.mobile.controllers;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.DataStore;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DataController {
    private boolean metricOneCurrent = true;
    private List<Metric> scoreboardMetrics = new ArrayList<>();
    private List<Metric> scoreboardMetricsTwo = new ArrayList<>();
    private List<Metric> reportMetrics = new ArrayList<>();
    private List<TeamObject> teams = new ArrayList<>();
    private List<MorePageContainer> morePage = new ArrayList<>();

    public DataController(){
        initializeData();
    }

    public void initializeData(){
        DataStore ds = DataStore.getInstance();
        initializeScoreboardMetrics();
        initializeScoreboardMetricsTwo();
        initializeReportMetrics();
        initializeMorePageObject();
        initializeTeamsObject();
        ds.setData(scoreboardMetrics);
    }
    //this is for testing
    public void initializeScoreboardMetrics(){
        scoreboardMetrics.add(new Metric("Contacts",5, 7, R.drawable.contact_icon, R.color.colorYellow));//add each metric section here
        scoreboardMetrics.add(new Metric("Appointments",3, 80,  R.drawable.appointment_icon, R.color.colorMoonBlue));
        scoreboardMetrics.add(new Metric("BB Signed",5, 10,  R.drawable.signed_icon, R.color.colorYellow));
        scoreboardMetrics.add(new Metric("Listings Taken",70, 70,  R.drawable.listing_icon, R.color.colorCorporateOrange));
        scoreboardMetrics.add(new Metric("Under Contract",27, 70,  R.drawable.contract_icon, R.color.colorMoonBlue));
        scoreboardMetrics.add(new Metric("Closed",17, 70,  R.drawable.closed_icon, R.color.colorMoonBlue));
    }

    public void initializeScoreboardMetricsTwo() {
        scoreboardMetricsTwo.add(new Metric("Contacts",7, 7, R.drawable.contact_icon, R.color.colorYellow));//add each metric section here
        scoreboardMetricsTwo.add(new Metric("Appointments",80, 80,  R.drawable.appointment_icon, R.color.colorMoonBlue));
        scoreboardMetricsTwo.add(new Metric("BB Signed",10, 10,  R.drawable.signed_icon, R.color.colorYellow));
        scoreboardMetricsTwo.add(new Metric("Listings Taken",0, 70,  R.drawable.listing_icon, R.color.colorCorporateOrange));
        scoreboardMetricsTwo.add(new Metric("Under Contract",70, 70,  R.drawable.contract_icon, R.color.colorMoonBlue));
        scoreboardMetricsTwo.add(new Metric("Closed",70, 70,  R.drawable.closed_icon, R.color.colorMoonBlue));
    }

    public void initializeReportMetrics(){
        reportMetrics.add(new Metric("Thank You Cards",1, 80, R.drawable.thankyou_card_icon, R.color.colorMoonBlue));//add each metric section here
        reportMetrics.add(new Metric("Open Houses",3, 80,  R.drawable.open_house_icon, R.color.colorMoonBlue));
        reportMetrics.add(new Metric("Referrals Received",5, 10,  R.drawable.referals_icon, R.color.colorYellow));
        reportMetrics.add(new Metric("Number of Dials",70, 70,  R.drawable.phone_icon, R.color.colorCorporateOrange));
        reportMetrics.add(new Metric("Added to Database",27, 70,  R.drawable.database_icon, R.color.colorMoonBlue));
        reportMetrics.add(new Metric("Appointments Set",17, 70,  R.drawable.appointment_icon, R.color.colorYellow));
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

    public void initializeTeamsObject() {
        teams.add(new TeamObject("Utah Life", 666));
        teams.add(new TeamObject("Century 21", 69));
        teams.add(new TeamObject("Sisu Realtor", 420));
    }

    public List<Metric> getMetrics() {
        return scoreboardMetrics;
    }

    public List<Metric> getReportMetrics() {
        return reportMetrics;
    }

    public List<TeamObject> getTeams() {
        return teams;
    }

    public List<MorePageContainer> getMorePageContainer() { return morePage; }

    public List<Metric> updateScoreboardTimeline() {

        if(metricOneCurrent) {
            metricOneCurrent = !metricOneCurrent;
            return scoreboardMetricsTwo;
        }
        else {
            metricOneCurrent = !metricOneCurrent;
            return scoreboardMetrics;
        }
    }
}
