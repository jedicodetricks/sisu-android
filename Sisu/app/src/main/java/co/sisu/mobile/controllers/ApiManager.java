package co.sisu.mobile.controllers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import co.sisu.mobile.BuildConfig;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncAuthenticatorNEW;
import co.sisu.mobile.api.AsyncDelete;
import co.sisu.mobile.api.AsyncGet;
import co.sisu.mobile.api.AsyncLeaderboardImage;
import co.sisu.mobile.api.AsyncPost;
import co.sisu.mobile.api.AsyncPut;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
import co.sisu.mobile.models.AsyncUpdateProfileImageJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.models.LeaderboardAgentModel;
import co.sisu.mobile.system.SaveSharedPreference;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.Cache;

/**
 * Created by Brady Groharing on 6/2/2018.
 */

public class ApiManager {

    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private String transactionID;
    private String timestamp;
    private String jwtStr;
    private String url = "https://api.sisu.co/";
    int cacheSize = 10 * 1024 * 1024; // 10MB
    Cache cache;
    private Gson gson;

    public ApiManager(Context context) {
        cache = new Cache(context.getCacheDir(), cacheSize);
        gson = new Gson();
    }

    public void getLeaderboardImage(AsyncServerEventListener cb, String agentId, LeaderboardAgentModel leaderboardAgentModel) {
        //GET
        //This is a special case because of the async call that it applies to the leaderboardAgentModel
        getJWT(agentId);
        new AsyncLeaderboardImage(cb, url, leaderboardAgentModel).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAuth(AsyncServerEventListener cb, String agentId, String email, String password) {
        //POST (PROBS NEED THIS ONE)
        getJWT(agentId);
        new AsyncAuthenticatorNEW(cb, url, email, password).execute(jwtStr, timestamp, transactionID);
    }

    //START OF GET CALLS

    public void getAgentFilters(AsyncServerEventListener cb, String agentId, int selectedTeamId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_AGENT_FILTERS;
        String currentUrl = url + "api/v1/agent/filters/" + agentId +"/" + selectedTeamId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getMarketStatus(AsyncServerEventListener cb, String agentId, int marketId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_MARKET_STATUS;
        String currentUrl = url + "api/v1/client/market_status/" + marketId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getScope(AsyncServerEventListener cb, String agentId, int selectedTeamId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_SCOPE;
        String currentUrl = url + "api/v1/agent/scopes/"+ agentId +"/" + selectedTeamId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getClientParams(AsyncServerEventListener cb, String agentId, String clientId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_CLIENT_SETTINGS;
        String currentUrl = url + "api/v1/parameter/edit-parameter/3/"+ clientId +"/activate_client";
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getMessageCenterInfo(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_MESSAGE_CENTER;
        String currentUrl = url + "api/v1/agent/push-message/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getAgentGoals(AsyncServerEventListener cb, String agentId, int teamId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_AGENT_GOALS;
        String currentUrl = url + "api/v2/agent/get-goals/"+ agentId + "/" + teamId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getSettings(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_SETTINGS;
        String currentUrl = url + "api/v1/parameter/get-parameters/2/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getTeams(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_TEAMS;
        String currentUrl = url + "api/v1/agent/get-teams/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getClients(AsyncServerEventListener cb, String agentId, int marketId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_CLIENTS;
        String currentUrl = url + "api/v2/agent/get-clients/" + agentId + "/" + marketId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getActivitySettings(AsyncServerEventListener cb, String agentId, int teamId, int marketId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_ACTIVITY_SETTINGS;
        String currentUrl = url + "api/v1/team/record-activities/" + teamId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getAgent(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_AGENT;
        String currentUrl = url + "api/v1/agent/edit-agent/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getLeaderboardYear(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_LEADERBOARDS;
        String currentUrl = url + "api/v1/team/leaderboards/" + formattedTeamId + "/" + formattedYear;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getLeaderboardYearAndMonth(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear, String formattedMonth) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_LEADERBOARDS;
        String currentUrl = url + "api/v1/team/leaderboards/" + formattedTeamId + "/" + formattedYear + "/" + formattedMonth;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getProfileImage(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_PROFILE_IMAGE;
        String currentUrl = url + "api/v1/image/3/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getTeamParams(AsyncServerEventListener cb, String agentId, int teamId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_TEAM_PARAMS;
        String currentUrl = url + "api/v1/parameter/edit-parameter/1/"+ teamId +"/slack_url";
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getClientNotes(AsyncServerEventListener cb, String agentId, String clientId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_NOTES;
        String currentUrl = url + "api/v1/client/logs/" + clientId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getFirebaseDevices(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_FIREBASE_DEVICES;
        String currentUrl = url + "api/v1/agent/device/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getColorScheme(AsyncServerEventListener cb, String agentId, int selectedTeamId, String isLightTheme) {
        //GET
        if(selectedTeamId == -1) {
            selectedTeamId = 0;
        }
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_COLOR_SCHEME;
        String currentUrl = url + "api/v1/team/theme/" + selectedTeamId + "/" + isLightTheme;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getLabels(AsyncServerEventListener cb, String agentId, int teamId) {
        //GET
        if(teamId == -1) {
            teamId = 0;
        }
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_LABELS;
        String currentUrl = url + "api/v1/team/market/" + teamId + "/" + Locale.getDefault().toString();
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getLeadSources(AsyncServerEventListener cb, String agent_id, int teamId) {
        if(teamId == -1) {
            teamId = 0;
        }
        getJWT(agent_id);
        ApiReturnTypes returnType = ApiReturnTypes.GET_LEAD_SOURCES;
        String currentUrl = url + "api/v1/team/get-lead-sources/" + teamId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getTeamAgents(AsyncServerEventListener cb, String agent_id, int teamId) {
        if(teamId == -1) {
            teamId = 0;
        }
        getJWT(agent_id);
        ApiReturnTypes returnType = ApiReturnTypes.GET_TEAM_AGENTS;
        String currentUrl = url + "api/v1/team/get-team-agents/" + teamId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    //START OF POST CALLS

    public void getTeamClients(AsyncServerEventListener cb, String agentId, int teamId, String contextFilter, String marketStatusFilter) {
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_TEAM_CLIENTS;
        String currentUrl = url + "api/v2/team/get-team-clients";
        JsonObject jsonRequest = new JsonObject();
        jsonRequest.addProperty("team_id", teamId);
        jsonRequest.addProperty("agent_id", Integer.valueOf(agentId));
        jsonRequest.addProperty("include_agent_info", false);
        jsonRequest.addProperty("return_tiles", true);
        jsonRequest.addProperty("lighter", false);
        jsonRequest.addProperty("saved_filter", marketStatusFilter);
        JsonArray fieldsArray = new JsonArray();
        fieldsArray.add("client_id");
        fieldsArray.add("first_name");
        fieldsArray.add("last_name");
        fieldsArray.add("email");
        fieldsArray.add("mobile_phone");
        fieldsArray.add("type_id");
        fieldsArray.add("is_locked");
        fieldsArray.add("commission_amt");
        jsonRequest.add("fieldsX", fieldsArray);
        JsonObject filter = new JsonObject();
        filter.addProperty("context_filter", contextFilter);
        filter.addProperty("client_filter", "");
        filter.addProperty("include_totals", true);
        filter.addProperty("record_limit", 0);
        filter.addProperty("order_by", "last_name");
        filter.addProperty("order_direction", "asc");
        filter.addProperty("page", 1);
        filter.addProperty("per_page", 40);
        filter.addProperty("name_filter", "");
        JsonObject stringFilters = new JsonObject();
        stringFilters.addProperty("status", "N");
        filter.add("string_filters", stringFilters);
        jsonRequest.add("filter", filter);

        new AsyncPost(cb, currentUrl, returnType, jsonRequest.toString()).execute(jwtStr, timestamp, transactionID);
    }

    public void getTileSetup(AsyncServerEventListener cb, String agentId, int teamId, Date startDate, Date endDate, String dashboardType) {
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_TILES;
        String currentUrl = url + "api/v1/get-dashboard-tiles";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStartTime = formatter.format(startDate);
        String formattedEndTime = formatter.format(endDate);

        String body = "{\"start_date\": \"" + formattedStartTime + "\",\"end_date\": \"" + formattedEndTime + "\", \"dashboard_type\": \"" + dashboardType + "\",\"team_id\":\"" + teamId + "\",\"agent_id\":\"" + agentId + "\",\"dashboard_name\":\"Default Dashboard\",\"include_data\":true}";

        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void getTileSetup(AsyncServerEventListener cb, String agentId, int teamId, Date startDate, Date endDate, String dashboardType, String idValue) {
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_TILES;
        String currentUrl = url + "api/v1/get-dashboard-tiles";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStartTime = formatter.format(startDate);
        String formattedEndTime = formatter.format(endDate);

        String body = "{\"start_date\": \"" + formattedStartTime + "\",\"end_date\": \"" + formattedEndTime + "\", \"dashboard_type\": \"" + dashboardType + "\",\"team_id\":\"" + teamId + "\",\"agent_id\":\"" + agentId + "\",\"context_filter\":\"" + idValue + "\",\"dashboard_name\":\"Default Dashboard\",\"include_data\":true}";

        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate, int marketId) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_ACTIVITIES;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStartTime = formatter.format(startDate);
        String formattedEndTime = formatter.format(endDate);

        String body = "{\"start_date\": \"" + formattedStartTime + "\",\"end_date\": \"" + formattedEndTime + "\",\"include_counts\":1,\"include_activities\":0}";
        String currentUrl = url + "api/v1/agent/activity/" + agentId + "/" + marketId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivities(AsyncServerEventListener cb, String agentId, String formattedStartTime, String formattedEndTime, int marketId) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_ACTIVITIES;
        String body = "{\"start_date\": \"" + formattedStartTime + "\",\"end_date\": \"" + formattedEndTime + "\",\"include_counts\":1,\"include_activities\":0}";
        String currentUrl = url + "api/v1/agent/activity/" + agentId + "/" + marketId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAddClient(AsyncServerEventListener cb, String agentId, ClientObject newClient) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.CREATE_CLIENT;
        String body = gson.toJson(newClient);
        body = body.replace("\"is_priority\":\"1\"", "\"is_priority\":true");
        body = body.replace("\"is_priority\":\"0\"", "\"is_priority\":false");
        body = body.replace("\"state\":null", "\"state\":\"\"");
        String currentUrl = url + "api/v1/client/edit-client/" + agentId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncFeedback(AsyncServerEventListener cb, String agentId, String feedback, String slackUrl) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.SEND_FEEDBACK;
        String body;

        String currentUrl = url + "api/v1/feedback/add-feedback/" + agentId;

        if(slackUrl != null) {
            body = "{\"feedback\":\"" + feedback +"\"}";
        }
        else {
            body = "{\"text\":\"" + feedback + "\"}";
        }

        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void addNote(AsyncServerEventListener cb, String agentId, String clientId, String note, String noteType) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.CREATE_NOTE;
        String body = "{\"log_type_id\":\"" + noteType + "\", \"note\":\"" + note + "\"}";
        String currentUrl = url + "api/v1/client/logs/" + clientId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void addAppointmentNote(AsyncServerEventListener cb, String agentId, String clientId, String note, String noteType, String createdTs) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.CREATE_APPT_NOTE;
        String body = "{\"log_type_id\":\"" + noteType + "\", \"note\":\"" + note + "\", \"created_ts\":\"" + createdTs + "\"}";
        String currentUrl = url + "api/v1/client/logs/" + clientId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendFirebaseToken(AsyncServerEventListener cb, Context context, AgentModel agent, String token) {
        //POST
        getJWT(agent.getAgent_id());
        ApiReturnTypes returnType = ApiReturnTypes.SEND_FIREBASE_TOKEN;
        FirebaseDeviceObject firebaseDeviceObject = generateFirebaseObject(agent, token, context);
        String body = gson.toJson(firebaseDeviceObject);
        String currentUrl = url + "api/v1/agent/device/" + agent.getAgent_id();
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendPushNotification(AsyncServerEventListener cb, String agentId, String teamId, String message) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.SEND_PUSH_NOTIFICATION;
        String body = "{\"body\":\"" + message + "\", \"title\":\"Message from Team Administrator\"}";
        String currentUrl = url + "api/v1/team/push-message/" + teamId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateActivitySettings(AsyncServerEventListener cb, String agentId, String updateObject, int teamId, int marketId) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_ACTIVITY_SETTINGS;
        String body = updateObject;
        String currentUrl = url + "api/v1/team/record-activities/" + teamId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }


    //START OF PUT CALLS


    public void sendAsyncUpdateActivities(AsyncServerEventListener cb, String agentId, AsyncUpdateActivitiesJsonObject activitiesJsonObject, int marketId) {
        //PUT
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_ACTIVITIES;
        String body = gson.toJson(activitiesJsonObject);
        String currentUrl = url + "api/v1/agent/activity/" + agentId + "/" + marketId;
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }


    public void sendAsyncUpdateClients(AsyncServerEventListener cb, String agentId, ClientObject currentClient) {
        //PUT
        getJWT(agentId);
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();
        String body = gson.toJson(currentClient);
        body = body.replace("\"is_priority\":\"1\"", "\"is_priority\":true");
        body = body.replace("\"is_priority\":\"0\"", "\"is_priority\":false");
        //TODO: Need this state replacement until Rick fixes the api (It's in the add as well)
        body = body.replace("\"state\":null", "\"state\":\"\"");
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_CLIENT;
        String currentUrl = url + "api/v1/client/edit-client/" + currentClient.getClient_id();
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateAgent(AsyncServerEventListener cb, String agentId, int teamId, String income, String reason) {
        //PUT
        //TODO: I don't know if this needs teamId / I don't think so, can probably remove
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_AGENT;
        String body = "";
        if(!income.equals("") && !reason.equals("")) {
            body = "{\"vision_statement\":\"" + reason + "\", \"desired_income\":\"" + income + "\"}";
        }
        else if(!reason.equals("")) {
            body = "{\"vision_statement\":\"" + reason + "\"}";
        }
        else if(!income.equals("")){
            body = "{\"desired_income\":\"" + income + "\"}";
        }
        String currentUrl = url + "api/v1/agent/edit-agent/" + agentId;
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateGoals(AsyncServerEventListener cb, String agentId, int teamId, AsyncUpdateAgentGoalsJsonObject asyncUpdateAgentGoalsJsonObject) {
        //PUT
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_GOALS;
        String body = gson.toJson(asyncUpdateAgentGoalsJsonObject);
        String currentUrl = url + "api/v1/agent/goal/" + agentId + "/" + teamId;
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfileImage(AsyncServerEventListener cb, String agentId, AsyncUpdateProfileImageJsonObject asyncUpdateProfileImageJsonObject) {
        //PUT
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_PROFILE_IMAGE;
        String body = gson.toJson(asyncUpdateProfileImageJsonObject);
        String currentUrl = url + "api/v1/image";
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfile(AsyncServerEventListener cb, String agentId, HashMap<String, String> changedFields) {
        //PUT
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_PROFILE;
        String body = "{";
        int counter = 0;
        for ( String key : changedFields.keySet() ) {
            String value = changedFields.get(key);
            body += "\"" + key + "\":\"" + value + "\"";
            if(counter < changedFields.size() - 1) {
                body += ",";
            }
            counter++;
        }
        body += "}";
        String currentUrl = url + "api/v1/agent/edit-agent/"+ agentId;
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateSettings(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject) {
        //PUT
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_SETTINGS;
        String body = gson.toJson(asyncUpdateSettingsJsonObject);
        String currentUrl = url + "api/v1/parameter/edit-parameter";
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void updateNote(AsyncServerEventListener cb, String agentId, String noteId, String note, String noteType) {
        //PUT
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.UPDATE_NOTE;
        String body = "{\"log_type_id\":\"" + noteType + "\", \"note\":\"" + note + "\"}";
        String currentUrl = url + "api/v1/client/logs/" + noteId;
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    public void refreshFirebaseToken(AsyncServerEventListener cb, Context context, AgentModel agent, String token, FirebaseDeviceObject currentDevice) {
        //PUT
        getJWT(agent.getAgent_id());
        if(currentDevice != null) {
            ApiReturnTypes returnType = ApiReturnTypes.UPDATE_FIREBASE;
            String body = gson.toJson(currentDevice);
            String currentUrl = url + "api/v1/agent/device/" + agent.getAgent_id();
            new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
        }
    }

    public void setClientParameter(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject activateClientObject) {
        //This is to reactivate clients, can probably be merged or at least an override with sendAsyncUpdateSettings
        //PUT
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.ACTIVATE_CLIENT;
        String body = gson.toJson(activateClientObject);
        String currentUrl = url + "api/v1/parameter/edit-parameter";
        new AsyncPut(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
    }

    //START OF DELETE CALLS

    public void deleteNote(AsyncServerEventListener cb, String agentId, String noteId) {
        //DELETE
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.DELETE_NOTE;
        String currentUrl = url + "api/v1/client/logs/" + noteId;
        new AsyncDelete(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getJWT(String agentId) {
        transactionID = UUID.randomUUID().toString();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, -60);
        timestamp = String.valueOf(date.getTimeInMillis());

        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);

        //TODO: The issuer is supposed to be random I think
        jwtStr = Jwts.builder()
                .claim("Client-Timestamp", timestamp)
                .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                .setIssuedAt(date.getTime())
                .setExpiration(expDate.getTime())
                .claim("Transaction-Id", transactionID)
                .claim("agent_id", agentId)
                .claim("client-version", BuildConfig.VERSION_CODE)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

//        Log.e("JWT", jwtStr);
//        Log.e("TRANS", transactionID);
//        Log.e("TIME", timestamp);
    }


    //Helper methods

    private FirebaseDeviceObject generateFirebaseObject(AgentModel agent, String token, Context context) {
        String uuid = UUID.randomUUID().toString();
        String deviceId = agent.getAgent_id() + "-" + uuid;
        String deviceName = agent.getAgent_id() + "'s android";
        if(agent.getFirst_name() != null && agent.getLast_name() != null) {
            deviceName = agent.getAgent_id() + "-" + agent.getFirst_name() + " " + agent.getLast_name() + " android " + uuid;
        }
        else if(agent.getLast_name() != null) {
            deviceName = agent.getAgent_id() + " " + agent.getLast_name() + "s android " + uuid;
        }
        SaveSharedPreference.setFirebaseDeviceId(context, deviceId);
        FirebaseDeviceObject firebaseDeviceObject = new FirebaseDeviceObject("Android", deviceId, deviceName, token);

        return firebaseDeviceObject;
    }


}
