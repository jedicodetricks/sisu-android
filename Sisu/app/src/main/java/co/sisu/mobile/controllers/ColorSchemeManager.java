package co.sisu.mobile.controllers;

import android.graphics.Color;

import co.sisu.mobile.models.TeamColorSchemeObject;

/**
 * Created by bradygroharing on 8/22/18.
 */

public class ColorSchemeManager {
    private int iconIdle;
    private int iconActive;
    private int appBackground;
    private int toolbarBackground;
    private int actionbarBackground;


    public void setColorScheme(TeamColorSchemeObject[] colorScheme) {
        //TODO: Loop through and set all the color shit

        for(TeamColorSchemeObject colorSchemeObject : colorScheme) {
            switch (colorSchemeObject.getName()) {
                case "icon_selected":
                    iconActive = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "icon_idle":
                    iconIdle = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "app_background":
                    appBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "bottombar_background":
                    toolbarBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "topbar_background":
                    actionbarBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;

            }

        }
    }

    public int getIconIdle() {
        return iconIdle;
    }

    public int getIconActive() {
        return iconActive;
    }

    public int getAppBackground() {
        return appBackground;
    }

    public int getToolbarBackground() {
        return toolbarBackground;
    }

    public int getActionbarBackground() {
        return actionbarBackground;
    }
}
