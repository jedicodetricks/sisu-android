package co.sisu.mobile.controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.fragments.ClientListFragment;
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

    public NavigationManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
    }


    public void replaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void navigateToClientList(String tab, String child) {
        Fragment fragment = null;
        try {
            fragment = ClientListFragment.newInstance(tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        swapToClientListBar(child);
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

    public void stackReplaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment and adding it to the stack
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).addToBackStack(fragmentTag).commit();
    }

    public void popStackReplaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commit();
    }



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

    public void swapToClientListBar(String child) {
        if(child != null) {
            addClientChild = child;
        }
        resetAllActionBars();
        activeClientListBar = true;
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
}
