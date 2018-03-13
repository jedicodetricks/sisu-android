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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamBarAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<TeamObject> mDataSource;

    public TeamBarAdapter(Context context, List<TeamObject> teams) {
        mContext = context;
        mDataSource = (ArrayList<TeamObject>) teams;
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
        View v = mInflater.inflate(R.layout.adapter_teams_list, parent, false);
        TeamObject info = (TeamObject) getItem(position);
        TextView textViewHome = v.findViewById(R.id.team_title);
        TextView letter = v.findViewById(R.id.team_letter);
        View block = v.findViewById(R.id.rectangle_at_the_top);
        String firstLetter = info.getName().charAt(0) + "";
        textViewHome.setText(info.getName());
        letter.setText(firstLetter);

        return v;
    }
}
