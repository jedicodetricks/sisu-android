package co.sisu.mobile.controllers;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.TeamBarAdapter;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.TeamObject;

/**
 * Created by bradygroharing on 5/25/18.
 */

public class ActionBarManager {
    private ParentActivity parentActivity;
    private ColorSchemeManager colorSchemeManager;
    private TextView pageTitle, teamLetter, backtionTitle;
    private View teamBlock;
    private DrawerLayout drawerLayout;
    private ActionBar bar;
    private List<TeamObject> teamsList;
    int selectedTeam = 0;
    private ClientObject selectedClient;


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
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        bar.setCustomView(R.layout.action_bar_layout);
        pageTitle = parentActivity.findViewById(R.id.action_bar_title);
        teamLetter = parentActivity.findViewById(R.id.team_letter);
        teamBlock = parentActivity.findViewById(R.id.action_bar_home);

        View view = parentActivity.getSupportActionBar().getCustomView();
        pageTitle.setText(parentActivity.localizeLabel(fragmentTag));
        View homeButton= view.findViewById(R.id.action_bar_home);
        homeButton.setOnClickListener(parentActivity);

//        if(teamsList != null && pageTitle.getText().toString().equals("Leaderboard")) {
//            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            teamBlock.setBackgroundColor(teamsList.get(selectedTeam).getColor());
//            teamLetter.setText(teamsList.get(selectedTeam).getTeamLetter().toUpperCase());
//            teamLetter.setBackgroundColor(teamsList.get(selectedTeam).getColor());
//        }
//        else {
        teamBlock.setVisibility(View.GONE);
        teamLetter.setVisibility(View.GONE);
//        }
        parentActivity.getSupportActionBar().setElevation(0);
    }

    public void initializeTeamBar(List<TeamObject> teamsList) {
        if(teamsList.size() > 0) {
            ListView mListView = parentActivity.findViewById(R.id.navViewList);
            mListView.setDivider(null);
            mListView.setDividerHeight(30);

            this.teamsList = teamsList;

            TeamBarAdapter adapter = new TeamBarAdapter(parentActivity.getBaseContext(), teamsList);
            mListView.setAdapter(adapter);

            mListView.setOnItemClickListener(parentActivity);

            teamBlock.setBackgroundColor(teamsList.get(0).getColor());
            teamLetter.setText(teamsList.get(0).getTeamLetter().toUpperCase());
            teamLetter.setBackgroundColor(teamsList.get(0).getColor());
        }
    }

    public void swapToSaveAction(final String titleString) {
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
            }
        });

    }

    public void swapToTitleBar(final String titleString) {
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
                teamLetter = parentActivity.findViewById(R.id.team_letter);
                teamBlock = parentActivity.findViewById(R.id.action_bar_home);
                pageTitle.setText(parentActivity.localizeLabel(titleString));
                View homeButton= parentActivity.findViewById(R.id.action_bar_home);
                homeButton.setOnClickListener(parentActivity);
                if(teamsList != null && titleString.equals("Leaderboard")) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    teamBlock.setBackgroundColor(teamsList.get(selectedTeam).getColor());
                    teamLetter.setText(teamsList.get(selectedTeam).getTeamLetter().toUpperCase());
                    teamLetter.setBackgroundColor(teamsList.get(selectedTeam).getColor());
                    teamBlock.setVisibility(View.VISIBLE);
                    teamLetter.setVisibility(View.VISIBLE);
                }
                else {
                    teamBlock.setVisibility(View.GONE);
                    teamLetter.setVisibility(View.GONE);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }
        });

    }

    public void swapToClientListBar(final String title) {
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


            }
        });
    }

    public void swapToAddClientBar(final String title) {
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

            }
        });
    }

    public void swapToEditClientBar() {
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
            }
        });
    }

    public void updateTeam(TeamObject team) {
        teamBlock.setBackgroundColor(team.getColor());
        teamLetter.setText(team.getTeamLetter());
        teamLetter.setBackgroundColor(team.getColor());
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
        int teamId = -1;
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


}
