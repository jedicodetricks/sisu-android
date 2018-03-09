package co.sisu.mobile.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.MoreListAdapter;
import co.sisu.mobile.adapters.RecordListAdapter;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.RecordService;
import co.sisu.mobile.models.Metric;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements AdapterView.OnItemClickListener {


    private ListView mListView;
    DataController dataController = new DataController();
    RecordService recordService = new RecordService();

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

        ImageView calendarLauncher = getView().findViewById(R.id.calender_date_picker);
        final Context context = getContext();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        String formattedDate = sdf.format(currentTime);

        TextView dateDisplay = getView().findViewById(R.id.record_date);
        dateDisplay.setText(formattedDate);

        calendarLauncher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(context, android.R.style.Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        //Todo your work here
                    }
                }, 2018, 02, 27);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                dialog.show();

            }
        });
    }

    private void initializeListView() {

        mListView = (ListView) getView().findViewById(R.id.record_list_view);
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
