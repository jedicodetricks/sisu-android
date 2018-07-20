package co.sisu.mobile.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncActivitySettings;
import co.sisu.mobile.api.AsyncAddClient;
import co.sisu.mobile.api.AsyncAgent;
import co.sisu.mobile.api.AsyncAgentGoals;
import co.sisu.mobile.api.AsyncClients;
import co.sisu.mobile.api.AsyncFeedback;
import co.sisu.mobile.api.AsyncLeaderboardImage;
import co.sisu.mobile.api.AsyncLeaderboardStats;
import co.sisu.mobile.api.AsyncProfileImage;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncSettings;
import co.sisu.mobile.api.AsyncTeams;
import co.sisu.mobile.api.AsyncUpdateActivities;
import co.sisu.mobile.api.AsyncUpdateActivitySettings;
import co.sisu.mobile.api.AsyncUpdateAgent;
import co.sisu.mobile.api.AsyncUpdateClients;
import co.sisu.mobile.api.AsyncUpdateGoals;
import co.sisu.mobile.api.AsyncUpdateProfile;
import co.sisu.mobile.api.AsyncUpdateProfileImage;
import co.sisu.mobile.api.AsyncUpdateSettings;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.AsyncUpdateAgentGoalsJsonObject;
import co.sisu.mobile.models.AsyncUpdateProfileImageJsonObject;
import co.sisu.mobile.models.AsyncUpdateSettingsJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.LeaderboardAgentModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by Brady Groharing on 6/2/2018.
 */

public class ApiManager {

    private String secretKey = "33SnhbgJaXFp6fYYd1Ru";

    private String transactionID;
    private String timestamp;
    private String jwtStr;

    public void sendAsyncActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate) {
        getJWT(agentId);
        new AsyncActivities(cb, agentId, startDate, endDate).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivities(AsyncServerEventListener cb, String agentId, String formattedStartTime, String formattedEndTime) {
        getJWT(agentId);
        new AsyncActivities(cb, agentId, formattedStartTime, formattedEndTime).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAgentGoals(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncAgentGoals(cb, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncSettings(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncSettings(cb, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncTeams(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncTeams(cb, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncClients(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncClients(cb, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateActivities(AsyncServerEventListener cb, String agentId, AsyncUpdateActivitiesJsonObject activitiesJsonObject) {
        getJWT(agentId);
        new AsyncUpdateActivities(cb, agentId, activitiesJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivitySettings(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncActivitySettings(cb, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateActivitySettings(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject updateObject) {
        getJWT(agentId);
        new AsyncUpdateActivitySettings(cb, updateObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAddClient(AsyncServerEventListener cb, String agentId, ClientObject newClient) {
        getJWT(agentId);
        new AsyncAddClient(cb, agentId, newClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateClients(AsyncServerEventListener cb, String agentId, ClientObject currentClient) {
        getJWT(agentId);
        new AsyncUpdateClients(cb, currentClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncFeedback(AsyncServerEventListener cb, String agentId, String feedback) {
        getJWT(agentId);
        new AsyncFeedback(cb, agentId, feedback).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAgent(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncAgent(cb, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateAgent(AsyncServerEventListener cb, String agentId, String income, String reason) {
        getJWT(agentId);
        new AsyncUpdateAgent(cb, agentId, income, reason).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateGoals(AsyncServerEventListener cb, String agentId, AsyncUpdateAgentGoalsJsonObject asyncUpdateAgentGoalsJsonObject) {
        getJWT(agentId);
        new AsyncUpdateGoals(cb, agentId, asyncUpdateAgentGoalsJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardYear(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear) {
        getJWT(agentId);
        new AsyncLeaderboardStats(cb, formattedTeamId, formattedYear, "").execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardYearAndMonth(AsyncServerEventListener cb, String agentId, String formattedTeamId, String formattedYear, String formattedMonth) {
        getJWT(agentId);
        new AsyncLeaderboardStats(cb, formattedTeamId, formattedYear, formattedMonth).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncLeaderboardImage(AsyncServerEventListener cb, String agentId, LeaderboardAgentModel leaderboardAgentModel) {
        getJWT(agentId);
        new AsyncLeaderboardImage(cb, leaderboardAgentModel).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncProfileImage(AsyncServerEventListener cb, String agentId) {
        getJWT(agentId);
        new AsyncProfileImage(cb, agentId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfileImage(AsyncServerEventListener cb, String agentId, AsyncUpdateProfileImageJsonObject asyncUpdateProfileImageJsonObject) {
        getJWT(agentId);
        new AsyncUpdateProfileImage(cb, asyncUpdateProfileImageJsonObject).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateProfile(AsyncServerEventListener cb, String agentId, HashMap<String, String> changedFields) {
        getJWT(agentId);
        new AsyncUpdateProfile(cb, agentId, changedFields).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateSettings(AsyncServerEventListener cb, String agentId, AsyncUpdateSettingsJsonObject asyncUpdateSettingsJsonObject) {
        getJWT(agentId);
        new AsyncUpdateSettings(cb, agentId, asyncUpdateSettingsJsonObject).execute(jwtStr, timestamp, transactionID);
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

}
