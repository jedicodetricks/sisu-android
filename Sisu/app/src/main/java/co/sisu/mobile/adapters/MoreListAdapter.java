package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.models.MorePageContainer;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class MoreListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<MorePageContainer> mDataSource;
    private ColorSchemeManager colorSchemeManager;

    public MoreListAdapter(Context context, List<MorePageContainer> items, ColorSchemeManager colorSchemeManager) {
        mContext = context;
        mDataSource = (ArrayList<MorePageContainer>) items;
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
        View rowView = mInflater.inflate(R.layout.adapter_more_list, parent, false);

        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.more_list_title);
        titleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());
        TextView subTitleTextView = rowView.findViewById(R.id.more_list_subtitle);
        subTitleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());

        // Get thumbnail element
        ImageView thumbnailImageView = rowView.findViewById(R.id.more_list_thumbnail);

        MorePageContainer morePageContainer = (MorePageContainer) getItem(position);
        if(morePageContainer.getSubTitle().equals("")) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rowView.findViewById(R.id.more_list_title).getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            titleTextView.setLayoutParams(layoutParams);
        }

        titleTextView.setText(morePageContainer.getTitle());
        subTitleTextView.setText(morePageContainer.getSubTitle());
        if(!morePageContainer.getTitle().equals("Slack")) {
            Drawable drawable = rowView.getResources().getDrawable(morePageContainer.getThumbnailId()).mutate();
            drawable.setColorFilter(colorSchemeManager.getMenuIcon(), PorterDuff.Mode.SRC_ATOP);
            thumbnailImageView.setImageDrawable(drawable);
        }
        else {
            if(colorSchemeManager.getAppBackground() == Color.WHITE) {
                thumbnailImageView.setImageDrawable(rowView.getResources().getDrawable(R.drawable.slack_icon_color));
            } else {
                thumbnailImageView.setImageDrawable(rowView.getResources().getDrawable(morePageContainer.getThumbnailId()));
            }
        }

        return rowView;
    }
}