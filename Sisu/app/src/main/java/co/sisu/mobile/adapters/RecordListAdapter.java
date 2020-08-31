package co.sisu.mobile.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
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
import co.sisu.mobile.models.DoubleMetric;
import co.sisu.mobile.models.Metric;

/**
 * Created by Brady Groharing on 2/24/2018.
 */

public class RecordListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<DoubleMetric> mDataSource;
    private RecordEventHandler mRecordEventHandler;
    private ColorSchemeManager colorSchemeManager;
    private ParentActivity parentActivity;
    private AsyncActivitySettingsObject firstOtherActivity;
    private final int smallerTitleSize = 18;

    public RecordListAdapter(Context context, List<DoubleMetric> items, RecordEventHandler recordEventHandler, ColorSchemeManager colorSchemeManager, AsyncActivitySettingsObject firstOtherActivity, ParentActivity parentActivity) {
        mContext = context;
        mDataSource = (ArrayList<DoubleMetric>) items;
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
        final DoubleMetric doubleMetric = (DoubleMetric) getItem(position);
        final Metric leftMetric = doubleMetric.getLeftMetric();
        final Metric rightMetric = doubleMetric.getRightMetric();

        rowView = mInflater.inflate(R.layout.adapter_double_record_table_row, parent, false);


        // Get title element
        TextView leftTitleView = rowView.findViewById(R.id.leftRecordTitle);
        leftTitleView.setTextColor(colorSchemeManager.getDarkerTextColor());
        leftTitleView.setText(leftMetric.getTitle());
        if(leftMetric.getTitle().length() >= smallerTitleSize) {
            leftTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smaller));
        }
//        if(leftMetric.getTitle().length() >= smallestTitleSize) {
//            leftTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smallest));
//        }

        // Get the row counter element
        final EditText leftRowCounter = rowView.findViewById(R.id.leftRowCounter);

        ImageView leftMinusButton = rowView.findViewById(R.id.leftMinusButton);
        Drawable minusDrawable = rowView.getResources().getDrawable(R.drawable.minus_icon).mutate();
        minusDrawable.setTint(colorSchemeManager.getRoundedButtonColor());
        leftMinusButton.setImageDrawable(minusDrawable);

        VectorChildFinder vector = new VectorChildFinder(rowView.getContext(), R.drawable.minus_icon, leftMinusButton);
        for (int i = 0; i < 7; i++) {
            String pathName = "orange_area" + (i + 1);
            VectorDrawableCompat.VFullPath path = vector.findPathByName(pathName);
            path.setFillColor(colorSchemeManager.getRoundedButtonColor());
            path.setStrokeColor(colorSchemeManager.getRoundedButtonColor());
        }

        leftMinusButton.invalidate();


        ImageView leftPlusButton = rowView.findViewById(R.id.leftPlusButton);
        VectorChildFinder plusVector = new VectorChildFinder(rowView.getContext(), R.drawable.add_icon, leftPlusButton);
        VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getRoundedButtonColor());
        plusPath.setStrokeColor(colorSchemeManager.getRoundedButtonColor());
        leftPlusButton.invalidate();


        leftMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!parentActivity.isTeamSwapOccurring()) {
                    int minusOne = leftMetric.getCurrentNum();
                    if(minusOne > 0) {
                        minusOne -= 1;
                    }
                    leftRowCounter.setText(String.valueOf(minusOne));
                }

            }
        });

        leftPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!parentActivity.isTeamSwapOccurring()) {
                    int plusOne = leftMetric.getCurrentNum() + 1;
                    leftRowCounter.setText(String.valueOf(plusOne));
                }

            }
        });

        leftRowCounter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!leftRowCounter.getText().toString().equals("")) {
                    if(Integer.valueOf(leftRowCounter.getText().toString()) != leftMetric.getCurrentNum()) {
                        switch(leftMetric.getType()) {
                            case "1TAPT":
                            case "CLSD":
                            case "UCNTR":
                            case "SGND":
                                mRecordEventHandler.onClientDirectorClicked(leftMetric);
                                break;
                            default:
                                mRecordEventHandler.onNumberChanged(leftMetric, Integer.valueOf(leftRowCounter.getText().toString()));
                                break;
                        }
                    }
                }
            }
        });

        leftRowCounter.setText(String.valueOf(leftMetric.getCurrentNum()));
        leftRowCounter.setTextColor(colorSchemeManager.getDarkerTextColor());


        //EVERYTHING FOR THE RIGHT SIDE

        TextView rightTitleView = rowView.findViewById(R.id.rightRecordTitle);
        rightTitleView.setTextColor(colorSchemeManager.getDarkerTextColor());
        final EditText rightRowCounter = rowView.findViewById(R.id.rightRowCounter);
        ImageView rightMinusButton = rowView.findViewById(R.id.rightMinusButton);
        ImageView rightPlusButton = rowView.findViewById(R.id.rightPlusButton);

        if(rightMetric != null) {
            rightTitleView.setText(rightMetric.getTitle());
            if(rightMetric.getTitle().length() >= smallerTitleSize) {
                rightTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smaller));
            }
//            if(rightMetric.getTitle().length() >= smallestTitleSize) {
//                rightTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, parentActivity.getResources().getDimension(R.dimen.font_smallest));
//            }
            // Get the row counter element

            minusDrawable = rowView.getResources().getDrawable(R.drawable.minus_icon).mutate();
            minusDrawable.setTint(colorSchemeManager.getRoundedButtonColor());
            rightMinusButton.setImageDrawable(minusDrawable);

            vector = new VectorChildFinder(rowView.getContext(), R.drawable.minus_icon, rightMinusButton);
            for (int i = 0; i < 7; i++) {
                String pathName = "orange_area" + (i + 1);
                VectorDrawableCompat.VFullPath path = vector.findPathByName(pathName);
                path.setFillColor(colorSchemeManager.getRoundedButtonColor());
                path.setStrokeColor(colorSchemeManager.getRoundedButtonColor());
            }

            rightMinusButton.invalidate();


            plusVector = new VectorChildFinder(rowView.getContext(), R.drawable.add_icon, rightPlusButton);
            plusPath = plusVector.findPathByName("orange_area");
            plusPath.setFillColor(colorSchemeManager.getRoundedButtonColor());
            plusPath.setStrokeColor(colorSchemeManager.getRoundedButtonColor());
            rightPlusButton.invalidate();


            rightMinusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!parentActivity.isTeamSwapOccurring()) {
                        int minusOne = rightMetric.getCurrentNum();
                        if(minusOne > 0) {
                            minusOne -= 1;
                        }
                        rightRowCounter.setText(String.valueOf(minusOne));
                    }

                }
            });

            rightPlusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!parentActivity.isTeamSwapOccurring()) {
                        int plusOne = rightMetric.getCurrentNum() + 1;
                        rightRowCounter.setText(String.valueOf(plusOne));
                    }

                }
            });

            rightRowCounter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if(!rightRowCounter.getText().toString().equals("")) {
                        if(Integer.valueOf(rightRowCounter.getText().toString()) != rightMetric.getCurrentNum()) {
                            switch(rightMetric.getType()) {
                                case "1TAPT":
                                case "CLSD":
                                case "UCNTR":
                                case "SGND":
                                    mRecordEventHandler.onClientDirectorClicked(rightMetric);
                                    break;
                                default:
                                    mRecordEventHandler.onNumberChanged(rightMetric, Integer.valueOf(rightRowCounter.getText().toString()));
                                    break;
                            }
                        }
                    }
                }
            });

            rightRowCounter.setText(String.valueOf(rightMetric.getCurrentNum()));
            rightRowCounter.setTextColor(colorSchemeManager.getDarkerTextColor());
        }
        else {
            //Make all the elements GONE
            rightTitleView.setVisibility(View.GONE);
            rightRowCounter.setVisibility(View.GONE);
            rightMinusButton.setVisibility(View.GONE);
            rightPlusButton.setVisibility(View.GONE);
        }



        return rowView;
    }
}
