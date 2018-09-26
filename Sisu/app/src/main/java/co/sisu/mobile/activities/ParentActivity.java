package co.sisu.mobile.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.MyFirebaseMessagingService;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.controllers.NotificationReceiver;
import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.RecordFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncFirebaseDeviceJsonObject;
import co.sisu.mobile.models.AsyncGoalsJsonObject;
import co.sisu.mobile.models.AsyncLabelsJsonObject;
import co.sisu.mobile.models.AsyncParameterJsonObject;
import co.sisu.mobile.models.AsyncSettingsJsonObject;
import co.sisu.mobile.models.AsyncTeamColorSchemeObject;
import co.sisu.mobile.models.AsyncUpdateActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.FirebaseDeviceObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.models.ParameterObject;
import co.sisu.mobile.models.TeamColorSchemeObject;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.models.UpdateActivitiesModel;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class ParentActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AsyncServerEventListener {

    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    public ColorSchemeManager colorSchemeManager;
    private MyFirebaseMessagingService myFirebaseMessagingService;
    public ProgressBar parentLoader;
    private String currentSelectedRecordDate = "";
    private boolean clientFinished = false;
    private boolean goalsFinished = false;
    private boolean teamParamFinished = false;
    private boolean settingsFinished = false;
    private boolean colorSchemeFinished = false;
    private boolean teamsFinished = false;
    private boolean labelsFinished = false;
    private boolean noNavigation = true;
    private String timeline = "month";
    private int timelineSelection = 5;
    private AgentModel agent;
    private NotesObject selectedNote;
    private LruCache<String, Bitmap> mMemoryCache;
    private boolean imageIsExpanded = false;
    private ImageView expanded;
    private FirebaseDeviceObject currentDevice;
    private ConstraintLayout layout;
    private Toolbar toolbar;
    private NavigationView navView;
    private ListView navViewList;
    private TextView navTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorCorporateGrey)));

        parentLoader = findViewById(R.id.parentLoader);

        dataController = new DataController();
        colorSchemeManager = new ColorSchemeManager();
        navigationManager = new NavigationManager(this);
        apiManager = new ApiManager(this);
        agent = getIntent().getParcelableExtra("Agent");
        dataController.setAgent(agent);
        apiManager.getFirebaseDevices(this, agent.getAgent_id());

        initParentFields();
        initializeButtons();
        apiManager.sendAsyncTeams(this, agent.getAgent_id());
        apiManager.sendAsyncClients(this, agent.getAgent_id());

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void initParentFields() {
        layout = findViewById(R.id.parentLayout);
        toolbar = findViewById(R.id.toolbar);
        navViewList = findViewById(R.id.navViewList);
        navTitle = findViewById(R.id.nav_title);
    }


    public void setActivityColors() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("SETTING COLORS", "PARENT ACTIVITY");
                layout.setBackgroundColor(colorSchemeManager.getAppBackground());
                toolbar.setBackgroundColor(colorSchemeManager.getToolbarBackground());
                navViewList.setBackgroundColor(colorSchemeManager.getAppBackground());
                navTitle.setBackgroundColor(colorSchemeManager.getAppBackground());
                navTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
                //change parentLoader here, if needed
                parentLoader = findViewById(R.id.parentLoader);
                if(colorSchemeManager.getAppBackground() == Color.WHITE) {
                    Rect bounds = parentLoader.getIndeterminateDrawable().getBounds();
                    parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark));
                    parentLoader.getIndeterminateDrawable().setBounds(bounds);
                } else {
                    Rect bounds = parentLoader.getIndeterminateDrawable().getBounds();
                    parentLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
                    parentLoader.getIndeterminateDrawable().setBounds(bounds);
                }
            }
        });
    }


    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
        else {
            Log.e("Key already exists", "Replacing " + key);
//            mMemoryCache.remove(key);
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

//    private void testSMSObserver() {
//        Log.e("TURNING ON SMS", "THIS IS A TEST");
//        ContentResolver contentResolver = getContentResolver();
//        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, new MySMSObserver(new Handler(), this));
//    }

    private void initializeButtons(){
        ImageView scoreBoardButton = findViewById(R.id.scoreboardView);
        scoreBoardButton.setOnClickListener(this);

        ImageView reportButton = findViewById(R.id.reportView);
        reportButton.setOnClickListener(this);

        ImageView recordButton = findViewById(R.id.recordView);
        recordButton.setOnClickListener(this);

        ImageView leaderBoardButton = findViewById(R.id.leaderBoardView);
        leaderBoardButton.setOnClickListener(this);

        ImageView moreButton = findViewById(R.id.moreView);
        moreButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(dataController.getUpdatedRecords().size() > 0) {
            updateRecordedActivities();
        }

        switch (v.getId()) {
            case R.id.action_bar_home:
                navigationManager.toggleDrawer();
                break;
            case R.id.scoreboardView:
                navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                break;
            case R.id.reportView:
                noNavigation = false;
                navigationManager.clearStackReplaceFragment(ReportFragment.class);
                break;
            case R.id.recordView:
                noNavigation = false;
                navigationManager.clearStackReplaceFragment(RecordFragment.class);
                break;
            case R.id.leaderBoardView:
                noNavigation = false;
                navigationManager.clearStackReplaceFragment(LeaderboardFragment.class);
                break;
            case R.id.moreView:
                noNavigation = false;
                navigationManager.clearStackReplaceFragment(MoreFragment.class);
                break;
            case R.id.cancelButton:
                navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
            default:
                break;
        }
    }

    public void updateRecordedActivities() {
        List<Metric> updatedRecords = dataController.getUpdatedRecords();
        List<UpdateActivitiesModel> updateActivitiesModels = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        AsyncUpdateActivitiesJsonObject activitiesJsonObject = new AsyncUpdateActivitiesJsonObject();
        for(Metric m : updatedRecords) {
            if(currentSelectedRecordDate.equals("")) {
                updateActivitiesModels.add(new UpdateActivitiesModel(formatter.format(d), m.getType(), m.getCurrentNum(), Integer.valueOf(agent.getAgent_id())));
            }
            else {
                updateActivitiesModels.add(new UpdateActivitiesModel(currentSelectedRecordDate, m.getType(), m.getCurrentNum(), Integer.valueOf(agent.getAgent_id())));
            }
        }
        UpdateActivitiesModel[] array = new UpdateActivitiesModel[updateActivitiesModels.size()];
        updateActivitiesModels.toArray(array);

        activitiesJsonObject.setActivities(array);

        apiManager.sendAsyncUpdateActivities(this, agent.getAgent_id(), activitiesJsonObject);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //This is what goes off when you click a new team.
        TeamObject team = (TeamObject) parent.getItemAtPosition(position);
        navigationManager.updateTeam(team);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.your_placeholder);
        navigationManager.updateSelectedTeam(position);
        apiManager.getTeamParams(this, dataController.getAgent().getAgent_id(), team.getId());
        switch (f.getTag()) {
            case "Scoreboard":
                ((ScoreboardFragment) f).teamSwap();
                break;
            case "Record":
                ((RecordFragment) f).teamSwap();
                break;
            case "Report":
                ((ReportFragment) f).teamSwap();
                break;
            case "Leaderboard":
                ((LeaderboardFragment) f).teamSwap();
                break;
        }
        navigationManager.closeDrawer();
    }

    private void navigateToScoreboard() {
        if(clientFinished && goalsFinished && settingsFinished && teamParamFinished && colorSchemeFinished && labelsFinished && noNavigation) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.clearStackReplaceFragment(ScoreboardFragment.class);
                }
            });
            clientFinished = false;
            goalsFinished = false;
            settingsFinished = false;
            teamParamFinished = false;
            colorSchemeFinished = false;
            labelsFinished = false;
            noNavigation = true;
        }
    }

    @Override
    public void onBackPressed() {
        navigationManager.onBackPressed();
    }

    public void showToast(final CharSequence msg){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(ParentActivity.this, msg,Toast.LENGTH_SHORT);
                View view = toast.getView();
                TextView text = (TextView) view.findViewById(android.R.id.message);
                text.setTextColor(Color.WHITE);
                text.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorCorporateOrange));
                view.setBackgroundResource(R.color.colorCorporateOrange);
                text.setPadding(20, 8, 20, 8);
                toast.show();
            }
        });
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Teams")) {
            dataController.setTeamsObject(ParentActivity.this, returnObject);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    navigationManager.initializeTeamBar(dataController.getTeamsObject());
                    if(dataController.getTeamsObject().size() > 0) {
                        apiManager.getTeamParams(ParentActivity.this, agent.getAgent_id(), dataController.getTeamsObject().get(0).getId());
                        SaveSharedPreference.setTeam(ParentActivity.this, navigationManager.getSelectedTeamId() + "");
                        if(settingsFinished) {
                            apiManager.getColorScheme(ParentActivity.this, agent.getAgent_id(), navigationManager.getSelectedTeamId(), dataController.getColorSchemeId());
                            apiManager.getLabels(ParentActivity.this, agent.getAgent_id(), navigationManager.getSelectedTeamId());
                        }
                    }
                    else {
                        teamParamFinished = true;
                        dataController.setSlackInfo(null);
                    }
                    teamsFinished = true;
                    apiManager.sendAsyncAgentGoals(ParentActivity.this, agent.getAgent_id());
                    apiManager.sendAsyncSettings(ParentActivity.this, agent.getAgent_id());
                }
            });
        }
        else if(asyncReturnType.equals("Get Firebase Device")) {
            AsyncFirebaseDeviceJsonObject asyncFirebaseDeviceJsonObject = (AsyncFirebaseDeviceJsonObject) returnObject;
            FirebaseDeviceObject[] devices = asyncFirebaseDeviceJsonObject.getDevices();
            String firebaseDeviceId = SaveSharedPreference.getFirebaseDeviceId(this);

            for(FirebaseDeviceObject fdo : devices) {
                if(fdo.getDevice_id().equals(firebaseDeviceId)) {
                    Log.e("Current Device", fdo.getDevice_id());
                    currentDevice = fdo;
                }
            }
            myFirebaseMessagingService = new MyFirebaseMessagingService(apiManager, dataController.getAgent(), this.getApplicationContext(), currentDevice);

            if(firebaseDeviceId.equals("")) {
                myFirebaseMessagingService.initFirebase();
            }
            else {
                myFirebaseMessagingService.refreshToken();
            }


        }
        else if(asyncReturnType.equals("Goals")) {
            AsyncGoalsJsonObject goals = (AsyncGoalsJsonObject) returnObject;
            AgentGoalsObject[] agentGoalsObject = goals.getGoalsObjects();
            dataController.setAgentGoals(agentGoalsObject);
            goalsFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Settings")) {
            AsyncSettingsJsonObject settingsJson = (AsyncSettingsJsonObject) returnObject;
            ParameterObject[] settings = settingsJson.getParameters();
            dataController.setSettings(settings); //sets settings, and fills with default alarm notification if empty/not set yet
            List<ParameterObject> newSettings = dataController.getSettings(); //this is the new settings object list including any defaults generated
            settingsFinished = true;
            int hour = 0;
            int minute = 0;
            int reminderActive = 0;
            for (ParameterObject s : newSettings) {
                Log.e(s.getName(), s.getValue());
                switch (s.getName()) {
                    case "daily_reminder_time":
                        String[] values = s.getValue().split(":");
                        try{
                            hour = Integer.parseInt(values[0]);
                            minute = Integer.parseInt(values[1]);
                            Log.e("ALARM TIME", hour + " " + minute);
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

            if(reminderActive == 1) {
                createNotificationAlarm(hour, minute, null); //sets the actual alarm with correct times from user settings
            }
            if(teamsFinished) {
                apiManager.getColorScheme(ParentActivity.this, agent.getAgent_id(), navigationManager.getSelectedTeamId(), dataController.getColorSchemeId());
                apiManager.getLabels(ParentActivity.this, agent.getAgent_id(), navigationManager.getSelectedTeamId());
            }
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Team Parameters")) {
            AsyncParameterJsonObject settingsJson = (AsyncParameterJsonObject) returnObject;
            if(settingsJson.getStatus_code().equals("-1")) {
                dataController.setSlackInfo(null);
            }
            else {
                ParameterObject params = settingsJson.getParameter();
                dataController.setSlackInfo(params.getValue());
            }
            teamParamFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Get Color Scheme")) {
            AsyncTeamColorSchemeObject colorJson = (AsyncTeamColorSchemeObject) returnObject;
            TeamColorSchemeObject[] colorScheme = colorJson.getTheme();
            colorSchemeManager.setColorScheme(colorScheme, dataController.getColorSchemeId());
            setActivityColors();
            colorSchemeFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Get Labels")) {
            AsyncLabelsJsonObject labelObject = (AsyncLabelsJsonObject) returnObject;
            HashMap<String, String> labels = labelObject.getMarket();
            dataController.setLabels(labels);
            labelsFinished = true;
            navigateToScoreboard();
        }
        else if(asyncReturnType.equals("Update Activities")) {
            dataController.clearUpdatedRecords();
        }
        else if(asyncReturnType.equals("Clients")) {
            dataController.setClientListObject(returnObject);
            clientFinished = true;
            navigateToScoreboard();
        }
    }

    public void createNotificationAlarm(int currentSelectedHour, int currentSelectedMinute, PendingIntent pendingIntent) {
        if(pendingIntent == null) {
            Intent myIntent = new Intent(this, NotificationReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 1412, myIntent, 0);
        }
        Calendar calendar = Calendar.getInstance();
        long currentTimeInMillis = calendar.getTimeInMillis();
        int interval = 1000 * 60 * 60 * 24; // One day

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MINUTE, currentSelectedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, currentSelectedHour);

        if(currentTimeInMillis > calendar.getTimeInMillis()) {
            Log.e("Calendar", "Calendar can't be set in the past");
            calendar.setTimeInMillis(calendar.getTimeInMillis() + interval);
        }

        Log.e("CALENDAR SET", calendar.getTime().toString());
        Log.e("CALENDAR CURRENT TIME", Calendar.getInstance().getTime().toString());

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

        Log.e("FAILURE", asyncReturnType);

    }

    private Animator mCurrentAnimator;
    float startScale;
    final Rect startBounds = new Rect();
    final Rect finalBounds = new Rect();
    final Point globalOffset = new Point();
    int mShortAnimationDuration;

    public void zoomImageFromThumb(View convertView, final View thumbView, Bitmap bmp) {
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        expanded = findViewById(R.id.expanded_image);

        if(imageIsExpanded) {
            unzoomImageFromThumbnail();

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
                            startBounds.left, finalBounds.centerX() / 2))
                    .with(ObjectAnimator.ofFloat(expanded, View.Y,
                            startBounds.top, finalBounds.centerY() / 2))
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
            expanded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    unzoomImageFromThumbnail();
                }
            });
        }

    }

    public void unzoomImageFromThumbnail() {
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
        final View parentView = findViewById(R.id.linearLayout);
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


    // GETTERS AND SETTERS

    public NotesObject getSelectedNote() {
        return selectedNote;
    }

    public void setSelectedNote(NotesObject selectedNote) {
        this.selectedNote = selectedNote;
    }

    public void setSelectedClient(ClientObject client) {
        navigationManager.setSelectedClient(client);
    }

    public ClientObject getSelectedClient() {
        return navigationManager.getSelectedClient();
    }

    public void updateSelectedRecordDate(String formattedDate) {
        this.currentSelectedRecordDate = formattedDate;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public int getTimelineSelection() {
        return timelineSelection;
    }

    public void setTimelineSelection(int timelineSelection) {
        this.timelineSelection = timelineSelection;
    }

    public int getSelectedTeamId() {
        int teamId = navigationManager.getSelectedTeamId();
        return teamId;
    }

    public NavigationManager getNavigationManager() {
        return navigationManager;
    }

    public DataController getDataController() {
        return dataController;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public ColorSchemeManager getColorSchemeManager() {
        return colorSchemeManager;
    }

    public boolean imageExists(Context context, String id) {
        return "".equals(id) || context.getDir(id, Context.MODE_PRIVATE).exists();
    }

    public int getMarketId() {
        return navigationManager.getMarketId();
    }

    public HashMap<String, String> getLabels() {
        return dataController.getLabels();
    }

    public String localizeLabel(String toCheck) {
        String toReturn = "";
        if(toCheck != null) {
            toReturn = dataController.localizeLabel(toCheck);
        }
        return toReturn;
    }

}
