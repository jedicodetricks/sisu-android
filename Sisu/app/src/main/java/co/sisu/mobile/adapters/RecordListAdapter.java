package co.sisu.mobile.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.models.Metric;

/**
 * Created by Brady Groharing on 2/24/2018.
 */

public class RecordListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Metric> mDataSource;
    private RecordEventHandler mRecordEventHandler;

    public RecordListAdapter(Context context, List<Metric> items, RecordEventHandler recordEventHandler) {
        mContext = context;
        mDataSource = (ArrayList<Metric>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecordEventHandler = recordEventHandler;
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
        View rowView = mInflater.inflate(R.layout.adapter_record_list, parent, false);

        final Metric metric = (Metric) getItem(position);

        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.record_list_title);

        // Get thumbnail element
        ImageView thumbnailImageView = rowView.findViewById(R.id.record_list_thumbnail);

        // Get the row counter element
        final EditText rowCounter = rowView.findViewById(R.id.rowCounter);

        ImageView minusButton = rowView.findViewById(R.id.minusButton);
        ImageView plusButton = rowView.findViewById(R.id.plusButton);


        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minusOne = metric.getCurrentNum();
                if(minusOne > 0) {
                    minusOne -= 1;
                }
                rowCounter.setText(String.valueOf(minusOne));
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int plusOne = metric.getCurrentNum() + 1;
                rowCounter.setText(String.valueOf(plusOne));
            }
        });
//
//
        rowCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!rowCounter.getText().toString().equals("")) {
                    if(Integer.valueOf(rowCounter.getText().toString()) != metric.getCurrentNum()) {
                        switch(metric.getTitle()) {
                            case "1st Time Appts":
                            case "Buyers Signed":
                            case "Sellers Signed":
                            case "Buyers Under Contract":
                            case "Sellers Under Contract":
                            case "Buyers Closed":
                            case "Sellers Closed":
                                mRecordEventHandler.onClientDirectorClicked(metric);
                                break;
                            default:
                                mRecordEventHandler.onNumberChanged(metric, Integer.valueOf(rowCounter.getText().toString()));
                                break;
                        }
                    }
                }
            }
        });

        titleTextView.setText(metric.getTitle());
        thumbnailImageView.setImageResource(metric.getThumbnailId());
        rowCounter.setText(String.valueOf(metric.getCurrentNum()));
        if(metric.getTitle().equals("1st Time Appts") ||
                metric.getTitle().equals("Buyers Signed") ||
                metric.getTitle().equals("Sellers Signed") ||
                metric.getTitle().equals("Buyers Under Contract") ||
                metric.getTitle().equals("Sellers Under Contract") ||
                metric.getTitle().equals("Sellers Closed") ||
                metric.getTitle().equals("Buyers Closed")){
            minusButton.setVisibility(View.INVISIBLE);
            rowCounter.setEnabled(false);
        }

        return rowView;
    }
}
