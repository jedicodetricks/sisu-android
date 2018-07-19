package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.ClientListAdapter;
import co.sisu.mobile.adapters.NoteListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ClientNoteEvent;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AsyncNotesJsonObject;
import co.sisu.mobile.models.NotesObject;

/**
 * Created by bradygroharing on 7/17/18.
 */

public class ClientNoteFragment extends Fragment implements AsyncServerEventListener, View.OnClickListener, ClientNoteEvent {

    private ListView mListView;
    private ParentActivity parentActivity;
    private DataController dataController;
    private ApiManager apiManager;
    private NavigationManager navigationManager;
    private ConstraintLayout contentView;
    private TextView addButton;

    public ClientNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_client_note, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initListView();
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        apiManager.getClientNotes(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id());
        initAddButton();
    }

    private void initListView() {
        mListView = getView().findViewById(R.id.note_list_view);
    }

    private void fillListViewWithData(List<NotesObject> noteList) {
        if(getContext() != null) {
            NoteListAdapter adapter = new NoteListAdapter(getContext(), noteList, this);
            mListView.setAdapter(adapter);

//            mListView.setOnItemClickListener(this);
        }

    }

    private void initAddButton() {
        addButton = parentActivity.findViewById(R.id.addClientButton);
        if(addButton != null) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(this);
        }
        else {
//            addButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Get Notes")) {
            AsyncNotesJsonObject asyncNotesJsonObject = (AsyncNotesJsonObject) returnObject;
            final NotesObject[] allNotes = asyncNotesJsonObject.getClient_logs();
            Log.e("SIZE", String.valueOf(allNotes.length));
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillListViewWithData(new ArrayList<>(Arrays.asList(allNotes)));
                }
            });
        }
        else if(asyncReturnType.equals("Delete Notes")) {
            parentActivity.showToast("Note has been deleted");
            apiManager.getClientNotes(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id());
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addClientButton:
                navigationManager.stackReplaceFragment(AddNoteFragment.class);
                break;
        }
    }

    @Override
    public void editButtonClicked(NotesObject noteObject) {
        Log.e("EDIT", "YES");
    }

    @Override
    public void deleteButtonClicked(NotesObject noteObject) {
        apiManager.deleteNote(this, dataController.getAgent().getAgent_id(), noteObject.getId());
    }
}
