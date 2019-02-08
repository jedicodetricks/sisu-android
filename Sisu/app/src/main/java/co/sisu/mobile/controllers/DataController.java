package co.sisu.mobile.controllers;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.ActivitiesCounterModel;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.AsyncClientJsonObject;
import co.sisu.mobile.models.AsyncTeamsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.SelectedActivities;
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
    private List<Metric> recordObject;

    private AgentModel agent;

    private List<ClientObject> pipelineList;
    private List<ClientObject> signedList;
    private List<ClientObject> contractList;
    private List<ClientObject> closedList;
    private List<ClientObject> archivedList;
    private List<Metric> updatedRecords;
    private List<ParameterObject> settings;
    private LinkedHashMap<String, SelectedActivities> activitiesSelected;
    private HashMap<String, String> labels;

    private String slackInfo;
    private boolean messageCenterVisible = false;
    private AsyncActivitySettingsObject[] activitySettings;

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
        masterActivitiesObject = new ArrayList<>();
        labels = new HashMap<>();
    }

    private void initializeMorePageObject(boolean isRecruiting) {
        morePage = new ArrayList<>();
//        morePage.add(new MorePageContainer("Teams", "Configure team settings, invites, challenges", R.drawable.team_icon_active));
        if(isRecruiting) {
            morePage.add(new MorePageContainer("Recruits", "Modify your pipeline", R.drawable.clients_icon_active));
        }
        else {
            morePage.add(new MorePageContainer("Clients", "Modify your pipeline", R.drawable.clients_icon_active));
        }
        morePage.add(new MorePageContainer("My Profile", "Setup", R.drawable.client_icon_active));
        morePage.add(new MorePageContainer("Goal Setup", "Set goals, edit activities, record settings", R.drawable.setup_icon_active));
        morePage.add(new MorePageContainer("Activity Settings", "Select which activities you want to track", R.drawable.record_icon_active));
        morePage.add(new MorePageContainer("Settings", "Application settings", R.drawable.settings_icon_active));
        morePage.add(new MorePageContainer("Feedback", "Provide Feedback", R.drawable.feedback_icon_active));
        if(slackInfo != null) {
            morePage.add(new MorePageContainer("Slack", "Send a Slack message", R.drawable.slack_icon));
        }
        if(messageCenterVisible) {
            morePage.add(new MorePageContainer("Message Center", "Review messages", R.drawable.text_message_icon_active));
        }
        morePage.add(new MorePageContainer("Logout", "", R.drawable.logout_icon_active));
    }

    public List<MorePageContainer> getMorePageContainer(boolean isRecruiting) {
        initializeMorePageObject(isRecruiting);
        return morePage;
    }

    public void setTeamsObject(Context context, Object returnObject) {
        teamsObject = new ArrayList<>();
        AsyncTeamsJsonObject teamsObjects = (AsyncTeamsJsonObject) returnObject;
        TeamJsonObject[] teams = teamsObjects.getTeams();
        int colorCounter = 0;
        for(int i = 0; i < teams.length; i++) {
            teamsObject.add(new TeamObject(teams[i].getName(), Integer.valueOf(teams[i].getTeam_id()), ContextCompat.getColor(context, teamColors[colorCounter]), Integer.valueOf(teams[i].getMarket_id()), teams[i].getRole()));
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

    public void setRecordActivities(Object returnObject, boolean isRecruiting) {
        recordObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

        Metric contact = new Metric("Contacts", "CONTA", 0, 0, R.drawable.contact_icon, R.color.colorCorporateOrange, 99);
        Metric firstAppointment = new Metric("1st Time Appts", "1TAPT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 98);
        Metric signed = new Metric("Signed", "SGND", 0, 0, R.drawable.signed_icon, R.color.colorCorporateOrange, 97);
        Metric closed = new Metric("Closed", "CLSD", 0, 0, R.drawable.closed_icon, R.color.colorCorporateOrange, 95);

        Metric contract = new Metric("Under Contract", "UCNTR", 0, 0, R.drawable.contract_icon, R.color.colorCorporateOrange, 96);

        AgentGoalsObject[] goals = agent.getAgentGoalsObject();

        for(int i = 0; i < counters.length; i++) {
            if (counters[i].getCoalesce() != null) {
                counters[i].setName(counters[i].getCoalesce());
            }

            if (goals != null) {
                for (AgentGoalsObject ago : goals) {
                    if(isRecruiting) {
                        //TODO: Recruiting shit here
                        if(ago != null) {
                            if (counters[i].getActivity_type().equals(ago.getGoal_id())) {

                                if (ago.getGoal_id().equals("SUNDC") || ago.getGoal_id().equals("BUNDC")) {
                                    contract.setGoalNum(contract.getGoalNum() + Double.parseDouble(ago.getValue()));
                                }
                                else if (ago.getGoal_id().equals("SAPPT") || ago.getGoal_id().equals("BAPPT")) {
                                    firstAppointment.setGoalNum(firstAppointment.getGoalNum() + Double.parseDouble(ago.getValue()));
                                }
                                else if(ago.getGoal_id().equals("SSGND") || ago.getGoal_id().equals("BSGND")) {
                                    signed.setGoalNum(signed.getGoalNum() + Double.parseDouble(ago.getValue()));
                                }
                                else {
                                    counters[i].setGoalNum(Double.parseDouble(ago.getValue()));
                                }
                            }
                        }
                    }
                    else {
                        if (counters[i].getActivity_type().equals(ago.getGoal_id())) {

                            if (ago.getGoal_id().equals("SCLSD") || ago.getGoal_id().equals("BCLSD")) {
                                closed.setGoalNum(closed.getGoalNum() + Double.parseDouble(ago.getValue()));
                            }
                            else if (ago.getGoal_id().equals("SUNDC") || ago.getGoal_id().equals("BUNDC")) {
                                contract.setGoalNum(contract.getGoalNum() + Double.parseDouble(ago.getValue()));
                            }
                            else if (ago.getGoal_id().equals("SAPPT") || ago.getGoal_id().equals("BAPPT")) {
                                firstAppointment.setGoalNum(firstAppointment.getGoalNum() + Double.parseDouble(ago.getValue()));
                            }
                            else if(ago.getGoal_id().equals("SSGND") || ago.getGoal_id().equals("BSGND")) {
                                signed.setGoalNum(signed.getGoalNum() + Double.parseDouble(ago.getValue()));
                            }
                            else {
                                counters[i].setGoalNum(Double.parseDouble(ago.getValue()));
                            }
                        }
                    }
                }
            }

//            Metric metric = new Metric(localizeLabel(counters[i].getName()), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
            Metric metric = new Metric((counters[i].getName()), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
            setMetricThumbnail(metric);
            switch (counters[i].getActivity_type()) {
                case "CONTA":
                    contact.setCurrentNum(contact.getCurrentNum() + metric.getCurrentNum());
                    contact.setGoalNum(contact.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(contact);
                    break;
                case "BSGND":
                case "SSGND":
                    signed.setCurrentNum(signed.getCurrentNum() + metric.getCurrentNum());
                    signed.setGoalNum(signed.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(signed);
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
        }

        if(!isRecruiting) {
            recordObject.add(contract);
            recordObject.add(signed);
        }
        recordObject.add(contact);
        recordObject.add(firstAppointment);
        recordObject.add(closed);
        for(Metric m : activitiesObject) {
            switch(m.getType()) {
                case "CONTA":
                case "BSGND":
                case "SSGND":
                case "BUNDC":
                case "SUNDC":
                case "BCLSD":
                case "SCLSD":
                case "BAPPT":
                case "SAPPT":
                    break;
                    default:
                recordObject.add(m);
            }
        }
        recordObject = sortActivitiesObjectByWeight(recordObject);
    }

    public List<Metric> getRecordActivities() {
        return recordObject;
    }

    public void setRecruitingScoreboardActivities(Object returnObject) {{
        scoreboardObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

        Metric firstAppointment = new Metric("1st Time Appts", "1TAPT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric closed = new Metric("Closed", "CLSD", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric contract = new Metric("Under Contract", "UCNTR", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric showing = new Metric("Listings Taken", "LSTT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric signed = new Metric("Buyers Signed", "BBSGD", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric contact = new Metric("Contacts", "CONTA", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);


        AgentGoalsObject[] goals = agent.getAgentGoalsObject();
        boolean contactEdited = false;
        boolean signedEdited = false;
        boolean showingEdited = false;
        boolean contractEdited = false;
        boolean closedEdited = false;
        boolean firstAppointmentEdited = false;

        for(int i = 0; i < counters.length; i++) {
            if (counters[i].getCoalesce() != null) {
                counters[i].setName(counters[i].getCoalesce());
            }

            if (goals != null) {
                for (AgentGoalsObject ago : goals) {
                        //TODO: Recruiting shit here
                    if(ago != null) {
                        if (counters[i].getActivity_type().equals(ago.getGoal_id())) {

                            if (ago.getGoal_id().equals("SUNDC") || ago.getGoal_id().equals("BUNDC")) {
                                contract.setGoalNum(contract.getGoalNum() + Double.parseDouble(ago.getValue()));
                            } else if (ago.getGoal_id().equals("SAPPT") || ago.getGoal_id().equals("BAPPT")) {
                                firstAppointment.setGoalNum(firstAppointment.getGoalNum() + Double.parseDouble(ago.getValue()));
                            } else if (ago.getGoal_id().equals("SSGND")) {
                                showing.setGoalNum(Double.parseDouble(ago.getValue()));
                            } else {
                                counters[i].setGoalNum(Double.parseDouble(ago.getValue()));
                            }
                        }
                    }
                }
            }

//            Metric metric = new Metric(localizeLabel(counters[i].getName()), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
            Metric metric = new Metric((counters[i].getName()), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
            setMetricThumbnail(metric);
            switch (counters[i].getActivity_type()) {
                case "CONTA":
                    contact.setCurrentNum(contact.getCurrentNum() + metric.getCurrentNum());
                    contact.setGoalNum(contact.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(contact);
                    contactEdited = true;
                    break;
                case "BSGND":
                    signed.setCurrentNum(signed.getCurrentNum() + metric.getCurrentNum());
                    signed.setGoalNum(signed.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(signed);
                    signedEdited = true;
                    break;
                case "SSGND":
                    showing.setCurrentNum(showing.getCurrentNum() + metric.getCurrentNum());
                    showing.setGoalNum(showing.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(showing);
                    showingEdited = true;
                    break;
                case "BUNDC":
                case "SUNDC":
                    contract.setCurrentNum(contract.getCurrentNum() + metric.getCurrentNum());
                    contract.setGoalNum(contract.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(contract);
                    contactEdited = true;
                    break;
                case "BCLSD":
                case "SCLSD":
                    closed.setCurrentNum(closed.getCurrentNum() + metric.getCurrentNum());
                    closed.setGoalNum(closed.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(closed);
                    closedEdited = true;
                    break;
                case "BAPPT":
                case "SAPPT":
                    firstAppointment.setCurrentNum(firstAppointment.getCurrentNum() + metric.getCurrentNum());
                    firstAppointment.setGoalNum(firstAppointment.getGoalNum() + metric.getGoalNum());
                    setupMetricGoals(firstAppointment);
                    firstAppointmentEdited = true;
                    break;
            }
        }

        //TODO: Right now I just need contact because Rick isn't sending that down correctly
        if(!contactEdited) {
            for(AgentGoalsObject ago : goals) {
                if(ago.getGoal_id().equals("CONTA")) {
                    contact.setCurrentNum(contact.getCurrentNum() + contact.getCurrentNum());
                    contact.setGoalNum(contact.getGoalNum() + contact.getGoalNum());
                    setupMetricGoals(contact);
                    break;
                }
            }
        }

        scoreboardObject.add(firstAppointment);
        scoreboardObject.add(closed);
        scoreboardObject.add(contract);
        scoreboardObject.add(showing);
        scoreboardObject.add(contact);
        scoreboardObject.add(signed);
    }

    }

    public void setScoreboardActivities(Object returnObject, boolean isRecruiting) {
        scoreboardObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

        // Needed with recruiting
        Metric contact = new Metric("Contacts", "CONTA", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric firstAppointment = new Metric("1st Time Appts", "1TAPT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric signed = new Metric("Buyers Signed", "BBSGD", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric showing = new Metric("Listings Taken", "LSTT", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);

        // Not needed in recruiting
        Metric closed = new Metric("Closed", "CLSD", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);
        Metric contract = new Metric("Under Contract", "UCNTR", 0, 0, R.drawable.appointment_icon, R.color.colorCorporateOrange, 0);


        AgentGoalsObject[] goals = agent.getAgentGoalsObject();
        boolean contactEdited = false;

        for(int i = 0; i < counters.length; i++) {
            if (counters[i].getCoalesce() != null) {
                counters[i].setName(counters[i].getCoalesce());
            }

            if (goals != null) {
                for (AgentGoalsObject ago : goals) {
                    if(isRecruiting) {
                        if(ago != null) {
                            if (counters[i].getActivity_type().equals(ago.getGoal_id())) {
                                if(ago.getGoal_id().equalsIgnoreCase("SAPPT")) {
                                    firstAppointment.setGoalNum(firstAppointment.getGoalNum() + Double.parseDouble(ago.getValue()));
                                    signed.setGoalNum(signed.getGoalNum() + Double.parseDouble(ago.getValue()));
                                    showing.setGoalNum(showing.getGoalNum() + Double.parseDouble(ago.getValue()));

                                }
                                else if (ago.getGoal_id().equals("BAPPT")) {
                                    firstAppointment.setGoalNum(firstAppointment.getGoalNum() + Double.parseDouble(ago.getValue()));
                                } else if (ago.getGoal_id().equals("BCLSD")) {
                                    signed.setGoalNum(signed.getGoalNum() + Double.parseDouble(ago.getValue()));
                                } else if (ago.getGoal_id().equals("SCLSD")) {
                                    showing.setGoalNum(showing.getGoalNum() + Double.parseDouble(ago.getValue()));
                                } else {
                                    counters[i].setGoalNum(Double.parseDouble(ago.getValue()));
                                }
                            }
                        }
                    }
                    else {
                        if (counters[i].getActivity_type().equals(ago.getGoal_id())) {
                            if (ago.getGoal_id().equals("SCLSD") || ago.getGoal_id().equals("BCLSD")) {
                                closed.setGoalNum(closed.getGoalNum() + Double.parseDouble(ago.getValue()));
                            } else if (ago.getGoal_id().equals("SUNDC") || ago.getGoal_id().equals("BUNDC")) {
                                contract.setGoalNum(contract.getGoalNum() + Double.parseDouble(ago.getValue()));
                            } else if (ago.getGoal_id().equals("SAPPT") || ago.getGoal_id().equals("BAPPT")) {
                                firstAppointment.setGoalNum(firstAppointment.getGoalNum() + Double.parseDouble(ago.getValue()));
                            }
                            else if(ago.getGoal_id().equals("SSGND")) {
                                showing.setGoalNum(Double.parseDouble(ago.getValue()));
                            }
                            else {
                                counters[i].setGoalNum(Double.parseDouble(ago.getValue()));
                            }
                        }
                    }
                }
            }

            if(isRecruiting) {
                Metric metric = new Metric((counters[i].getName()), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
                setMetricThumbnail(metric);
                switch (counters[i].getActivity_type()) {
                    case "CONTA":
                        contact.setCurrentNum(contact.getCurrentNum() + metric.getCurrentNum());
                        contact.setGoalNum(contact.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(contact);
                        contactEdited = true;
                        break;
                    case "BCLSD":
                        signed.setCurrentNum(signed.getCurrentNum() + metric.getCurrentNum());
                        signed.setGoalNum(signed.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(signed);
                        break;
                    case "SCLSD":
                        showing.setCurrentNum(showing.getCurrentNum() + metric.getCurrentNum());
                        showing.setGoalNum(showing.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(showing);
                        break;
                    case "BAPPT":
                    case "SAPPT":
                        firstAppointment.setCurrentNum(firstAppointment.getCurrentNum() + metric.getCurrentNum());
                        firstAppointment.setGoalNum(firstAppointment.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(firstAppointment);
                        break;
                }
            }
            else {
                Metric metric = new Metric((counters[i].getName()), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
                setMetricThumbnail(metric);
                switch (counters[i].getActivity_type()) {
                    case "CONTA":
                        contact.setCurrentNum(contact.getCurrentNum() + metric.getCurrentNum());
                        contact.setGoalNum(contact.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(contact);
                        contactEdited = true;
                        break;
                    case "BSGND":
                        signed.setCurrentNum(signed.getCurrentNum() + metric.getCurrentNum());
                        signed.setGoalNum(signed.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(signed);
                        break;
                    case "SSGND":
                        showing.setCurrentNum(showing.getCurrentNum() + metric.getCurrentNum());
                        showing.setGoalNum(showing.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(showing);
                        break;
                    case "BUNDC":
                    case "SUNDC":
                        contract.setCurrentNum(contract.getCurrentNum() + metric.getCurrentNum());
                        contract.setGoalNum(contract.getGoalNum() + metric.getGoalNum());
                        setupMetricGoals(contract);
                        contactEdited = true;
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
            }

        }

        //TODO: Right now I just need contact because Rick isn't sending that down correctly
        if(!contactEdited) {
            for(AgentGoalsObject ago : goals) {
                if(ago.getGoal_id().equals("CONTA")) {
                    contact.setCurrentNum(contact.getCurrentNum() + contact.getCurrentNum());
                    contact.setGoalNum(contact.getGoalNum() + contact.getGoalNum());
                    setupMetricGoals(contact);
                    break;
                }
            }
        }

        scoreboardObject.add(firstAppointment);
        scoreboardObject.add(closed);
        scoreboardObject.add(contract);
        scoreboardObject.add(showing);
        scoreboardObject.add(contact);
        scoreboardObject.add(signed);
    }

    public void setActivitiesObject(Object returnObject, boolean isRecruiting) {
        activitiesObject = new ArrayList<>();
        masterActivitiesObject = new ArrayList<>();
        AsyncActivitiesJsonObject activitiesJsonObject = (AsyncActivitiesJsonObject) returnObject;
        ActivitiesCounterModel[] counters = activitiesJsonObject.getCounters();

        AgentGoalsObject[] goals = agent.getAgentGoalsObject();

        for(int i = 0; i < counters.length; i++) {
            if(counters[i].getCoalesce() != null) {
               counters[i].setName(counters[i].getCoalesce());
            }

            if(goals != null) {
                for(AgentGoalsObject ago : goals) {
                    if(ago != null) {
                        if(counters[i].getActivity_type().equals(ago.getGoal_id())) {
                            counters[i].setGoalNum(Double.parseDouble(ago.getValue()));
                        }
                    }

                }
            }


            Metric metric = new Metric((counters[i].getName()), counters[i].getActivity_type(), Double.valueOf(counters[i].getCount()).intValue(), counters[i].getGoalNum(), 0, R.color.colorCorporateOrange, counters[i].getWeight());
            setMetricThumbnail(metric);
            if(!isRecruiting) {
                switch(counters[i].getActivity_type()) {
                    case "CONTA":
                        metric.setWeight(99);
                        break;
                    case "BSGND":
                        metric.setWeight(96);
                        break;
                    case "SSGND":
                        metric.setWeight(95);
                        break;
                    case "BUNDC":
                        metric.setWeight(94);
                        break;
                    case "SUNDC":
                        metric.setWeight(93);
                        break;
                    case "BCLSD":
                        metric.setWeight(92);
                        break;
                    case "SCLSD":
                        metric.setWeight(91);
                        break;
                    case "BAPPT":
                        metric.setWeight(98);
                        break;
                    case "SAPPT":
                        metric.setWeight(97);
                        break;
                }
            }

            masterActivitiesObject.add(metric);

//            if(activitiesSelected.containsKey(metric.getType())) {
//                SelectedActivities selectedActivities = activitiesSelected.get(metric.getType());
//                selectedActivities.setName(metric.getTitle());
//                if(selectedActivities.getValue().equals("0")) {
//                    continue;
//                }
//            }
            if(isRecruiting) {
                boolean toAdd = true;
                switch(counters[i].getActivity_type()) {
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
                if(toAdd) {
                    activitiesObject.add(metric);
                }
            }
            else {
                activitiesObject.add(metric);
            }

        }

        activitiesObject = sortActivitiesObjectByWeight(activitiesObject);
    }

    private void setupMetricGoals(Metric m) {
        double goalNum = m.getGoalNum();
        m.setDailyGoalNum(goalNum / 30);
        m.setWeeklyGoalNum(goalNum / 4);
        m.setYearlyGoalNum(goalNum * 12);
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
        for(Metric m : activities) {
            if(!isSelectableActivity(m.getType())) {
                importantList.add(m);
            }
            else {
                otherList.add(m);
            }
        }

        List<Metric> finalList = new ArrayList<>();
        for(Metric m : importantList) {
            finalList.add(m);
        }

        List<String> sortedList = setupCurrentSorting(activitiesSelected);
        int weightCounter = 0;
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


//
//        for(Metric m : otherList) {
//            finalList.add(m);
//        }

        return finalList;
    }

    private List<String> setupCurrentSorting(LinkedHashMap<String, SelectedActivities> activitiesSelected) {
        List<String> currentSorting = new ArrayList<>();
        for (String key : activitiesSelected.keySet()) {
            currentSorting.add(key);
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

    public void setClientListObject(Object returnObject, boolean isRecruiting) {
        AsyncClientJsonObject clientParentObject = (AsyncClientJsonObject) returnObject;
        ClientObject[] clientObject = clientParentObject.getClients();
        resetClientLists();
        for(ClientObject co : clientObject) {
            removeDecimalsFromAmounts(co);
            if(co.getStatus().equalsIgnoreCase("D")) {
                //Archived List
                archivedList.add(co);
            }
            else {
                sortIntoList(co, isRecruiting);
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

    private void sortIntoList(ClientObject co, boolean isRecruiting) {
        boolean isClosed = false, isContract = false, isSigned = false, closedPrevYear = false;
        Date date;
        Calendar currentTime = Calendar.getInstance();
        Calendar updatedTime = Calendar.getInstance();
        int year = Calendar.getInstance().get(Calendar.YEAR);

        if(isRecruiting) {
            if(co.getClosed_dt() != null) {
                //Closed List
                date = getFormattedDateFromApiReturn(co.getClosed_dt());
                updatedTime.setTime(date);
                if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    isContract = true;
                }
                if(updatedTime.get(Calendar.YEAR) != year) {
                    closedPrevYear = true;
                }
            }
            if(co.getAppt_dt() != null) {
                //Contract List
                date = getFormattedDateFromApiReturn(co.getAppt_dt());
                updatedTime.setTime(date);
                if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    isSigned = true;
                }
            }
//            if(co.getSigned_dt() != null) {
//                //Signed List
//                date = getFormattedDateFromApiReturn(co.getSigned_dt());
//                updatedTime.setTime(date);
//                if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
//                    isSigned = true;
//                }
//            }
        }
        else {
            if(co.getClosed_dt() != null) {
                //Closed List
                date = getFormattedDateFromApiReturn(co.getClosed_dt());
                updatedTime.setTime(date);
                if(updatedTime.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    isClosed = true;
                }
                if(updatedTime.get(Calendar.YEAR) != year) {
                    closedPrevYear = true;
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
        }


        if (closedPrevYear) {
            //We'll do nothing with these
        }
        else if(isClosed) {
            closedList.add(co);
        }
        else if(isContract) {
            contractList.add(co);
            if(isRecruiting) {
                closedList.add(co);
            }
        }
        else if(isSigned) {
            signedList.add(co);
            if(isRecruiting) {
                closedList.add(co);
            }
        }
        else {
            //Pipeline List
            pipelineList.add(co);
            if(isRecruiting) {
                closedList.add(co);
            }
        }
    }

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
        if(this.agent != null) {
            AgentGoalsObject[] currentGoals = this.agent.getAgentGoalsObject();
            agent.setAgentGoalsObject(currentGoals);
        }
        this.agent = agent;
    }

    public void setAgentGoals(AgentGoalsObject[] agentGoalsObject) {
        if(agentGoalsObject.length < 9) {
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

    public void setSettings(ParameterObject[] settings) {
        int arraySize = settings.length;
        List<String> existingSettings = new ArrayList<>();
        List<ParameterObject> newSettings = new ArrayList<>();
        List<ParameterObject> relevantSettings = new ArrayList<>();

        for (ParameterObject s : settings) {
            switch (s.getName()) {
                case "local_timezone":
                    existingSettings.add("local_timezone");
                    newSettings.add(s);
                    break;
                case "daily_reminder_time":
                    existingSettings.add("daily_reminder_time");
                    newSettings.add(s);
                    break;
                case "lights":
                    existingSettings.add("lights");
                    newSettings.add(s);
                    break;
//                    case "biometrics":
                case "daily_reminder":
                    existingSettings.add("daily_reminder");
                    newSettings.add(s);
                    break;
                case "record_activities":
                    existingSettings.add("record_activities");
                    newSettings.add(s);
                    break;
                default:
                    newSettings.add(s);

            }
        }

        if(!existingSettings.contains("local_timezone")) {
            newSettings.add(getDefaultLocalTimezone());
            arraySize++;
        }

        if(!existingSettings.contains("daily_reminder_time")) {
            newSettings.add(getDefaultDailyReminderTime());
            arraySize++;
        }

        if(!existingSettings.contains("daily_reminder")) {
            newSettings.add(getDefaultDailyReminder());
            arraySize++;
        }

        if(!existingSettings.contains("record_activities")) {
            newSettings.add(getDefaultRecordActivities());
            arraySize++;
        }

        if(!existingSettings.contains("lights")) {
            newSettings.add(getDefaultLights());
            arraySize++;
        }

        ParameterObject[] array = new ParameterObject[arraySize];

        settings = newSettings.toArray(array);

        for (ParameterObject s : settings) {
            switch (s.getName()) {
                case "local_timezone":
                    if(s.getValue().equals("{}")) {
                        s.setValue("America/Denver");
                    }
                    relevantSettings.add(s);
                    break;
                case "daily_reminder_time":
                    if(s.getValue().equals("{}")) {
                        s.setValue("17:00");
                    }
                    relevantSettings.add(s);
                    break;
                case "lights":
                    if(s.getValue().equals("{}")) {
                        s.setValue("0");
                    }
                    relevantSettings.add(s);
                    break;
//                    case "biometrics":
                case "daily_reminder":
                    if(s.getValue().equals("{}")) {
                        s.setValue("1");
                    }
                    relevantSettings.add(s);
                    break;
                case "record_activities":
//                    if(s.getValue().equals("{}")) {
//                        s = setDefaultActivitesSelected();
//                    }
//                    setupSelectedActivities(s);
                    break;
            }
        }
        this.settings = relevantSettings;

    }

    private LinkedHashMap<String, SelectedActivities> setupSelectedActivities(AsyncActivitySettingsObject[] s) {
        activitiesSelected = new LinkedHashMap<>();
//        if(s != null) {
//            String formattedString = s.getValue().replace("\"", "").replace("{", "").replace("}", "");
//            String[] splitString = formattedString.split(",");
//
//            if(splitString.length > 1) {
        for(AsyncActivitySettingsObject setting : s) {
            if(setting.getValue() == true) {
                activitiesSelected.put(setting.getActivity_type(), new SelectedActivities(setting.getValue(), setting.getActivity_type(), setting.getName()));
            }
        }
        return activitiesSelected;
//            }
//
//
//            if(masterActivitiesObject.size() > 0) {
//                for(Metric m : masterActivitiesObject) {
//                    if(activitiesSelected.containsKey(m.getType())) {
//                        SelectedActivities selectedActivities = activitiesSelected.get(m.getType());
//                        selectedActivities.setName(m.getTitle());
//                        if(selectedActivities.getValue().equals("0")) {
//                            continue;
//                        }
//                    }
//                    else {
////                        This is to reconcile the object to make sure it's got the new stuff in it.
//                        if (isSelectableActivity(m.getType())) {
//                            Log.e("PUTTING", m.getTitle());
//                            activitiesSelected.put(m.getType(), new SelectedActivities("1", m.getType(), m.getTitle()));
//                        }
//                    }
//                }
//            }
//        }
//
    }

    public boolean isSelectableActivity(String type) {

        switch (type) {
            case "CONTA":
            case "BAPPT":
            case "SAPPT":
            case "BSGND":
            case "SSGND":
            case "BUNDC":
            case "SUNDC":
            case "BCLSD":
            case "SCLSD":
            case "SGND":
            case "1TAPT":
            case "CLSD":
            case "UCNTR":
            case "LSTT":
            case "BBSGD":
                return false;
        }

        return true;
    }

    public List<ParameterObject> getSettings() {
        return settings;
    }

    public void clearUpdatedRecords() {
        updatedRecords = new ArrayList<>();
    }

    public AsyncActivitySettingsObject[] getActivitiesSelected() {
        return activitySettings;
    }

    public void setActivitiesSelected(AsyncActivitySettingsObject[] activitiesSelected) {
        //TODO: Reinstate the defaults
//        if(activitiesSelected == null) {
//            activitiesSelected = setDefaultActivitesSelected();
//        }
        this.activitySettings = activitiesSelected;
        this.activitiesSelected = setupSelectedActivities(activitiesSelected);
    }

    private void setDefaultGoalsObject(AgentGoalsObject[] agentGoalsObject) {
        //TODO: This doesn't work with agent goals v2
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

    private ParameterObject[] setDefaultSettingsObject(ParameterObject[] settings) {
        List<String> addedSettings = new ArrayList<>();
        ParameterObject[] updatedSettings = new ParameterObject[4];

        for (ParameterObject s : settings) {
            switch (s.getName()) {
                case "local_timezone":
                    addedSettings.add("local_timezone");
                    updatedSettings[0] = s;
                    break;
                case "daily_reminder_time":
                    addedSettings.add("daily_reminder_time");
                    updatedSettings[1] = s;
                    break;
//                    case "lights":
//                    case "biometrics":
                case "daily_reminder":
                    addedSettings.add("daily_reminder");
                    updatedSettings[2] = s;
                    break;
                case "record_activities":
                    addedSettings.add("record_activities");
                    updatedSettings[3] = s;
            }
        }


        if(!addedSettings.contains("local_timezone")) {
            updatedSettings[0] = (new ParameterObject("local_timezone", "N", "", "0"));
        }

        if(!addedSettings.contains("daily_reminder_time")) {
            updatedSettings[1] = (new ParameterObject("daily_reminder_time", "N", "11:01", "5"));

        }

        if(!addedSettings.contains("daily_reminder")) {
            updatedSettings[2] = (new ParameterObject("daily_reminder", "N", "0", "3"));

        }

        if(!addedSettings.contains("record_activities")) {
            updatedSettings[3] = (new ParameterObject("record_activities", "N", "{\"THANX\":1,\"APPTT\":1,\"SHWNG\":1,\"REFFR\":1,\"REFFC\":1,\"ADDDB\":1,\"5STAR\":1,\"EXERS\":1,\"PCMAS\":1,\"OPENH\":1,\"APPTS\":1,\"HOURP\":1,\"DIALS\":1,\"BSHNG\":1,\"MEDIT\":1}", "7"));

        }

        return updatedSettings;
    }

    private ParameterObject setDefaultActivitesSelected() {
        ParameterObject activites = (new ParameterObject("record_activities", "N", "{\"THANX\":1,\"APPTT\":1,\"SHWNG\":1,\"REFFR\":1,\"REFFC\":1,\"ADDDB\":1,\"5STAR\":1,\"EXERS\":1,\"PCMAS\":1,\"OPENH\":1,\"APPTS\":1,\"HOURP\":1,\"DIALS\":1,\"BSHNG\":1,\"MEDIT\":1}", "7"));
        return activites;
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
        if(settings.size() > 0) {
            for (ParameterObject p : settings) {
                if(p.getName().equalsIgnoreCase("lights")) {
                    return p.getValue();
                }
            }
        }
        return "0";
    }

    public HashMap<String, String> getLabels() {
        return labels;
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

    public List<Metric> getMasterActivitiesObject() {
        return masterActivitiesObject;
    }

    public void sortSelectedActivities(AsyncActivitySettingsObject[] currentActivitiesSorting) {
        LinkedHashMap<String, SelectedActivities> itemArray = new LinkedHashMap<>();
        for(int i = 0; i < activitiesSelected.size(); i++) {
            itemArray.put(currentActivitiesSorting[i].getActivity_type(), activitiesSelected.get(currentActivitiesSorting[i]));
        }
        activitiesSelected = itemArray;
    }

    public void setMessageCenterVisible(boolean b) {
        messageCenterVisible = b;
    }

    public AsyncActivitySettingsObject getFirstOtherActivity() {
        AsyncActivitySettingsObject firstOtherActivity = null;
        for(AsyncActivitySettingsObject setting : activitySettings) {
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
        return firstOtherActivity;
    }

    public int getNumOfActiveAgents() {
        return closedList.size();
    }
}
