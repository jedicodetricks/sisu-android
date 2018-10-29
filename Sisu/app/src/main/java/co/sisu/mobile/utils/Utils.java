package co.sisu.mobile.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.SplashScreenActivity;

/**
 * Created by Brady Groharing on 3/25/2018.
 */

public class Utils {

    public static NotificationManager mManager;

    @SuppressWarnings("static-access")
    public static void generateNotification(Context context, String title, String text) {

        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "420")
                .setSmallIcon(R.drawable.sisu_mark)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);



        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(666, mBuilder.build());
    }

    @SuppressWarnings("static-access")
    public static AlertDialog generateDialogue(Context context, String title, String text) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("HELLO WORLD")
                .setPositiveButton("FIRE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}