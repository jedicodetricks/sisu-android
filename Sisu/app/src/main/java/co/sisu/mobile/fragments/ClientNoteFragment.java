package co.sisu.mobile.fragments;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.sisu.mobile.ApiReturnTypes;
import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.NoteListAdapter;
import co.sisu.mobile.adapters.PushModelListAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ClientNoteEvent;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.models.AsyncMessageCenterObject;
import co.sisu.mobile.models.AsyncNotesJsonObject;
import co.sisu.mobile.models.NotesObject;
import co.sisu.mobile.models.PushModel;
import okhttp3.Response;

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
    private Gson gson;

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
        gson = new Gson();
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        if(parentActivity.getIsNoteFragment()) {
            apiManager.getClientNotes(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id());
        }
        else {
            apiManager.getMessageCenterInfo(this, dataController.getAgent().getAgent_id());
        }
        initAddButton();
    }

    private void initListView() {
        mListView = getView().findViewById(R.id.note_list_view);
    }

    private void fillListViewWithData(List<NotesObject> noteList) {
        if(getContext() != null) {
            NoteListAdapter adapter = new NoteListAdapter(getContext(), noteList, this, parentActivity.colorSchemeManager);
            mListView.setAdapter(adapter);

//            mListView.setOnItemClickListener(this);
        }

    }

    private void fillListViewWithMessageCenterData(List<PushModel> pushModelList) {
        if(getContext() != null) {
            PushModelListAdapter adapter = new PushModelListAdapter(getContext(), pushModelList, this, parentActivity.colorSchemeManager);
            mListView.setAdapter(adapter);

//            mListView.setOnItemClickListener(this);
        }

    }

    private void initAddButton() {
        addButton = parentActivity.findViewById(R.id.addClientButton);
        if(addButton != null) {
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(this);
            TextView edit = parentActivity.findViewById(R.id.editClientListButton);
            edit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
//        if(asyncReturnType.equals("Get Notes")) {
//            AsyncNotesJsonObject asyncNotesJsonObject = gson.fromJson(response.body().charStream(), AsyncNotesJsonObject.class);
//            AsyncNotesJsonObject asyncNotesJsonObject = (AsyncNotesJsonObject) returnObject;
//            final NotesObject[] allNotes = asyncNotesJsonObject.getClient_logs();
//            Log.e("SIZE", String.valueOf(allNotes.length));
//            parentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    fillListViewWithData(new ArrayList<>(Arrays.asList(allNotes)));
//                }
//            });
//        }
        if(asyncReturnType.equals("Delete Notes")) {
            parentActivity.showToast("Note has been deleted");
            apiManager.getClientNotes(this, dataController.getAgent().getAgent_id(), parentActivity.getSelectedClient().getClient_id());
        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_MESSAGE_CENTER) {
            AsyncMessageCenterObject messageCenterObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncMessageCenterObject.class);
            final PushModel[] pushModels = messageCenterObject.getPush_messages();
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillListViewWithMessageCenterData(new ArrayList<>(Arrays.asList(pushModels)));
                }
            });
        }
        else if(returnType == ApiReturnTypes.GET_NOTES) {
            AsyncNotesJsonObject asyncNotesJsonObject = gson.fromJson(((Response) returnObject).body().charStream(), AsyncNotesJsonObject.class);
            final NotesObject[] allNotes = asyncNotesJsonObject.getClient_logs();
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fillListViewWithData(new ArrayList<>(Arrays.asList(allNotes)));
                }
            });
        }
    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {

    }

    @Override
    public void onEventFailed(Object returnObject, ApiReturnTypes returnType) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addClientButton:
                if(!parentActivity.getSelectedClient().getIs_locked().equals("1")) {
                    parentActivity.setSelectedNote(null);
                    navigationManager.stackReplaceFragment(AddNoteFragment.class);
                }
                break;
        }
    }

    @Override
    public void editButtonClicked(NotesObject noteObject) {
        if(!parentActivity.getSelectedClient().getIs_locked().equals("1")) {
            parentActivity.setSelectedNote(noteObject);
            navigationManager.stackReplaceFragment(AddNoteFragment.class);
        }
    }

    @Override
    public void deleteButtonClicked(NotesObject noteObject) {
        if(!parentActivity.getSelectedClient().getIs_locked().equals("1")) {
            apiManager.deleteNote(this, dataController.getAgent().getAgent_id(), noteObject.getId());
        }
    }
}
