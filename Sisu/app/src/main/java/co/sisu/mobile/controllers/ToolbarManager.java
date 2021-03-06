package co.sisu.mobile.controllers;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.widget.ImageView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.enums.FragmentName;

/**
 * Created by bradygroharing on 5/22/18.
 */

public class ToolbarManager {
    private ParentActivity parentActivity;
    private ColorSchemeManager colorSchemeManager;
    private String currentActivity = "Scoreboard";

    public ToolbarManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.colorSchemeManager = parentActivity.getColorSchemeManager();
    }

    public void updateColorSchemeManager(final ColorSchemeManager colorSchemeManager) {
        this.colorSchemeManager = colorSchemeManager;
        resetToolbarImages(currentActivity);
    }

    public void resetToolbarImages(final String inputActivity) {
        parentActivity.runOnUiThread(() -> {
            currentActivity = inputActivity;

            ImageView scoreBoardButton = parentActivity.findViewById(R.id.scoreboardView);
            Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.home_icon, null).mutate();
            drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
            scoreBoardButton.setImageDrawable(drawable);

//                ImageView reportButton = parentActivity.findViewById(R.id.reportView);
//                drawable = parentActivity.getResources().getDrawable(R.drawable.report_icon).mutate();
//                drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
//                reportButton.setImageDrawable(drawable);

            ImageView clientButton = parentActivity.findViewById(R.id.reportView);
            drawable = parentActivity.getResources().getDrawable(R.drawable.trans_disabled, null).mutate();
//            drawable.setColorFilter(ContextCompat.getColor(context, colorSchemeManager.getIconIdle()), PorterDuff.Mode.SRC_ATOP);
            drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
            clientButton.setImageDrawable(drawable);

            ImageView recordButton = parentActivity.findViewById(R.id.recordView);
            drawable = parentActivity.getResources().getDrawable(R.drawable.record_icon, null).mutate();
//            drawable.setColorFilter(ContextCompat.getColor(context, colorSchemeManager.getIconIdle()), PorterDuff.Mode.SRC_ATOP);
            drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
            recordButton.setImageDrawable(drawable);

            ImageView leaderBoardButton = parentActivity.findViewById(R.id.leaderBoardView);
            drawable = parentActivity.getResources().getDrawable(R.drawable.leaderboard_icon, null).mutate();
//            drawable.setColorFilter(ContextCompat.getColor(context, colorSchemeManager.getIconIdle()), PorterDuff.Mode.SRC_ATOP);
            drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
            leaderBoardButton.setImageDrawable(drawable);

            ImageView moreButton = parentActivity.findViewById(R.id.moreView);
            drawable = parentActivity.getResources().getDrawable(R.drawable.more_icon, null).mutate();
//            drawable.setColorFilter(ContextCompat.getColor(context, colorSchemeManager.getIconIdle()), PorterDuff.Mode.SRC_ATOP);
            drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
            moreButton.setImageDrawable(drawable);

            switch (inputActivity) {
                case "Scoreboard":
                    drawable = parentActivity.getResources().getDrawable(R.drawable.home_icon_active, null).mutate();
//                    drawable.setColorFilter(ContextCompat.getColor(context, colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
                    drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
                    scoreBoardButton.setImageDrawable(drawable);
                    break;
                case "Report":
                    drawable = parentActivity.getResources().getDrawable(R.drawable.trans_tab, null).mutate();
//                    drawable.setColorFilter(ContextCompat.getColor(parentActivity, colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
                    drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
                    clientButton.setImageDrawable(drawable);
                    break;
                case "Record":
                    drawable = parentActivity.getResources().getDrawable(R.drawable.record_icon_active, null).mutate();
//                    drawable.setColorFilter(ContextCompat.getColor(parentActivity, colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
                    drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
                    recordButton.setImageDrawable(drawable);
                    break;
                case "Leaderboard":
                    drawable = parentActivity.getResources().getDrawable(R.drawable.leaderboard_icon_active, null).mutate();
//                    drawable.setColorFilter(ContextCompat.getColor(parentActivity, colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
                    drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
                    leaderBoardButton.setImageDrawable(drawable);
                    break;
                case "More":
                case "Add Client Note":
                case "Feedback":
                    drawable = parentActivity.getResources().getDrawable(R.drawable.more_icon_active, null).mutate();
//                    drawable.setColorFilter(ContextCompat.getColor(parentActivity, colorSchemeManager.getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);
                    drawable.setColorFilter(colorSchemeManager.getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
                    moreButton.setImageDrawable(drawable);
                    break;
                default:
                    break;
            }
        });

    }

    public void manage(@NonNull String fragmentName) {
        String currentFragmentName = "";
        if(fragmentName.equals(FragmentName.DASHBOARD.label)) {
            currentFragmentName = "Scoreboard";
        }
        else if(fragmentName.equals(FragmentName.CLIENTS.label)) {
            currentFragmentName = "Report";
        }
        else if(fragmentName.equals(FragmentName.RECORD.label)) {
            currentFragmentName = "Record";
        }
        else if(fragmentName.equals(FragmentName.LEADERBOARD.label)) {
            currentFragmentName = "Leaderboard";
        }
        else if(fragmentName.equals(FragmentName.MORE.label)) {
            currentFragmentName = "More";
        }
        resetToolbarImages(currentFragmentName);
    }
}
