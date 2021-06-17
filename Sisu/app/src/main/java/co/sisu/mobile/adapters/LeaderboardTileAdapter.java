package co.sisu.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import co.sisu.mobile.R;

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
