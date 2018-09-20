package co.sisu.mobile.controllers;

import android.graphics.Color;

import co.sisu.mobile.R;
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



    public void setColorScheme(TeamColorSchemeObject[] colorScheme, String colorSchemeId) {

//        setDefaults(colorSchemeId);
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
                case "logo":
                    logo = colorSchemeObject.getTheme_data();
                    break;
                case "icon":
                    icon = colorSchemeObject.getTheme_data();
                    break;
                case "darker_text":
                    darkerTextColor = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "topbar_text":
                    actionbarText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "button_text":
                    buttonText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "button_selected":
                    buttonSelected = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "button_border":
                    buttonBorder = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "button_background":
                    buttonBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "bottombar_text":
                    toolbarText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "menu_background":
                    menuBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "menu_text":
                    menuText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "menu_selected":
                    menuSelected = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "spinner_text":
                    spinnerText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "spinner_background":
                    spinnerBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "progress_background":
                    progressBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "segment_selected":
                    segmentSelected = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "menu_selected_text":
                    menuSelectedText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "segment_line":
                    segmentLine = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "segment_background":
                    segmentBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "line":
                    line = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
            }

        }
    }

    private void setDefaults(String colorSchemeId) {

        if(colorSchemeId.equals("0")) {
            //Dark Theme
            iconActive = R.color.colorCorporateOrange;
            iconIdle = R.color.colorCorporateGrey;
            appBackground = R.color.colorAlmostBlack;
            toolbarBackground = R.color.colorDarkGrey;
            actionbarBackground = R.color.colorAlmostBlack;
            normalTextColor = R.color.colorLightGrey;
            lighterTextColor = R.color.colorLightGrey;
//            logo = ;
//            icon = ;
            darkerTextColor = R.color.colorLightGrey;
            actionbarText = R.color.colorLightGrey;
            buttonText = R.color.colorLightGrey;
            buttonSelected = R.color.colorCorporateOrange;
            buttonBorder = R.color.colorLightGrey;
            buttonBackground = R.color.colorAlmostBlack;
            toolbarText = R.color.colorLabelText;
            menuBackground = R.color.colorAlmostBlack;
            menuText = R.color.colorLightGrey;
            menuSelected = R.color.colorCorporateOrange;
            spinnerText = R.color.colorLightGrey;
            spinnerBackground = R.color.colorAlmostBlack;
            progressBackground = R.color.colorWhite;
            segmentSelected = R.color.colorCorporateOrange;
            menuSelectedText = R.color.colorLightGrey;
            segmentLine = R.color.colorCorporateOrange;
            segmentBackground = R.color.colorCorporateOrange;
            line = R.color.colorCorporateOrange;
        }
        else {
            //Light Theme
            iconActive = R.color.colorCorporateOrange;
            iconIdle = R.color.colorLightGrey;
            appBackground = R.color.colorWhite;
            toolbarBackground = R.color.colorWhite;
            actionbarBackground = R.color.colorWhite;
            normalTextColor = R.color.colorDarkGrey;
            lighterTextColor = R.color.colorDarkGrey;
//            logo = ;
//            icon = ;
            darkerTextColor = R.color.colorDarkGrey;
            actionbarText = R.color.colorDarkGrey;
            buttonText = R.color.colorDarkGrey;
            buttonSelected = R.color.colorCorporateOrange;
            buttonBorder = R.color.colorLightGrey;
            buttonBackground = R.color.colorCorporateOrange;
            toolbarText = R.color.colorDarkGrey;
            menuBackground = R.color.colorWhite;
            menuText = R.color.colorDarkGrey;
            menuSelected = R.color.colorCorporateOrange;
            spinnerText = R.color.colorDarkGrey;
            spinnerBackground = R.color.colorWhite;
            progressBackground = R.color.colorCorporateOrange;
            segmentSelected = R.color.colorCorporateOrange;
            menuSelectedText = R.color.colorDarkGrey;
            segmentLine = R.color.colorCorporateOrange;
            segmentBackground = R.color.colorCorporateOrange;
            line = R.color.colorCorporateOrange;
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

    public int getButtonText() {
        return buttonText;
    }

    public int getButtonSelected() {
        return buttonSelected;
    }

    public int getButtonBorder() {
        return buttonBorder;
    }

    public int getButtonBackground() {
        return buttonBackground;
    }

    public int getToolbarText() {
        return toolbarText;
    }

    public int getMenuBackground() {
        return menuBackground;
    }

    public int getMenuText() {
        return menuText;
    }

    public int getMenuSelected() {
        return menuSelected;
    }

    public int getSpinnerText() {
        return spinnerText;
    }

    public int getProgressBackground() {
        return progressBackground;
    }

    public String getLogo() {
        return logo;
    }

    public int getSegmentSelected() {
        return segmentSelected;
    }

    public int getMenuSelectedText() {
        return menuSelectedText;
    }

    public int getSegmentLine() {
        return segmentLine;
    }

    public int getLine() {
        return line;
    }

    public int getSegmentBackground() {
        return segmentBackground;
    }

    public int getSpinnerBackground() {
        return spinnerBackground;
    }

    public String getIcon() {
        return icon;
    }
}
