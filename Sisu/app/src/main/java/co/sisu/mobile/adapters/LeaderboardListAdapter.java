package co.sisu.mobile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.Inflater;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.models.LeaderboardAgentModel;
import co.sisu.mobile.models.LeaderboardObject;

/**
 * Created by Brady Groharing on 2/25/2018.
 */

public class LeaderboardListAdapter extends BaseAdapter {

    private Context _context;
    // child data in format of header title, child title
    private List<LeaderboardAgentModel> listDataChild;
    private ParentActivity parentActivity;
    private ImageView thumbnail;
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private String url = "https://api.sisu.co/";
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public LeaderboardListAdapter(Context context,
                                  List<LeaderboardAgentModel> listChildData, ParentActivity parent, ApiManager apiManager, String agent_id, ImageLoader imageLoader) {
        this._context = context;
        this.listDataChild = listChildData;
        this.parentActivity = parent;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.imageLoader = imageLoader;

    }

//    @Override
//    public View getChildView(int groupPosition, final int childPosition,
//                             boolean isLastChild, View convertView, ViewGroup parent) {
//
//        final LeaderboardAgentModel childText = (LeaderboardAgentModel) getChild(groupPosition, childPosition);
//
//        if (convertView == null) {
//            LayoutInflater infalInflater = (LayoutInflater) this._context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.leaderboard_group_items, null);
//        }
//        thumbnail = convertView.findViewById(R.id.leaderboard_list_thumbnail);
//
//
//
//        //This will always be null the first time through
//        final Bitmap bmp = childText.getImage();
//        if(bmp == null) {
//            thumbnail.setImageResource(R.drawable.client_icon);
//            thumbnail.setEnabled(false);
//        }
//        else {
//            thumbnail.setImageBitmap(bmp);
//            thumbnail.setEnabled(true);
//        }
//        final View parentView = parentActivity.findViewById(R.id.linearLayout);
//
//        thumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                parentActivity.getUtils().zoomImageFromThumb(parentView, thumbnail, bmp, parentActivity, parentActivity.findViewById(R.id.expanded_image));
//            }
//        });
//
//        TextView title = convertView.findViewById(R.id.leaderboardItemTitle);
//        TextView subtitle = convertView.findViewById(R.id.leaderboardItemSubTitle);
//        TextView score = convertView.findViewById(R.id.leaderboardScore);
//        TextView position = convertView.findViewById(R.id.leaderboardPosition);
//        ImageView trophy = convertView.findViewById(R.id.trophyIcon);
//
//        title.setText(childText.getName());
//        if(childText.getItemCount().contains(".")) {
//            score.setText(childText.getItemCount().substring(0, childText.getItemCount().indexOf('.')));
//        } else {
//            score.setText(childText.getItemCount());
//        }
//        position.setText(childText.getPlace());
//        if(childPosition > 2) {
//            trophy.setVisibility(View.INVISIBLE);
//        } else {
//            trophy.setVisibility(View.VISIBLE);
//        }
//        return convertView;
//    }

//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded,
//                             View convertView, ViewGroup parent) {
//        LeaderboardObject leaderboardObject = (LeaderboardObject) getGroup(groupPosition);
//
//        if (convertView == null) {
//            LayoutInflater infalInflater = (LayoutInflater) this._context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.leaderboard_group_headers, null);
//        }
////        Set the text styling for the list headers of leaderboards
//        styleHeaders(convertView, leaderboardObject.getName(), isExpanded, leaderboardObject.getColor());
//
//        return convertView;
//    }

    @Override
    public int getCount() {
        return listDataChild.size();
    }

    @Override
    public Object getItem(int position) {
        return listDataChild.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.leaderboard_group_items, parent, false);
        final LeaderboardAgentModel child = (LeaderboardAgentModel) getItem(position);

        thumbnail = rowView.findViewById(R.id.leaderboard_list_thumbnail);

        //This will always be null the first time through
        final Bitmap bmp = child.getImage();
        if(bmp == null) {
            thumbnail.setImageResource(R.drawable.client_icon);
//            thumbnail.setEnabled(false);
        }
        else {
            thumbnail.setImageBitmap(bmp);
//            thumbnail.setEnabled(true);
        }
        String imageUrl = url + "api/v1/image/" + child.getImageUrl();
        imageLoader.displayImage(imageUrl, thumbnail, animateFirstListener);
        final View parentView = parentActivity.findViewById(R.id.linearLayout);

        thumbnail.setOnClickListener(v -> {
            parentActivity.getUtils().zoomImageFromThumb(parentView, v.findViewById(R.id.leaderboard_list_thumbnail), parentActivity, parentActivity.findViewById(R.id.expanded_image));
        });

        TextView title = rowView.findViewById(R.id.leaderboardItemTitle);
        TextView subtitle = rowView.findViewById(R.id.leaderboardItemSubTitle);
        TextView score = rowView.findViewById(R.id.leaderboardScore);
        TextView place = rowView.findViewById(R.id.leaderboardPosition);
        ImageView trophy = rowView.findViewById(R.id.trophyIcon);

        title.setText(child.getName());
        if(child.getItemCount().contains(".")) {
            score.setText(child.getItemCount().substring(0, child.getItemCount().indexOf('.')));
        } else {
            score.setText(child.getItemCount());
        }
        place.setText(child.getPlace());
        if(position > 2) {
            trophy.setVisibility(View.INVISIBLE);
        } else {
            trophy.setVisibility(View.VISIBLE);
        }
        return rowView;
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

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    imageView.setEnabled(true);
                    displayedImages.add(imageUri);
                }
            }
        }
    }



}
