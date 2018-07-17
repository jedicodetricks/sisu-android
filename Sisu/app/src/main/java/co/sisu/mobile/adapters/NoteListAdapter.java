package co.sisu.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.fragments.ClientNoteFragment;
import co.sisu.mobile.models.MorePageContainer;
import co.sisu.mobile.models.NotesObject;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class NoteListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<NotesObject> mDataSource;


    public NoteListAdapter(Context context, List<NotesObject> noteList) {
        mContext = context;
        mDataSource = (ArrayList<NotesObject>) noteList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.adapter_note_list, parent, false);

        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.note_list_title);

        TextView subTitleTextView = rowView.findViewById(R.id.note_list_subtitle);


        NotesObject noteObject = (NotesObject) getItem(position);

        titleTextView.setText(noteObject.getClient_id());
        subTitleTextView.setText(noteObject.getNote());

        return rowView;
    }
}