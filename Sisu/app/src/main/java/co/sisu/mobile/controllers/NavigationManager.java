package co.sisu.mobile.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import java.util.List;
import java.util.Stack;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.fragments.ClientListFragment;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by bradygroharing on 5/22/18.
 */

public class NavigationManager {

    private ParentActivity parentActivity;
    private ToolbarManager toolbarManager;
    private ActionBarManager actionBarManager;
    private String fragmentTag;
    private ClientObject selectedClient;
    private TextView backtionTitle, title;
    private Stack<Class> backStack;

    public NavigationManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.toolbarManager = new ToolbarManager(parentActivity);
        this.actionBarManager = new ActionBarManager(parentActivity);
        this.actionBarManager.initializeActionBar(fragmentTag);
        backStack = new Stack<>();
    }


    public void replaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        manageActionBar(fragmentClass);
        if(backStack.size() > 0) {
            backStack.pop();
        }
        backStack.add(fragmentClass);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    private void manageActionBar(Class fragmentClass) {
        if(fragmentClass.getSimpleName().equals("AddClientFragment")) {
            swapToAddClientBar();
        }
        else if(fragmentClass.getSimpleName().equals("ClientListFragment")) {
            swapToClientListBar();
        }
        else if(fragmentClass.getSimpleName().equals("FeedbackFragment") || fragmentClass.getSimpleName().equals("MoreFragment") ||
                fragmentClass.getSimpleName().equals("ScoreboardFragment") || fragmentClass.getSimpleName().equals("ReportFragment") ||
                fragmentClass.getSimpleName().equals("LeaderboardFragment")) {
            sortTitleBar(fragmentClass);
        }
        else {
            sortSaveActionBar(fragmentClass);
        }

    }

    private void sortSaveActionBar(Class fragmentClass) {
        if(fragmentClass.getSimpleName().equals("RecordFragment")) {
            fragmentTag = "Record";
            swapToSaveAction("Record");
            toolbarManager.resetToolbarImages(fragmentTag);
        }
        else if(fragmentClass.getSimpleName().equals("MyProfileFragment")) {
            fragmentTag = "MyProfile";
            swapToSaveAction("My Profile");
        }
        else if(fragmentClass.getSimpleName().equals("GoalSetupFragment")) {
            fragmentTag = "GoalSetup";
            swapToSaveAction("Goal Setup");
        }
        else if(fragmentClass.getSimpleName().equals("ActivitySettingsFragment")) {
            fragmentTag = "ActivitySettings";
            swapToSaveAction("Activity Settings");
        }
        else if(fragmentClass.getSimpleName().equals("SettingsFragment")) {
            fragmentTag = "Settings";
            swapToSaveAction("Settings");
        }
    }

    private void sortTitleBar(Class fragmentClass) {
        if(fragmentClass.getSimpleName().equals("FeedbackFragment")) {
            fragmentTag = "Feedback";
            swapToTitleBar("Feedback");
        }
        else if(fragmentClass.getSimpleName().equals("MoreFragment")) {
            fragmentTag = "More";
            swapToTitleBar("More");
        }
        else if(fragmentClass.getSimpleName().equals("ScoreboardFragment")) {
            fragmentTag = "Scoreboard";
            swapToTitleBar("Scoreboard");
        }
        else if(fragmentClass.getSimpleName().equals("ReportFragment")) {
            fragmentTag = "Report";
            swapToTitleBar("Report");
        }
        else if(fragmentClass.getSimpleName().equals("LeaderboardFragment")) {
            fragmentTag = "Leaderboard";
            swapToTitleBar("Leaderboard");
        }

        toolbarManager.resetToolbarImages(fragmentTag);
    }

    public void stackReplaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        manageActionBar(fragmentClass);
        backStack.add(fragmentClass);
        // Insert the fragment by replacing any existing fragment and adding it to the stack
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commit();
    }

    public void clearStackReplaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        manageActionBar(fragmentClass);
        if(backStack.size() > 0) {
            backStack.clear();
        }
        backStack.add(fragmentClass);
        actionBarManager.initializeActionBar(fragmentTag);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void navigateToClientList(String tab) {
        Fragment fragment = null;
        try {
            fragment = ClientListFragment.newInstance(tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        backStack.add(ClientListFragment.class);
        swapToClientListBar();
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commit();
        toolbarManager.resetToolbarImages("More");
    }

    // ACTION BARS

    public void swapToSaveAction(final String titleString) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
                backtionTitle = parentActivity.findViewById(R.id.actionBarTitle);
                if(titleString == null) {
                    String displayName = "";
                    if(selectedClient.getFirst_name() != null) {
                        displayName += selectedClient.getFirst_name() + " ";
                    }
                    if(selectedClient.getLast_name() != null) {
                        displayName += selectedClient.getLast_name();
                    }
                    backtionTitle.setText(displayName);
                } else {
                    backtionTitle.setText(titleString);
                }
            }
        });

    }

    public void swapToTitleBar(final String titleString) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_title_layout);
                title = parentActivity.findViewById(R.id.title);
                title.setText(titleString);
            }
        });

    }

    public void swapToClientListBar() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_clients_layout);
            }
        });
    }

    public void swapToAddClientBar() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_add_client_layout);
            }
        });
    }

    public void onBackPressed() {
        if(backStack.size() < 2 /*&& backPressed < 1*/) { //needs if statement checking if on root fragment, app is always on root activity.. need fragment management
            AlertDialog dialog = new AlertDialog.Builder(parentActivity)
                    .setIcon(R.drawable.sisu_mark)
                    .setTitle("Closing Sisu")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parentActivity.finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .setOnKeyListener( new Dialog.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                parentActivity.finish();
                                dialog.dismiss();
                            }
                            return true;
                        }
                    })
                    .show();
        } else {
            backStack.pop();
            Log.e("BACK STACK", String.valueOf(backStack.size()));
            Log.e("BACK STACK FRAG", String.valueOf(backStack.get(backStack.size() - 1)));
            replaceFragment(backStack.get(backStack.size() - 1));
        }
    }


    public void initializeTeamBar(List<TeamObject> teamsObject) {
        actionBarManager.initializeTeamBar(teamsObject);
    }

    public void updateTeam(TeamObject team) {
        actionBarManager.updateTeam(team);
    }

    public void toggleDrawer() {
        actionBarManager.toggleDrawer();
    }

    public void closeDrawer() {
        actionBarManager.closeDrawer();
    }

    public void setSelectedClient(ClientObject selectedClient) {
        this.selectedClient = selectedClient;
    }

    public ClientObject getSelectedClient() {
        return selectedClient;
    }

    public int getSelectedTeamId() {
        return actionBarManager.getSelectedTeamId();
    }

    public void updateSelectedTeam(int position) {
        actionBarManager.updateSelectedTeam(position);
    }
}
