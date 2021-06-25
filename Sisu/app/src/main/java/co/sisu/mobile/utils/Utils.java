package co.sisu.mobile.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.activities.SplashScreenActivity;
import co.sisu.mobile.controllers.NotificationReceiver;
import co.sisu.mobile.models.ParameterObject;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Brady Groharing on 3/25/2018.
 */

public class Utils {

    private Animator mCurrentAnimator;
    private float startScale;
    private final Rect startBounds = new Rect();
    private final Rect finalBounds = new Rect();
    private final Point globalOffset = new Point();
    private int mShortAnimationDuration;
    private boolean imageIsExpanded = false;

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

    public void showToast(final CharSequence msg, @NonNull ParentActivity parentActivity) {
        parentActivity.runOnUiThread(() -> {
            Toast toast = Toast.makeText(parentActivity, msg,Toast.LENGTH_SHORT);
            // All of this was deprecated as of SDK 30. Maybe swap to snack bar
//                View view = toast.getView();
//                TextView text = view.findViewById(android.R.id.message);
//                text.setTextColor(colorSchemeManager.getLighterText());
//                view.getBackground().setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_IN);
//                text.setPadding(20, 8, 20, 8);
            toast.show();
        });
    }

    public void createNotificationAlarm(int currentSelectedHour, int currentSelectedMinute, PendingIntent pendingIntent, Context context) {
        if(pendingIntent == null) {
            Intent myIntent = new Intent(context, NotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 1412, myIntent, 0);
        }
        Calendar calendar = Calendar.getInstance();
        long currentTimeInMillis = calendar.getTimeInMillis();
        int interval = 1000 * 60 * 60 * 24; // One day

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, currentSelectedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, currentSelectedHour);

        if(currentTimeInMillis > calendar.getTimeInMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + interval);
        }

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }

    public void createNotificationAlarmIfActive(List<ParameterObject> settingsObject, PendingIntent pendingIntent, Context context) {
        int hour = 0;
        int minute = 0;
        int reminderActive = 0;
        for (ParameterObject s : settingsObject) {
            switch (s.getName()) {
                case "daily_reminder_time":
                    String[] values = s.getValue().split(":");
                    try{
                        hour = Integer.parseInt(values[0]);
                        minute = Integer.parseInt(values[1]);
                    } catch(NumberFormatException nfe) {
                        hour = 17;
                        minute = 0;
                    }
                    break;
                case "daily_reminder":
                    try{
                        reminderActive = Integer.parseInt(s.getValue());

                    } catch(NumberFormatException nfe) {
                        reminderActive = 1;
                    }
            }
        }
        if(pendingIntent == null) {
            Intent myIntent = new Intent(context, NotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(context, 1412, myIntent, 0);
        }
        if(reminderActive == 1) {
            Calendar calendar = Calendar.getInstance();
            long currentTimeInMillis = calendar.getTimeInMillis();
            int interval = 1000 * 60 * 60 * 24; // One day

            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.HOUR_OF_DAY, hour);

            if(currentTimeInMillis > calendar.getTimeInMillis()) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + interval);
            }

            AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
        }

    }

    public int getPercentComplete(double currentNum, double goalNum){
        if(goalNum == 0) {
            if(currentNum > 0) {
                return 100;
            }
            else {
                return 0;
            }
        }

        return (int) ((currentNum/goalNum) * 100);
    }

    public void zoomImageFromThumb(View convertView, final View thumbView, Bitmap bmp, @NonNull Context context, ImageView expanded) {
        mShortAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

        if(imageIsExpanded) {
            unzoomImageFromThumbnail(convertView, expanded);
        }
        else {
            imageIsExpanded = true;
            // If there's an animation in progress, cancel it
            // immediately and proceed with this one.
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Load the high-resolution "zoomed-in" image.
            expanded.setImageBitmap(bmp);

            // Calculate the starting and ending bounds for the zoomed-in image.
            // This step involves lots of math. Yay, math.
//            final Rect startBounds = new Rect();
//            final Rect finalBounds = new Rect();
//            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            thumbView.getGlobalVisibleRect(startBounds);
            convertView.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            // Adjust the start bounds to be the same aspect ratio as the final
            // bounds using the "center crop" technique. This prevents undesirable
            // stretching during the animation. Also calculate the start scaling
            // factor (the end scaling factor is always 1.0).
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                // Extend start bounds horizontally
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation
            // begins, it will position the zoomed-in view in the place of the
            // thumbnail.
            convertView.setAlpha(.5f);
            expanded.setVisibility(View.VISIBLE);

            // Set the pivot point for SCALE_X and SCALE_Y transformations
            // to the top-left corner of the zoomed-in view (the default
            // is the center of the view).
            expanded.setPivotX(0f);
            expanded.setPivotY(0f);

            // Construct and run the parallel animation of the four translation and
            // scale properties (X, Y, SCALE_X, and SCALE_Y).
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(expanded, View.X,
                            startBounds.left, (float) (finalBounds.centerX() / 2)))
                    .with(ObjectAnimator.ofFloat(expanded, View.Y,
                            startBounds.top, (float) (finalBounds.centerY() / 2)))
                    .with(ObjectAnimator.ofFloat(expanded, View.SCALE_X,
                            startScale, 1f))
                    .with(ObjectAnimator.ofFloat(expanded,
                            View.SCALE_Y, startScale, 1f));
            set.setDuration(mShortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;

            // Upon clicking the zoomed-in image, it should zoom back down
            // to the original bounds and show the thumbnail instead of
            // the expanded image.
            expanded.setOnClickListener(view -> unzoomImageFromThumbnail(convertView, expanded));
        }
    }

    public void unzoomImageFromThumbnail(View parentView, ImageView expanded) {
        imageIsExpanded = false;
        final float startScaleFinal = startScale;
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(expanded, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expanded,
                                View.Y, startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expanded,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(expanded,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
//        final View parentView = findViewById(R.id.linearLayout);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                parentView.setAlpha(1f);
                expanded.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                parentView.setAlpha(1f);
                expanded.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

}