package co.sisu.mobile.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.adapters.DropdownAdapter;
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

    private int selectedStartYear = 0;
    private int selectedStartMonth = 0;
    private int selectedStartDay = 0;
    private int selectedEndYear = 0;
    private int selectedEndMonth = 0;
    private int selectedEndDay = 0;
    private Spinner spinner;
    private String formattedStartTime;
    private String formattedEndTime;
    private Calendar calendar = Calendar.getInstance();
    private Date selectedStartTime;
    private Date selectedEndTime;
    private boolean needsProgress;
    private boolean pastTimeline;

    private int numOfRows = 1;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentActivity = (ParentActivity) getActivity();
        dataController = parentActivity.getDataController();
        navigationManager = parentActivity.getNavigationManager();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        loader = parentActivity.findViewById(R.id.parentLoader);
        loader.setVisibility(View.INVISIBLE);
        this.inflater = inflater;

        // Create the parent layout that all the rows will go in
        View parentLayout = inflater.inflate(R.layout.activity_tile_template_test_parentlayout, container, false);
        RelativeLayout parentRelativeLayout = parentLayout.findViewById(R.id.tileRelativeLayout);
        initializeTimelineSelector(parentLayout);
        //

        String inputJSON = loadJSONFromAsset(getContext());
        JSONObject tileTemplate = null;
        JSONArray tile_rows = null;
        try {
            tileTemplate = new JSONObject(inputJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (tileTemplate != null) {
            try {
                tile_rows = tileTemplate.getJSONArray("tile_rows");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("TILE ROWS", String.valueOf(tile_rows.length()));
            for(int i = 1; i < tile_rows.length(); i++) {
                try {
                    HorizontalScrollView horizontalScrollView = createRowFromJSON(tile_rows.getJSONObject(i), container);
                    if(horizontalScrollView != null) {
                        // Add one here to account for the spinner's ID.
                        horizontalScrollView.setId(numOfRows + 1);
                        RelativeLayout.LayoutParams horizontalParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        horizontalParam.setMargins(6, 6, 6, 6);
                        horizontalParam.addRule(RelativeLayout.BELOW, numOfRows);

                        parentRelativeLayout.addView(horizontalScrollView, horizontalParam);
                        numOfRows++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        else {



            // We'll need a horizontal view for every single row
            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
            horizontalScrollView.setId(50);
//        horizontalScrollView.setBackgroundColor(Color.RED);

            HorizontalScrollView horizontalScrollView2 = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
            horizontalScrollView2.setId(51);
//        horizontalScrollView2.setBackgroundColor(Color.BLUE);

            HorizontalScrollView horizontalScrollView3 = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
            horizontalScrollView3.setId(52);
//        horizontalScrollView3.setBackgroundColor(Color.GREEN);

            // I don't think we need these now
//        RelativeLayout.LayoutParams horizontalParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        horizontalParams.setMargins(6, 6, 6, 6);
//
//        RelativeLayout.LayoutParams horizontalParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        horizontalParams2.setMargins(6, 6, 6, 6);
//
//        RelativeLayout.LayoutParams horizontalParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        horizontalParams3.setMargins(6, 6, 6, 6);

//        horizontalScrollView.setLayoutParams(horizontalParams);
//        horizontalScrollView2.setLayoutParams(horizontalParams2);
//        horizontalScrollView3.setLayoutParams(horizontalParams3);

            // This view gets loaded with all the components for a row then goes into the horizontal view. This is the view that decides the height
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 375);
            RelativeLayout.LayoutParams relativeParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 375);
            RelativeLayout.LayoutParams relativeParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 450);

            View view = inflater.inflate(R.layout.activity_tile_template_linear_test, container, false);

            view.setLayoutParams(relativeParams);
//        RelativeLayout relativeLayout = view.findViewById(R.id.tileRelativeLayout);
            LinearLayout linearLayout = view.findViewById(R.id.tileLinearLayout);

            View v = createSmallHeaderView(container, "YTD Closed Volume", "$1.03m", false);
            v.setId(1);

            View v2 = createSmallHeaderView(container, "YTD Closed Units", "6.75", false);
            v2.setId(2);

            View v3 = createProgressView(container, "title", 50, "#FFFFFF", false);
            v3.setId(3);

            LinearLayout.LayoutParams textviewparam = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 350, 1);
            textviewparam.setMargins(6, 6, 6, 6);
//        textviewparam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        textviewparam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        textviewparam.addRule(RelativeLayout.CENTER_VERTICAL);

            LinearLayout.LayoutParams textview1param = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 350, 1);
            textview1param.setMargins(6, 6, 6, 6);
//        textview1param.addRule(RelativeLayout.RIGHT_OF, 1);
//        textview1param.addRule(RelativeLayout.CENTER_VERTICAL);

            RelativeLayout.LayoutParams textview3param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 200);
            textview3param.setMargins(6, 6, 6, 6);
            textview3param.addRule(RelativeLayout.RIGHT_OF, 2);
            textview3param.addRule(RelativeLayout.CENTER_VERTICAL);

            linearLayout.addView(v, textviewparam);
            linearLayout.addView(v2, textview1param);
//        linearLayout.addView(v3, textview3param);

//        View progress1 = createProgressView(container);
//        progress1.setId(11);


            // ROW TWO
            // ROW TWO
            // ROW TWO

            View view2 = inflater.inflate(R.layout.activity_tile_template_linear_test, container, false);
            view2.setLayoutParams(relativeParams2);
            LinearLayout linearLayout2 = view2.findViewById(R.id.tileLinearLayout);


            View smallHeader1 = createSmallHeaderView(container, "MTD U/C Volume", "$100K", false);
            smallHeader1.setId(11);

            View smallHeader2 = createSmallHeaderView(container, "MTD Closed Volume", "$450K", false);
            smallHeader2.setId(12);

            View progress3 = createProgressView(container, "title", 50, "#FFFFFF", false);
            progress3.setId(13);

            LinearLayout.LayoutParams progressParam = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 350, 1);
            progressParam.setMargins(6, 6, 6, 6);
//        progressParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        progressParam.addRule(RelativeLayout.CENTER_IN_PARENT);

//        RelativeLayout.LayoutParams progressParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 150);
//        progressParam2.setMargins(6, 6, 6, 6);
//        progressParam2.addRule(RelativeLayout.RIGHT_OF, 11);

//        RelativeLayout.LayoutParams progressParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 100);
//        progressParam3.setMargins(6, 6, 6, 6);
//        progressParam3.addRule(RelativeLayout.RIGHT_OF, 12);

            linearLayout2.addView(smallHeader1, progressParam);
            linearLayout2.addView(smallHeader2, progressParam);
//        relativeLayout2.addView(progress3, progressParam3);


            // ROW THREE
            // ROW THREE
            // ROW THREE
            View view3 = inflater.inflate(R.layout.activity_tile_template_linear_test, container, false);
            view3.setLayoutParams(relativeParams3);
//        RelativeLayout relativeLayout3 = view3.findViewById(R.id.tileRelativeLayout);
            LinearLayout linearLayout3 = view3.findViewById(R.id.tileLinearLayout);

            View progressView = createProgressView(container, "title", 50, "#FFFFFF", false);
            progressView.setId(31);

            View progressView2 = createProgressView(container, "title", 50, "#FFFFFF", false);
            progressView2.setId(32);

            View progressView3 = createProgressView(container, "title", 50, "#FFFFFF", false);
            progressView3.setId(33);

            LinearLayout.LayoutParams progressViewParam = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 450, 1);
            progressViewParam.setMargins(6, 6, 6, 6);
//        progressViewParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        progressViewParam.addRule(RelativeLayout.CENTER_IN_PARENT);

            RelativeLayout.LayoutParams progressViewParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 150);
            progressViewParam2.setMargins(6, 6, 6, 6);
            progressViewParam2.addRule(RelativeLayout.RIGHT_OF, 31);
            progressViewParam2.addRule(RelativeLayout.CENTER_IN_PARENT);

            RelativeLayout.LayoutParams progressViewParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 100);
            progressViewParam3.setMargins(6, 6, 6, 6);
            progressViewParam3.addRule(RelativeLayout.RIGHT_OF, 32);

            linearLayout3.addView(progressView, progressViewParam);
//        relativeLayout3.addView(progressView2, progressViewParam2);
//        relativeLayout3.addView(progressView3, progressViewParam3);

            RelativeLayout.LayoutParams horizontalParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            horizontalParam.setMargins(6, 6, 6, 6);
            horizontalParam.addRule(RelativeLayout.BELOW, 99);

            RelativeLayout.LayoutParams horizontalParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            horizontalParam2.setMargins(6, 6, 6, 6);
            horizontalParam2.addRule(RelativeLayout.BELOW, 50);
            horizontalParam2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            RelativeLayout.LayoutParams horizontalParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            horizontalParam3.setMargins(6, 6, 6, 6);
            horizontalParam3.addRule(RelativeLayout.BELOW, 51);

//        HorizontalScrollView.LayoutParams horizTest = new HorizontalScrollView.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, HorizontalScrollView.LayoutParams.MATCH_PARENT);
//        horizTest.setMargins(6, 6, 6, 6);

            horizontalScrollView.addView(linearLayout);
            horizontalScrollView2.addView(linearLayout2);
            horizontalScrollView3.addView(linearLayout3);

            parentRelativeLayout.addView(horizontalScrollView, horizontalParam);
            parentRelativeLayout.addView(horizontalScrollView2, horizontalParam2);
            parentRelativeLayout.addView(horizontalScrollView3, horizontalParam3);
        }

        return parentLayout;
    }

    @SuppressLint("ResourceType")
    private HorizontalScrollView createRowFromJSON(JSONObject rowObject, ViewGroup container) {
        Log.e("TILE ROWS", String.valueOf(rowObject));
        try {
            JSONArray rowTiles = rowObject.getJSONArray("tiles");
            Double height = rowObject.getDouble("rowheight");
            Double innerGap = rowObject.getDouble("innerGap");
            Boolean disabled = rowObject.getBoolean("disabled");
            Boolean square = rowObject.getBoolean("square");
            Integer maxTiles = rowObject.getInt("max_tiles");

            int correctedHeight = height.intValue() + 200;
            List<View> rowViews = new ArrayList<>();


            for(int i = 0; i < rowTiles.length(); i++) {
                JSONObject tileObject = rowTiles.getJSONObject(i);
                String type = tileObject.getString("type");

                switch (type) {
                    case "smallHeader":
                        String header = tileObject.getString("header");
                        String footer = tileObject.getString("value");
                        Boolean rounded = tileObject.getBoolean("rounded");
                        Log.e("Type", "SMALL HEADER");
//                        View v = createSmallHeaderView(container, header, footer, rounded);
                        View v = createSmallHeaderView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    case "progress":
                        String title = tileObject.getString("under_title");
                        Double currentProgress = tileObject.getDouble("current");
                        int formattedCurrentProgress = currentProgress.intValue();
                        String progressColor = tileObject.getString("color");
                        rounded = tileObject.getBoolean("rounded");
                        v = createProgressView(container, tileObject);
                        v.setId(i);
                        rowViews.add(v);
                        break;
                    default:
                        Log.e("TYPE", "OTHER");
                        break;
                }
            }

            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, correctedHeight + 12);

            View view = null;

//            if(rowViews.size() > maxTiles) {
//                view = inflater.inflate(R.layout.activity_tile_template_test, container, false);
//                RelativeLayout relativeLayout = view.findViewById(R.id.tileRelativeLayout);
//                RelativeLayout.LayoutParams childRelativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, correctedHeight);
//                childRelativeParams.setMargins(6, 6, 6, 6);
//
//                // Starting this at 50 so that it doesn't get confused with other IDs.
//                int viewCounter = 50;
//                for(View v: rowViews) {
//                    v.setId(viewCounter);
//                    if(viewCounter > 50) {
//                        childRelativeParams.addRule(RelativeLayout.RIGHT_OF, viewCounter - 1);
//                    }
//                    relativeLayout.addView(v, childRelativeParams);
//                    viewCounter++;
//                }
//                horizontalScrollView.addView(relativeLayout);
//            }
//            else {
                view = inflater.inflate(R.layout.activity_tile_template_linear_test, container, false);
                LinearLayout linearLayout = view.findViewById(R.id.tileLinearLayout);
                LinearLayout.LayoutParams textviewparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, correctedHeight, 1);
                textviewparam.setMargins(6, 6, 6, 6);

                for(View v: rowViews) {
                    linearLayout.addView(v, textviewparam);
                }
                horizontalScrollView.addView(linearLayout);
//            }

            view.setLayoutParams(relativeParams);

            return horizontalScrollView;

        } catch (JSONException e) {
            // That means this is probably a spacer
            try {
                Double height = rowObject.getDouble("rowheight");
                int correctedHeight = height.intValue() + 200;

                HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inflater.inflate(R.layout.activity_tile_template_test_scrollview, container, false);
                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, correctedHeight + 12);
                View view = inflater.inflate(R.layout.activity_tile_template_linear_test, container, false);
                view.setLayoutParams(relativeParams);
                return horizontalScrollView;
            } catch (JSONException e1) {
                // If we get here, we've really screwed it up.
                e1.printStackTrace();
            }

        }

        return null;

    }

    private View createSmallHeaderView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_smallheader_layout, row, false);

        String headerText = tileObject.getString("header");
        String footerText = tileObject.getString("value");
        Boolean rounded = tileObject.getBoolean("rounded");
        String headerColor = tileObject.getString("header_text_color");
        String footerColor = tileObject.getString("footer_text_color");
        String headerSize = tileObject.getString("font_header");
        String footerSize = tileObject.getString("font_footer");

        TextView header = rowView.findViewById(R.id.smallHeaderTileHeader);
        TextView footer = rowView.findViewById(R.id.smallHeaderTileFooter);
        header.setText(headerText);
        header.setTextColor(Color.parseColor(headerColor));
        header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(headerSize));

        footer.setText(footerText);
        footer.setTextColor(Color.parseColor(footerColor));
        footer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextViewSizing(footerSize));
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);
//        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
            rowView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            GradientDrawable border = new GradientDrawable();
//            border.setColor(0xFFFFFFFF); //white background
            border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
            rowView.setBackground(border);
        }

        return rowView;
    }

    private float getTextViewSizing(String size) {
        float returnSize;
        switch(size) {
            case "small":
                returnSize = getResources().getDimension(R.dimen.font_small);
                break;
            case "medium":
                returnSize = getResources().getDimension(R.dimen.font_normal);
                break;
            case "large":
                returnSize = getResources().getDimension(R.dimen.font_larger);
                break;
            default:
                returnSize = getResources().getDimension(R.dimen.font_mega);
                Log.e("TEXTVIEW SIZE", "Error setting TextView Size: " + size);
                break;
        }

        return returnSize;
    }

    private void createRowOne() {

    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            AssetManager assets = context.getAssets();
            InputStream is = assets.open("input.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

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

    private View createSmallHeaderView(ViewGroup row, String headerText, String footerText, boolean rounded) {
//        String headerText = "YTD Closed Volume";
//        String footerText = "$500K";

        View rowView = inflater.inflate(R.layout.tile_smallheader_layout, row, false);

        TextView header = rowView.findViewById(R.id.smallHeaderTileHeader);
        TextView footer = rowView.findViewById(R.id.smallHeaderTileFooter);
        header.setText(headerText);
        footer.setText(footerText);
        header.setGravity(View.TEXT_ALIGNMENT_CENTER);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
            rowView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            GradientDrawable border = new GradientDrawable();
//            border.setColor(0xFFFFFFFF); //white background
            border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
            rowView.setBackground(border);
        }

        return rowView;
    }

    private View createProgressView(ViewGroup row, JSONObject tileObject) throws JSONException {
        View rowView = inflater.inflate(R.layout.tile_progress_layout, row, false);

        String title = tileObject.getString("under_title");
        Double currentProgress = tileObject.getDouble("current");
        Double maxProgress = tileObject.getDouble("max");
        int formattedCurrentProgress = currentProgress.intValue();
        String progressColor = tileObject.getString("color");
        Boolean rounded = tileObject.getBoolean("rounded");

        CircularProgressBar progress = rowView.findViewById(R.id.progressTileProgressBar);
        TextView titleView = rowView.findViewById(R.id.progressTileHeader);
        titleView.setText(title);
        TextView currentProgressText = rowView.findViewById(R.id.progressTileCurrentNumber);
        currentProgressText.setText(currentProgress.intValue() + "");
        TextView goalProgressText = rowView.findViewById(R.id.progressTileGoalNumber);
        goalProgressText.setText(maxProgress.intValue() + "");
        progress.setColor(Color.parseColor(progressColor));
//        progress.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
        progress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setProgressWithAnimation(formattedCurrentProgress, ANIMATION_DURATION);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        rowView.setClipToOutline(true);
        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
            rowView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            GradientDrawable border = new GradientDrawable();
//            border.setColor(0xFFFFFFFF); //white background
            border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
            rowView.setBackground(border);
        }
//        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(12, 12, 12, 12);
//        rowView.setLayoutParams(layoutParams);
        return rowView;
    }

    private View createProgressView(ViewGroup row, String title, int currentProgress, String color, boolean rounded) {
        View rowView = inflater.inflate(R.layout.tile_progress_layout, row, false);

        CircularProgressBar progress = rowView.findViewById(R.id.progressTileProgressBar);
        TextView titleView = rowView.findViewById(R.id.progressTileHeader);
        titleView.setText(title);
        TextView currentProgressText = rowView.findViewById(R.id.progressTileCurrentNumber);
        TextView goalProgressText = rowView.findViewById(R.id.progressTileGoalNumber);
        progress.setColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateOrange));
//        progress.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorCorporateGrey));
        progress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
        progress.setProgressWithAnimation(currentProgress, ANIMATION_DURATION);
        rowView.setBackgroundColor((ContextCompat.getColor(rowView.getContext(), R.color.colorLightGrey)));
        rowView.setClipToOutline(true);
        if(rounded) {
            GradientDrawable roundedCorners = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners);
            roundedCorners.setColor(ContextCompat.getColor(getContext(), R.color.colorAlmostBlack));
            rowView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_rounded_corners));
        }
        else {
            GradientDrawable border = new GradientDrawable();
//            border.setColor(0xFFFFFFFF); //white background
            border.setStroke(1, 0xFFFFFFFF); //black border with full opacity
            rowView.setBackground(border);
        }
//        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(12, 12, 12, 12);
//        rowView.setLayoutParams(layoutParams);
        return rowView;
    }

    @SuppressLint("ResourceType")
    private void initializeTimelineSelector(View view) {
        spinner = view.findViewById(R.id.tileTimelineSelector);
        spinner.setId(1);
        List<String> spinnerArray = initSpinnerArray();

        DropdownAdapter adapter = new DropdownAdapter(getContext(), R.layout.spinner_item, spinnerArray, colorSchemeManager);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.spinner_item,
//                spinnerArray
//        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calendar = Calendar.getInstance();
//                loader.setVisibility(View.VISIBLE);

                switch (position) {
                    case 0:
                        //Yesterday
                        pastTimeline = true;
                        parentActivity.setTimeline("day");
                        parentActivity.setTimelineSelection(0);
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        needsProgress = false;
                        break;
                    case 1:
                        //Today
                        pastTimeline = false;
                        parentActivity.setTimeline("day");
                        parentActivity.setTimelineSelection(1);

                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        needsProgress = false;
                        break;
                    case 2:
                        //Last Week
                        needsProgress = false;
                        pastTimeline = true;
                        parentActivity.setTimeline("week");
                        parentActivity.setTimelineSelection(2);

                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;

                        calendar.add(Calendar.DAY_OF_WEEK, 6);
                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                        break;
                    case 3:
                        //This Week
                        needsProgress = true;
                        pastTimeline = false;
                        parentActivity.setTimeline("week");
                        parentActivity.setTimelineSelection(3);

                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;

                        calendar.add(Calendar.DAY_OF_WEEK, 6);
                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                        break;
                    case 4:
                        //Last Month
                        needsProgress = false;
                        pastTimeline = true;
                        parentActivity.setTimeline("month");
                        parentActivity.setTimelineSelection(4);

                        calendar.add(Calendar.MONTH, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 5:
                        //This Month
                        needsProgress = true;
                        pastTimeline = false;
                        parentActivity.setTimeline("month");
                        parentActivity.setTimelineSelection(5);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 6:
                        //Last year
                        needsProgress = false;
                        pastTimeline = true;
                        parentActivity.setTimeline("year");
                        parentActivity.setTimelineSelection(6);
                        calendar.add(Calendar.YEAR, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = 12;
                        selectedEndDay = 31;
                        break;
                    case 7:
                        //This year
                        needsProgress = true;
                        pastTimeline = false;
                        parentActivity.setTimeline("year");
                        parentActivity.setTimelineSelection(7);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = 12;
                        selectedEndDay = 31;
                        break;
                }

                String formattedStartMonth = String.valueOf(selectedStartMonth);
                String formattedEndMonth = String.valueOf(selectedEndMonth);
                String formattedStartDay = String.valueOf(selectedStartDay);
                String formattedEndDay = String.valueOf(selectedEndDay);

                if(selectedStartDay < 10) {
                    formattedStartDay = "0" + selectedStartDay;
                }

                if(selectedEndDay < 10) {
                    formattedEndDay = "0" + selectedEndDay;
                }

                if(selectedStartMonth < 10) {
                    formattedStartMonth = "0" + selectedStartMonth;
                }

                if(selectedEndMonth < 10) {
                    formattedEndMonth = "0" + selectedEndMonth;
                }



                formattedStartTime = selectedStartYear + "-" + formattedStartMonth + "-" + formattedStartDay;
                formattedEndTime = selectedEndYear + "-" + formattedEndMonth + "-" + formattedEndDay;
                selectedStartTime = getDateFromFormattedTime(formattedStartTime);
                selectedEndTime = getDateFromFormattedTime(formattedEndTime);

                apiManager.sendAsyncActivities(TileTemplateFragment.this, dataController.getAgent().getAgent_id(), formattedStartTime, formattedEndTime, parentActivity.getSelectedTeamMarketId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Yesterday");
        spinnerArray.add("Today");
        spinnerArray.add("Last Week");
        spinnerArray.add("This Week");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");

        String thisMonth = sdf.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String lastMonth = sdf.format(calendar.getTime());
        spinnerArray.add(lastMonth);
        spinnerArray.add(thisMonth);

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy");
        String thisYear = sdf.format(calendar.getTime());

        calendar.add(Calendar.YEAR, -1);
        String lastYear = sdf.format(calendar.getTime());
        spinnerArray.add(lastYear);
        spinnerArray.add(thisYear);
//        spinnerArray.add("All Records");

        return spinnerArray;
    }

    private Date getDateFromFormattedTime(String formattedTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = formatter.parse(formattedTime);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
