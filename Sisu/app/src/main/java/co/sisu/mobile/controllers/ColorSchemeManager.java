package co.sisu.mobile.controllers;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

import co.sisu.mobile.R;
import co.sisu.mobile.models.TeamColorSchemeObject;

/**
 * Created by bradygroharing on 8/22/18.
 */

public class ColorSchemeManager {
    private int iconIdle;
    private int iconActive;
    private int appBackground;
    private int bottomBarBackground;
    private int bottomBarText;
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
    private int roundedButtonColor;
    private String icon;
    private int menuIcon;
    private int primaryColor;

    public ColorSchemeManager() {}

    public ColorSchemeManager(JSONObject theme) throws JSONException {
        // TODO: Fix how we deal with this. There is no error handling
            if(theme.has("app_background")) {
                appBackground = Color.parseColor(theme.getString("app_background"));
            }
            if(theme.has("bottombar_background")) {
                bottomBarBackground = Color.parseColor(theme.getString("bottombar_background"));
            }
            if(theme.has("bottombar_text")) {
                bottomBarText = Color.parseColor(theme.getString("bottombar_text"));
            }
            if(theme.has("button_background")) {
                buttonBackground = Color.parseColor(theme.getString("button_background"));
            }
            if(theme.has("button_border")) {
                buttonBorder = Color.parseColor(theme.getString("button_border"));
            }
            if(theme.has("button_selected")) {
                buttonSelected = Color.parseColor(theme.getString("button_selected"));
            }
            if(theme.has("button_text")) {
                buttonText = Color.parseColor(theme.getString("button_text"));
            }
            if(theme.has("darker_text")) {
                darkerTextColor = Color.parseColor(theme.getString("darker_text"));
            }
            if(theme.has("icon_idle")) {
                iconIdle = Color.parseColor(theme.getString("icon_idle"));
            }
            if(theme.has("icon_active")) {
                iconActive = Color.parseColor(theme.getString("icon_active"));
            }
            if(theme.has("lighter_text")) {
                lighterTextColor = Color.parseColor(theme.getString("lighter_text"));
            }
            if(theme.has("line")) {
                line = Color.parseColor(theme.getString("line"));
            }
            if(theme.has("logo")) {
                // TODO: Do something different
//                appBackground = Color.parseColor(theme.getString("app_background"));
            }
            if(theme.has("menu_background")) {
                menuBackground = Color.parseColor(theme.getString("menu_background"));
            }
            if(theme.has("menu_icon")) {
                menuIcon = Color.parseColor(theme.getString("menu_icon"));
            }
            if(theme.has("menu_selected")) {
                menuSelected = Color.parseColor(theme.getString("menu_selected"));
            }
            if(theme.has("menu_selected_text")) {
                menuSelectedText = Color.parseColor(theme.getString("menu_selected_text"));
            }
            if(theme.has("menu_text")) {
                menuText = Color.parseColor(theme.getString("menu_text"));
            }
            if(theme.has("normal_text")) {
                normalTextColor = Color.parseColor(theme.getString("normal_text"));
            }
            if(theme.has("progress_background")) {
                progressBackground = Color.parseColor(theme.getString("progress_background"));
            }
            if(theme.has("progress_complete")) {
//                appBackground = Color.parseColor(theme.getString("progress_complete"));
            }
            if(theme.has("progress_offtrack")) {
//                appBackground = Color.parseColor(theme.getString("progress_offtrack"));
            }
            if(theme.has("progress_ontrack")) {
//                appBackground = Color.parseColor(theme.getString("progress_ontrack"));
            }
            if(theme.has("rounded_button")) {
                roundedButtonColor = Color.parseColor(theme.getString("rounded_button"));
            }
            if(theme.has("segment_background")) {
                segmentBackground = Color.parseColor(theme.getString("segment_background"));
            }
            if(theme.has("segment_line")) {
                segmentLine = Color.parseColor(theme.getString("segment_line"));
            }
            if(theme.has("segment_text")) {
//                appBackground = Color.parseColor(theme.getString("segment_text"));
            }
            if(theme.has("spinner_background")) {
                spinnerBackground = Color.parseColor(theme.getString("spinner_background"));
            }
            if(theme.has("spinner_text")) {
                spinnerText = Color.parseColor(theme.getString("spinner_text"));
            }
            if(theme.has("topbar_background")) {
//                appBackground = Color.parseColor(theme.getString("topbar_background"));
            }
            if(theme.has("topbar_text")) {
//                appBackground = Color.parseColor(theme.getString("topbar_text"));
            }
        if(theme.has("primary_color")) {
            primaryColor = Color.parseColor(theme.getString("primary_color"));
        }

    }


    public void setColorScheme(TeamColorSchemeObject[] colorScheme, String colorSchemeId) {
        setDefaults(colorSchemeId);
        icon = null;
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
                case "rounded_button":
                    roundedButtonColor = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "menu_icon":
                    menuIcon = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
            }
        }

        if(buttonBackground == buttonSelected) {
            buttonSelected = Color.GRAY;
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
            roundedButtonColor = R.color.colorCorporateOrange;
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
            roundedButtonColor = R.color.colorCorporateOrange;
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

    public int getBottomBarBackground() {
        return bottomBarBackground;
    }

    public int getBottomBarText() {
        return bottomBarText;
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

    public int getRoundedButtonColor() {
        return roundedButtonColor;
    }

    public void setRoundedButtonColor(int roundedButtonColor) {
        this.roundedButtonColor = roundedButtonColor;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }
}
