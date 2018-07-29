package co.sisu.mobile.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.LeaderboardItemsObject;

/**
 * Created by Brady Groharing on 7/28/2018.
 */

public class LeaderboardInfoDialog extends Dialog implements android.view.View.OnClickListener {

    public ParentActivity parentActivity;
    Bitmap bmp;
    private LeaderboardItemsObject leaderboardInformation;

    public LeaderboardInfoDialog(ParentActivity a, Bitmap bmp, LeaderboardItemsObject leaderboardItemsObject) {
        super(a);
        this.parentActivity = a;
        this.bmp = bmp;
        this.leaderboardInformation = leaderboardItemsObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_leaderboard_info);
        ImageView image = findViewById(R.id.leaderboard_dialog_image);
        TextView title = findViewById(R.id.leaderboard_dialog_title);
        title.setText(leaderboardInformation.getLabel());
        if(bmp != null) {
            image.setImageBitmap(bmp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
        dismiss();
    }
}
