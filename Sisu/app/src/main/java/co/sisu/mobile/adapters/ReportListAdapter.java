package co.sisu.mobile.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.Metric;

/**
 * Created by Brady Groharing on 2/24/2018.
 */

public class ReportListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Metric> mDataSource;
    private String timeline;
    private ColorSchemeManager colorSchemeManager;
    private AsyncActivitySettingsObject firstOtherActivity;

    public ReportListAdapter(Context context, List<Metric> items, String timeline, ColorSchemeManager colorSchemeManager, AsyncActivitySettingsObject firstOtherActivity) {
        mContext = context;
        mDataSource = (ArrayList<Metric>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.timeline = timeline;
        this.colorSchemeManager = colorSchemeManager;
        this.firstOtherActivity = firstOtherActivity;
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
        View rowView = null;
        Metric metric = (Metric) getItem(position);

        if(metric.getType().equalsIgnoreCase(firstOtherActivity.getActivity_type())) {
            rowView = mInflater.inflate(R.layout.adapter_report_list_other_hack, parent, false);
            TextView otherText = rowView.findViewById(R.id.otherText);
            otherText.setTextColor(colorSchemeManager.getDarkerTextColor());
        }
        else {
            rowView = mInflater.inflate(R.layout.adapter_report_list, parent, false);
        }


        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.report_list_title);
        titleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());
        // Get subtitle element
        TextView subtitleTextView = rowView.findViewById(R.id.report_list_subtitle);
        subtitleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());
        // Get percentage text element
        TextView percentageTextView = rowView.findViewById(R.id.report_percentage_text);
        percentageTextView.setTextColor(colorSchemeManager.getDarkerTextColor());
        // Get thumbnail element
        ImageView thumbnailImageView = rowView.findViewById(R.id.report_list_thumbnail);

        ProgressBar progressBar = rowView.findViewById(R.id.progressBar);
        progressBar.setProgressBackgroundTintList(ColorStateList.valueOf(rowView.getResources().getColor(R.color.colorCorporateGrey)));

        titleTextView.setText(metric.getTitle());
        if(metric.getCurrentNum() < 0) {
            metric.setCurrentNum(0);
        }
        else {
            String goalNum = "0";
            switch (timeline) {
                case "day":
                    goalNum = String.valueOf(metric.getDailyGoalNum());
                    break;
                case "week":
                    goalNum = String.valueOf(metric.getWeeklyGoalNum());
                    break;
                case "month":
                    goalNum = String.valueOf(metric.getGoalNum());
                    break;
                case "year":
                    goalNum = String.valueOf(metric.getYearlyGoalNum());
                    break;
            }
            if(goalNum.equals("0")) {
                goalNum = "1";
            }

            if(!goalNum.startsWith("0") && goalNum.contains(".0")) {
                goalNum = goalNum.replace(".0", "");
            }

            if(goalNum.length() > 6) {
                goalNum = goalNum.substring(0, 5);
            }

            if(metric.getType().equals("CONTA") ||
                metric.getType().equals("BUNDC") ||
                metric.getType().equals("SUNDC") ||
                metric.getType().equals("BSGND") ||
                metric.getType().equals("SSGND") ||
                metric.getType().equals("SAPPT") ||
                metric.getType().equals("BAPPT") ||
                metric.getType().equals("BCLSD") ||
                metric.getType().equals("SCLSD")) {

                subtitleTextView.setText(metric.getCurrentNum() + " of " + goalNum);
                percentageTextView.setText((metric.getPercentComplete(timeline) > 100 ? 100: metric.getPercentComplete(timeline)) + "% complete");
                progressBar.setProgress(metric.getPercentComplete(timeline));
                progressBar.setScaleY(4f);
                progressBar.setProgressTintList(ColorStateList.valueOf(metric.getColor()));
                animateBars(progressBar);

            } else {
                subtitleTextView.setText("Total: " + metric.getCurrentNum());
                percentageTextView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
        Drawable drawable = rowView.getResources().getDrawable(metric.getThumbnailId()).mutate();
        drawable.setColorFilter(colorSchemeManager.getIconActive(), PorterDuff.Mode.SRC_ATOP);
        thumbnailImageView.setImageDrawable(drawable);

        return rowView;
    }

    private void animateBars(ProgressBar progressBar){
        final int ANIMATION_DURATION = 2500;
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, progressBar.getProgress());
        animation.setDuration(ANIMATION_DURATION);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}
