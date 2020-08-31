package co.sisu.mobile.controllers;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import java.util.Stack;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.fragments.ClientListFragment;

/**
 * Created by bradygroharing on 5/22/18.
 */

public class NavigationManager {

    private ParentActivity parentActivity;
    private ToolbarManager toolbarManager;
    private String fragmentTag;
    private Stack<Class> backStack;
    private ColorSchemeManager colorSchemeManager;

    public NavigationManager(ParentActivity parentActivity) {
        this.colorSchemeManager = parentActivity.colorSchemeManager;
        this.parentActivity = parentActivity;
        this.toolbarManager = new ToolbarManager(parentActivity);
        backStack = new Stack<>();
    }

    public void replaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentTag = fragmentClass.getSimpleName();
        // TODO: Action Bar Issue
//        manageActionBar(fragmentClass, "");
        if(backStack.size() > 0) {
            backStack.pop();
        }
        toolbarManager.manage(fragmentClass);
        backStack.add(fragmentClass);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void stackReplaceFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentTag = fragmentClass.getSimpleName();
        // TODO: Action Bar Issue
        toolbarManager.manage(fragmentClass);
//        manageActionBar(fragmentClass, "");
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
        fragmentTag = fragmentClass.getSimpleName();
        // TODO: Action Bar Issue
//        manageActionBar(fragmentClass, "");
        if(backStack.size() > 0) {
            backStack.clear();
        }
        toolbarManager.manage(fragmentClass);
        backStack.add(fragmentClass);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void clearStackReplaceFragment(Class fragmentClass, String titleString) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragmentTag = fragmentClass.getSimpleName();
        //TODO: Action Bar Issue
//        manageActionBar(fragmentClass, titleString);
        if(backStack.size() > 0) {
            backStack.clear();
        }
        toolbarManager.manage(fragmentClass);
        backStack.add(fragmentClass);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void navigateToClientList(String tab) {
        //TODO: Action Bar issue
        Fragment fragment = null;
        try {
            fragment = ClientListFragment.newInstance(tab);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Insert the fragment by replacing any existing fragment
        backStack.add(ClientListFragment.class);
//        actionBarManager.swapToClientListBar(null, DISABLE_DRAWER);
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commit();
        toolbarManager.resetToolbarImages("More");
    }

    public void onBackPressed() {
        if(fragmentTag != null && !fragmentTag.equalsIgnoreCase("ClientNoteFragment")) {
            //TODO: Action Bar issue
//            actionBarManager.resetClient();
        }
        if(backStack.size() < 2 /*&& backPressed < 1*/) { //needs if statement checking if on root fragment, app is always on root activity.. need fragment management
            if(colorSchemeManager.getAppBackground() != Color.WHITE) {
                AlertDialog dialog = new AlertDialog.Builder(parentActivity,R.style.darkDialog)
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
                AlertDialog dialog = new AlertDialog.Builder(parentActivity,R.style.lightDialog)
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
            }
        } else {
            backStack.pop();
            replaceFragment(backStack.get(backStack.size() - 1));
        }
    }

    public String getCurrentFragment() {
        return fragmentTag;
    }

}
