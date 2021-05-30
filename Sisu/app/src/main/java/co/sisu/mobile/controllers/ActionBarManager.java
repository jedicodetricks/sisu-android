package co.sisu.mobile.controllers;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.AgentModelStringSuperUser;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.ScopeBarModel;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by bradygroharing on 5/25/18.
 */

public class ActionBarManager implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ParentActivity parentActivity;
    private ColorSchemeManager colorSchemeManager;
    private DataController dataController;
    private TextView pageTitle, backtionTitle, teamAgentsTitle;
    private ImageView teamIcon;
    private DrawerLayout drawerLayout;
    private ActionBar bar;
    private List<TeamObject> teamsList;
    int selectedTeam = 0;
    private ClientObject selectedClient;
//    private TeamObject currentTeam;
    private boolean isAdminMode = false;
    private String myAgentId = "";
    private AgentModelStringSuperUser[] allTeamAgents;
    private AgentModelStringSuperUser[] justMyAgent = new AgentModelStringSuperUser[1];
    private ListView teamAgentListView;
    private boolean actionBarColorSet = false;
    private String displayTitle = "";

    //New Action Bar Stuff
    private View teamSelectButton;
    private TextView actionBarTitle;
    private TextView actionBarActionText;
    private TextView teamLetter;
    private View teamBlock;
    private ImageView actionImage;
    //

    public ActionBarManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.dataController = parentActivity.getDataController();
        this.colorSchemeManager = parentActivity.getColorSchemeManager();
        initActionBar();
    }

    public void initActionBar() {
        teamSelectButton = parentActivity.findViewById(R.id.action_bar_home);
        teamSelectButton.setOnClickListener(parentActivity);

        actionBarTitle = parentActivity.findViewById(R.id.actionBarTitle);

        actionBarActionText = parentActivity.findViewById(R.id.saveButton);
        actionBarActionText.setOnClickListener(parentActivity);

        teamLetter = parentActivity.findViewById(R.id.team_letter);
        teamLetter.setOnClickListener(parentActivity);

        teamBlock = parentActivity.findViewById(R.id.team_icon);
        teamBlock.setOnClickListener(parentActivity);

        actionImage = parentActivity.findViewById(R.id.actionBarActionImage);
        actionImage.setOnClickListener(parentActivity);
    }

    public void initTeamBar() {
        List<TeamObject> teamsList = dataController.getTeamsObject();
        if(teamsList.size() > 0) {
            teamLetter.setText(teamsList.get(0).getTeamLetter().toUpperCase());
            teamLetter.setBackgroundColor(colorSchemeManager.getPrimaryColor());
            teamBlock.setBackgroundColor(colorSchemeManager.getPrimaryColor());
        }
        //
    }

    public void setToTitleBar(final String titleString, boolean showTeamSelectButton) {
        parentActivity.runOnUiThread(() -> {
            if(showTeamSelectButton) {
                teamSelectButton.setVisibility(View.VISIBLE);
                teamLetter.setVisibility(View.VISIBLE);
                teamBlock.setVisibility(View.VISIBLE);
            }
            else {
                teamSelectButton.setVisibility(View.INVISIBLE);
                teamLetter.setVisibility(View.INVISIBLE);
                teamBlock.setVisibility(View.INVISIBLE);
            }
            actionBarTitle.setText(titleString);
            actionBarActionText.setVisibility(View.INVISIBLE);
            actionImage.setVisibility(View.GONE);
        });
    }

    public void setToFilterBar(final String titleString) {
        parentActivity.runOnUiThread(() -> {
            actionBarTitle.setText(titleString);
            actionBarActionText.setText("Filters");
            actionBarActionText.setVisibility(View.VISIBLE);
            teamSelectButton.setVisibility(View.INVISIBLE);
            teamLetter.setVisibility(View.INVISIBLE);
            teamBlock.setVisibility(View.INVISIBLE);
            actionImage.setVisibility(View.GONE);
        });
    }

    public void setToSaveBar(final String titleString) {
        parentActivity.runOnUiThread(() -> {
            actionBarTitle.setText(titleString);
            actionBarActionText.setText("Save");
            actionBarActionText.setVisibility(View.VISIBLE);
            teamSelectButton.setVisibility(View.INVISIBLE);
            teamLetter.setVisibility(View.INVISIBLE);
            teamBlock.setVisibility(View.INVISIBLE);
            actionImage.setVisibility(View.GONE);
        });
    }

    public void setToEditBar(final String titleString) {
        parentActivity.runOnUiThread(() -> {
            actionBarTitle.setText(titleString);
            actionBarActionText.setVisibility(View.GONE);
            teamSelectButton.setVisibility(View.INVISIBLE);
            teamLetter.setVisibility(View.INVISIBLE);
            teamBlock.setVisibility(View.INVISIBLE);
            actionImage.setVisibility(View.VISIBLE);
            Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.drag_drop, null).mutate();
            drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
            actionImage.setImageDrawable(drawable);
        });
    }

    public void setToAddBar(final String titleString) {
        parentActivity.runOnUiThread(() -> {
            actionBarTitle.setText(titleString);
            actionBarActionText.setVisibility(View.GONE);
            teamSelectButton.setVisibility(View.INVISIBLE);
            teamLetter.setVisibility(View.INVISIBLE);
            teamBlock.setVisibility(View.INVISIBLE);
            actionImage.setVisibility(View.VISIBLE);
            VectorChildFinder plusVector = new VectorChildFinder(parentActivity, R.drawable.add_icon, actionImage);
            VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
            plusPath.setFillColor(colorSchemeManager.getPrimaryColor());
            plusPath.setStrokeColor(colorSchemeManager.getPrimaryColor());
            actionImage.invalidate();
        });
    }

    public void setToSaveDeleteBar(final String titleString) {
        parentActivity.runOnUiThread(() -> {
            actionBarTitle.setText(titleString);
            actionBarActionText.setVisibility(View.VISIBLE);
            actionBarActionText.setText("Save");
            teamSelectButton.setVisibility(View.INVISIBLE);
            teamLetter.setVisibility(View.INVISIBLE);
            teamBlock.setVisibility(View.INVISIBLE);
            actionImage.setVisibility(View.VISIBLE);
            Drawable drawable = parentActivity.getResources().getDrawable(R.drawable.trash_icon, null).mutate();
            drawable.setColorFilter(colorSchemeManager.getIconIdle(), PorterDuff.Mode.SRC_ATOP);
            actionImage.setImageDrawable(drawable);
        });
    }

    public int getSelectedTeamId() {
        int teamId = 0;
        if(teamsList != null) {
            teamId = teamsList.get(selectedTeam).getId();

        }
        return teamId;
    }

    public void setTitle(String title) {
        parentActivity.runOnUiThread(() -> {
            displayTitle = title;
            actionBarTitle.setText(title);
        });

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
