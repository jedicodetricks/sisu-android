package co.sisu.mobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamBarAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mDataSource;

    public TeamBarAdapter(Context context, List<String> teams) {
        mContext = context;
        mDataSource = (ArrayList<String>) teams;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = mInflater.inflate(R.layout.adapter_record_list, parent, false);
        TextView textViewHome = (TextView) v.findViewById(R.id.record_list_title);

        textViewHome.setText("HELLO");

        return v;
    }
}
