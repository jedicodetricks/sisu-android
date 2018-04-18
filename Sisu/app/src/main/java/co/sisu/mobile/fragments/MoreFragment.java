package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.MoreListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.system.SaveSharedPreference;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class MoreFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    DataController dataController;
    ParentActivity activity;
    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dataController = new DataController();
        View toReturn = inflater.inflate(R.layout.activity_more, container, false);
        return toReturn;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity = (ParentActivity) getActivity();
        initializeListView();
        inititializeButtons();
    }

    private void inititializeButtons() {

    }

    private void initializeListView() {

        mListView = getView().findViewById(R.id.record_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        final List<MorePageContainer> morePageContainerList = dataController.getMorePageContainer();

        MoreListAdapter adapter = new MoreListAdapter(getContext(), morePageContainerList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MorePageContainer value = (MorePageContainer) parent.getItemAtPosition(position);

        switch(value.getTitle()) {
            case "Teams":
                Toast.makeText(getContext(), value.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case "Clients":
                activity.stackReplaceFragment(ClientListFragment.class);
                activity.swapToClientListBar();
                break;
            case "My Profile":
                activity.stackReplaceFragment(MyProfileFragment.class);
                activity.swapToBacktionBar("My Profile");
                break;
            case "Setup":
                activity.stackReplaceFragment(SetupFragment.class);
                activity.swapToBacktionBar("Setup");
                break;
            case "Settings":
                activity.stackReplaceFragment(SettingsFragment.class);
                activity.swapToBacktionBar("Settings");
                break;
            case "Feedback":
                activity.stackReplaceFragment(FeedbackFragment.class);
                activity.swapToBacktionBar("Feedback");
                break;
            case "Logout":
                activity.logout();
                SaveSharedPreference.setUserName(getContext(), "");
                break;
        }
    }
}
