package co.sisu.mobile.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import co.sisu.mobile.utils.Utils;

/**
 * Created by Brady Groharing on 3/25/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("App", "called receiver method");
        try{
            Utils.generateNotification(context);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
