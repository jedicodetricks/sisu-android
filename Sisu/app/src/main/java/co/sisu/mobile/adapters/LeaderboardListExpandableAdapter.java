package co.sisu.mobile.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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
    int[] teamColors = {R.color.colorCorporateOrange, R.color.colorMoonBlue, R.color.colorYellow, R.color.colorLightGrey};
    private int colorCounter = 0;
    private ApiManager apiManager;
    private ParentActivity parentActivity;
    private String agentId;
    private ImageView thumbnail, expanded;
    private String imageName;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    public LeaderboardListExpandableAdapter(Context context, List<LeaderboardObject> listDataHeader,
                                            HashMap<LeaderboardObject, List<LeaderboardItemsObject>> listChildData, ParentActivity parent, ApiManager apiManager, String agent_id) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        colorCounter = 0;
        this.parentActivity = parent;
        this.apiManager = apiManager;
        this.agentId = agent_id;
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


        mShortAnimationDuration = parentActivity.getResources().getInteger(android.R.integer.config_shortAnimTime);
        expanded = parentActivity.findViewById(R.id.expanded_image);

        //This will always be null the first time through
        final Bitmap bmp = childText.getImage();
        if(bmp == null) {
            thumbnail.setImageResource(R.drawable.contact_icon);
            thumbnail.setClickable(false);
//            imageName = childText.getProfile();
//            if(parentActivity.imageExists(_context, imageName) && imageName != null) {
//                Log.e("CALLING IMAGE", imageName + "");
//                //Bitmap image = parentActivity.getImage(imageName);
//                //if(image != null) {
////                    new LeaderboardImageTask(childText, thumbnail, agentId).execute(imageName);//this is where setting the image is actually happening, calls-download, then sets in onPost
//                //}
//            }
//            else {
//                Log.e("THIS SHIT IS NULL", childText.getLabel());
//                //This would be a default image
//            }
        }
        else {
            thumbnail.setImageBitmap(bmp);
        }
        final View parentView = parentActivity.findViewById(R.id.linearLayout);

        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(parentView, thumbnail, bmp);
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

    private void zoomImageFromThumb(View convertView, final View thumbView, Bitmap bmp) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        expanded.setImageBitmap(bmp);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        TextView title = convertView.findViewById(R.id.leaderboardItemTitle);
        thumbView.getGlobalVisibleRect(startBounds);
        convertView.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        convertView.setAlpha(.5f);
        expanded.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expanded.setPivotX(0f);
        expanded.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expanded, View.X,
                        startBounds.left, finalBounds.centerX() / 2))
                .with(ObjectAnimator.ofFloat(expanded, View.Y,
                        startBounds.top, finalBounds.centerY() / 2))
                .with(ObjectAnimator.ofFloat(expanded, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expanded,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expanded, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expanded,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expanded,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expanded,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                final View parentView = parentActivity.findViewById(R.id.linearLayout);
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parentView.setAlpha(1f);
                        expanded.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        parentView.setAlpha(1f);
                        expanded.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

}
