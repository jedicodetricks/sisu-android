package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.models.ScopeBarModel;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamAgentsListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<ScopeBarModel> mDataSource;
    private ColorSchemeManager colorSchemeManager;

    public TeamAgentsListAdapter(Context context, List<ScopeBarModel> agents, ColorSchemeManager colorSchemeManager) {
        mContext = context;
        mDataSource = agents;
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
        View v = mInflater.inflate(R.layout.adapter_team_agents_list, parent, false);
        ScopeBarModel info = (ScopeBarModel) getItem(position);
        TextView textViewHome = v.findViewById(R.id.team_agent_title);
        ImageView leftIcon = v.findViewById(R.id.team_list_icon);
        ConstraintLayout layout = v.findViewById(R.id.team_agents_layout);

        textViewHome.setText(info.getName());
        if(info.getName().equalsIgnoreCase("-- Groups --") || info.getName().equalsIgnoreCase("-- Agents --")) {
            leftIcon.setVisibility(View.GONE);
            v.setClickable(false);
        }
//        layout.setBackgroundColor(colorSchemeManager.getAppBackground());
//        textViewHome.setBackgroundColor(colorSchemeManager.getAppBackground());
        //TODO: This isn't changing colors correctly for some reason so it's GRAY right now
        textViewHome.setTextColor(Color.GRAY);

        return v;
    }
}
