package co.sisu.mobile.controllers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.TeamAgentsListAdapter;
import co.sisu.mobile.adapters.TeamBarAdapter;
import co.sisu.mobile.models.AgentModelStringSuperUser;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.TeamObject;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by bradygroharing on 5/25/18.
 */

public class ActionBarManager {
    private ParentActivity parentActivity;
    private ColorSchemeManager colorSchemeManager;
    private TextView pageTitle, teamLetter, backtionTitle;
    private ImageView teamIcon;
    private View teamBlock;
    private DrawerLayout drawerLayout;
    private ActionBar bar;
    private List<TeamObject> teamsList;
    int selectedTeam = 0;
    private ClientObject selectedClient;
    private TeamObject currentTeam;
    private AgentModelStringSuperUser[] teamAgents;


    public ActionBarManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.colorSchemeManager = parentActivity.getColorSchemeManager();
        bar = parentActivity.getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setDisplayShowCustomEnabled(true);
        View customView = parentActivity.getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        bar.setCustomView(customView);
        TextView title = parentActivity.findViewById(R.id.action_bar_title);
        title.setText(parentActivity.localizeLabel(parentActivity.getResources().getString(R.string.scoreboard)));
        Toolbar parent =(Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0,0);
        parent.setPaddingRelative(0,0,0,0);
        drawerLayout = parentActivity.findViewById(R.id.drawer_layout);
    }

    public void initializeActionBar(String fragmentTag) {

        bar.setCustomView(R.layout.action_bar_layout);
        pageTitle = parentActivity.findViewById(R.id.action_bar_title);
        teamLetter = parentActivity.findViewById(R.id.team_letter);
        teamIcon = parentActivity.findViewById(R.id.team_icon);
        teamBlock = parentActivity.findViewById(R.id.action_bar_home);

        View view = parentActivity.getSupportActionBar().getCustomView();
        pageTitle.setText(parentActivity.localizeLabel(fragmentTag));
        View homeButton= view.findViewById(R.id.action_bar_home);
        homeButton.setOnClickListener(parentActivity);

        teamBlock.setVisibility(View.GONE);
        teamLetter.setVisibility(View.GONE);

        parentActivity.getSupportActionBar().setElevation(0);
    }

    public void initializeTeamBar(List<TeamObject> teamsList) {
        if(teamsList.size() > 0) {
            ListView mListView = parentActivity.findViewById(R.id.navViewList);
            mListView.setDivider(null);
            mListView.setDividerHeight(30);

            this.teamsList = teamsList;


            mListView.setOnItemClickListener(parentActivity);
            if(colorSchemeManager.getIcon() != null) {
                //teamsList.get(selectedTeam).setIcon("https://s3-us-west-2.amazonaws.com/sisu-shared-storage/team_logo/Better_Homes_and_Gardens_Real_Estate_Logo.jpg");
                teamsList.get(selectedTeam).setIcon(colorSchemeManager.getIcon());
            } else {
                teamLetter.setText(teamsList.get(0).getTeamLetter().toUpperCase());
                teamLetter.setBackgroundColor(teamsList.get(0).getColor());
            }

            teamBlock.setBackgroundColor(teamsList.get(0).getColor());
            TeamBarAdapter adapter = new TeamBarAdapter(parentActivity.getBaseContext(), teamsList, colorSchemeManager);
            mListView.setAdapter(adapter);
        }
    }

    public void initializeTeamAgents(AgentModelStringSuperUser[] teamAgents) {
//        if(teamAgents.length > 0) {
//            ListView mListView = parentActivity.findViewById(R.id.team_agent_list);
//            mListView.setDivider(null);
//            mListView.setDividerHeight(30);
//
//            this.teamAgents = teamAgents;
//
//            mListView.setOnItemClickListener(parentActivity);
//
//            TeamAgentsListAdapter adapter = new TeamAgentsListAdapter(parentActivity.getBaseContext(), teamAgents, colorSchemeManager);
//            mListView.setAdapter(adapter);
//        }
    }

    public void swapToSaveAction(final String titleString, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
                parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getActionbarBackground()));
                backtionTitle = parentActivity.findViewById(R.id.actionBarTitle);
                backtionTitle.setTextColor(colorSchemeManager.getActionbarText());
                TextView saveButton = parentActivity.findViewById(R.id.saveButton);
                saveButton.setTextColor(colorSchemeManager.getActionbarText());

                if(titleString == null) {
                    String displayName = "";
                    if(selectedClient.getFirst_name() != null) {
                        displayName += selectedClient.getFirst_name() + " ";
                    }
                    if(selectedClient.getLast_name() != null) {
                        displayName += selectedClient.getLast_name();
                    }
                    backtionTitle.setText(displayName);
                } else {
                    backtionTitle.setText(parentActivity.localizeLabel(titleString));
                }

                manageDrawerEnabled(isDrawerEnabled);
            }
        });
    }

    public void swapToSaveEditAction(final String titleString, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_save_edit_layout);
                backtionTitle = parentActivity.findViewById(R.id.actionBarTitle);
                if(titleString == null) {
                    String displayName = "";
                    if(selectedClient.getFirst_name() != null) {
                        displayName += selectedClient.getFirst_name() + " ";
                    }
                    if(selectedClient.getLast_name() != null) {
                        displayName += selectedClient.getLast_name();
                    }
                    backtionTitle.setText(displayName);
                } else {
                    backtionTitle.setText(titleString);
                }

                manageDrawerEnabled(isDrawerEnabled);
            }
        });
    }

    public void swapToTitleBar(final String titleString, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_layout);
//                title = parentActivity.findViewById(R.id.title);
                parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getActionbarBackground()));
//                ConstraintLayout layout = parentActivity.findViewById(R.id.action_bar_parent);
//                layout.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorClay));

                pageTitle = parentActivity.findViewById(R.id.action_bar_title);
                pageTitle.setTextColor(colorSchemeManager.getActionbarText());
//                teamLetter = parentActivity.findViewById(R.id.team_letter);
//                teamBlock = parentActivity.findViewById(R.id.action_bar_home);
//                teamIcon = parentActivity.findViewById(R.id.team_icon);
                pageTitle.setText(parentActivity.localizeLabel(titleString));
//                View homeButton= parentActivity.findViewById(R.id.action_bar_home);
//                homeButton.setOnClickListener(parentActivity);
                manageDrawerEnabled(isDrawerEnabled);
//                if(teamsList != null) {
////                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//                    teamBlock.setBackgroundColor(teamsList.get(selectedTeam).getColor());
//                    if(colorSchemeManager.getIcon() != null) {
//                        Picasso.with(parentActivity).load(Uri.parse(colorSchemeManager.getIcon())).into(teamIcon);
//                        //teamIcon.setImageURI(Uri.parse(colorSchemeManager.getIcon()));
//                        //teamsList.get(selectedTeam).setIcon("https://s3-us-west-2.amazonaws.com/sisu-shared-storage/team_logo/Better_Homes_and_Gardens_Real_Estate_Logo.jpg");
//                        teamsList.get(selectedTeam).setIcon(colorSchemeManager.getIcon());
//                        teamIcon.setVisibility(View.VISIBLE);
//                        teamBlock.setVisibility(View.INVISIBLE);
//                        teamLetter.setVisibility(View.GONE);
//                    } else {
//                        teamLetter.setText(teamsList.get(selectedTeam).getTeamLetter().toUpperCase());
//                        teamLetter.setBackgroundColor(teamsList.get(selectedTeam).getColor());
//                        teamBlock.setVisibility(View.VISIBLE);
//                        teamLetter.setVisibility(View.VISIBLE);
//                        teamIcon.setVisibility(View.GONE);
//                    }
//                }
//                else {
////                    teamBlock.setVisibility(View.GONE);
////                    teamLetter.setVisibility(View.GONE);
////                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//                }
            }
        });

    }

    public void swapToClientListBar(final String title, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_clients_layout);
                parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getActionbarBackground()));
                pageTitle = parentActivity.findViewById(R.id.actionBarTitle);
                if(title != null) {
                    pageTitle.setText(parentActivity.localizeLabel(title));
                }
                pageTitle.setTextColor(colorSchemeManager.getActionbarText());

                manageDrawerEnabled(isDrawerEnabled);
            }
        });
    }

    public void swapToAddClientBar(final String title, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_add_client_layout);
                parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getActionbarBackground()));
                pageTitle = parentActivity.findViewById(R.id.actionBarTitle);
                if(title != null) {
                    pageTitle.setText(parentActivity.localizeLabel(title));
                    TextView sendButton = parentActivity.findViewById(R.id.addClientSaveButton);
                    sendButton.setText("Send Slack");
                }

                pageTitle.setTextColor(colorSchemeManager.getActionbarText());

                manageDrawerEnabled(isDrawerEnabled);
            }
        });
    }

    private void manageDrawerEnabled(boolean isDrawerEnabled) {
        teamLetter = parentActivity.findViewById(R.id.team_letter);
        teamBlock = parentActivity.findViewById(R.id.action_bar_home);
        teamIcon = parentActivity.findViewById(R.id.team_icon);

        if(isDrawerEnabled) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            teamBlock.setOnClickListener(parentActivity);
            teamIcon.setOnClickListener(parentActivity);
            if(teamsList != null) {
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                teamBlock.setBackgroundColor(teamsList.get(selectedTeam).getColor());
                if(colorSchemeManager.getIcon() != null) {
                    Picasso.with(parentActivity).load(Uri.parse(colorSchemeManager.getIcon())).into(teamIcon);
                    //teamIcon.setImageURI(Uri.parse(colorSchemeManager.getIcon()));
                    teamsList.get(selectedTeam).setIcon(colorSchemeManager.getIcon());
                    teamIcon.setVisibility(View.VISIBLE);
                    teamBlock.setVisibility(View.INVISIBLE);
                    teamLetter.setVisibility(View.GONE);
                } else {
                    teamLetter.setText(teamsList.get(selectedTeam).getTeamLetter().toUpperCase());
                    teamLetter.setBackgroundColor(teamsList.get(selectedTeam).getColor());
                    teamBlock.setVisibility(View.VISIBLE);
                    teamLetter.setVisibility(View.VISIBLE);
                    teamIcon.setVisibility(View.GONE);
                }
            }
            else {
                    teamBlock.setVisibility(View.GONE);
                    teamLetter.setVisibility(View.GONE);
                    teamIcon.setVisibility(View.GONE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }
        else {
            if(teamLetter != null) {
                teamLetter.setVisibility(View.GONE);
            }
            if(teamBlock != null) {
                teamBlock.setVisibility(View.GONE);
            }
            if(teamIcon != null) {
                teamIcon.setVisibility(View.GONE);
            }
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void swapToEditClientBar(final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_client_edit_layout);
                parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getActionbarBackground()));
                backtionTitle = parentActivity.findViewById(R.id.actionBarTitle);
                backtionTitle.setTextColor(colorSchemeManager.getActionbarText());
                String displayName = "";
                if(selectedClient.getFirst_name() != null) {
                    displayName += selectedClient.getFirst_name() + " ";
                }
                if(selectedClient.getLast_name() != null) {
                    displayName += selectedClient.getLast_name();
                }
                backtionTitle.setText(parentActivity.localizeLabel(displayName));

                manageDrawerEnabled(isDrawerEnabled);
            }
        });
    }

    public void updateTeam(TeamObject team) {
        currentTeam = team;
        if(colorSchemeManager.getIcon() != null) {
            Picasso.with(parentActivity).load(Uri.parse(colorSchemeManager.getIcon())).into(teamIcon);
            //teamIcon.setImageURI(Uri.parse(colorSchemeManager.getIcon()));
            teamIcon.setVisibility(View.VISIBLE);
            teamLetter.setVisibility(View.GONE);
            teamBlock.setBackgroundColor(Color.TRANSPARENT);
            SaveSharedPreference.setIcon(parentActivity, colorSchemeManager.getIcon());
        } else {
            teamLetter.setText(team.getTeamLetter());
            teamLetter.setBackgroundColor(team.getColor());
            teamBlock.setBackgroundColor(team.getColor());
            teamBlock.setVisibility(View.VISIBLE);
            teamLetter.setVisibility(View.VISIBLE);
            teamIcon.setVisibility(View.INVISIBLE);
        }
    }

    public TeamObject getCurrentTeam() {
        return currentTeam;
    }

    public void toggleDrawer() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public int getSelectedTeamId() {
        int teamId = 0;
        if(teamsList != null) {
            teamId = teamsList.get(selectedTeam).getId();

        }
        return teamId;
    }

    public void updateSelectedTeam(int position) {
        selectedTeam = position;
    }

    public void setSelectedClient(ClientObject selectedClient) {
        this.selectedClient = selectedClient;
    }

    public void updateTeamAgents(AgentModelStringSuperUser[] teamAgents) {
        this.teamAgents = teamAgents;
    }

    public ClientObject getSelectedClient() {
        return selectedClient;
    }

    public int getMarketId() {
        int marketId = 0;
        if(teamsList != null) {
            marketId = teamsList.get(selectedTeam).getMarket_id();
        }
        return marketId;
    }

    public void updateColorSchemeManager(ColorSchemeManager colorSchemeManager) {
        this.colorSchemeManager = colorSchemeManager;
        if(colorSchemeManager.getIcon() != null) {
            Picasso.with(parentActivity).load(Uri.parse(colorSchemeManager.getIcon())).into(teamIcon);
            //teamIcon.setImageURI(Uri.parse(colorSchemeManager.getIcon()));
            teamIcon.setVisibility(View.VISIBLE);
            teamLetter.setVisibility(View.GONE);
            teamBlock.setBackgroundColor(Color.TRANSPARENT);
            SaveSharedPreference.setIcon(parentActivity, colorSchemeManager.getIcon());
        } else {
            teamLetter.setText(currentTeam.getTeamLetter());
            teamLetter.setBackgroundColor(currentTeam.getColor());
            teamBlock.setBackgroundColor(currentTeam.getColor());
            teamBlock.setVisibility(View.VISIBLE);
            teamLetter.setVisibility(View.VISIBLE);
            teamIcon.setVisibility(View.INVISIBLE);
        }
    }

}
