package co.sisu.mobile.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.Metric;

/**
 * Created by Brady Groharing on 2/24/2018.
 */

public class ReportListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Metric> mDataSource;

    public ReportListAdapter(Context context, List<Metric> items) {
        mContext = context;
        mDataSource = (ArrayList<Metric>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.adapter_report_list, parent, false);

        // Get title element
        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.recipe_list_title);

        // Get subtitle element
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.recipe_list_subtitle);

        // Get thumbnail element
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(R.id.recipe_list_thumbnail);

        ProgressBar progressBar =
                (ProgressBar) rowView.findViewById(R.id.progressBar);

        Metric metric = (Metric) getItem(position);

        titleTextView.setText(metric.getTitle());
        subtitleTextView.setText(metric.getCurrentNum() + "/" + metric.getGoalNum());
        progressBar.setProgress(50);
        Picasso.with(mContext).load(metric.getThumbnailId()).into(thumbnailImageView);


        return rowView;
    }
}
