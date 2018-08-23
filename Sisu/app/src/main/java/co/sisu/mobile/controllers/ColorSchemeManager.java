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
    private int buttonText;
    private int buttonSelected;
    private int buttonBorder;
    private int buttonBackground;
    private int actionbarText;
    private int toolbarText;
    private int normalTextColor;
    private int lighterTextColor;
    private int darkerTextColor;
    private int menuBackground;
    private int menuText;
    private int menuSelected;
    private int spinnerText;
    private int progressBackground;
    private String logo;
    private int segmentSelected;
    private int menuSelectedText;
    private int segmentLine;
    private int line;
    private int segmentBackground;
    private int spinnerBackground;
    private String icon;



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
                case "normal_text":
                    normalTextColor = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "lighter_text":
                    lighterTextColor = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "darker_text":
                    darkerTextColor = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "topbar_text":
                    actionbarText = Color.parseColor(colorSchemeObject.getTheme_data());
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

    public int getNormalTextColor() {
        return normalTextColor;
    }

    public int getLighterTextColor() {
        return lighterTextColor;
    }

    public int getDarkerTextColor() {
        return darkerTextColor;
    }

    public int getActionbarText() {
        return actionbarText;
    }
}
