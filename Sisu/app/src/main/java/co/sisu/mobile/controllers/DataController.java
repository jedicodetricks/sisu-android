package co.sisu.mobile.controllers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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
import co.sisu.mobile.models.SelectedActivities;
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
    private List<Metric> masterActivitiesObject;
    private List<Metric> scoreboardObject;
    private AgentModel agent;
    private List<AgentGoalsObject> updatedGoals;

    private List<ClientObject> pipelineList;
    private List<ClientObject> signedList;
    private List<ClientObject> contractList;
    private List<ClientObject> closedList;
    private List<ClientObject> archivedList;
    private List<Metric> updatedRecords;
    private List<SettingsObject> settings;
    private HashMap<String, SelectedActivities> activitiesSelected;

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
        updatedGoals = new ArrayList<>();
        masterActivitiesObject = new ArrayList<>();
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
        masterActivitiesObject = new ArrayList<>();
        activitiesSelected = new HashMap<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

        Arrays.sort(counters);
        Metric firstAppointment = new Metric("1st Time Appts", "1TAPT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 99);
        Metric closed = new Metric("Closed", "CLSD", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 98);
        Metric contract = new Metric("Under Contract", "UCNTR", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 97);
        Metric showing = new Metric("Listings Taken", "LSTT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 96);

        AgentGoalsObject[] goals = agent.getAgentGoalsObject();

        for(int i = 0; i < counters.length; i++) {
            if(counters[i].getCoalesce() != null) {
               counters[i].setName(counters[i].getCoalesce());
            }

            if(goals != null) {
                for(AgentGoalsObject ago : goals) {
                    if(counters[i].getActivity_type().equals(ago.getGoal_id())) {

                        if(ago.getGoal_id().equals("SCLSD") || ago.getGoal_id().equals("BCLSD")) {
                            closed.setGoalNum(closed.getGoalNum() + Integer.parseInt(ago.getValue()));
                        }
                        else if(ago.getGoal_id().equals("SUNDC") || ago.getGoal_id().equals("BUNDC")) {
                            contract.setGoalNum(contract.getGoalNum() + Integer.parseInt(ago.getValue()));
                        }
                        else if(ago.getGoal_id().equals("SAPPT") || ago.getGoal_id().equals("BAPPT")) {
                            firstAppointment.setGoalNum(firstAppointment.getGoalNum() + Integer.parseInt(ago.getValue()));
                        }
                        else if(ago.getGoal_id().equals("SSGND")) {
                            showing.setGoalNum(Integer.parseInt(ago.getValue()));
                        }
                        else {
                            counters[i].setGoalNum(Integer.parseInt(ago.getValue()));

                        }
                    }
                }
            }


            Metric metric = new Metric(counters[i].getName(), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
//            Log.e("ACTIVITIES", metric.getTitle() + ": " + metric.getCurrentNum());
            setMetricThumbnail(metric);
            switch(counters[i].getActivity_type()) {
                case "CONTA":
                case "BSGND":
                    setupMetricGoals(metric);
                    scoreboardObject.add(metric);
                    break;
                case "SSGND":
                    showing.setCurrentNum(metric.getCurrentNum());
                    setupMetricGoals(showing);
                    break;
                case "BUNDC":
                case "SUNDC":
                    contract.setCurrentNum(contract.getCurrentNum() + metric.getCurrentNum());
                    contract.setGoalNum(contract.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(contract);
                    break;
                case "BCLSD":
                case "SCLSD":
                    closed.setCurrentNum(closed.getCurrentNum() + metric.getCurrentNum());
                    closed.setGoalNum(closed.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(closed);
                    break;
                case "BAPPT":
                case "SAPPT":
                    firstAppointment.setCurrentNum(firstAppointment.getCurrentNum() + metric.getCurrentNum());
                    firstAppointment.setGoalNum(firstAppointment.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(firstAppointment);
                    break;
            }
            masterActivitiesObject.add(metric);

            if(activitiesSelected.containsKey(metric.getType())) {
                SelectedActivities selectedActivities = activitiesSelected.get(metric.getType());
                selectedActivities.setName(metric.getTitle());
                if(selectedActivities.getValue().equals("0")) {
                    continue;
                }
            }
            activitiesObject.add(metric);
        }
//        setupMetricGoals(firstAppointment);
//        setupMetricGoals(closed);
//        setupMetricGoals(contract);
//        setupMetricGoals(showing);
        scoreboardObject.add(firstAppointment);
        scoreboardObject.add(closed);
        scoreboardObject.add(contract);
        scoreboardObject.add(showing);
//        masterActivitiesObject.add(firstAppointment);
//        masterActivitiesObject.add(closed);
//        masterActivitiesObject.add(contract);
//        masterActivitiesObject.add(showing);
//        activitiesObject.add(firstAppointment);
//        activitiesObject.add(closed);
//        activitiesObject.add(contract);
//        activitiesObject.add(showing);

        sortActivitesObjectByWeight();
    }

    private void setupMetricGoals(Metric m) {
        int goalNum = m.getGoalNum();
        m.setDailyGoalNum(goalNum / 30);
        m.setWeeklyGoalNum(goalNum / 4);
        m.setYearlyGoalNum(goalNum * 12);
    }

    private void sortActivitesObjectByWeight() {
        Metric[] array = new Metric[activitiesObject.size()];
        activitiesObject.toArray(array);
        Arrays.sort(array);

        activitiesObject = new ArrayList<Metric>(Arrays.asList(array));
//        for(Metric m : activitiesObject) {
//            Log.e("Metric", m.getTitle() + " : " + m.getWeight());
//        }
    }

    private void setMetricThumbnail(Metric metric) {
        if(metric.getTitle() != null) {

            switch (metric.getType()) {
                case "BSHNG":
                    metric.setThumbnailId(R.drawable.open_house_icon);
                    break;
                case "CONTA":
                    metric.setThumbnailId(R.drawable.contact_icon);
                    break;
                case "SHWNG":
                    metric.setThumbnailId(R.drawable.listing_icon);
                    break;
                case "APPTS":
                    metric.setThumbnailId(R.drawable.appointment_icon);
                    break;
                case "DIALS":
                    metric.setThumbnailId(R.drawable.phone_icon);
                    break;
                case "THANX":
                    metric.setThumbnailId(R.drawable.thankyou_card_icon);
                    break;
                case "OPENH":
                    metric.setThumbnailId(R.drawable.open_house_icon);
                    break;
                case "ADDDB":
                    metric.setThumbnailId(R.drawable.database_icon);
                    break;
                case "REFFR":
                    metric.setThumbnailId(R.drawable.referals_icon);
                    break;
                case "REFFC":
                    metric.setThumbnailId(R.drawable.referals_icon);
                    break;
                case "APPTT":
                    metric.setThumbnailId(R.drawable.appointment_icon);
                    break;
                case "PCMAS":
                    metric.setThumbnailId(R.drawable.cma_icon);
                    break;
                case "EXERS":
                    metric.setThumbnailId(R.drawable.exercise_icon);
                    break;
                case "TEAM2":
                    metric.setThumbnailId(R.drawable.meditate_icon);
                    break;
                case "HOURP":
                    metric.setThumbnailId(R.drawable.clock_icon);
                    break;
                case "BCLSD":
                    metric.setThumbnailId(R.drawable.closed_icon);
                    break;
                case "BSGND":
                    metric.setThumbnailId(R.drawable.signed_icon);
                    break;
                case "BUNDC":
                    metric.setThumbnailId(R.drawable.contract_icon);
                    break;
                case "BAPPT":
                    metric.setThumbnailId(R.drawable.appointment_icon);
                    break;
                case "SCLSD":
                    metric.setThumbnailId(R.drawable.closed_icon);
                    break;
                case "SSGND":
                    metric.setThumbnailId(R.drawable.signed_icon);
                    break;
                case "SUNDC":
                    metric.setThumbnailId(R.drawable.contract_icon);
                    break;
                case "SAPPT":
                    metric.setThumbnailId(R.drawable.appointment_icon);
                    break;
                default:
                    metric.setThumbnailId(R.drawable.leaderboard_icon);
                    break;
            }
        }
    }

    private void removeDecimalsFromAmounts(ClientObject co) {
        if(co.getCommission_amt() != null) {
            String commission = co.getCommission_amt().substring(0, co.getCommission_amt().indexOf("."));
            co.setCommission_amt(commission);
        }

        if(co.getTrans_amt() != null) {
            String trans = co.getTrans_amt().substring(0, co.getTrans_amt().indexOf("."));
            co.setTrans_amt(trans);
        }

        if(co.getGross_commission_amt() != null) {
            String gci = co.getGross_commission_amt().substring(0, co.getGross_commission_amt().indexOf("."));
            co.setGross_commission_amt(gci);

        }
    }

    public void setClientListObject(Object returnObject) {
        AsyncClientJsonObject clientParentObject = (AsyncClientJsonObject) returnObject;
        ClientObject[] clientObject = clientParentObject.getClients();
        resetClientLists();
        for(int i = 0; i < clientObject.length; i++) {
            ClientObject co = clientObject[i];
            removeDecimalsFromAmounts(co);
            if(co.getStatus().equalsIgnoreCase("D")) {
                //Archived List
                archivedList.add(co);
            }
            else {
                sortIntoList(co);
            }
        }
    }

    private void resetClientLists() {
        pipelineList = new ArrayList<>();
        signedList = new ArrayList<>();
        contractList = new ArrayList<>();
        closedList = new ArrayList<>();
        archivedList = new ArrayList<>();
    }

    private void sortIntoList(ClientObject co) {
        boolean isClosed = false, isContract = false, isSigned = false;
        Date date = null;
        Calendar currentTime = Calendar.getInstance();
        Calendar updatedTime = Calendar.getInstance();
        if(co.getClosed_dt() != null) {
            //Closed List
            date = getFormattedDateFromApiReturn(co.getClosed_dt());
            updatedTime.setTime(date);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                isClosed = true;
            }
        }
        if(co.getUc_dt() != null) {
            //Contract List
            date = getFormattedDateFromApiReturn(co.getUc_dt());
            updatedTime.setTime(date);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                isContract = true;
            }
        }
        if(co.getSigned_dt() != null) {
            //Signed List
            date = getFormattedDateFromApiReturn(co.getSigned_dt());
            updatedTime.setTime(date);
            if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                isSigned = true;
            }
        }

        if(isClosed) {
            closedList.add(co);
        }
        else if(isContract) {
            contractList.add(co);
        }
        else if(isSigned) {
            signedList.add(co);
        }
        else {
            //Pipeline List
            pipelineList.add(co);
        }
    }

//    private void getTime(Date d, Calendar updatedTime, TextView displayView) {
//        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
//
//        try {
//            d = sdf.parse(displayView.getText().toString());
//            updatedTime.setTime(d);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    private Date getFormattedDateFromApiReturn(String dateString) {
        dateString = dateString.replace("00:00:00 GMT", "");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        Date d = null;
        try {
            d = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
//        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        return calendar.getTime();
    }

//    public void setSelectedClientObject(Object returnObject) {
//        //AsyncClientJsonObject clientObject = (AsyncClientJsonObject) returnObject;
//        //do some type of client update api call here and possibly add to a list like above
//    }

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
                    s.setValue(TimeZone.getDefault().getID().toString());
                case "daily_reminder_time":
                case "lights":
                case "biometrics":
                case "daily_reminder":
                    relevantSettings.add(s);
                    break;
                case "record_activities":
                    setupSelectedActivities(s);
            }
        }
        this.settings = relevantSettings;
    }

    private void setupSelectedActivities(SettingsObject s) {
        activitiesSelected = new HashMap<>();
        if(s != null) {
            String formattedString = s.getValue().replace("\"", "").replace("{", "").replace("}", "");
            String[] splitString = formattedString.split(",");

            //TODO: This needs to check if the value of the parameter is "" (It should only ever be that the first time)
            for(String setting : splitString) {
                String[] splitSetting = setting.split(":");
                activitiesSelected.put(splitSetting[0], new SelectedActivities(splitSetting[1], splitSetting[0]));
            }

            if(masterActivitiesObject.size() > 0) {
                for(Metric m : masterActivitiesObject) {
                    if(activitiesSelected.containsKey(m.getType())) {
                        SelectedActivities selectedActivities = activitiesSelected.get(m.getType());
                        selectedActivities.setName(m.getTitle());
                        if(selectedActivities.getValue().equals("0")) {
                            continue;
                        }
                    }
                }
            }
        }

    }

    public List<SettingsObject> getSettings() {
        return settings;
    }

    public void clearUpdatedRecords() {
        updatedRecords = new ArrayList<>();
    }

    public void setSpecificGoal(AgentGoalsObject selectedGoal, int value) {
        selectedGoal.setValue(String.valueOf(value));
        updatedGoals.add(selectedGoal);
    }

    public HashMap<String, SelectedActivities> getActivitiesSelected() {
        return activitiesSelected;
    }

    public void setActivitiesSelected(SettingsObject activitiesSelected) {
        setupSelectedActivities(activitiesSelected);
    }

    public void setActivityGoals() {
        AgentGoalsObject[] goals = agent.getAgentGoalsObject();

        for(Metric metric : masterActivitiesObject) {
            for(AgentGoalsObject ago : goals) {
                Log.e("METRIC", metric.getTitle());
                Log.e("GOAL", ago.getName());
                if(metric.getTitle().equals(ago.getName())) {
                    Log.e("GOAL MATCHES", metric.getTitle());
                }
            }
        }
    }
}
