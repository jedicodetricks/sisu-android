package co.sisu.mobile.controllers;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;

import java.util.Stack;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;

/**
 * Created by bradygroharing on 5/22/18.
 */

@SuppressWarnings("rawtypes")
public class NavigationManager {

    private ParentActivity parentActivity;
    private ToolbarManager toolbarManager;
    private String fragmentTag;
    private Stack<Class> backStack;
    private ColorSchemeManager colorSchemeManager;

    public NavigationManager(ParentActivity parentActivity) {
        this.colorSchemeManager = parentActivity.getColorSchemeManager();
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
        if(backStack.size() > 0) {
            backStack.pop();
        }
        toolbarManager.manage(fragmentClass.getSimpleName());
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
        toolbarManager.manage(fragmentClass.getSimpleName());
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
        if(backStack.size() > 0) {
            backStack.clear();
        }
        toolbarManager.manage(fragmentClass.getSimpleName());
        backStack.add(fragmentClass);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment, fragmentTag).commitAllowingStateLoss();
    }

    public void onBackPressed() {
        if(backStack.size() < 2 /*&& backPressed < 1*/) { //needs if statement checking if on root fragment, app is always on root activity.. need fragment management
            if(colorSchemeManager.getAppBackground() != Color.WHITE) {
                new AlertDialog.Builder(parentActivity,R.style.darkDialog)
                        .setIcon(R.drawable.sisu_mark)
                        .setTitle("Closing Sisu")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> parentActivity.finish())
                        .setNegativeButton("No", null)
                        .setOnKeyListener((dialog, keyCode, event) -> {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                parentActivity.finish();
                                dialog.dismiss();
                            }
                            return true;
                        })
                        .show();
            } else {
                new AlertDialog.Builder(parentActivity,R.style.lightDialog)
                        .setIcon(R.drawable.sisu_mark)
                        .setTitle("Closing Sisu")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> parentActivity.finish())
                        .setNegativeButton("No", null)
                        .setOnKeyListener((dialog, keyCode, event) -> {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                parentActivity.finish();
                                dialog.dismiss();
                            }
                            return true;
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

    public void updateColorSchemeManager(ColorSchemeManager colorSchemeManager) {
        toolbarManager.updateColorSchemeManager(colorSchemeManager);
    }
}
