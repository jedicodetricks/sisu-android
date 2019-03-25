package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.models.AgentModelStringSuperUser;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamAgentsListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private AgentModelStringSuperUser[] mDataSource;
    private ColorSchemeManager colorSchemeManager;

    public TeamAgentsListAdapter(Context context, AgentModelStringSuperUser[] agents, ColorSchemeManager colorSchemeManager) {
        mContext = context;
        mDataSource = agents;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.colorSchemeManager = colorSchemeManager;
    }

    @Override
    public int getCount() {
        return mDataSource.length;
    }

    @Override
    public Object getItem(int position) {
        return mDataSource[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.adapter_team_agents_list, parent, false);
        AgentModelStringSuperUser info = (AgentModelStringSuperUser) getItem(position);
        TextView textViewHome = v.findViewById(R.id.team_agent_title);
        ConstraintLayout layout = v.findViewById(R.id.team_agents_layout);

        textViewHome.setText(info.getFirst_name() + " " + info.getLast_name());
//        layout.setBackgroundColor(colorSchemeManager.getAppBackground());
//        textViewHome.setBackgroundColor(colorSchemeManager.getAppBackground());
        //TODO: This isn't changing colors correctly for some reason so it's GRAY right now
        textViewHome.setTextColor(Color.GRAY);

        return v;
    }
}
