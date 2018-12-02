package co.sisu.mobile.controllers;

import android.content.Context;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import co.sisu.mobile.ApiReturnTypes;
import co.sisu.mobile.api.AsyncActivities;
import co.sisu.mobile.api.AsyncAddClient;
import co.sisu.mobile.api.AsyncAddFirebaseDevice;
import co.sisu.mobile.api.AsyncAddNotes;
import co.sisu.mobile.api.AsyncAuthenticatorNEW;
import co.sisu.mobile.api.AsyncDeleteNotes;
import co.sisu.mobile.api.AsyncFeedback;
import co.sisu.mobile.api.AsyncGet;
import co.sisu.mobile.api.AsyncLeaderboardImage;
import co.sisu.mobile.api.AsyncPost;
import co.sisu.mobile.api.AsyncPushMessage;
import co.sisu.mobile.api.AsyncServerEventListener;
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
import co.sisu.mobile.system.SaveSharedPreference;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.Cache;
import okhttp3.Request;
import okhttp3.RequestBody;

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
    //TODO Team swap won't work because of the different return types

    //TODO: THIS MIGHT BE A SPECIAL CASE BECAUSE OF ASYNC. Keep looking into it.
    public void getLeaderboardImage(AsyncServerEventListener cb, String agentId, LeaderboardAgentModel leaderboardAgentModel) {
        //GET
        getJWT(agentId);
        new AsyncLeaderboardImage(cb, url, leaderboardAgentModel).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAuth(AsyncServerEventListener cb, String agentId, String email, String password) {
        //POST (PROBS NEED THIS ONE)
        getJWT(agentId);
        new AsyncAuthenticatorNEW(cb, url, email, password).execute(jwtStr, timestamp, transactionID);
    }

    //START OF GET CALLS

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

    public void getActivitySettings(AsyncServerEventListener cb, String agentId, int teamId) {
        //GET
        //TODO: Update subscribers for all marketId and teamId calls
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_ACTIVITY_SETTINGS;
        String currentUrl = url + "api/v1/agent/record-activities/"+ agentId + "/" + teamId;
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

    //START OF POST CALLS

    public void sendAsyncActivities (AsyncServerEventListener cb, String agentId, Date startDate, Date endDate, int marketId) {
        //POST TODO: fix returns for all POSTs
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_ACTIVITIES;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStartTime = formatter.format(startDate);
        String formattedEndTime = formatter.format(endDate);

        String body = "{\"start_date\": \"" + formattedStartTime + "\",\"end_date\": \"" + formattedEndTime + "\",\"include_counts\":1,\"include_activities\":0}";
        String currentUrl = url + "api/v1/agent/activity/" + agentId + "/" + marketId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//        new AsyncActivities(cb, url, agentId, startDate, endDate, marketId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncActivities(AsyncServerEventListener cb, String agentId, String formattedStartTime, String formattedEndTime, int marketId) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.GET_ACTIVITIES;
        String body = "{\"start_date\": \"" + formattedStartTime + "\",\"end_date\": \"" + formattedEndTime + "\",\"include_counts\":1,\"include_activities\":0}";
        String currentUrl = url + "api/v1/agent/activity/" + agentId + "/" + marketId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//        new AsyncActivities(cb, url, agentId, formattedStartTime, formattedEndTime, marketId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncAddClient(AsyncServerEventListener cb, String agentId, ClientObject newClient) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.CREATE_CLIENT;
        String body = gson.toJson(newClient);
        body = body.replace("\"is_priority\":\"1\"", "\"is_priority\":true");
        body = body.replace("\"is_priority\":\"0\"", "\"is_priority\":false");
        String currentUrl = url + "api/v1/client/edit-client/" + agentId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//        new AsyncAddClient(cb, url, agentId, newClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncFeedback(AsyncServerEventListener cb, String agentId, String feedback, String slackUrl) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.SEND_FEEDBACK;
        String body;


        String currentUrl = url + "api/v1/feedback/add-feedback/" + agentId;

        if(slackUrl != null) {
            body = "{\"feedback\":\"" + feedback +"\"}";
            new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//            new AsyncFeedback(cb, slackUrl, agentId, feedback, true).execute(jwtStr, timestamp, transactionID);
        }
        else {
            body = "{\"text\":\"" + feedback + "\"}";
            new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//            new AsyncFeedback(cb, url, agentId, feedback, false).execute(jwtStr, timestamp, transactionID);
        }
    }

    public void addNote(AsyncServerEventListener cb, String agentId, String clientId, String note, String noteType) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.CREATE_NOTE;
        String body = "{\"log_type_id\":\"" + noteType + "\", \"note\":\"" + note + "\"}";
        String currentUrl = url + "api/v1/client/logs/" + clientId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//        new AsyncAddNotes(cb, url, clientId, note, noteType).execute(jwtStr, timestamp, transactionID);
    }

    public void sendFirebaseToken(AsyncServerEventListener cb, Context context, AgentModel agent, String token) {
        //POST
        getJWT(agent.getAgent_id());
        ApiReturnTypes returnType = ApiReturnTypes.SEND_FIREBASE_TOKEN;
        FirebaseDeviceObject firebaseDeviceObject = generateFirebaseObject(agent, token, context);
        String body = gson.toJson(firebaseDeviceObject);
        String currentUrl = url + "api/v1/agent/device/" + agent.getAgent_id();
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//        new AsyncAddFirebaseDevice(cb, url, context, agent, token).execute(jwtStr, timestamp, transactionID);
    }

    public void sendPushNotification(AsyncServerEventListener cb, String agentId, String teamId, String message) {
        //POST
        getJWT(agentId);
        ApiReturnTypes returnType = ApiReturnTypes.SEND_PUSH_NOTIFICATION;
        String body = "{\"body\":\"" + message + "\", \"title\":\"Message from Team Administrator\"}";
        String currentUrl = url + "api/v1/team/push-message/" + teamId;
        new AsyncPost(cb, currentUrl, returnType, body).execute(jwtStr, timestamp, transactionID);
//        new AsyncPushMessage(cb, url, teamId, message, false).execute(jwtStr, timestamp, transactionID);
    }


    //START OF PUT CALLS


    public void sendAsyncUpdateActivities(AsyncServerEventListener cb, String agentId, AsyncUpdateActivitiesJsonObject activitiesJsonObject, int marketId) {
        getJWT(agentId);
        new AsyncUpdateActivities(cb, url, agentId, activitiesJsonObject, marketId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateActivitySettings(AsyncServerEventListener cb, String agentId, String updateObject, int marketId) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateActivitySettings(cb, url, updateObject, agentId, marketId).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateClients(AsyncServerEventListener cb, String agentId, ClientObject currentClient) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateClients(cb, url, currentClient).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateAgent(AsyncServerEventListener cb, String agentId, int teamId, String income, String reason) {
        //PUT
        //TODO: I don't know if this needs teamId
        getJWT(agentId);
        new AsyncUpdateAgent(cb, url, agentId, teamId, income, reason).execute(jwtStr, timestamp, transactionID);
    }

    public void sendAsyncUpdateGoals(AsyncServerEventListener cb, String agentId, int teamId, AsyncUpdateAgentGoalsJsonObject asyncUpdateAgentGoalsJsonObject) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateGoals(cb, url, agentId, teamId, asyncUpdateAgentGoalsJsonObject).execute(jwtStr, timestamp, transactionID);
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

    public void updateNote(AsyncServerEventListener cb, String agentId, String noteId, String note, String noteType) {
        //PUT
        getJWT(agentId);
        new AsyncUpdateNotes(cb, url, noteId, note, noteType).execute(jwtStr, timestamp, transactionID);
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

    //START OF DELETE CALLS

    public void deleteNote(AsyncServerEventListener cb, String agentId, String noteId) {
        //DELETE
        getJWT(agentId);
        new AsyncDeleteNotes(cb, url, noteId).execute(jwtStr, timestamp, transactionID);
    }

    public void getJWT(String agentId) {
        transactionID = UUID.randomUUID().toString();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, -60);
        timestamp = String.valueOf(date.getTimeInMillis());

        Calendar expDate = Calendar.getInstance();
        expDate.add(Calendar.DATE, 30);

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
