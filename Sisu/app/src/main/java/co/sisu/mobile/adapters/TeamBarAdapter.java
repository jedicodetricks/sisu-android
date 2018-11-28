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
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Brady Groharing on 3/12/2018.
 */

public class TeamBarAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<TeamObject> mDataSource;
    private ColorSchemeManager colorSchemeManager;

    public TeamBarAdapter(Context context, List<TeamObject> teams, ColorSchemeManager colorSchemeManager) {
        mContext = context;
        mDataSource = (ArrayList<TeamObject>) teams;
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = mInflater.inflate(R.layout.adapter_teams_list, parent, false);
        TeamObject info = (TeamObject) getItem(position);
        TextView textViewHome = v.findViewById(R.id.team_title);
        TextView letter = v.findViewById(R.id.list_team_letter);
        View block = v.findViewById(R.id.rectangle_at_the_top);
        ImageView icon = v.findViewById(R.id.team_list_icon);
        ConstraintLayout layout = v.findViewById(R.id.team_layout);

        if(info.getIcon() != null) {
            Picasso.with(mContext).load(Uri.parse(info.getIcon())).into(icon);
            icon.setVisibility(View.VISIBLE);
            letter.setVisibility(View.GONE);
            SaveSharedPreference.setIcon(mContext, info.getIcon());
            icon.setPadding(0, Math.round(mContext.getResources().getDimension(R.dimen.spacing_tiny)), 0, 0);
            block.setBackgroundColor(Color.TRANSPARENT);
        } else {
            letter.setText(info.getTeamLetter());
            letter.setBackgroundColor(info.getColor());
            block.setBackgroundColor(info.getColor());
        }

        textViewHome.setText(info.getName());
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());
        textViewHome.setBackgroundColor(colorSchemeManager.getAppBackground());
        //TODO: This isn't changing colors correctly for some reason so it's GRAY right now
        textViewHome.setTextColor(Color.GRAY);

        return v;
    }
}
