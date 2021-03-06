package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.models.LeaderboardItemsObject;
import co.sisu.mobile.models.LeaderboardObject;

/**
 * Created by Brady Groharing on 2/25/2018.
 */

public class LeaderboardListExpandableAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<LeaderboardObject> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<LeaderboardObject, List<LeaderboardItemsObject>> _listDataChild;
    private ParentActivity parentActivity;
    private ImageView thumbnail;

    public LeaderboardListExpandableAdapter(Context context, List<LeaderboardObject> listDataHeader,
                                            HashMap<LeaderboardObject, List<LeaderboardItemsObject>> listChildData, ParentActivity parent, ApiManager apiManager, String agent_id) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.parentActivity = parent;
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

        final LeaderboardItemsObject childText = (LeaderboardItemsObject) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.leaderboard_group_items, null);
        }
        thumbnail = convertView.findViewById(R.id.leaderboard_list_thumbnail);



        //This will always be null the first time through
        final Bitmap bmp = childText.getImage();
        if(bmp == null) {
            thumbnail.setImageResource(R.drawable.client_icon);
            thumbnail.setEnabled(false);
        }
        else {
            thumbnail.setImageBitmap(bmp);
            thumbnail.setEnabled(true);
        }
        final View parentView = parentActivity.findViewById(R.id.linearLayout);

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.getUtils().zoomImageFromThumb(parentView, thumbnail, bmp, parentActivity, parentActivity.findViewById(R.id.expanded_image));
            }
        });

        TextView title = convertView.findViewById(R.id.leaderboardItemTitle);
        TextView subtitle = convertView.findViewById(R.id.leaderboardItemSubTitle);
        TextView score = convertView.findViewById(R.id.leaderboardScore);
        TextView position = convertView.findViewById(R.id.leaderboardPosition);
        ImageView trophy = convertView.findViewById(R.id.trophyIcon);

        title.setText(childText.getLabel());
        if(childText.getValue().contains(".")) {
            score.setText(childText.getValue().substring(0, childText.getValue().indexOf('.')));
        } else {
            score.setText(childText.getValue());
        }
        position.setText(childText.getPlace());
        if(childPosition > 2) {
            trophy.setVisibility(View.INVISIBLE);
        } else {
            trophy.setVisibility(View.VISIBLE);
        }
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
        LeaderboardObject leaderboardObject = (LeaderboardObject) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.leaderboard_group_headers, null);
        }
//        Set the text styling for the list headers of leaderboards
        styleHeaders(convertView, leaderboardObject.getName(), isExpanded, leaderboardObject.getColor());

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

    private void styleHeaders(View convertView, String headerTitle, boolean isExpanded, int headerColor){
        TextView lblListHeader = convertView.findViewById(R.id.record_list_title);
        ImageView thumb = convertView.findViewById(R.id.leaderboard_list_thumbnail);
        int imageResourceId = isExpanded ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
        thumb.setImageResource(imageResourceId);
        thumb.setVisibility(View.VISIBLE);
        lblListHeader.setText(headerTitle);
        convertView.setBackgroundColor(ContextCompat.getColor(_context, headerColor));
        lblListHeader.setBackgroundColor(ContextCompat.getColor(_context, headerColor));
        thumb.setBackgroundColor(ContextCompat.getColor(_context, headerColor));
    }



}
