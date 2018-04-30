package co.sisu.mobile.adapters;

import android.content.Context;
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
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.models.SelectedActivities;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivityListAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<SelectedActivities> mDataSource;

    public ActivityListAdapter(Context context, List<SelectedActivities> items) {
        mContext = context;
        mDataSource = (ArrayList<SelectedActivities>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TextView titleTextView = rowView.findViewById(R.id.activity_list_title);

        Switch activitySwitch = rowView.findViewById(R.id.activity_list_switch);

        titleTextView.setText(selectedActivity.getName());
        activitySwitch.setChecked(parseValue(selectedActivity.getValue()));
        activitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedActivity.setValue(jsonIsChecked(isChecked));
            }
        });
        return rowView;
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
