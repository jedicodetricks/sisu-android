package co.sisu.mobile.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.SelectedActivities;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivityListAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<SelectedActivities> mDataSource;
    private ColorSchemeManager colorSchemeManager;
    private Switch activitySwitch;
    private TextView titleTextView;

    public ActivityListAdapter(Context context, List<SelectedActivities> items, ColorSchemeManager colorSchemeManager) {
        mContext = context;
        mDataSource = (ArrayList<SelectedActivities>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.colorSchemeManager = colorSchemeManager;
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.adapter_activity_list, parent, false);
        final SelectedActivities selectedActivity = (SelectedActivities) getItem(position);
        // Get title element
        titleTextView = rowView.findViewById(R.id.activity_list_title);

        activitySwitch = rowView.findViewById(R.id.activity_list_switch);

        titleTextView.setText(selectedActivity.getName());
        activitySwitch.setChecked(parseValue(selectedActivity.getValue()));
        activitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedActivity.setValue(jsonIsChecked(isChecked));
            }
        });

        setColorScheme();
        return rowView;
    }

    private void setColorScheme() {
        titleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());
//        activitySwitch.setHighlightColor(colorSchemeManager.getSegmentSelected());
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] thumbColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getSegmentSelected()
        };

        int[] trackColors = new int[] {
                Color.GRAY,
                colorSchemeManager.getSegmentSelected()
        };

        DrawableCompat.setTintList(DrawableCompat.wrap(activitySwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(activitySwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
    }

    private boolean parseValue(String value) {
        if(value.equals("0")) {
            return false;
        }
        return true;
    }

    private String jsonIsChecked(boolean isChecked) {
        if(isChecked) {
            return "1";
        }
        return "0";
    }
}
