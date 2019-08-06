package co.sisu.mobile.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private RelativeLayout relativeLayout;
    private LayoutInflater inflater;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        this.inflater = inflater;
//        tableLayout = (TableLayout) inflater.inflate(R.layout.activity_tile_template, container, false);
//        TableLayout.LayoutParams viewLayout = new TableLayout.LayoutParams(container.getWidth(), container.getHeight());
//        tableLayout.setLayoutParams(viewLayout);
//        return tableLayout;
//    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        this.inflater = inflater;

        // Create the parent layout that all the rows will go in
        View parentLayout = inflater.inflate(R.layout.activity_tile_template_test_parentlayout, container, false);
        RelativeLayout parentRelativeLayout = parentLayout.findViewById(R.id.tileRelativeLayout);

        // We'll need a horizontal view for every single row
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
        horizontalScrollView.setId(50);
        horizontalScrollView.setBackgroundColor(Color.RED);

        HorizontalScrollView horizontalScrollView2 = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
        horizontalScrollView2.setId(51);
        horizontalScrollView2.setBackgroundColor(Color.BLUE);

        HorizontalScrollView horizontalScrollView3 = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
        horizontalScrollView3.setId(52);
        horizontalScrollView3.setBackgroundColor(Color.GREEN);

        RelativeLayout.LayoutParams horizontalParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        horizontalParams.setMargins(6, 6, 6, 6);

        RelativeLayout.LayoutParams horizontalParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        horizontalParams2.setMargins(6, 6, 6, 6);

        RelativeLayout.LayoutParams horizontalParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        horizontalParams3.setMargins(6, 6, 6, 6);

//        horizontalScrollView.setLayoutParams(horizontalParams);
//        horizontalScrollView2.setLayoutParams(horizontalParams2);
//        horizontalScrollView3.setLayoutParams(horizontalParams3);

        // This view gets loaded with all the components for a row then goes into the horizontal view. This is the view that decides the height
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 800);
        RelativeLayout.LayoutParams relativeParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 300);
        RelativeLayout.LayoutParams relativeParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 625);

        View view = inflater.inflate(R.layout.activity_tile_template_test, container, false);
        View view2 = inflater.inflate(R.layout.activity_tile_template_test, container, false);
        View view3 = inflater.inflate(R.layout.activity_tile_template_test, container, false);

        view.setLayoutParams(relativeParams);
        RelativeLayout relativeLayout = view.findViewById(R.id.tileRelativeLayout);

        view2.setLayoutParams(relativeParams2);
        RelativeLayout relativeLayout2 = view2.findViewById(R.id.tileRelativeLayout);

        view3.setLayoutParams(relativeParams3);
        RelativeLayout relativeLayout3 = view3.findViewById(R.id.tileRelativeLayout);


        View v = createProgressView(container);
        v.setId(1);

        View v2 = createSmallHeaderView(container);
        v2.setId(2);

        View v3 = createProgressView(container);
        v3.setId(3);

        RelativeLayout.LayoutParams textviewparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 350);
        textviewparam.setMargins(6, 6, 6, 6);
//        textviewparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        textviewparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        textviewparam.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams textview1param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 250);
        textview1param.setMargins(6, 6, 6, 6);
        textview1param.addRule(RelativeLayout.RIGHT_OF, 1);
        textview1param.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams textview3param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 200);
        textview3param.setMargins(6, 6, 6, 6);
        textview3param.addRule(RelativeLayout.RIGHT_OF, 2);
        textview3param.addRule(RelativeLayout.CENTER_VERTICAL);

        relativeLayout.addView(v, textviewparam);
        relativeLayout.addView(v2, textview1param);
        relativeLayout.addView(v3, textview3param);

//        View progress1 = createProgressView(container);
//        progress1.setId(11);

        View smallHeader1 = createSmallHeaderView(container);
        smallHeader1.setId(11);

        View progress2 = createProgressView(container);
        progress2.setId(12);

        View progress3 = createProgressView(container);
        progress3.setId(13);

        RelativeLayout.LayoutParams progressParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 250);
        progressParam.setMargins(6, 6, 6, 6);
//        progressParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        progressParam.addRule(RelativeLayout.CENTER_IN_PARENT);

//        RelativeLayout.LayoutParams progressParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 150);
//        progressParam2.setMargins(6, 6, 6, 6);
//        progressParam2.addRule(RelativeLayout.RIGHT_OF, 11);

//        RelativeLayout.LayoutParams progressParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 100);
//        progressParam3.setMargins(6, 6, 6, 6);
//        progressParam3.addRule(RelativeLayout.RIGHT_OF, 12);

        relativeLayout2.addView(smallHeader1, progressParam);
//        relativeLayout2.addView(progress2, progressParam2);
//        relativeLayout2.addView(progress3, progressParam3);



        View progressView = createProgressView(container);
        progressView.setId(31);

        View progressView2 = createProgressView(container);
        progressView2.setId(32);

        View progressView3 = createProgressView(container);
        progressView3.setId(33);

        RelativeLayout.LayoutParams progressViewParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 250);
        progressViewParam.setMargins(6, 6, 6, 6);
        progressViewParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        progressViewParam.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams progressViewParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 150);
        progressViewParam2.setMargins(6, 6, 6, 6);
        progressViewParam2.addRule(RelativeLayout.RIGHT_OF, 31);
        progressViewParam2.addRule(RelativeLayout.CENTER_IN_PARENT);

        RelativeLayout.LayoutParams progressViewParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 100);
        progressViewParam3.setMargins(6, 6, 6, 6);
        progressViewParam3.addRule(RelativeLayout.RIGHT_OF, 32);

        relativeLayout3.addView(progressView, progressViewParam);
        relativeLayout3.addView(progressView2, progressViewParam2);
//        relativeLayout3.addView(progressView3, progressViewParam3);

        RelativeLayout.LayoutParams horizontalParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        horizontalParam.setMargins(6, 6, 6, 6);

        RelativeLayout.LayoutParams horizontalParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        horizontalParam2.setMargins(6, 6, 6, 6);
        horizontalParam2.addRule(RelativeLayout.BELOW, 50);
        horizontalParam2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        RelativeLayout.LayoutParams horizontalParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        horizontalParam3.setMargins(6, 6, 6, 6);
        horizontalParam3.addRule(RelativeLayout.BELOW, 51);

//        HorizontalScrollView.LayoutParams horizTest = new HorizontalScrollView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, HorizontalScrollView.LayoutParams.MATCH_PARENT);
//        horizTest.setMargins(6, 6, 6, 6);

        horizontalScrollView.addView(relativeLayout);
        horizontalScrollView2.addView(relativeLayout2);
        horizontalScrollView3.addView(relativeLayout3);

        parentRelativeLayout.addView(horizontalScrollView, horizontalParam);
        parentRelativeLayout.addView(horizontalScrollView2, horizontalParam2);
        parentRelativeLayout.addView(horizontalScrollView3, horizontalParam3);

        return parentLayout;
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
    }

    // This is the class to create rows via TableLayout
    private void createRow(int height, int maxTiles) {
        int rowWeight = 100 / maxTiles;
        TableRow row = new TableRow(tableLayout.getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, height);

//        View v = new View(row.getContext());
//        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
//        v.setBackgroundColor(Color.rgb(51, 51, 51));
//        lp.setMargins(12, 12, 12, 12);
//        lp.setMarginEnd(24);
//        lp.setMarginStart(24);
        row.setLayoutParams(lp);
//        for (int i = 0; i < maxTiles; i++) {
//            View rowView = createSmallHeaderView(row);
//            rowView.setMinimumHeight(height);
//            row.addView(rowView);
//        }

        for (int i = 0; i < maxTiles; i++) {
//            View rowView = createProgressView(row);
//            rowView.setRight(24);
//            rowView.setPadding(48, 0, 48, 0);
//            row.addView(rowView);
//            if(i < maxTiles -1) {
//            row.addView(v);
//            }
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

    private void createRowLinear(int height, int maxTiles) {
        int rowWeight = 100 / maxTiles;
        View firstView;
        View previousView = null;
        for (int i = 0; i < maxTiles; i++) {
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
            View rowView = createProgressView(relativeLayout);
//            rowView.setRight(24);
//            rowView.setPadding(48, 0, 48, 0);
//            row.addView(rowView);
//            if(i < maxTiles -1) {
//            row.addView(v);
//            }
            if(i == 0) {
                firstView = rowView;
                previousView = rowView;
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
            else {
                relativeParams.addRule(RelativeLayout.RIGHT_OF, previousView.getId());
                previousView = rowView;

            }
            rowView.setLayoutParams(relativeParams);
//            relativeLayout.addView(rowView, relativeParams);
//            parentActivity.setContentView(relativeLayout, relativeParams);
        }

    }

    private View createSmallHeaderView(ViewGroup row) {
        String headerText = "YTD Closed Volume";
        String footerText = "$500K";

        View rowView = inflater.inflate(R.layout.tile_smallheader_layout, row, false);

        TextView header = rowView.findViewById(R.id.smallHeaderTileHeader);
        TextView footer = rowView.findViewById(R.id.smallHeaderTileFooter);
        header.setText(headerText);
        footer.setText(footerText);
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
        roundedCorners.setColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
        rowView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners) );
        return rowView;
    }

    private View createProgressView(ViewGroup row) {
        View rowView = inflater.inflate(R.layout.tile_progress_layout, row, false);

        CircularProgressBar progress = rowView.findViewById(R.id.progressTileProgressBar);
        progress.setColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
//        progress.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
        progress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setProgressWithAnimation(50, ANIMATION_DURATION);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        rowView.setClipToOutline(true);
        GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
        roundedCorners.setColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
        rowView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners) );
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(12, 12, 12, 12);
        rowView.setLayoutParams(layoutParams);
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
