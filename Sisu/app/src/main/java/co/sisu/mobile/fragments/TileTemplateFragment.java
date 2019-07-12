package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.utils.CircularProgressBar;

import static android.view.FrameMetrics.ANIMATION_DURATION;

/**
 * Created by bradygroharing on 2/21/18.
 */

public class TileTemplateFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private ProgressBar loader;
    private TableLayout tableLayout;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        tableLayout = (TableLayout) inflater.inflate(R.layout.activity_tile_template, container, false);
        TableLayout.LayoutParams viewLayout = new TableLayout.LayoutParams(container.getWidth(), container.getHeight());
        tableLayout.setLayoutParams(viewLayout);
        return tableLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        loader.setVisibility(View.INVISIBLE);
        createRow(50, 2);
        createRow(300, 2);

    }

    private void createRow(int height, int maxTiles) {
        int rowWeight = 100 / maxTiles;
        TableRow row = new TableRow(tableLayout.getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        row.setLayoutParams(lp);

//        for (int i = 0; i < maxTiles; i++) {
//            View rowView = createSmallHeaderView(row);
//            rowView.setMinimumHeight(height);
//            row.addView(rowView);
//        }

        for (int i = 0; i < maxTiles; i++) {
            View rowView = createProgressView(row);
            row.addView(rowView);
        }


//            CheckBox checkBox = new CheckBox(parentActivity);
//            TextView tv = new TextView(parentActivity);
//            ImageButton addBtn = new ImageButton(parentActivity);
//            addBtn.setImageResource(R.drawable.add_icon);
//            ImageButton minusBtn = new ImageButton(parentActivity);
//            minusBtn.setImageResource(R.drawable.minus_icon);
//            TextView qty = new TextView(parentActivity);
//            checkBox.setText("hello");
//            qty.setText("10");
//            row.addView(checkBox);
//            row.addView(minusBtn);
//            row.addView(qty);
//            row.addView(addBtn);
        tableLayout.addView(row);

    }

    private View createSmallHeaderView(TableRow row) {
        String headerText = "YTD Closed Volume";
        String footerText = "$500K";

        View rowView = inflater.inflate(R.layout.tile_smallheader_layout, row, false);

        TextView header = rowView.findViewById(R.id.smallHeaderTileHeader);
        TextView footer = rowView.findViewById(R.id.smallHeaderTileFooter);
        header.setText(headerText);
        footer.setText(footerText);
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);
        return rowView;
    }

    private View createProgressView(TableRow row) {
        View rowView = inflater.inflate(R.layout.tile_progress_layout, row, false);

        CircularProgressBar progress = rowView.findViewById(R.id.progressTileProgressBar);
        progress.setColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
        progress.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
        progress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setProgressWithAnimation(50, ANIMATION_DURATION);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        return rowView;
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }
}
