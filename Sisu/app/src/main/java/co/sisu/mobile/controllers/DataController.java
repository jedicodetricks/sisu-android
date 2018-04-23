package co.sisu.mobile.controllers;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.ActivitiesCounterModel;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.AsyncClientJsonObject;
import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.SettingsObject;
import co.sisu.mobile.models.TeamJsonObject;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DataController {
    private List<MorePageContainer> morePage = new ArrayList<>();

    private int[] teamColors = {R.color.colorCorporateOrange, R.color.colorMoonBlue, R.color.colorYellow, R.color.colorLightGrey};
    private List<TeamObject> teamsObject;
    private List<Metric> activitiesObject;
    private List<Metric> scoreboardObject;
    private AgentModel agent;

    private List<ClientObject> pipelineList;
    private List<ClientObject> signedList;
    private List<ClientObject> contractList;
    private List<ClientObject> closedList;
    private List<ClientObject> archivedList;
    private List<Metric> updatedRecords;
    private List<SettingsObject> settings;


    public DataController(){
        teamsObject = new ArrayList<>();
        activitiesObject = new ArrayList<>();
        scoreboardObject = new ArrayList<>();
        pipelineList = new ArrayList<>();
        signedList = new ArrayList<>();
        contractList = new ArrayList<>();
        closedList = new ArrayList<>();
        archivedList = new ArrayList<>();
        updatedRecords = new ArrayList<>();
        initializeMorePageObject();
    }

    public void initializeMorePageObject() {
//        morePage.add(new MorePageContainer("Teams", "Configure team settings, invites, challenges", R.drawable.team_icon_active));
        morePage.add(new MorePageContainer("Clients", "Modify your pipeline", R.drawable.clients_icon_active));
        morePage.add(new MorePageContainer("My Profile", "Setup", R.drawable.client_icon_active));
        morePage.add(new MorePageContainer("Goal Setup", "Set goals, edit activities, record settings", R.drawable.setup_icon_active));
        morePage.add(new MorePageContainer("Activity Settings", "Select which activities you want to track", R.drawable.record_icon_active));
        morePage.add(new MorePageContainer("Settings", "Application settings", R.drawable.settings_icon_active));
        morePage.add(new MorePageContainer("Feedback", "Provide Feedback", R.drawable.feedback_icon_active));
        morePage.add(new MorePageContainer("Logout", "", R.drawable.logout_icon_active));
    }

    public List<MorePageContainer> getMorePageContainer() { return morePage; }

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

    public List<Metric> getScoreboardObject() {
        return scoreboardObject;
    }

    public void setScoreboardObject(List<Metric> scoreboardObject) {
        this.scoreboardObject = scoreboardObject;
    }

    public void setActivitiesObject(Object returnObject) {
        activitiesObject = new ArrayList<>();
        scoreboardObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

        Arrays.sort(counters);
        Metric firstAppointment = new Metric("1st Time Appts", "1TAPT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange);

        for(int i = 0; i < counters.length; i++) {
            Metric metric = new Metric(counters[i].getName(), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), 42, 0, R.color.colorCorporateOrange);
//            Log.e("ACTIVITIES", metric.getTitle() + ": " + metric.getCurrentNum());
            setMetricThumbnail(metric);
            activitiesObject.add(metric);
            switch(counters[i].getName()) {
                case "Contacts":
//                case "Appointments":
                case "Buyer Signed":
                case "Open Houses":
                case "Buyer Under Contract":
                case "Buyer Closed":
                    scoreboardObject.add(metric);
                    break;
                case "Buyer Initial Appointments":
                case "Seller Initial Appointments":
                    firstAppointment.setCurrentNum(firstAppointment.getCurrentNum() + metric.getCurrentNum());
                    firstAppointment.setGoalNum(firstAppointment.getGoalNum() + metric.getGoalNum());
                    break;
            }
        }
        scoreboardObject.add(firstAppointment);
    }

    private void setMetricThumbnail(Metric metric) {
        switch (metric.getTitle()) {
            case "Buyer Showings":
                metric.setThumbnailId(R.drawable.open_house_icon);
                break;
            case "Contacts":
                metric.setThumbnailId(R.drawable.contact_icon);
                break;
            case "Listing Showings":
                metric.setThumbnailId(R.drawable.listing_icon);
                break;
            case "Other Appointments":
                metric.setThumbnailId(R.drawable.appointment_icon);
                break;
            case "Number of Dials":
                metric.setThumbnailId(R.drawable.phone_icon);
                break;
            case "Thank You Cards":
                metric.setThumbnailId(R.drawable.thankyou_card_icon);
                break;
            case "Open Houses":
                metric.setThumbnailId(R.drawable.open_house_icon);
                break;
            case "Added to Database":
                metric.setThumbnailId(R.drawable.database_icon);
                break;
            case "Referrals Requested":
                metric.setThumbnailId(R.drawable.referals_icon);
                break;
            case "Referrals Received":
                metric.setThumbnailId(R.drawable.referals_icon);
                break;
            case "Appointments Set":
                metric.setThumbnailId(R.drawable.appointment_icon);
                break;
            case "Prepared CMA":
                metric.setThumbnailId(R.drawable.contract_icon);
                break;
            case "Exercise":
                metric.setThumbnailId(R.drawable.leaderboard_icon);
                break;
            case "Hours Prospected":
                metric.setThumbnailId(R.drawable.clock_icon);
                break;
            case "Buyer Closed":
                metric.setThumbnailId(R.drawable.closed_icon);
                break;
            case "Buyer Signed":
                metric.setThumbnailId(R.drawable.signed_icon);
                break;
            case "Buyer Under Contract":
                metric.setThumbnailId(R.drawable.contract_icon);
                break;
            case "Buyer Initial Appointments":
                metric.setThumbnailId(R.drawable.appointment_icon);
                break;
            case "Seller Closed":
                metric.setThumbnailId(R.drawable.closed_icon);
                break;
            case "Seller Signed":
                metric.setThumbnailId(R.drawable.signed_icon);
                break;
            case "Seller Under Contract":
                metric.setThumbnailId(R.drawable.contract_icon);
                break;
            case "Seller Initial Appointments":
                metric.setThumbnailId(R.drawable.appointment_icon);
                break;
            default:
                metric.setThumbnailId(R.drawable.leaderboard_icon);
                break;
        }
    }

    public void setClientListObject(Object returnObject) {
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

    public void setSelectedClientObject(Object returnObject) {
        //AsyncClientJsonObject clientObject = (AsyncClientJsonObject) returnObject;
        //do some type of client update api call here and possibly add to a list like above
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

    public void setRecordUpdated(Metric recordUpdated) {
        if(!updatedRecords.contains(recordUpdated)) {
            updatedRecords.add(recordUpdated);
        }
    }

    public List<Metric> getUpdatedRecords() {
        return updatedRecords;
    }

    public void setSettings(SettingsObject[] settings) {

        List<SettingsObject> relevantSettings = new ArrayList<>();

        for(SettingsObject s : settings) {
            switch(s.getName()) {
                case "local_timezone":
                case "daily_reminder_time":
                case "lights":
                case "biometrics":
                case "daily_reminder":
                    relevantSettings.add(s);
            }
        }
        this.settings = relevantSettings;
    }

    public List<SettingsObject> getSettings() {
        return settings;
    }

    public void clearUpdatedRecords() {
        updatedRecords = new ArrayList<>();
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
