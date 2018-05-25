package co.sisu.mobile.controllers;

import android.widget.ImageView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;

/**
 * Created by bradygroharing on 5/22/18.
 */

public class ToolbarManager {
    private ParentActivity parentActivity;

    public ToolbarManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    public void resetToolbarImages(String inputActivity) {
        ImageView scoreBoardButton = parentActivity.findViewById(R.id.scoreboardView);
        scoreBoardButton.setImageResource(R.drawable.home_icon);

        ImageView reportButton = parentActivity.findViewById(R.id.reportView);
        reportButton.setImageResource(R.drawable.report_icon);

        ImageView recordButton = parentActivity.findViewById(R.id.recordView);
        recordButton.setImageResource(R.drawable.record_icon);

        ImageView leaderBoardButton = parentActivity.findViewById(R.id.leaderBoardView);
        leaderBoardButton.setImageResource(R.drawable.leaderboard_icon);

        ImageView moreButton = parentActivity.findViewById(R.id.moreView);
        moreButton.setImageResource(R.drawable.more_icon);

        switch (inputActivity) {
            case "Scoreboard":
                scoreBoardButton.setImageResource(R.drawable.home_icon_active);
                break;
            case "Report":
                reportButton.setImageResource(R.drawable.report_icon_active);
                break;
            case "Record":
                recordButton.setImageResource(R.drawable.record_icon_active);
                break;
            case "Leaderboard":
                leaderBoardButton.setImageResource(R.drawable.leaderboard_icon_active);
                break;
            case "More":
                moreButton.setImageResource(R.drawable.more_icon_active);
                break;
            case "Feedback":
                moreButton.setImageResource(R.drawable.more_icon_active);
                break;
        }
    }
}
