package co.sisu.mobile.controllers;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import co.sisu.mobile.ApiReturnTypes;
import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncActivitySettings;
import co.sisu.mobile.api.AsyncAddClient;
import co.sisu.mobile.api.AsyncAddFirebaseDevice;
import co.sisu.mobile.api.AsyncAddNotes;
import co.sisu.mobile.api.AsyncAgent;
import co.sisu.mobile.api.AsyncAuthenticatorNEW;
import co.sisu.mobile.api.AsyncDeleteNotes;
import co.sisu.mobile.api.AsyncFeedback;
import co.sisu.mobile.api.AsyncGet;
import co.sisu.mobile.api.AsyncGetFirebaseDevices;
import co.sisu.mobile.api.AsyncGetNotes;
import co.sisu.mobile.api.AsyncGetTeamColorScheme;
import co.sisu.mobile.api.AsyncLabels;
import co.sisu.mobile.api.AsyncLeaderboardImage;
import co.sisu.mobile.api.AsyncLeaderboardStats;
import co.sisu.mobile.api.AsyncProfileImage;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncTeamParameters;
import co.sisu.mobile.api.AsyncUpdateActivities;
import co.sisu.mobile.api.AsyncUpdateActivitySettings;
import co.sisu.mobile.api.AsyncUpdateAgent;
import co.sisu.mobile.api.AsyncUpdateClients;
import co.sisu.mobile.api.AsyncUpdateFirebaseDevice;
import co.sisu.mobile.api.AsyncUpdateGoals;
import co.sisu.mobile.api.AsyncUpdateNotes;
import co.sisu.mobile.api.AsyncUpdateProfile;
import co.sisu.mobile.api.AsyncUpdateProfileImage;
import co.sisu.mobile.api.AsyncUpdateSettings;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
import co.sisu.mobile.models.AsyncUpdateProfileImageJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.models.LeaderboardAgentModel;
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
    private String url = "http://staging.sisu.co/";
    int cacheSize = 10 * 1024 * 1024; // 10MB
    Cache cache;

    public ApiManager(Context context) {
        cache = new Cache(context.getCacheDir(), cacheSize);
    }

    public void getClientParams(AsyncServerEventListener cb, String agentId, String clientId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_CLIENT_SETTINGS;
        String currentUrl = url + "api/v1/parameter/edit-parameter/3/"+ clientId +"/activate_client";
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
//        AsyncActivateClientSettings
    }

    public void getMessageCenterInfo(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_MESSAGE_CENTER;
        String currentUrl = url + "api/v1/agent/push-message/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
    }

    public void getAgentGoals(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_AGENT_GOALS;
        String currentUrl = url + "api/v1/agent/get-goals/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
//        new AsyncAgentGoals(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void getSettings(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_SETTINGS;
        String currentUrl = url + "api/v1/parameter/get-parameters/2/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
//        new AsyncSettings(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void getTeams(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_TEAMS;
        String currentUrl = url + "api/v1/agent/get-teams/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
//        new AsyncTeams(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void getClients(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_CLIENTS;
        String currentUrl = url + "api/v1/agent/get-clients/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
//        new AsyncClients(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void getActivitySettings(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_ACTIVITY_SETTINGS;
        String currentUrl = url + "api/v1/parameter/edit-parameter/2/"+ agentId +"/record_activities";
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
//        new AsyncActivitySettings(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void getAgent(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_AGENT;
        String currentUrl = url + "api/v1/agent/edit-agent/" + agentId;
        new AsyncGet(cb, currentUrl, returnType).execute(jwtStr, timestamp, transactionID);
//        new AsyncAgent(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardYear(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear) {
        //GET
        getJWT(agentId);
        new AsyncLeaderboardStats(cb, url, formattedTeamId, formattedYear, "").execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardYearAndMonth(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear, String formattedMonth) {
        //GET
        getJWT(agentId);
        new AsyncLeaderboardStats(cb, url, formattedTeamId, formattedYear, formattedMonth).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardImage(AsyncServerEventListener cb, String agentId, LeaderboardAgentModel leaderboardAgentModel) {
        //GET
        getJWT(agentId);
        new AsyncLeaderboardImage(cb, url, leaderboardAgentModel).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncProfileImage(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        new AsyncProfileImage(cb, url, agentId, cache).execute(jwtStr, timestamp, transactionID);
    }

    public void getTeamParams(AsyncServerEventListener cb, String agentId, int teamId) {
        //GET
        getJWT(agentId);
        new AsyncTeamParameters(cb, url, teamId).execute(jwtStr, timestamp, transactionID);
    }

    public void getClientNotes(AsyncServerEventListener cb, String agentId, String clientId) {
        //GET
        getJWT(agentId);
        new AsyncGetNotes(cb, url, clientId).execute(jwtStr, timestamp, transactionID);
    }

    public void getFirebaseDevices(AsyncServerEventListener cb, String agentId) {
        //GET
        getJWT(agentId);
        new AsyncGetFirebaseDevices(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void getColorScheme(AsyncServerEventListener cb, String agentId, int selectedTeamId, String isLightTheme) {
        //GET
        if(selectedTeamId == -1) {
            selectedTeamId = 0;
        }
        getJWT(agentId);
        new AsyncGetTeamColorScheme(cb, url, selectedTeamId, isLightTheme).execute(jwtStr, timestamp, transactionID);
    }

    public void getLabels(AsyncServerEventListener cb, String agentId, int teamId) {
        //GET
        if(teamId == -1) {
            teamId = 0;
        }
        getJWT(agentId);
        new AsyncLabels(cb, url, teamId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate) {
        //POST
        getJWT(agentId);
        new AsyncActivities(cb, url, agentId, startDate, endDate).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivities(AsyncServerEventListener cb, String agentId, String formattedStartTime, String formattedEndTime) {
        //POST
        getJWT(agentId);
        new AsyncActivities(cb, url, agentId, formattedStartTime, formattedEndTime).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAddClient(AsyncServerEventListener cb, String agentId, ClientObject newClient) {
        //POST
        getJWT(agentId);
        new AsyncAddClient(cb, url, agentId, newClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncFeedback(AsyncServerEventListener cb, String agentId, String feedback, String slackUrl) {
        //POST
        getJWT(agentId);
        if(slackUrl != null) {
            new AsyncFeedback(cb, slackUrl, agentId, feedback, true).execute(jwtStr, timestamp, transactionID);
        }
        else {
            new AsyncFeedback(cb, url, agentId, feedback, false).execute(jwtStr, timestamp, transactionID);
        }
    }

    public void sendAsyncUpdateActivities(AsyncServerEventListener cb, String agentId, AsyncUpdateActivitiesJsonObject activitiesJsonObject) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateActivities(cb, url, agentId, activitiesJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateActivitySettings(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject updateObject) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateActivitySettings(cb, url, updateObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateClients(AsyncServerEventListener cb, String agentId, ClientObject currentClient) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateClients(cb, url, currentClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateAgent(AsyncServerEventListener cb, String agentId, String income, String reason) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateAgent(cb, url, agentId, income, reason).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateGoals(AsyncServerEventListener cb, String agentId, AsyncUpdateAgentGoalsJsonObject asyncUpdateAgentGoalsJsonObject) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateGoals(cb, url, agentId, asyncUpdateAgentGoalsJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfileImage(AsyncServerEventListener cb, String agentId, AsyncUpdateProfileImageJsonObject asyncUpdateProfileImageJsonObject) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateProfileImage(cb, url, asyncUpdateProfileImageJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfile(AsyncServerEventListener cb, String agentId, HashMap<String, String> changedFields) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateProfile(cb, url, agentId, changedFields).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateSettings(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateSettings(cb, url, agentId, asyncUpdateSettingsJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAuth(AsyncServerEventListener cb, String agentId, String email, String password) {
        //POST (PROBS NEED THIS ONE)
        getJWT(agentId);
        new AsyncAuthenticatorNEW(cb, url, email, password).execute(jwtStr, timestamp, transactionID);
    }

    public void addNote(AsyncServerEventListener cb, String agentId, String clientId, String note, String noteType) {
        //POST
        getJWT(agentId);
        new AsyncAddNotes(cb, url, clientId, note, noteType).execute(jwtStr, timestamp, transactionID);
    }

    public void updateNote(AsyncServerEventListener cb, String agentId, String noteId, String note, String noteType) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateNotes(cb, url, noteId, note, noteType).execute(jwtStr, timestamp, transactionID);
    }

    public void deleteNote(AsyncServerEventListener cb, String agentId, String noteId) {
        //DELETE
        getJWT(agentId);
        new AsyncDeleteNotes(cb, url, noteId).execute(jwtStr, timestamp, transactionID);
    }



    public void sendFirebaseToken(AsyncServerEventListener cb, Context context, AgentModel agent, String token) {
        //POST
        getJWT(agent.getAgent_id());
        new AsyncAddFirebaseDevice(cb, url, context, agent, token).execute(jwtStr, timestamp, transactionID);

    }

    public void refreshFirebaseToken(AsyncServerEventListener cb, Context context, AgentModel agent, String token, FirebaseDeviceObject currentDevice) {
        //PUT
        getJWT(agent.getAgent_id());
        if(currentDevice != null) {
            new AsyncUpdateFirebaseDevice(cb, url, context, agent, token, currentDevice).execute(jwtStr, timestamp, transactionID);
        }
    }

    public void setClientParameter(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject activateClientObject) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateSettings(cb, url, agentId, activateClientObject).execute(jwtStr, timestamp, transactionID);
    }

    public void getJWT(String agentId) {
        transactionID = UUID.randomUUID().toString();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, -60);
        timestamp = String.valueOf(date.getTimeInMillis());

        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 1);

        jwtStr = Jwts.builder()
                .claim("Client-Timestamp", timestamp)
                .setIssuer("sisu-android:8c535552-bf1f-4e46-bd70-ea5cb71fef4d")
                .setIssuedAt(date.getTime())
                .setExpiration(expDate.getTime())
                .claim("Transaction-Id", transactionID)
                .claim("agent_id", agentId)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

//        Log.e("JWT", jwtStr);
//        Log.e("TRANS", transactionID);
//        Log.e("TIME", timestamp);
    }

}
