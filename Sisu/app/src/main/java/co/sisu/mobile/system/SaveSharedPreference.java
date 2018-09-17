package co.sisu.mobile.system;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bradygroharing on 3/23/18.
 */

public class SaveSharedPreference
{
    static final String PREF_USER_ID= "userid";
    static final String PREF_USER_NAME= "username";
    static final String PREF_USER_PASSWORD= "password";
    static final String JWT = "jwt";
    static final String CLIENT_TIMESTAMP = "client-timestamp";
    static final String TRANS_ID = "trans-id";
    static final String FIREBASE_DEVICE_ID = "firebase-id";

    static SharedPreferences getSharedPreferences(Context ctx) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("login", Context.MODE_PRIVATE);
        return sharedPref;
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void setFirebaseDeviceId(Context ctx, String deviceId)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(FIREBASE_DEVICE_ID, deviceId);
        editor.commit();
    }

    public static String getFirebaseDeviceId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(FIREBASE_DEVICE_ID, "");
    }

    public static void setUserId(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, userName);
        editor.commit();
    }

    public static String getUserId(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }

    public static void setUserPassword(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_PASSWORD, userName);
        editor.commit();
    }

    public static String getUserPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_PASSWORD, "");
    }

    public static String getJWT(Context ctx) {
        return getSharedPreferences(ctx).getString(JWT, "");
    }

    public static void setJWT(Context ctx, String jwt)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(JWT, jwt);
        editor.commit();
    }

    public static String getClientTimestamp(Context ctx) {
        return getSharedPreferences(ctx).getString(CLIENT_TIMESTAMP, "");
    }

    public static void setClientTimestamp(Context ctx, String timestamp)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(CLIENT_TIMESTAMP, timestamp);
        editor.commit();
    }

    public static String getTransId(Context ctx) {
        return getSharedPreferences(ctx).getString(TRANS_ID, "");
    }

    public static void setTransId(Context ctx, String transId)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(TRANS_ID, transId);
        editor.commit();
    }


}
