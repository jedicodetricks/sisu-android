package co.sisu.mobile.controllers;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncActivitySettings;
import co.sisu.mobile.api.AsyncAddClient;
import co.sisu.mobile.api.AsyncAddFirebaseDevice;
import co.sisu.mobile.api.AsyncAddNotes;
import co.sisu.mobile.api.AsyncAgent;
import co.sisu.mobile.api.AsyncAgentGoals;
import co.sisu.mobile.api.AsyncAuthenticatorNEW;
import co.sisu.mobile.api.AsyncClients;
import co.sisu.mobile.api.AsyncDeleteNotes;
import co.sisu.mobile.api.AsyncFeedback;
import co.sisu.mobile.api.AsyncGetFirebaseDevices;
import co.sisu.mobile.api.AsyncGetNotes;
import co.sisu.mobile.api.AsyncLeaderboardImage;
import co.sisu.mobile.api.AsyncLeaderboardStats;
import co.sisu.mobile.api.AsyncProfileImage;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncSettings;
import co.sisu.mobile.api.AsyncTeamParameters;
import co.sisu.mobile.api.AsyncTeams;
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
    private String url = "https://beta.sisu.co/";
    int cacheSize = 10 * 1024 * 1024; // 10MB
    Cache cache;

    public ApiManager(Context context) {
        cache = new Cache(context.getCacheDir(), cacheSize);
    }

    public void sendAsyncActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate) {
        getJWT(agentId);
        new AsyncActivities(cb, url, agentId, startDate, endDate).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivities(AsyncServerEventListener cb, String agentId, String formattedStartTime, String formattedEndTime) {
        getJWT(agentId);
        new AsyncActivities(cb, url, agentId, formattedStartTime, formattedEndTime).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAgentGoals(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncAgentGoals(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncSettings(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncSettings(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncTeams(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncTeams(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncClients(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncClients(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateActivities(AsyncServerEventListener cb, String agentId, AsyncUpdateActivitiesJsonObject activitiesJsonObject) {
        getJWT(agentId);
        new AsyncUpdateActivities(cb, url, agentId, activitiesJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivitySettings(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncActivitySettings(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateActivitySettings(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject updateObject) {
        getJWT(agentId);
        new AsyncUpdateActivitySettings(cb, url, updateObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAddClient(AsyncServerEventListener cb, String agentId, ClientObject newClient) {
        getJWT(agentId);
        new AsyncAddClient(cb, url, agentId, newClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateClients(AsyncServerEventListener cb, String agentId, ClientObject currentClient) {
        getJWT(agentId);
        new AsyncUpdateClients(cb, url, currentClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncFeedback(AsyncServerEventListener cb, String agentId, String feedback, String slackUrl) {
        getJWT(agentId);
        if(slackUrl != null) {
            Log.e("GOING TO SLACK", "YES");
            new AsyncFeedback(cb, slackUrl, agentId, feedback, true).execute(jwtStr, timestamp, transactionID);
        }
        else {
            new AsyncFeedback(cb, url, agentId, feedback, false).execute(jwtStr, timestamp, transactionID);
        }
    }

    public void sendAsyncAgent(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncAgent(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateAgent(AsyncServerEventListener cb, String agentId, String income, String reason) {
        getJWT(agentId);
        new AsyncUpdateAgent(cb, url, agentId, income, reason).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateGoals(AsyncServerEventListener cb, String agentId, AsyncUpdateAgentGoalsJsonObject asyncUpdateAgentGoalsJsonObject) {
        getJWT(agentId);
        new AsyncUpdateGoals(cb, url, agentId, asyncUpdateAgentGoalsJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardYear(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear) {
        getJWT(agentId);
        new AsyncLeaderboardStats(cb, url, formattedTeamId, formattedYear, "").execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardYearAndMonth(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear, String formattedMonth) {
        getJWT(agentId);
        new AsyncLeaderboardStats(cb, url, formattedTeamId, formattedYear, formattedMonth).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardImage(AsyncServerEventListener cb, String agentId, LeaderboardAgentModel leaderboardAgentModel) {
        getJWT(agentId);
        new AsyncLeaderboardImage(cb, url, leaderboardAgentModel).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncProfileImage(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncProfileImage(cb, url, agentId, cache).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfileImage(AsyncServerEventListener cb, String agentId, AsyncUpdateProfileImageJsonObject asyncUpdateProfileImageJsonObject) {
        getJWT(agentId);
        new AsyncUpdateProfileImage(cb, url, asyncUpdateProfileImageJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfile(AsyncServerEventListener cb, String agentId, HashMap<String, String> changedFields) {
        getJWT(agentId);
        new AsyncUpdateProfile(cb, url, agentId, changedFields).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateSettings(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject) {
        getJWT(agentId);
        new AsyncUpdateSettings(cb, url, agentId, asyncUpdateSettingsJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAuth(AsyncServerEventListener cb, String agentId, String email, String password) {
        getJWT(agentId);
        new AsyncAuthenticatorNEW(cb, url, email, password).execute(jwtStr, timestamp, transactionID);
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
    }

    public void addNote(AsyncServerEventListener cb, String agentId, String clientId, String note, String noteType) {
        getJWT(agentId);
        new AsyncAddNotes(cb, url, clientId, note, noteType).execute(jwtStr, timestamp, transactionID);
    }

    public void updateNote(AsyncServerEventListener cb, String agentId, String noteId, String note, String noteType) {
        getJWT(agentId);
        new AsyncUpdateNotes(cb, url, noteId, note, noteType).execute(jwtStr, timestamp, transactionID);
    }

    public void getClientNotes(AsyncServerEventListener cb, String agentId, String clientId) {
        getJWT(agentId);
        new AsyncGetNotes(cb, url, clientId).execute(jwtStr, timestamp, transactionID);
    }

    public void deleteNote(AsyncServerEventListener cb, String agentId, String noteId) {
        getJWT(agentId);
        new AsyncDeleteNotes(cb, url, noteId).execute(jwtStr, timestamp, transactionID);
    }


    public void getTeamParams(AsyncServerEventListener cb, String agentId, int teamId) {
        getJWT(agentId);
        new AsyncTeamParameters(cb, url, teamId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendFirebaseToken(AsyncServerEventListener cb, Context context, AgentModel agent, String token) {
        getJWT(agent.getAgent_id());
        new AsyncAddFirebaseDevice(cb, url, context, agent, token).execute(jwtStr, timestamp, transactionID);

    }

    public void refreshFirebaseToken(AsyncServerEventListener cb, Context context, AgentModel agent, String token, FirebaseDeviceObject currentDevice) {
        getJWT(agent.getAgent_id());
        new AsyncUpdateFirebaseDevice(cb, url, context, agent, token, currentDevice).execute(jwtStr, timestamp, transactionID);

    }

    public void getFirebaseDevices(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncGetFirebaseDevices(cb, url, agentId).execute(jwtStr, timestamp, transactionID);
    }
}
