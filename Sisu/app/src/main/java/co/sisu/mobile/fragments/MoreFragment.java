package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import co.sisu.mobile.R;
import co.sisu.mobile.adapters.MoreListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.MorePageContainer;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class MoreFragment extends Fragment{

    private ListView mListView;
    DataController dataController = new DataController();

    public MoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_more, container, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
    public void onClick(View view) {
        Toast.makeText(getContext(), String.valueOf(view.getId()), Toast.LENGTH_SHORT);

    }
}
