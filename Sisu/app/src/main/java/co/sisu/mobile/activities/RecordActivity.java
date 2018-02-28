package co.sisu.mobile.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.sisu.mobile.R;
import co.sisu.mobile.adapters.RecordListAdapter;
import java.util.Calendar;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.models.Metric;


public class RecordActivity extends AppCompatActivity {

    private ListView mListView;
    DataController dataController = new DataController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initializeListView();
        initializeCalendarHandler();
    }

    private void initializeCalendarHandler() {
        ImageView calendarLauncher = findViewById(R.id.calender_date_picker);
        final Context context = this;
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
        String formattedDate = sdf.format(currentTime);

        TextView dateDisplay = findViewById(R.id.record_date);
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

        mListView = (ListView) findViewById(R.id.record_list_view);
        mListView.setDivider(null);
        mListView.setDividerHeight(30);

        final List<Metric> metricList = dataController.getMetrics();

        RecordListAdapter adapter = new RecordListAdapter(this, metricList);
        mListView.setAdapter(adapter);
    }


}
