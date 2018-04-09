package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;

/**
 * Created by Brady Groharing on 2/25/2018.
 */

public class LeaderboardListExpandableAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public LeaderboardListExpandableAdapter(Context context, List<String> listDataHeader,
                                            HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.leaderboard_group_items, null);
        }

        TextView title = convertView.findViewById(R.id.leaderboardItemTitle);
        TextView subtitle = convertView.findViewById(R.id.leaderboardItemSubTitle);
        TextView score = convertView.findViewById(R.id.leaderboardScore);
        TextView position = convertView.findViewById(R.id.leaderboardPosition);

        title.setText(childText);
        subtitle.setText("Testing");
        score.setText("12,345");
        position.setText(String.valueOf(childPosition + 1));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.leaderboard_group_headers, null);
        }
//        Set the text styling for the list headers of leaderboards
        styleHeaders(convertView, headerTitle, isExpanded);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void styleHeaders(View convertView, String headerTitle, boolean isExpanded){
        TextView lblListHeader = convertView.findViewById(R.id.record_list_title);
        ImageView thumb = convertView.findViewById(R.id.leaderboard_list_thumbnail);
        int imageResourceId = isExpanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
        thumb.setImageResource(imageResourceId);
        thumb.setVisibility(View.VISIBLE);

        Typeface mtypeFace = ResourcesCompat.getFont(_context, R.font.roboto_regular);
        lblListHeader.setTypeface(mtypeFace);
        lblListHeader.setTextColor( ContextCompat.getColor(_context, R.color.colorWhite));
        lblListHeader.setTextSize(20);
        lblListHeader.setText(headerTitle);

        switch(headerTitle) {
            case "Overall Leaderboard":
                convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorCorporateOrange));
                lblListHeader.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorCorporateOrange));
                thumb.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorCorporateOrange));
                break;
            case "Under Contract":
                convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorMoonBlue));
                lblListHeader.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorMoonBlue));
                thumb.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorMoonBlue));
                break;
            case "Closed":
                convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorYellow));
                lblListHeader.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorYellow));
                thumb.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorYellow));
                break;
        }
    }
}
