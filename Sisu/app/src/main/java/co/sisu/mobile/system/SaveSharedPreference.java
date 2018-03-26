package co.sisu.mobile.system;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bradygroharing on 3/23/18.
 */

public class SaveSharedPreference
{
    static final String PREF_USER_NAME= "username";

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
}
