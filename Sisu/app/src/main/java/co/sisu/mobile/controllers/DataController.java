package co.sisu.mobile.controllers;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.ActivitiesCounterModel;
import co.sisu.mobile.models.ActivitySettingsObject;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.SelectedActivities;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DataController {
    private List<MorePageContainer> morePage = new ArrayList<>();

//    private int[] teamColors = {R.color.colorCorporateOrange, R.color.colorMoonBlue, R.color.colorYellow, R.color.colorLightGrey};
    private List<TeamObject> teamsObject;
    private List<Metric> activitiesObject;
    private List<Metric> masterActivitiesObject;
    // TODO: Anything with scoreboardObject is wrong now
    private List<Metric> scoreboardObject;
    private List<Metric> recordObject;

    private AgentModel agent;

    private List<Metric> updatedRecords;
    private List<ParameterObject> settings;
    private LinkedHashMap<String, SelectedActivities> activitiesSelected;
    private HashMap<String, String> labels;

    private String slackInfo;
    private boolean messageCenterVisible = false;
    private ActivitySettingsObject[] activitySettings;
    private JSONArray activitySettingsNew;

    private ClientObject selectedClient;
    private TeamObject selectedTeamObject = null;

    public DataController(){
        teamsObject = new ArrayList<>();
        activitiesObject = new ArrayList<>();
        scoreboardObject = new ArrayList<>();
        updatedRecords = new ArrayList<>();
        masterActivitiesObject = new ArrayList<>();
        labels = new HashMap<>();
    }

    private void initializeMorePageObject(boolean isAdmin) {
        morePage = new ArrayList<>();
//        morePage.add(new MorePageContainer("Teams", "Configure team settings, invites, challenges", R.drawable.team_icon_active));
//        if(isRecruiting) {
//            morePage.add(new MorePageContainer("Recruits", "Modify your pipeline", R.drawable.clients_icon_active));
//        }
//        else {
//            morePage.add(new MorePageContainer("Clients", "Modify your pipeline", R.drawable.clients_icon_active));
//        }

        morePage.add(new MorePageContainer("My Profile", "Setup", R.drawable.client_icon_active));
        // TODO: Ask why goal setup is gone now?
//        morePage.add(new MorePageContainer("Goal Setup", "Set goals, edit activities, record settings", R.drawable.setup_icon_active));
        if(isAdmin) {
            morePage.add(new MorePageContainer("Activity Settings", "Select which activities you want to track", R.drawable.record_icon_active));
        }
        morePage.add(new MorePageContainer("Settings", "Application settings", R.drawable.settings_icon_active));
        morePage.add(new MorePageContainer("Feedback", "Provide Feedback", R.drawable.feedback_icon_active));
        if(slackInfo != null) {
            morePage.add(new MorePageContainer("Slack", "Send a Slack message", R.drawable.slack_icon));
        }
        if(messageCenterVisible) {
            morePage.add(new MorePageContainer("Message Center", "Review messages", R.drawable.text_message_icon_active));
        }

        if(agent.getIs_superuser() && agent.getFirst_name().equals("Brady")) {
            morePage.add(new MorePageContainer("Sisu Login", "Login as any agent", R.drawable.sisu_mark));
        }
        morePage.add(new MorePageContainer("Logout", "", R.drawable.logout_icon_active));
    }

    public List<MorePageContainer> getMorePageContainer(boolean isAdmin) {
        initializeMorePageObject(isAdmin);
        return morePage;
    }

    public void setTeamsObject(@NonNull JSONArray teamsArray) {
        teamsObject = new ArrayList<>();
        for(int i = 0; i < teamsArray.length(); i++) {
            try {
                JSONObject currentTeam = teamsArray.getJSONObject(i);
                teamsObject.add(new TeamObject(currentTeam));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public List<TeamObject> getTeamsObject() {
        return teamsObject;
    }

    public List<Metric> getActivitiesObject() {
        return activitiesObject;
    }

    public void setRecordActivities(Object returnObject, boolean isRecruiting) {
        recordObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

//        Metric contact = new Metric("Contacts", "CONTA", 0, 0, R.drawable.contact_icon, R.color.colorCorporateOrange, 99);
//        Metric firstAppointment = new Metric("1st Time Appts", "1TAPT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 98);
//        Metric signed = new Metric("Signed", "SGND", 0, 0, R.drawable.signed_icon, R.color.colorCorporateOrange, 97);
//        Metric closed = new Metric("Closed", "CLSD", 0, 0, R.drawable.closed_icon, R.color.colorCorporateOrange, 95);
//        Metric contract = new Metric("Under Contract", "UCNTR", 0, 0, R.drawable.contract_icon, R.color.colorCorporateOrange, 96);


        for (ActivitiesCounterModel activitiesCounterModel : counters) {
//            if (counters[i].getCoalesce() != null) {
//                counters[i].setName(counters[i].getCoalesce());
//            }

            Metric metric = new Metric((activitiesCounterModel.getName()), activitiesCounterModel.getActivity_type(), Double.valueOf(activitiesCounterModel.getCount()).intValue(), activitiesCounterModel.getGoalNum(), 0, R.color.colorCorporateOrange, activitiesCounterModel.getWeight());
            setMetricThumbnail(metric);
            switch (activitiesCounterModel.getActivity_type()) {
//                case "CONTA":
//                    contact.setCurrentNum(contact.getCurrentNum() + metric.getCurrentNum());
//                    contact.setGoalNum(contact.getGoalNum() + metric.getGoalNum());
//                    setupMetricGoals(contact);
//                    break;
//                case "BSGND":
//                case "SSGND":
//                    signed.setCurrentNum(signed.getCurrentNum() + metric.getCurrentNum());
//                    signed.setGoalNum(signed.getGoalNum() + metric.getGoalNum());
//                    setupMetricGoals(signed);
//                    break;
//                case "BUNDC":
//                case "SUNDC":
//                    contract.setCurrentNum(contract.getCurrentNum() + metric.getCurrentNum());
//                    contract.setGoalNum(contract.getGoalNum() + metric.getGoalNum());
//                    setupMetricGoals(contract);
//                    break;
//                case "BCLSD":
//                case "SCLSD":
//                    closed.setCurrentNum(closed.getCurrentNum() + metric.getCurrentNum());
//                    closed.setGoalNum(closed.getGoalNum() + metric.getGoalNum());
//                    setupMetricGoals(closed);
//                    break;
//                case "BAPPT":
//                case "SAPPT":
//                    firstAppointment.setCurrentNum(firstAppointment.getCurrentNum() + metric.getCurrentNum());
//                    firstAppointment.setGoalNum(firstAppointment.getGoalNum() + metric.getGoalNum());
//                    setupMetricGoals(firstAppointment);
//                    break;
            }
        }

        for(String s : activitiesSelected.keySet()) {

        }

        if(!isRecruiting) {
//            recordObject.add(contract);
//            recordObject.add(signed);
        }
//        recordObject.add(contact);
//        recordObject.add(firstAppointment);
//        recordObject.add(closed);
        List<Metric> secondaryRecordObject = new ArrayList<>();
        List<Metric> selectedActivitiesRecordObject = new ArrayList<>();
        List<Metric> formattedSelectedRecordObject = new ArrayList<>();
        for(Metric m : activitiesObject) {
            switch(m.getType()) {
//                case "CONTA":
//                case "BSGND":
//                case "SSGND":
//                case "BUNDC":
//                case "SUNDC":
//                case "BCLSD":
//                case "SCLSD":
//                case "BAPPT":
//                case "SAPPT":
//                    break;
                default:
                    secondaryRecordObject.add(m);
            }
        }

        int counter = activitiesSelected.size();
        for(String s : activitiesSelected.keySet()) {
            SelectedActivities currentActivity = activitiesSelected.get(s);
            selectedActivitiesRecordObject.add(new Metric(currentActivity.getName(), currentActivity.getType(), 0, 0, R.drawable.sisu_mark, 0, counter));
            counter--;
        }

        for(int i = 0; i < secondaryRecordObject.size(); i++) {
            Metric recordMetric = secondaryRecordObject.get(i);
            for(int k = 0; k < selectedActivitiesRecordObject.size(); k++) {
                Metric selectedMetric = selectedActivitiesRecordObject.get(k);
                if(recordMetric.getType().equalsIgnoreCase(selectedMetric.getType())) {
                    Metric tempMetric = new Metric(selectedMetric.getTitle(), selectedMetric.getType(), recordMetric.getCurrentNum(), recordMetric.getGoalNum(), recordMetric.getThumbnailId(), recordMetric.getColor(), selectedMetric.getWeight());
                    recordMetric.setWeight(selectedMetric.getWeight());
                    selectedActivitiesRecordObject.set(k, tempMetric);
//                    formattedSelectedRecordObject.add(tempMetric);
                    break;
                }
            }
        }

        recordObject.addAll(selectedActivitiesRecordObject);

        recordObject = sortActivitiesObjectByWeight(recordObject);
    }

    public List<Metric> getRecordActivities() {
        return recordObject;
    }

    public void setActivitiesObject(Object returnObject, boolean isRecruiting) {
        activitiesObject = new ArrayList<>();
        masterActivitiesObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

//        AgentGoalsObject[] goals = agent.getAgentGoalsObject();

        for (ActivitiesCounterModel counter : counters) {
            if (counter.getCoalesce() != null) {
                counter.setName(counter.getCoalesce());
            }

//            if (goals != null) {
//                for (AgentGoalsObject ago : goals) {
//                    if (ago != null) {
//                        if (counter.getActivity_type().equals(ago.getGoal_id())) {
//                            counter.setGoalNum(Double.parseDouble(ago.getValue()));
//                        }
//                    }
//
//                }
//            }


            Metric metric = new Metric((counter.getName()), counter.getActivity_type(), Double.valueOf(counter.getCount()).intValue(), counter.getGoalNum(), 0, R.color.colorCorporateOrange, counter.getWeight());
            setMetricThumbnail(metric);
//            if(!isRecruiting) {
//                switch(counters[i].getActivity_type()) {
//                    case "CONTA":
//                        metric.setWeight(99);
//                        break;
//                    case "BSGND":
//                        metric.setWeight(96);
//                        break;
//                    case "SSGND":
//                        metric.setWeight(95);
//                        break;
//                    case "BUNDC":
//                        metric.setWeight(94);
//                        break;
//                    case "SUNDC":
//                        metric.setWeight(93);
//                        break;
//                    case "BCLSD":
//                        metric.setWeight(92);
//                        break;
//                    case "SCLSD":
//                        metric.setWeight(91);
//                        break;
//                    case "BAPPT":
//                        metric.setWeight(98);
//                        break;
//                    case "SAPPT":
//                        metric.setWeight(97);
//                        break;
//                }
//            }

            masterActivitiesObject.add(metric);

//            if(activitiesSelected.containsKey(metric.getType())) {
//                SelectedActivities selectedActivities = activitiesSelected.get(metric.getType());
//                selectedActivities.setName(metric.getTitle());
//                if(selectedActivities.getValue().equals("0")) {
//                    continue;
//                }
//            }
            if (isRecruiting) {
                boolean toAdd = true;
                switch (counter.getActivity_type()) {
                    case "CONTA":
                        metric.setWeight(99);
                        break;
                    case "BCLSD":
                        metric.setWeight(92);
                        break;
                    case "SCLSD":
                        metric.setWeight(91);
                        break;
                    case "BAPPT":
                        metric.setWeight(96);
                        break;
                    case "SAPPT":
                        metric.setWeight(95);
                        break;
                    case "BUNDC":
                    case "SUNDC":
                    case "BSGND":
                    case "SSGND":
                        toAdd = false;
                        break;
                }
                if (toAdd) {
                    activitiesObject.add(metric);
                }
            } else {
                activitiesObject.add(metric);
            }

        }

        activitiesObject = sortActivitiesObjectByWeight(activitiesObject);
    }

    private void setupMetricGoals(Metric m) {
        double goalNum = m.getGoalNum();
        m.setDailyGoalNum(goalNum / 30);
        m.setDailyGoalNum((double)Math.round(m.getDailyGoalNum() * 1000d) / 1000d);
        m.setWeeklyGoalNum(goalNum / 4);
        m.setWeeklyGoalNum((double)Math.round(m.getWeeklyGoalNum() * 1000d) / 1000d);
        m.setYearlyGoalNum(goalNum * 12);
        m.setYearlyGoalNum((double)Math.round(m.getYearlyGoalNum() * 1000d) / 1000d);
    }

    private List<Metric> sortActivitiesObjectByWeight(List<Metric> activities) {
        Metric[] array = new Metric[activities.size()];
        activities.toArray(array);
        Arrays.sort(array);

        activities = new ArrayList<>(Arrays.asList(array));
        activities = sortActivitiesObjectByActivitySettings(activities);
        return activities;
    }

    private List<Metric> sortActivitiesObjectByActivitySettings(List<Metric> activities) {
        List<Metric> importantList = new ArrayList<>();
        List<Metric> otherList = new ArrayList<>();
        otherList.addAll(activities);

        List<Metric> finalList = new ArrayList<>();
        finalList.addAll(importantList);

        List<String> sortedList = setupCurrentSorting(activitiesSelected);
        for(String s : sortedList) {
            for(Metric m : otherList) {
//                if(m.getWeight() < 80) {
                    if(m.getType().equalsIgnoreCase(s)) {
                        finalList.add(m);
//                        m.setWeight(sortedList.size() - weightCounter);
//                        weightCounter++;
                        break;
                    }
//                }
            }
        }

        return finalList;
    }

    private List<String> setupCurrentSorting(LinkedHashMap<String, SelectedActivities> activitiesSelected) {
        List<String> currentSorting = new ArrayList<>();
        if(activitiesSelected != null) {
            for (String key : activitiesSelected.keySet()) {
                currentSorting.add(key);
            }
        }

        return currentSorting;
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
                    metric.setThumbnailId(R.drawable.home_icon);
                    break;
            }
        }
    }

    public AgentModel getAgent() {
        return agent;
    }

    public void setAgent(AgentModel agent) {
        if(this.agent != null) {
            AgentGoalsObject[] currentGoals = this.agent.getAgentGoalsObject();
            agent.setAgentGoalsObject(currentGoals);
        }
        this.agent = agent;
    }

    public void setAgentGoals(AgentGoalsObject[] agentGoalsObject, boolean isRecruiting) {
        if(agentGoalsObject.length < 9 && !isRecruiting) {
            setDefaultGoalsObject(agentGoalsObject);
        }
        else {
            this.agent.setAgentGoalsObject(agentGoalsObject);
            for(AgentGoalsObject ago : agentGoalsObject) {
                for(int i = 0; i < scoreboardObject.size(); i++) {
                    if(ago.getGoal_id().equals(scoreboardObject.get(i).getType())) {
                        scoreboardObject.get(i).setGoalNum(Double.parseDouble(ago.getValue()));
                        setupMetricGoals(scoreboardObject.get(i));
                        break;
                    }
                }

                for(Metric m : activitiesObject) {
                    if(ago.getGoal_id().equals(m.getType())) {
                        m.setGoalNum(Double.parseDouble(ago.getValue()));
                        setupMetricGoals(m);
                        break;
                    }
                }
                for(Metric m : masterActivitiesObject) {
                    if(ago.getGoal_id().equals(m.getType())) {
                        m.setGoalNum(Double.parseDouble(ago.getValue()));
                        setupMetricGoals(m);
                        break;
                    }
                }
            }
        }
    }

    public void setRecordUpdated(Metric recordUpdated) {
        if(!updatedRecords.contains(recordUpdated)) {
            updatedRecords.add(recordUpdated);
        }
    }

    public List<Metric> getUpdatedRecords() {
        return updatedRecords;
    }

    public void setSettings(List<ParameterObject> settings) {
        List<ParameterObject> relevantSettings = new ArrayList<>();
        for(int i = 0; i < settings.size(); i++) {
            ParameterObject currentParam = settings.get(i);
            switch (currentParam.getName()) {
                case "local_timezone":
                    if (currentParam.getValue().equals("{}")) {
                        currentParam.setValue("America/Denver");
                    }
                    relevantSettings.add(currentParam);
                    break;
                case "daily_reminder_time":
                    if (currentParam.getValue().equals("{}")) {
                        currentParam.setValue("17:00");
                    }
                    relevantSettings.add(currentParam);
                    break;
                case "lights":
                    if (currentParam.getValue().equals("{}")) {
                        currentParam.setValue("0");
                    }
                    relevantSettings.add(currentParam);
                    break;
//                    case "biometrics":
                case "daily_reminder":
                    if (currentParam.getValue().equals("{}")) {
                        currentParam.setValue("1");
                    }
                    relevantSettings.add(currentParam);
                    break;
                case "record_activities":
//                    if(currentParam.getValue().equals("{}")) {
//                        currentParam = setDefaultActivitesSelected();
//                    }
//                    setupSelectedActivities(currentParam);
                    break;
            }
        }
        this.settings = relevantSettings;
    }

    public void setSettings(JSONArray settings) {
        List<String> existingSettings = new ArrayList<>();
        List<ParameterObject> newSettings = new ArrayList<>();

        for(int i = 0; i < settings.length(); i++) {
            JSONObject s;
            try {
                s = settings.getJSONObject(i);
                switch (s.getString("name")) {
                    case "local_timezone":
                        existingSettings.add("local_timezone");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "daily_reminder_time":
                        existingSettings.add("daily_reminder_time");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "lights":
                        existingSettings.add("lights");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "daily_reminder":
                        existingSettings.add("daily_reminder");
                        newSettings.add(new ParameterObject(s));
                        break;
                    case "record_activities":
                        existingSettings.add("record_activities");
                        newSettings.add(new ParameterObject(s));
                        break;
                    default:
                        newSettings.add(new ParameterObject(s));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if(!existingSettings.contains("local_timezone")) {
            newSettings.add(getDefaultLocalTimezone());
        }

        if(!existingSettings.contains("daily_reminder_time")) {
            newSettings.add(getDefaultDailyReminderTime());
        }

        if(!existingSettings.contains("daily_reminder")) {
            newSettings.add(getDefaultDailyReminder());
        }

        if(!existingSettings.contains("record_activities")) {
            newSettings.add(getDefaultRecordActivities());
        }

        if(!existingSettings.contains("lights")) {
            newSettings.add(getDefaultLights());
         }

        this.settings = newSettings;
    }

    private LinkedHashMap<String, SelectedActivities> setupSelectedActivities(ActivitySettingsObject[] s) {
        activitiesSelected = new LinkedHashMap<>();

        if(s != null) {
            for(ActivitySettingsObject setting : s) {
                if(setting.getValue()) {
                    activitiesSelected.put(setting.getActivity_type(), new SelectedActivities(setting.getValue(), setting.getActivity_type(), setting.getName()));
                }
            }
        }

        return activitiesSelected;
    }

    public List<ParameterObject> getSettings() {
        return settings;
    }

    public void clearUpdatedRecords() {
        updatedRecords = new ArrayList<>();
    }

    public void setActivitiesSelected(ActivitySettingsObject[] activitySettings) {
        this.activitySettings = activitySettings;
        this.activitiesSelected = setupSelectedActivities(activitySettings);
//        addActivitySettingsToRecordActivities();
    }

    public void setActivitiesSelected(JSONArray activitySettingsObject) {
        List<ActivitySettingsObject> activitySettingsObjectList = new ArrayList<>();
        for(int i = 0; i < activitySettingsObject.length(); i++) {
            try {
                JSONObject currentActivitySetting = activitySettingsObject.getJSONObject(i);
                activitySettingsObjectList.add(new ActivitySettingsObject((currentActivitySetting)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ActivitySettingsObject[] activitySettingsArray = new ActivitySettingsObject[activitySettingsObjectList.size()];
        activitySettingsObjectList.toArray(activitySettingsArray);
        this.activitySettings = activitySettingsArray;
        this.activitiesSelected = setupSelectedActivities(activitySettings);
//        addActivitySettingsToRecordActivities();
    }

    public void setActivitySettingsNew(JSONArray activitySettings) {
        activitySettingsNew = activitySettings;
    }

    public JSONArray getActivitySettingsNew() {
        return activitySettingsNew;
    }

    private void setDefaultGoalsObject(AgentGoalsObject[] agentGoalsObject) {
        //TODO: This doesn't work with agent goals v2
        // TODO: I think this is deprecated. Leaving a breakpoint to see if it ever shows up
        List<String> addedGoals = new ArrayList<>();
        AgentGoalsObject[] updatedAgentGoalsObject = new AgentGoalsObject[9];

        for (AgentGoalsObject ago : agentGoalsObject) {
            switch (ago.getGoal_id()) {
                case "CONTA":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[0] = ago;
                    break;
                case "BAPPT":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[1] = ago;
                    break;
                case "SAPPT":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[2] = ago;
                    break;
                case "BSGND":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[3] = ago;
                    break;
                case "SSGND":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[4] = ago;
                    break;
                case "BUNDC":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[5] = ago;
                    break;
                case "SUNDC":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[6] = ago;
                    break;
                case "BCLSD":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[7] = ago;
                    break;
                case "SCLSD":
                    addedGoals.add(ago.getGoal_id());
                    updatedAgentGoalsObject[8] = ago;
                    break;
            }
        }
//        if(!addedGoals.contains("CONTA")) {
//            updatedAgentGoalsObject[0] = new AgentGoalsObject(agent.getAgent_id(), "CONTA", "Contacts", "0");
//
//        }
//
//        if(!addedGoals.contains("BAPPT")) {
//            updatedAgentGoalsObject[1] = new AgentGoalsObject(agent.getAgent_id(), "BAPPT", "Buyer Appointments", "0");
//
//        }
//
//        if(!addedGoals.contains("SAPPT")) {
//            updatedAgentGoalsObject[2] = new AgentGoalsObject(agent.getAgent_id(), "SAPPT", "Seller Appointments", "0");
//
//        }
//
//        if(!addedGoals.contains("BSGND")) {
//            updatedAgentGoalsObject[3] = new AgentGoalsObject(agent.getAgent_id(), "BSGND", "Buyers Signed", "0");
//
//        }
//
//        if(!addedGoals.contains("SSGND")) {
//            updatedAgentGoalsObject[4] = new AgentGoalsObject(agent.getAgent_id(), "SSGND", "Sellers Signed", "0");
//
//        }
//
//        if(!addedGoals.contains("BUNDC")) {
//            updatedAgentGoalsObject[5] = new AgentGoalsObject(agent.getAgent_id(), "BUNDC", "Buyers Under Contract", "0");
//
//        }
//
//        if(!addedGoals.contains("SUNDC")) {
//            updatedAgentGoalsObject[6] = new AgentGoalsObject(agent.getAgent_id(), "SUNDC", "Sellers Under Contract", "0");
//
//        }
//
//        if(!addedGoals.contains("BCLSD")) {
//            updatedAgentGoalsObject[7] = new AgentGoalsObject(agent.getAgent_id(), "BCLSD", "Buyers Closed", "0");
//
//        }
//
//        if(!addedGoals.contains("SCLSD")) {
//            updatedAgentGoalsObject[8] = new AgentGoalsObject(agent.getAgent_id(), "SCLSD", "Sellers Closed", "0");
//
//        }

        this.agent.setAgentGoalsObject(updatedAgentGoalsObject);
    }

    private ParameterObject getDefaultLocalTimezone() {
        return new ParameterObject("local_timezone", "N", "America/Denver", "0");
    }

    private ParameterObject getDefaultDailyReminderTime() {
        return new ParameterObject("daily_reminder_time", "N", "17:00", "5");
    }

    private ParameterObject getDefaultDailyReminder() {
        return new ParameterObject("daily_reminder", "N", "1", "3");
    }

    private ParameterObject getDefaultRecordActivities() {
        return new ParameterObject("record_activities", "N", "{\"THANX\":1,\"APPTT\":1,\"SHWNG\":1,\"REFFR\":1,\"REFFC\":1,\"ADDDB\":1,\"5STAR\":1,\"EXERS\":1,\"PCMAS\":1,\"OPENH\":1,\"APPTS\":1,\"HOURP\":1,\"DIALS\":1,\"BSHNG\":1,\"MEDIT\":1}", "7");
    }

    private ParameterObject getDefaultLights() {
        return new ParameterObject("lights", "N", "0", "5");
    }

    public void setAgentIncomeAndReason(AgentModel agentModel) {
        agent.setDesired_income(agentModel.getDesired_income());
        agent.setVision_statement(agentModel.getVision_statement());
    }

    public void setSlackInfo(String slackInfo) {
        this.slackInfo = slackInfo;
    }

    public String getSlackInfo() {
        return slackInfo;
    }

    public String getColorSchemeId() {
        if(settings != null && settings.size() > 0) {
            for (ParameterObject p : settings) {
                if(p.getName().equalsIgnoreCase("lights")) {
                    return p.getValue();
                }
            }
        }
        return "0";
    }

    public void setLabels(HashMap<String, String> labels) {
        if(labels != null) {
            this.labels = labels;
        }
    }

    public String localizeLabel(String toCheck) {
        String value = toCheck;
        if (labels.containsKey(toCheck)) {
            value = labels.get(toCheck);
        }
        return value;
    }

    public Metric getContactsMetric() {
        for (Metric m : scoreboardObject) {
            if (m.getType().equalsIgnoreCase("CONTA")) {
                return m;
            }
        }
        return null;
    }

    public void setMessageCenterVisible(boolean b) {
        messageCenterVisible = b;
    }

    public ActivitySettingsObject getFirstOtherActivity() {
        ActivitySettingsObject firstOtherActivity = null;
        if(activitySettings != null) {
            for(ActivitySettingsObject setting : activitySettings) {
                if(setting.getValue()) {
                    if(firstOtherActivity == null) {
                        firstOtherActivity = setting;
                    }
                    else {
                        if(setting.getDisplay_order() > firstOtherActivity.getDisplay_order()) {
                            firstOtherActivity = setting;
                        }
                    }
                }
            }
        }

        return firstOtherActivity;
    }

    public ClientObject getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClient(ClientObject selectedClient) {
        this.selectedClient = selectedClient;
    }

    public TeamObject getCurrentSelectedTeam() {
        return selectedTeamObject;
    }

    public int getCurrentSelectedTeamId() {
        return selectedTeamObject.getId();
    }

    public int getCurrentSelectedTeamMarketId() {
        return selectedTeamObject.getMarket_id();
    }

    public void setSelectedTeamObject(TeamObject selectedTeamObject) {
        this.selectedTeamObject = selectedTeamObject;
    }

}
