package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.RecordEventHandler;
import co.sisu.mobile.models.AsyncActivitySettingsObject;
import co.sisu.mobile.models.Metric;

/**
 * Created by Brady Groharing on 2/24/2018.
 */

public class RecordListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Metric> mDataSource;
    private RecordEventHandler mRecordEventHandler;
    private ColorSchemeManager colorSchemeManager;
    private ParentActivity parentActivity;
    private AsyncActivitySettingsObject firstOtherActivity;

    public RecordListAdapter(Context context, List<Metric> items, RecordEventHandler recordEventHandler, ColorSchemeManager colorSchemeManager, AsyncActivitySettingsObject firstOtherActivity, ParentActivity parentActivity) {
        mContext = context;
        mDataSource = (ArrayList<Metric>) items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRecordEventHandler = recordEventHandler;
        this.colorSchemeManager = colorSchemeManager;
        this.firstOtherActivity = firstOtherActivity;
        this.parentActivity = parentActivity;
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

        View rowView = null;
        final Metric metric = (Metric) getItem(position);

        if(metric.getType().equalsIgnoreCase(firstOtherActivity.getActivity_type()) && position != getCount() - 1) {
            rowView = mInflater.inflate(R.layout.adapter_record_list_other_hack, parent, false);
            TextView otherText = rowView.findViewById(R.id.otherText);
            otherText.setTextColor(colorSchemeManager.getDarkerTextColor());
        }
        else {
            rowView = mInflater.inflate(R.layout.adapter_record_list, parent, false);
        }


        // Get title element
        TextView titleTextView = rowView.findViewById(R.id.record_list_title);
        titleTextView.setTextColor(colorSchemeManager.getDarkerTextColor());
        // Get thumbnail element
        ImageView thumbnailImageView = rowView.findViewById(R.id.record_list_thumbnail);

        // Get the row counter element
        final EditText rowCounter = rowView.findViewById(R.id.rowCounter);

        ImageView minusButton = rowView.findViewById(R.id.minusButton);
        Drawable minusDrawable = rowView.getResources().getDrawable(R.drawable.minus_icon).mutate();
        minusDrawable.setTint(colorSchemeManager.getRoundedButtonColor());
        minusButton.setImageDrawable(minusDrawable);

        VectorChildFinder vector = new VectorChildFinder(rowView.getContext(), R.drawable.minus_icon, minusButton);
        for (int i = 0; i < 7; i++) {
            String pathName = "orange_area" + (i + 1);
            VectorDrawableCompat.VFullPath path = vector.findPathByName(pathName);
            path.setFillColor(colorSchemeManager.getRoundedButtonColor());
            path.setStrokeColor(colorSchemeManager.getRoundedButtonColor());
        }

        minusButton.invalidate();


        ImageView plusButton = rowView.findViewById(R.id.plusButton);
        VectorChildFinder plusVector = new VectorChildFinder(rowView.getContext(), R.drawable.add_icon, plusButton);
        VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getRoundedButtonColor());
        plusPath.setStrokeColor(colorSchemeManager.getRoundedButtonColor());
        plusButton.invalidate();


        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!parentActivity.isTeamSwapOccurring()) {
                    int minusOne = metric.getCurrentNum();
                    if(minusOne > 0) {
                        minusOne -= 1;
                    }
                    rowCounter.setText(String.valueOf(minusOne));
                }

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!parentActivity.isTeamSwapOccurring()) {
                    int plusOne = metric.getCurrentNum() + 1;
                    rowCounter.setText(String.valueOf(plusOne));
                }

            }
        });

        if(metric.getType().equals("1TAPT") ||
                metric.getType().equals("CLSD") ||
                metric.getType().equals("UCNTR") ||
                metric.getType().equals("SGND")) {
            rowCounter.setEnabled(false);
        } else {
            rowCounter.setEnabled(true);
        }

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
                        switch(metric.getType()) {
                            case "1TAPT":
                            case "CLSD":
                            case "UCNTR":
                            case "SGND":
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
        Drawable drawable = rowView.getResources().getDrawable(metric.getThumbnailId()).mutate();
        drawable.setColorFilter(colorSchemeManager.getIconActive(), PorterDuff.Mode.SRC_ATOP);
        thumbnailImageView.setImageDrawable(drawable);
        rowCounter.setText(String.valueOf(metric.getCurrentNum()));
        rowCounter.setTextColor(colorSchemeManager.getDarkerTextColor());

        switch(metric.getType()) {
            case "1TAPT":
            case "CLSD":
            case "UCNTR":
            case "SGND":
                minusButton.setVisibility(View.INVISIBLE);
                rowCounter.setEnabled(false);
                break;
        }

        return rowView;
    }
}
