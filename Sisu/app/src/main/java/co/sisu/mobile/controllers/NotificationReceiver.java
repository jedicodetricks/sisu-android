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
      /*Intent service1 = new Intent(context, MyAlarmService.class);
        context.startService(service1);*/
        Log.i("App", "called receiver method");
        try{
            Utils.generateNotification(context, "Sisu", "Reminder: Enter your numbers for today!");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
