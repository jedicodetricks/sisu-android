package co.sisu.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.Metric;

/**
 * Created by Jeff on 4/18/2018.
 */

public class ActivityListAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Metric> mDataSource;

    public ActivityListAdapter(Context context, List<Metric> items) {
        mContext = context;
        mDataSource = (ArrayList<Metric>) items;
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
        final Metric metric = (Metric) getItem(position);
        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.activity_list_title);

        Switch activitySwitch = rowView.findViewById(R.id.activity_list_switch);

        titleTextView.setText(metric.getTitle());

        return rowView;
    }
}
