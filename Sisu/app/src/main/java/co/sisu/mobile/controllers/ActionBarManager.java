package co.sisu.mobile.controllers;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.squareup.picasso.Picasso;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.TeamAgentsListAdapter;
import co.sisu.mobile.adapters.TeamBarAdapter;
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
    TeamAgentsListAdapter wholeTeamAdapter;
    TeamAgentsListAdapter justMyAgentAdapter;
    private boolean actionBarColorSet = false;
    private String displayTitle = "";

    //New Action Bar Stuff
    private View teamSelectButton;
    private TextView actionBarTitle;
    private TextView actionBarActionText;
    private TextView teamLetter;
    private View teamBlock;
    //

    public ActionBarManager(ParentActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.dataController = parentActivity.getDataController();
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
    }

    public void initTeamBar() {
        List<TeamObject> teamsList = dataController.getTeamsObject();
        //Custom Action Bar stuff
        if(teamsList.size() > 0) {
            ListView mListView = parentActivity.findViewById(R.id.navViewList);
            mListView.setDivider(null);
            mListView.setDividerHeight(30);

            mListView.setOnItemClickListener(this);

            teamLetter.setText(teamsList.get(0).getTeamLetter().toUpperCase());
            teamLetter.setBackgroundColor(teamsList.get(0).getColor());

            teamBlock.setBackgroundColor(teamsList.get(0).getColor());
            TeamBarAdapter adapter = new TeamBarAdapter(parentActivity.getBaseContext(), teamsList, colorSchemeManager);
            mListView.setAdapter(adapter);
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

            // TODO: We'll need to move this to the popupMenu so we don't manage the drawer

//            if(dataController.getSelectedTeamObject() != null) {
//                if(dataController.getSelectedTeamObject().getRole().equals("ADMIN") || isAdminMode) {
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
//                    if(teamAgentsTitle != null) {
//                        teamAgentsTitle.setVisibility(View.VISIBLE);
//                    }
//                }
//                else {
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
//                    if(teamAgentsTitle != null) {
//                        teamAgentsTitle.setVisibility(View.GONE);
//                    }
//                }
//            }
//            manageDrawerEnabled(isDrawerEnabled);
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
        });
    }







    public void initializeTeamAgents(List<ScopeBarModel> scopeBar, String myAgentId) {
        this.myAgentId = myAgentId;
//        allTeamAgents = teamAgents;
//        for (AgentModelStringSuperUser agent : teamAgents) {
//            if(agent.getAgent_id().equals(myAgentId)) {
//                justMyAgent[0] = agent;
//                break;
//            }
//        }
//        if(teamAgents.length > 0) {
//            wholeTeamAdapter = new TeamAgentsListAdapter(parentActivity.getBaseContext(), teamAgents, colorSchemeManager);
//            justMyAgentAdapter = new TeamAgentsListAdapter(parentActivity.getBaseContext(), justMyAgent, colorSchemeManager);
//            if(isAdminMode) {
//                // Swap the team agent list to only allow the agent to switch back to themselves
//                if(justMyAgentAdapter != null) {
//                    teamAgentListView.setAdapter(justMyAgentAdapter);
//                }
//            }
//            else {
//                if(wholeTeamAdapter != null) {
//                    teamAgentListView.setAdapter(wholeTeamAdapter);
//                }
//            }
//        }
//        wholeTeamAdapter = new TeamAgentsListAdapter(parentActivity.getBaseContext(), scopeBar, colorSchemeManager);
//        if(wholeTeamAdapter != null) {
//            teamAgentListView.setAdapter(wholeTeamAdapter);
//        }

    }

    public void swapToSaveAction(final String titleString, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(() -> {
            parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
            parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
            backtionTitle = parentActivity.findViewById(R.id.actionBarTitle);
            backtionTitle.setTextColor(colorSchemeManager.getLighterTextColor());
            TextView saveButton = parentActivity.findViewById(R.id.saveButton);
            saveButton.setTextColor(colorSchemeManager.getLighterTextColor());

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
        });
    }

    public void swapToSaveAction(final String titleString, final boolean isDrawerEnabled, boolean isClientTileFragment) {
        parentActivity.runOnUiThread(() -> {
            parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_back_layout);
            parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
            backtionTitle = parentActivity.findViewById(R.id.actionBarTitle);
            backtionTitle.setTextColor(colorSchemeManager.getLighterTextColor());
            TextView saveButton = parentActivity.findViewById(R.id.saveButton);
            saveButton.setText("Filters");
            saveButton.setTextColor(colorSchemeManager.getLighterTextColor());

            backtionTitle.setText(parentActivity.localizeLabel(titleString));

            manageDrawerEnabled(isDrawerEnabled);
        });
    }

    public void swapToSaveEditAction(final String titleString, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(() -> {
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
        });
    }

//    public void swapToTitleBar(final String titleString, final boolean isDrawerEnabled, String actualTitleString) {
//        parentActivity.runOnUiThread(() -> {
//            parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_layout);
//            View actionBar = parentActivity.getSupportActionBar().getCustomView();
//            actionBar.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
//
//            pageTitle = parentActivity.findViewById(R.id.action_bar_title);
//            pageTitle.setTextColor(colorSchemeManager.getLighterTextColor());
//            pageTitle.setText(actualTitleString);
//            teamAgentsTitle = parentActivity.findViewById(R.id.team_agents_title);
//
////            if(isAdminMode) {
////                teamAgentsTitle.setText("");
////            }
////            else {
////                teamAgentsTitle.setText("");
////            }
//
//            if(currentTeam != null) {
//                if(currentTeam.getRole().equals("ADMIN") || isAdminMode) {
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
//                    if(teamAgentsTitle != null) {
//                        teamAgentsTitle.setVisibility(View.VISIBLE);
//                    }
//                }
//                else {
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
//                    if(teamAgentsTitle != null) {
//                        teamAgentsTitle.setVisibility(View.GONE);
//                    }
//                }
//            }
//
//            manageDrawerEnabled(isDrawerEnabled);
//        });
//
//    }

    public void swapToClientListBar(final String title, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(() -> {
            parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_clients_layout);
            parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
            pageTitle = parentActivity.findViewById(R.id.actionBarTitle);
            if(title != null) {
                pageTitle.setText(parentActivity.localizeLabel(title));
            }
            pageTitle.setTextColor(colorSchemeManager.getActionbarText());

            manageDrawerEnabled(isDrawerEnabled);
        });
    }

    public void swapToAddClientBar(final String title, final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(() -> {
            parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_add_client_layout);
            parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
            pageTitle = parentActivity.findViewById(R.id.actionBarTitle);
            if(title != null) {
                pageTitle.setText(parentActivity.localizeLabel(title));
                TextView sendButton = parentActivity.findViewById(R.id.addClientSaveButton);
                sendButton.setText("Send Slack");
            }

            pageTitle.setTextColor(colorSchemeManager.getActionbarText());

            manageDrawerEnabled(isDrawerEnabled);
        });
    }

    private void manageDrawerEnabled(boolean isDrawerEnabled) {
        teamLetter = parentActivity.findViewById(R.id.team_letter);
        teamBlock = parentActivity.findViewById(R.id.action_bar_home);
        teamIcon = parentActivity.findViewById(R.id.team_icon);
        teamAgentsTitle = parentActivity.findViewById(R.id.team_agents_title);
        if(isDrawerEnabled) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START);
            teamBlock.setOnClickListener(parentActivity);
            teamIcon.setOnClickListener(parentActivity);
            if(teamAgentsTitle != null) {
                teamAgentsTitle.setOnClickListener(parentActivity);
            }

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
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START);
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
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.START);
        }
    }

    public void swapToEditClientBar(final boolean isDrawerEnabled) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parentActivity.getSupportActionBar().setCustomView(R.layout.action_bar_client_edit_layout);
                parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
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
//        currentTeam = team;
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

        if(team.getRole().equals("ADMIN") || isAdminMode) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.END);
            if(teamAgentsTitle != null) {
                teamAgentsTitle.setVisibility(View.VISIBLE);
            }
        }
        else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END);
            if(teamAgentsTitle != null) {
                teamAgentsTitle.setVisibility(View.GONE);
            }
        }
    }

//    public TeamObject getCurrentTeam() {
//        return currentTeam;
//    }

    public void toggleDrawer() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        else {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public void toggleTeamDrawer() {
        if(drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }
        else {
            drawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void closeTeamAgentsDrawer() {
        drawerLayout.closeDrawer(Gravity.RIGHT);
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

    public void setAdminMode(boolean isAdminMode) {
        this.isAdminMode = isAdminMode;
        if(teamAgentsTitle != null) {
            if(isAdminMode) {
                teamAgentsTitle.setText("Return");
            }
            else {
                teamAgentsTitle.setText("Team");
            }
        }
    }

    public void updateColorSchemeManager(ColorSchemeManager colorSchemeManager) {
//        this.colorSchemeManager = colorSchemeManager;
//        parentActivity.runOnUiThread(() -> {
//            if(colorSchemeManager.getIcon() != null) {
//                Picasso.with(parentActivity).load(Uri.parse(colorSchemeManager.getIcon())).into(teamIcon);
//                //teamIcon.setImageURI(Uri.parse(colorSchemeManager.getIcon()));
//                teamIcon.setVisibility(View.VISIBLE);
//                teamLetter.setVisibility(View.GONE);
//                teamBlock.setBackgroundColor(Color.TRANSPARENT);
//                if(colorSchemeManager.getIcon() != null) {
//                    SaveSharedPreference.setIcon(parentActivity, colorSchemeManager.getIcon());
//                }
//            } else {
//                teamLetter = parentActivity.findViewById(R.id.team_letter);
//                teamIcon = parentActivity.findViewById(R.id.team_icon);
//                teamBlock = parentActivity.findViewById(R.id.action_bar_home);
//                if(currentTeam != null) {
//                    teamLetter.setText(currentTeam.getTeamLetter());
//                    teamLetter.setBackgroundColor(currentTeam.getColor());
//                    teamBlock.setBackgroundColor(currentTeam.getColor());
//                    teamBlock.setVisibility(View.VISIBLE);
//                    teamLetter.setVisibility(View.VISIBLE);
//                    teamIcon.setVisibility(View.INVISIBLE);
//                }
//            }
//            parentActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
//            actionBarColorSet = true;
//        });
    }

    public void resetClient() {
        this.selectedClient = null;
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
