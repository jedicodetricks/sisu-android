package co.sisu.mobile.controllers;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;

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

    public void updateColorSchemeManager(ColorSchemeManager colorSchemeManager) {
        this.colorSchemeManager = colorSchemeManager;
        resetToolbarImages(currentActivity);
    }

    public void resetToolbarImages(final String inputActivity) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentActivity = inputActivity;

                ImageView scoreBoardButton = parentActivity.findViewById(R.id.scoreboardView);
                Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.home_icon).mutate();
                drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
                scoreBoardButton.setImageDrawable(drawable);

//                ImageView reportButton = parentActivity.findViewById(R.id.reportView);
//                drawable = parentActivity.getResources().getDrawable(R.drawable.report_icon).mutate();
//                drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
//                reportButton.setImageDrawable(drawable);

                ImageView clientButton = parentActivity.findViewById(R.id.reportView);
                drawable = parentActivity.getResources().getDrawable(R.drawable.trans_disabled).mutate();
                drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
                clientButton.setImageDrawable(drawable);

                ImageView recordButton = parentActivity.findViewById(R.id.recordView);
                drawable = parentActivity.getResources().getDrawable(R.drawable.record_icon).mutate();
                drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
                recordButton.setImageDrawable(drawable);

                ImageView leaderBoardButton = parentActivity.findViewById(R.id.leaderBoardView);
                drawable = parentActivity.getResources().getDrawable(R.drawable.leaderboard_icon).mutate();
                drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
                leaderBoardButton.setImageDrawable(drawable);

                ImageView moreButton = parentActivity.findViewById(R.id.moreView);
                drawable = parentActivity.getResources().getDrawable(R.drawable.more_icon).mutate();
                drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
                moreButton.setImageDrawable(drawable);

                switch (inputActivity) {
                    case "Scoreboard":
                        drawable = parentActivity.getResources().getDrawable(R.drawable.home_icon_active).mutate();
                        drawable.setColorFilter(colorSchemeManager.getMenuIcon(), PorterDuff.Mode.SRC_ATOP);
                        scoreBoardButton.setImageDrawable(drawable);
                        break;
                    case "Report":
                        drawable = parentActivity.getResources().getDrawable(R.drawable.trans_tab).mutate();
                        clientButton.setImageDrawable(drawable);
                        break;
                    case "Record":
                        drawable = parentActivity.getResources().getDrawable(R.drawable.record_icon_active).mutate();
                        drawable.setColorFilter(colorSchemeManager.getMenuIcon(), PorterDuff.Mode.SRC_ATOP);
                        recordButton.setImageDrawable(drawable);
                        break;
                    case "Leaderboard":
                        drawable = parentActivity.getResources().getDrawable(R.drawable.leaderboard_icon_active).mutate();
                        drawable.setColorFilter(colorSchemeManager.getMenuIcon(), PorterDuff.Mode.SRC_ATOP);
                        leaderBoardButton.setImageDrawable(drawable);
                        break;
                    case "More":
                    case "Add Client Note":
                    case "Feedback":
                        drawable = parentActivity.getResources().getDrawable(R.drawable.more_icon_active).mutate();
                        drawable.setColorFilter(colorSchemeManager.getMenuIcon(), PorterDuff.Mode.SRC_ATOP);
                        moreButton.setImageDrawable(drawable);
                        break;
                }
            }
        });

    }
}
