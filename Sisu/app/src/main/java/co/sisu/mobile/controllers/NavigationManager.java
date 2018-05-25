package co.sisu.mobile.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import java.util.Stack;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.fragments.ActivitySettingsFragment;
import co.sisu.mobile.fragments.AddClientFragment;
import co.sisu.mobile.fragments.ClientEditFragment;
import co.sisu.mobile.fragments.ClientListFragment;
import co.sisu.mobile.fragments.FeedbackFragment;
import co.sisu.mobile.fragments.LeaderboardFragment;
import co.sisu.mobile.fragments.MoreFragment;
import co.sisu.mobile.fragments.ReportFragment;
import co.sisu.mobile.fragments.ScoreboardFragment;
import co.sisu.mobile.models.ClientObject;

/**
 * Created by bradygroharing on 5/22/18.
 */

public class NavigationManager {

    private ParentActivity parentActivity;

    private boolean activeBacktionBar = false;
    private boolean activeClientListBar = false;
    private boolean activeTitleBar = false;
    private boolean activeAddClientBar = false;
    private String addClientChild = "";
    private String fragmentTag;
    private boolean clientFinished = false;
    private boolean goalsFinished = false;
    private boolean settingsFinished = false;
    private ClientObject selectedClient;
    private TextView backtionTitle, title;
    private Stack<Class> backStack;

    public NavigationManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
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
            swapToAddClientBar(null);
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
            //Backtion Bar
            swapToBacktionBar("Testing", null);
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
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
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
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void navigateToClientList(String tab, Class child) {
        Fragment fragment = null;
        try {
            fragment = ClientListFragment.newInstance(tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        backStack.add(child);
        swapToClientListBar();
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();

        if(child !=  null) {
            fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
        }
        else {
            fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
        }
        parentActivity.resetToolbarImages("more");
    }

//    public void navigateToScoreboard() {
//        if(clientFinished && goalsFinished && settingsFinished) {
//            parentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    parentActivity.resetToolbarImages("scoreboard");
//                    replaceFragment(ScoreboardFragment.class);
//                }
//            });
//        }
//    }




    // ACTION BARS

    public void swapToBacktionBar(final String titleString, final String child) {
        //Get it?! Back action... Backtion!
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetAllActionBars();
                if(child != null) {
                    addClientChild = child;
                }
                activeBacktionBar = true;
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
        resetAllActionBars();
        activeTitleBar = true;
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
        resetAllActionBars();
//        activeClientListBar = true;
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_clients_layout);
            }
        });
    }

    public void swapToAddClientBar(String child) {
        resetAllActionBars();
        activeAddClientBar = true;
        addClientChild = child;
        parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_add_client_layout);
    }

    private void resetAllActionBars() {
        activeBacktionBar = false;
        activeClientListBar = false;
        activeTitleBar = false;
        activeAddClientBar = false;
    }

    public void onBackPressed(FragmentManager fragManager) {
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
            replaceFragment(backStack.get(backStack.size() - 1));
            Log.e("BACK STACK", String.valueOf(backStack.size()));
//            for(int i = 0; i < backStack.size(); i++) {
//                Log.e("BACK " + i, backStack.get(i).getSimpleName());
//            }
        }
    }
}
