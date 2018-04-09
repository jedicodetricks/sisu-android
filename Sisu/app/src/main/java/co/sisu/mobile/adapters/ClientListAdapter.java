package co.sisu.mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.models.ClientObject;

/**
 * Created by Brady Groharing on 3/15/2018.
 */

public class ClientListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<ClientObject> mDataSource;

    public ClientListAdapter(Context context, List<ClientObject> items) {
        mContext = context;
        mDataSource = (ArrayList<ClientObject>) items;
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.adapter_client_list, parent, false);

        final ClientObject clientObject = (ClientObject) getItem(position);

        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.client_list_title);

        // Get subtitle element
        TextView subtitleTextView = rowView.findViewById(R.id.client_list_subtitle);

        // Get thumbnail element
//        ImageView thumbnailImageView = rowView.findViewById(R.id.client_list_thumbnail);

        // Get the row counter element
//        final EditText rowCounter = rowView.findViewById(R.id.rowCounter);
//
//        ImageView minusButton = rowView.findViewById(R.id.minusButton);
//        ImageView plusButton = rowView.findViewById(R.id.plusButton);
//
//        minusButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((ListView) parent).performItemClick(convertView, position, 0);
//                rowCounter.setText(String.valueOf(metric.getCurrentNum()));
//            }
//        });
//
//        plusButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((ListView) parent).performItemClick(convertView, position, 1);
//                rowCounter.setText(String.valueOf(metric.getCurrentNum()));
//
//            }
//        });



//        titleTextView.setText(clientObject.getName());
//        subtitleTextView.setText("$" + clientObject.getPrice());


        return rowView;
    }
}
