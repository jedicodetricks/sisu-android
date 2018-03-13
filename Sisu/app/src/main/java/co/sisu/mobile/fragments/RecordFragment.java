package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.RecordListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.Metric;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements AdapterView.OnItemClickListener {


    private ListView mListView;
    DataController dataController = new DataController();

    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_record, container, false);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initializeListView();
        initializeCalendarHandler();
    }

    private void initializeCalendarHandler() {

        final ImageView calendarLauncher = getView().findViewById(R.id.calender_date_picker);
        final Context context = getContext();
//        Date currentTime = Calendar.getInstance().getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
//        String formattedDate = sdf.format(currentTime);
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        final int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        updateDisplayDate(currentYear, currentMonth, currentDay);

        calendarLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog dialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    //Todo your work here
//                    Toast.makeText(getContext(), "hi", Toast.LENGTH_SHORT).show();
                    updateDisplayDate(year, month, day);
                    }
                }, currentYear, currentMonth, currentDay);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

            }
        });
    }

    private void updateDisplayDate(int year, int month, int day) {
        Date d = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        month += 1;
        String formatDate = year + "/" + month + "/" + day;
        try {
            d = formatter.parse(formatDate);
            Calendar updatedTime = Calendar.getInstance();
            updatedTime.setTime(d);

            TextView dateDisplay = getView().findViewById(R.id.record_date);
            dateDisplay.setText(sdf.format(updatedTime.getTime()));
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error parsing selected date", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void initializeListView() {

        mListView = getView().findViewById(R.id.record_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        final List<Metric> metricList = dataController.getMetrics();

        RecordListAdapter adapter = new RecordListAdapter(getContext(), metricList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Metric metric = (Metric) adapterView.getItemAtPosition(position);
//        Toast.makeText(getContext(), String.valueOf(metric.getCurrentNum() + ":" + id), Toast.LENGTH_SHORT).show();

        if(id == 0) {
            metric.setCurrentNum(metric.getCurrentNum() - 1);
        }
        else {
            metric.setCurrentNum(metric.getCurrentNum() + 1);
        }
    }
}
