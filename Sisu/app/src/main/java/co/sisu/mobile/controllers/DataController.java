package co.sisu.mobile.controllers;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.sisu.mobile.R;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.DataStore;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DataController {
    private Context context;
    private boolean metricOneCurrent = true;
    private boolean recordOneCurrent = true;
    private boolean masterOneCurrent = true;
    private List<Metric> scoreboardMetrics = new ArrayList<>();
    private List<Metric> scoreboardMetricsTwo = new ArrayList<>();
    private List<Metric> recordMetricsOne = new ArrayList<>();
    private List<Metric> recordMetricsTwo = new ArrayList<>();
    private List<Metric> masterMetricsOne = new ArrayList<>();
    private List<Metric> masterMetricsTwo = new ArrayList<>();
    private List<TeamObject> teams = new ArrayList<>();
    private List<MorePageContainer> morePage = new ArrayList<>();
    private List<ClientObject> clientObject = new ArrayList<>();

    public DataController(Context context){
        this.context = context;
        initializeData();
    }

    public void initializeData(){
        DataStore ds = DataStore.getInstance();
        initializeScoreboardMetrics();
        initializeScoreboardMetricsTwo();
        initializeRecordMetricsOne();
        initializeRecordMetricsTwo();
        initializeMorePageObject();
        initializeTeamsObject();
        initializeClientsObject();
        initializeMetrics();
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

    public void initializeRecordMetricsOne(){
        recordMetricsOne.add(new Metric("Thank You Cards",1, 80, R.drawable.thankyou_card_icon, R.color.colorMoonBlue));//add each metric section here
        recordMetricsOne.add(new Metric("Open Houses",3, 80,  R.drawable.open_house_icon, R.color.colorMoonBlue));
        recordMetricsOne.add(new Metric("Referrals Received",5, 10,  R.drawable.referals_icon, R.color.colorYellow));
        recordMetricsOne.add(new Metric("Number of Dials",70, 70,  R.drawable.phone_icon, R.color.colorCorporateOrange));
        recordMetricsOne.add(new Metric("Added to Database",27, 70,  R.drawable.database_icon, R.color.colorMoonBlue));
        recordMetricsOne.add(new Metric("Appointments Set",17, 70,  R.drawable.appointment_icon, R.color.colorYellow));
    }

    public void initializeRecordMetricsTwo(){
        recordMetricsTwo.add(new Metric("Thank You Cards",80, 80, R.drawable.thankyou_card_icon, R.color.colorMoonBlue));//add each metric section here
        recordMetricsTwo.add(new Metric("Open Houses",80, 80,  R.drawable.open_house_icon, R.color.colorMoonBlue));
        recordMetricsTwo.add(new Metric("Referrals Received",10, 10,  R.drawable.referals_icon, R.color.colorYellow));
        recordMetricsTwo.add(new Metric("Number of Dials",1, 70,  R.drawable.phone_icon, R.color.colorCorporateOrange));
        recordMetricsTwo.add(new Metric("Added to Database",70, 70,  R.drawable.database_icon, R.color.colorMoonBlue));
        recordMetricsTwo.add(new Metric("Appointments Set",70, 70,  R.drawable.appointment_icon, R.color.colorYellow));
    }

    private void initializeMetrics() {
        masterMetricsOne.addAll(scoreboardMetrics);
        masterMetricsOne.addAll(recordMetricsOne);
        masterMetricsTwo.addAll(scoreboardMetricsTwo);
        masterMetricsTwo.addAll(recordMetricsTwo);
    }

    public void initializeMorePageObject() {
//        morePage.add(new MorePageContainer("Teams", "Configure team settings, invites, challenges", R.drawable.team_icon_active));
        morePage.add(new MorePageContainer("Clients", "Modify your pipeline", R.drawable.clients_icon_active));
        morePage.add(new MorePageContainer("My Profile", "Setup", R.drawable.client_icon_active));
        morePage.add(new MorePageContainer("Setup", "Set goals, edit activities, record settings", R.drawable.setup_icon_active));
        morePage.add(new MorePageContainer("Settings", "Application settings", R.drawable.settings_icon_active));
        morePage.add(new MorePageContainer("Feedback", "Provide Feedback", R.drawable.feedback_icon_active));
        morePage.add(new MorePageContainer("Logout", "", R.drawable.logout_icon_active));
    }

    public void initializeTeamsObject() {
        teams.add(new TeamObject("Utah Life", 666, ContextCompat.getColor(context, R.color.colorCorporateOrange)));
        teams.add(new TeamObject("Century 21", 69, ContextCompat.getColor(context, R.color.colorLightGrey)));
        teams.add(new TeamObject("Sisu Realtor", 420, ContextCompat.getColor(context, R.color.colorMoonBlue)));
    }

    public void initializeClientsObject() {

        Random r = new Random();

        clientObject.add(new ClientObject("Jeremy Renner", String.valueOf(r.nextInt(50000))));
        clientObject.add(new ClientObject("Brenden Urie", String.valueOf(r.nextInt(50000))));
        clientObject.add(new ClientObject("Brandon Flowers", String.valueOf(r.nextInt(50000))));
        clientObject.add(new ClientObject("Pete Wentz", String.valueOf(r.nextInt(50000))));
        clientObject.add(new ClientObject("Brady Groharing", String.valueOf(r.nextInt(50000))));
        clientObject.add(new ClientObject("Jeff Jessop", String.valueOf(r.nextInt(50000))));
        clientObject.add(new ClientObject("Ferb", String.valueOf(r.nextInt(50000))));
        clientObject.add(new ClientObject("Phineas", String.valueOf(r.nextInt(50000))));
    }

    public List<Metric> getMetrics() {
        if(metricOneCurrent) {
            return scoreboardMetrics;
        }
        else {
            return scoreboardMetricsTwo;
        }
    }

    public List<Metric> getReportMetrics() {
        if(recordOneCurrent) {
            return recordMetricsOne;
        }
        else {
            return recordMetricsTwo;
        }
    }

    public List<Metric> getMasterMetrics() {
        if(masterOneCurrent) {
            return masterMetricsOne;
        }
        else {
            return masterMetricsTwo;
        }
    }

    public List<TeamObject> getTeams() {
        return teams;
    }

    public List<MorePageContainer> getMorePageContainer() { return morePage; }

    public List<ClientObject> getClientObject() {
        return clientObject;
    }

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

    public List<Metric> updateRecordMetrics() {

        if(recordOneCurrent) {
            recordOneCurrent = !recordOneCurrent;
            return recordMetricsTwo;
        }
        else {
            recordOneCurrent = !recordOneCurrent;
            return recordMetricsOne;
        }
    }

    public List<Metric> updateMasterMetrics() {

        if(masterOneCurrent) {
            masterOneCurrent = !masterOneCurrent;
            return masterMetricsTwo;
        }
        else {
            masterOneCurrent = !masterOneCurrent;
            return masterMetricsOne;
        }
    }

}
