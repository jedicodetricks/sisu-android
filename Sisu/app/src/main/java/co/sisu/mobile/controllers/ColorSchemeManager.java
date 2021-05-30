package co.sisu.mobile.controllers;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.TeamColorSchemeObject;

/**
 * Created by bradygroharing on 8/22/18.
 */

public class ColorSchemeManager {
    private int iconIdle;
    private int appBackground;
    private int bottombarBackground;
    private int buttonText;
    private int buttonSelected;
    private int buttonBorder;
    private int buttonBackground;
    private int normalText;
    private int lighterText;
    private int darkerText;
    private int menuBackground;
    private int menuText;
    private int menuSelected;
    private int spinnerText;
    private int progressBackground;
    private String logo;
    private int segmentSelected;
    private int menuSelectedText;
    private int line;
    private int segmentBackground;
    private int spinnerBackground;
    private int roundedButton;
    private String icon;
    private int menuIcon;
    private int primaryColor;
    private JSONObject defaultColorObject;
    private int lightLine;
    private int progressOffTrack;
    private int progressOnTrack;
    private int progressComplete;
    private int topbarBackground;
    private int topbarText;
    private int iconSelected;
    private String logoUrl;
    private String logoImage;
    private int segmentLine;

    public ColorSchemeManager(ParentActivity parentActivity) {
        createDefaultColorObject(parentActivity);
    }

    public ColorSchemeManager(@NonNull JSONObject theme, ParentActivity parentActivity) throws JSONException {
        createDefaultColorObject(parentActivity);
        if(theme.has("app_background")) {
            appBackground = getColorFromString("app_background", theme);
        }
        if(theme.has("primary_color")) {
            primaryColor = getColorFromString("primary_color", theme);
        }
        if(theme.has("light_line")) {
            lightLine = getColorFromString("light_line", theme);
        }
        if(theme.has("progress_background")) {
            progressBackground = getColorFromString("progress_background", theme);
        }
        if(theme.has("progress_complete")) {
            progressComplete = getColorFromString("progress_complete", theme);
        }
        if(theme.has("progress_offtrack")) {
            progressOffTrack = getColorFromString("progress_offtrack", theme);
        }
        if(theme.has("progress_ontrack")) {
            progressOnTrack = getColorFromString("progress_ontrack", theme);
        }
        if(theme.has("button_background")) {
            buttonBackground = getColorFromString("button_background", theme);
        }
        if(theme.has("button_selected")) {
            buttonSelected = getColorFromString("button_selected", theme);
        }
        if(theme.has("button_text")) {
            buttonText = getColorFromString("button_text", theme);
        }
        if(theme.has("button_border")) {
            buttonBorder = getColorFromString("button_border", theme);
        }
        if(theme.has("topbar_background")) {
            topbarBackground = getColorFromString("topbar_background", theme);
        }
        if(theme.has("topbar_text")) {
            topbarText = getColorFromString("topbar_text", theme);
        }
        if(theme.has("bottombar_background")) {
            bottombarBackground = getColorFromString("bottombar_background", theme);
        }
        if(theme.has("normal_text")) {
            normalText = getColorFromString("normal_text", theme);
        }
        if(theme.has("lighter_text")) {
            lighterText = getColorFromString("lighter_text", theme);
        }
        if(theme.has("darker_text")) {
            darkerText = getColorFromString("darker_text", theme);
        }
        if(theme.has("menu_background")) {
            menuBackground = getColorFromString("menu_background", theme);
        }
        if(theme.has("menu_text")) {
            menuText = getColorFromString("menu_text", theme);
        }
        if(theme.has("menu_selected")) {
            menuSelected = getColorFromString("menu_selected", theme);
        }
        if(theme.has("menu_selected_text")) {
            menuSelectedText = getColorFromString("menu_selected_text", theme);
        }
        if(theme.has("menu_icon")) {
            menuIcon = getColorFromString("menu_icon", theme);
        }
        if(theme.has("segment_selected")) {
            segmentSelected = getColorFromString("segment_selected", theme);
        }
        if(theme.has("segment_background")) {
            segmentBackground = getColorFromString("segment_background", theme);
        }
        if(theme.has("line")) {
            line = getColorFromString("line", theme);
        }
        if(theme.has("spinner_background")) {
            spinnerBackground = getColorFromString("spinner_background", theme);
        }
        if(theme.has("spinner_text")) {
            spinnerText = getColorFromString("spinner_text", theme);
        }
        if(theme.has("icon_idle")) {
            iconIdle = getColorFromString("icon_idle", theme);
        }
        if(theme.has("icon_selected")) {
            iconSelected = getColorFromString("icon_selected", theme);
        }
        if(theme.has("rounded_button")) {
            roundedButton = getColorFromString("rounded_button", theme);
        }
        if(theme.has("logo")) {
            // TODO: Do something different
            logo = theme.getString("logo");
        }
        if(theme.has("icon")) {
            // TODO: Do something different
            icon = theme.getString("icon");
        }
    }

    private int getColorFromString(String colorString, JSONObject theme) {
        try {
            return Color.parseColor(theme.getString(colorString));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return getColorFromDefaultObject(colorString);
    }

    private int getColorFromDefaultObject(String colorString) {
        try {
            return Color.parseColor(defaultColorObject.getString(colorString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.color.sisuOrange;
    }


    public void setColorScheme(@NonNull TeamColorSchemeObject[] colorScheme, ParentActivity parentActivity) {
        // TODO: This is called by the MainActivity. We should probably just store the default colors somewhere so it doesn't have to do that.
        if(parentActivity != null) {
            setDefaults(parentActivity);
        }
        icon = null;
        for(TeamColorSchemeObject colorSchemeObject : colorScheme) {
            switch (colorSchemeObject.getName()) {
                case "icon_selected":
                    iconSelected = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "icon_idle":
                    iconIdle = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "app_background":
                    appBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "bottombar_background":
                    bottombarBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "topbar_background":
                    topbarBackground = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "normal_text":
                    normalText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "lighter_text":
                    lighterText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "logo":
                    logo = colorSchemeObject.getTheme_data();
                    break;
                case "icon":
                    icon = colorSchemeObject.getTheme_data();
                    break;
                case "darker_text":
                    darkerText = Color.parseColor(colorSchemeObject.getTheme_data());
                    break;
                case "topbar_text":
                    topbarText = Color.parseColor(colorSchemeObject.getTheme_data());
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
//                    toolbarText = Color.parseColor(colorSchemeObject.getTheme_data());
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
                    roundedButton = Color.parseColor(colorSchemeObject.getTheme_data());
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

    private void setDefaults(ParentActivity parentActivity) {
        appBackground = ContextCompat.getColor(parentActivity, R.color.sisuAlmostBlack);
        primaryColor = ContextCompat.getColor(parentActivity, R.color.sisuOrange);
        lightLine = ContextCompat.getColor(parentActivity, R.color.sisuLightLineGrey);
        progressBackground = ContextCompat.getColor(parentActivity, R.color.sisuProgressDark);
        progressOffTrack = ContextCompat.getColor(parentActivity, R.color.sisuBlue);
        progressOnTrack = ContextCompat.getColor(parentActivity, R.color.sisuYellow);
        progressComplete = ContextCompat.getColor(parentActivity, R.color.sisuOrange);
        buttonBackground = ContextCompat.getColor(parentActivity, R.color.sisuAlmostBlack);
        buttonSelected = ContextCompat.getColor(parentActivity, R.color.sisuAlmostBlack);
        buttonText = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        buttonBorder = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        topbarBackground = ContextCompat.getColor(parentActivity, R.color.sisuAlmostBlack);
        topbarText = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        bottombarBackground = ContextCompat.getColor(parentActivity, R.color.sisuDarkest);
        normalText = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        lighterText = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        darkerText = ContextCompat.getColor(parentActivity, R.color.sisuWhite);
        menuBackground = ContextCompat.getColor(parentActivity, R.color.sisuDarkest);
        menuText = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        menuSelected = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        menuSelectedText = ContextCompat.getColor(parentActivity, R.color.sisuOrange);
        menuIcon = ContextCompat.getColor(parentActivity, R.color.sisuOrange);
        segmentSelected = ContextCompat.getColor(parentActivity, R.color.sisuWhite);
        segmentBackground = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        line = ContextCompat.getColor(parentActivity, R.color.sisuLineGrey);
        spinnerBackground = ContextCompat.getColor(parentActivity, R.color.sisuDarkest);
        spinnerText = ContextCompat.getColor(parentActivity, R.color.sisuWhite);
        iconIdle = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        iconSelected = ContextCompat.getColor(parentActivity, R.color.sisuLightGrey);
        roundedButton = ContextCompat.getColor(parentActivity, R.color.sisuOrange);
        logoUrl = "https://s3-us-west-2.amazonaws.com/sisu-shared-storage/generic/sisu-logo.png";
        logoImage = "sisu-logo-lg";
        icon = "sisu-logo-lg";
    }

    private void createDefaultColorObject(ParentActivity parentActivity) {
        defaultColorObject = new JSONObject();
        setDefaults(parentActivity);
        try {
            defaultColorObject.put("app_background", appBackground);
            defaultColorObject.put("primary_color", primaryColor);
            defaultColorObject.put("light_line", lightLine);
            defaultColorObject.put("progress_background", progressBackground);
            defaultColorObject.put("progress_offtrack", progressOffTrack);
            defaultColorObject.put("progress_ontrack", progressOnTrack);
            defaultColorObject.put("progress_complete", progressComplete);
            defaultColorObject.put("button_background", buttonBackground);
            defaultColorObject.put("button_selected", buttonSelected);
            defaultColorObject.put("button_text", buttonText);
            defaultColorObject.put("button_border", buttonBorder);
            defaultColorObject.put("topbar_background", topbarBackground);
            defaultColorObject.put("topbar_text", topbarText);
            defaultColorObject.put("bottombar_background", bottombarBackground);
            defaultColorObject.put("normal_text", normalText);
            defaultColorObject.put("lighter_text", lighterText);
            defaultColorObject.put("darker_text", darkerText);
            defaultColorObject.put("menu_background", menuBackground);
            defaultColorObject.put("menu_text", menuText);
            defaultColorObject.put("menu_selected", menuSelected);
            defaultColorObject.put("menu_selected_text", menuSelectedText);
            defaultColorObject.put("menu_icon", menuIcon);
            defaultColorObject.put("segment_line", segmentLine);
            defaultColorObject.put("segment_selected", segmentSelected);
            defaultColorObject.put("segment_background", segmentBackground);
            defaultColorObject.put("line", line);
            defaultColorObject.put("spinner_background", spinnerBackground);
            defaultColorObject.put("spinner_text", spinnerText);
            defaultColorObject.put("icon_idle", iconIdle);
            defaultColorObject.put("icon_selected", iconSelected);
            defaultColorObject.put("rounded_button", roundedButton);
            defaultColorObject.put("logo", logo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getIconIdle() {
        return iconIdle;
    }

    public int getAppBackground() {
        return appBackground;
    }

    public void setAppBackground(int appBackground) {
        this.appBackground = appBackground;
    }

    public int getBottombarBackground() {
        return bottombarBackground;
    }

    public void setBottombarBackground(int bottombarBackground) {
        this.bottombarBackground = bottombarBackground;
    }

    public int getButtonText() {
        return buttonText;
    }

    public void setButtonText(int buttonText) {
        this.buttonText = buttonText;
    }

    public int getButtonSelected() {
        return buttonSelected;
    }

    public void setButtonSelected(int buttonSelected) {
        this.buttonSelected = buttonSelected;
    }

    public int getButtonBorder() {
        return buttonBorder;
    }

    public void setButtonBorder(int buttonBorder) {
        this.buttonBorder = buttonBorder;
    }

    public int getButtonBackground() {
        return buttonBackground;
    }

    public void setButtonBackground(int buttonBackground) {
        this.buttonBackground = buttonBackground;
    }

    public int getNormalText() {
        return normalText;
    }

    public void setNormalText(int normalText) {
        this.normalText = normalText;
    }

    public int getLighterText() {
        return lighterText;
    }

    public void setLighterText(int lighterText) {
        this.lighterText = lighterText;
    }

    public int getDarkerText() {
        return darkerText;
    }

    public void setDarkerText(int darkerText) {
        this.darkerText = darkerText;
    }

    public int getMenuBackground() {
        return menuBackground;
    }

    public void setMenuBackground(int menuBackground) {
        this.menuBackground = menuBackground;
    }

    public int getMenuText() {
        return menuText;
    }

    public void setMenuText(int menuText) {
        this.menuText = menuText;
    }

    public int getMenuSelected() {
        return menuSelected;
    }

    public void setMenuSelected(int menuSelected) {
        this.menuSelected = menuSelected;
    }

    public int getSpinnerText() {
        return spinnerText;
    }

    public void setSpinnerText(int spinnerText) {
        this.spinnerText = spinnerText;
    }

    public int getProgressBackground() {
        return progressBackground;
    }

    public void setProgressBackground(int progressBackground) {
        this.progressBackground = progressBackground;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getSegmentSelected() {
        return segmentSelected;
    }

    public void setSegmentSelected(int segmentSelected) {
        this.segmentSelected = segmentSelected;
    }

    public int getMenuSelectedText() {
        return menuSelectedText;
    }

    public void setMenuSelectedText(int menuSelectedText) {
        this.menuSelectedText = menuSelectedText;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getSegmentBackground() {
        return segmentBackground;
    }

    public void setSegmentBackground(int segmentBackground) {
        this.segmentBackground = segmentBackground;
    }

    public int getSpinnerBackground() {
        return spinnerBackground;
    }

    public void setSpinnerBackground(int spinnerBackground) {
        this.spinnerBackground = spinnerBackground;
    }

    public int getRoundedButton() {
        return roundedButton;
    }

    public void setRoundedButton(int roundedButton) {
        this.roundedButton = roundedButton;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public JSONObject getDefaultColorObject() {
        return defaultColorObject;
    }

    public void setDefaultColorObject(JSONObject defaultColorObject) {
        this.defaultColorObject = defaultColorObject;
    }

    public int getLightLine() {
        return lightLine;
    }

    public void setLightLine(int lightLine) {
        this.lightLine = lightLine;
    }

    public int getProgressOffTrack() {
        return progressOffTrack;
    }

    public void setProgressOffTrack(int progressOffTrack) {
        this.progressOffTrack = progressOffTrack;
    }

    public int getProgressOnTrack() {
        return progressOnTrack;
    }

    public void setProgressOnTrack(int progressOnTrack) {
        this.progressOnTrack = progressOnTrack;
    }

    public int getProgressComplete() {
        return progressComplete;
    }

    public void setProgressComplete(int progressComplete) {
        this.progressComplete = progressComplete;
    }

    public int getTopbarBackground() {
        return topbarBackground;
    }

    public void setTopbarBackground(int topbarBackground) {
        this.topbarBackground = topbarBackground;
    }

    public int getTopbarText() {
        return topbarText;
    }

    public void setTopbarText(int topbarText) {
        this.topbarText = topbarText;
    }

    public int getIconSelected() {
        return iconSelected;
    }

    public void setIconSelected(int iconSelected) {
        this.iconSelected = iconSelected;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public int getSegmentLine() {
        return segmentLine;
    }

    public void setSegmentLine(int segmentLine) {
        this.segmentLine = segmentLine;
    }
}
