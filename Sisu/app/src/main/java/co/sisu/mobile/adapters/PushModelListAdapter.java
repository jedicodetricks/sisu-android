package co.sisu.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.ClientNoteEvent;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.models.PushModel;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class PushModelListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<PushModel> mDataSource;
    private ClientNoteEvent clientNoteEvent;
    private ColorSchemeManager colorSchemeManager;


    public PushModelListAdapter(Context context, List<PushModel> noteList, ClientNoteEvent clientNoteEvent, ColorSchemeManager colorSchemeManager) {
        this.colorSchemeManager = colorSchemeManager;
        mContext = context;
        mDataSource = (ArrayList<PushModel>) noteList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.clientNoteEvent = clientNoteEvent;
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
        titleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView subTitleTextView = rowView.findViewById(R.id.note_list_subtitle);
        subTitleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());

        ImageView deleteNoteButton = rowView.findViewById(R.id.deleteNoteIcon);
        deleteNoteButton.setVisibility(View.INVISIBLE);
        ImageView editNoteButton = rowView.findViewById(R.id.editNoteIcon);
        editNoteButton.setVisibility(View.INVISIBLE);


        final PushModel pushModel = (PushModel) getItem(position);


        Date d;
//        Tue, 17 Jul 2018 20:31:00 GMT
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        String dateString = "";

        Calendar calendar = Calendar.getInstance();
        try {
            d = sdf.parse(pushModel.getCreated_ts());
            calendar.setTime(d);

            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd/yyyy");

            dateString = format1.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        titleTextView.setText(pushModel.getTitle() + " - (" + dateString + ")");
        subTitleTextView.setText(pushModel.getBody());

        return rowView;
    }
}