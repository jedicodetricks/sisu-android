package co.sisu.mobile.controllers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.ActivitiesCounterModel;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.AsyncClientJsonObject;
import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.DataStore;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.TeamJsonObject;
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
    private List<MorePageContainer> morePage = new ArrayList<>();

    int[] teamColors = {R.color.colorCorporateOrange, R.color.colorMoonBlue, R.color.colorYellow, R.color.colorLightGrey};
    List<TeamObject> teamsObject;
    List<Metric> activitiesObject;
    AgentModel agent;

    List<ClientObject> pipelineList;
    List<ClientObject> signedList;
    List<ClientObject> contractList;
    List<ClientObject> closedList;
    List<ClientObject> archivedList;


    public DataController(Context context){
        this.context = context;
        teamsObject = new ArrayList<>();
        activitiesObject = new ArrayList<>();
        pipelineList = new ArrayList<>();
        signedList = new ArrayList<>();
        contractList = new ArrayList<>();
        closedList = new ArrayList<>();
        archivedList = new ArrayList<>();

        initializeData();
    }

    public void initializeData(){
        DataStore ds = DataStore.getInstance();
        initializeScoreboardMetrics();
        initializeScoreboardMetricsTwo();
        initializeRecordMetricsOne();
        initializeRecordMetricsTwo();
        initializeMorePageObject();
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

    public void setTeamsObject(Context context, Object returnObject) {
        teamsObject = new ArrayList<>();
        AsyncTeamsJsonObject teamsObjects = (AsyncTeamsJsonObject) returnObject;
        TeamJsonObject[] teams = teamsObjects.getTeams();
        int colorCounter = 0;
        for(int i = 0; i < teams.length; i++) {
            teamsObject.add(new TeamObject(teams[i].getName(), Integer.valueOf(teams[i].getTeam_id()), ContextCompat.getColor(context, teamColors[i])));
            if(colorCounter == teamColors.length - 1) {
                colorCounter = 0;
            }
            else {
                colorCounter++;
            }
        }
    }

    public List<TeamObject> getTeamsObject() {
        return teamsObject;
    }

    public List<Metric> getActivitiesObject() {
        return activitiesObject;
    }

    public void setActivitiesObject(Object returnObject) {
        activitiesObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

        for(int i = 0; i < counters.length; i++) {
            switch(counters[i].getName()) {
                case "Contacts":
                case "Appointments":
                case "Buyer Signed":
                case "Open Houses":
                case "Buyer Under Contract":
                case "Buyer Closed":
                    Metric metric = new Metric(counters[i].getName(), Double.valueOf(counters[i].getCount()).intValue(), 9000, 0, R.color.colorCorporateOrange);
                    activitiesObject.add(metric);
                    Log.e("Counter " + counters[i].getName(), String.valueOf(metric.getCurrentNum()));
            }
        }
        Log.e("SCOREBOARD TEST", String.valueOf(activitiesObject.size()));
    }

    public void setClientObject(Object returnObject) {
        AsyncClientJsonObject clientParentObject = (AsyncClientJsonObject) returnObject;
        ClientObject[] clientObject = clientParentObject.getClients();

        for(int i = 0; i < clientObject.length; i++) {
            ClientObject co = clientObject[i];
            if(co.getStatus().equalsIgnoreCase("D")) {
                //Archived List
                archivedList.add(co);
            }
            else if(co.getClosed_dt() != null) {
                //Closed List
                closedList.add(co);
            }
            else if(co.getPaid_dt() != null) {
                //Contract List
                contractList.add(co);
            }
            else if(co.getSigned_dt() != null) {
                //Signed List
                signedList.add(co);
            }
            else {
                //Pipeline List
                pipelineList.add(co);
            }
        }
    }

    public List<ClientObject> getPipelineList() {
        return pipelineList;
    }

    public List<ClientObject> getSignedList() {
        return signedList;
    }

    public List<ClientObject> getContractList() {
        return contractList;
    }

    public List<ClientObject> getClosedList() {
        return closedList;
    }

    public List<ClientObject> getArchivedList() {
        return archivedList;
    }

    public AgentModel getAgent() {
        return agent;
    }

    public void setAgent(AgentModel agent) {
        this.agent = agent;
    }

    public void setAgentGoals(AgentGoalsObject[] agentGoalsObject) {
        this.agent.setAgentGoalsObject(agentGoalsObject);
    }
}


//RICK'S METHOD FOR CREATING CLIENTS LISTS
//if underlineSegment.selectedSegmentIndex == PipelineType.pipeline.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.signed_dt == nil) }
//        clientArray  = clientArray.filter { ($0.uc_dt == nil) }
//        clientArray  = clientArray.filter { ($0.closed_dt == nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.signed.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.signed_dt != nil) }
//        clientArray  = clientArray.filter { ($0.uc_dt == nil) }
//        clientArray  = clientArray.filter { ($0.closed_dt == nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.contract.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.uc_dt != nil) }
//        clientArray  = clientArray.filter { ($0.closed_dt == nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.closed.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "N") }
//        clientArray  = clientArray.filter { ($0.closed_dt != nil) }
//        }
//        if underlineSegment.selectedSegmentIndex == PipelineType.archive.rawValue{
//        clientArray = ClientController.shared.clients
//        clientArray  = clientArray.filter { ($0.status == "D") }
//        }
