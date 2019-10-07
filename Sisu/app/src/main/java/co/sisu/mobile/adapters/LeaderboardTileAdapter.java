package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.LeaderboardItemsObject;
import co.sisu.mobile.models.LeaderboardObject;
import co.sisu.mobile.models.Metric;

/**
 * Created by Brady Groharing on 2/25/2018.
 */

public class LeaderboardTileAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private JSONArray mDataSource;

    public LeaderboardTileAdapter(Context context, JSONArray data) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mDataSource = data;
    }

    @Override
    public int getCount() {
        return mDataSource.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mDataSource.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView = null;
        final JSONObject object = (JSONObject) getItem(position);
        String headerText = "";
        String footerText = "";
        try {
            headerText = object.getString("agent_name");
            footerText = object.getString("item_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rowView = mInflater.inflate(R.layout.tile_smallheader_side_layout, parent, false);

        TextView header = rowView.findViewById(R.id.smallHeaderTileHeader);
        TextView footer = rowView.findViewById(R.id.smallHeaderTileFooter);

        header.setText(headerText);
        footer.setText(footerText);

        return rowView;
    }
}
