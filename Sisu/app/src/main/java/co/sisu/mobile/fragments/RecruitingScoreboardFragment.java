package co.sisu.mobile.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.NotificationActivity;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.adapters.DropdownAdapter;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
import co.sisu.mobile.enums.ApiReturnTypes;
import co.sisu.mobile.models.AsyncActivitiesJsonObject;
import co.sisu.mobile.models.ClientObject;
import co.sisu.mobile.models.Metric;
import co.sisu.mobile.utils.CircularProgressBar;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecruitingScoreboardFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener {

    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private int selectedStartYear = 0;
    private int selectedStartMonth = 0;
    private int selectedStartDay = 0;
    private int selectedEndYear = 0;
    private int selectedEndMonth = 0;
    private int selectedEndDay = 0;
    private ImageView addButton;
    private Calendar calendar = Calendar.getInstance();
    private Date selectedStartTime;
    private Date selectedEndTime;
    private ProgressBar loader;
    private int pendingVolume = 0;
    private int closedVolume = 0;
    private boolean needsProgress;
    private boolean pastTimeline;
    private Spinner spinner;
    private CircularProgressBar contact, appointments, signed, listing, underContract, closed;
    private String formattedStartTime;
    private String formattedEndTime;

    private CircularProgressBar contactsProgress, contactsProgressMark, appointmentsProgress, appointmentsProgressMark, bbSignedProgress, bbSignedProgressMark,
            listingsTakenProgress, listingsTakenProgressMark, underContractProgress, underContractProgressMark, closedProgress, closedProgressMark;

    private TextView contactsCurrentNumber, contactsGoalNumber, appointmentsCurrentNumber, appointmentsGoalNumber, bbSignedCurrentNumber, bbSignedGoalNumber,
            listingsTakenCurrentNumber, listingsTakenGoalNumber, agentsLostNumber, netAgentsNumber;

    private TextView pendingVolumeDisplay, closedVolumeDisplay;

    public RecruitingScoreboardFragment() {
        // Required empty public constructor
    }

    public void teamSwap() {
//        createAndAnimateProgressBars(dataController.updateScoreboardTimeline());
        loader.setVisibility(View.VISIBLE);
        apiManager.sendAsyncActivities(RecruitingScoreboardFragment.this, dataController.getAgent().getAgent_id(), formattedStartTime, formattedEndTime, parentActivity.getSelectedTeamMarketId());
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupUiVisuals();
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_recruiting_scoreboard, container, false);
    }

    private void setupUiVisuals() {
        setColorScheme();
        setupLabels();
    }

    private void initOtherStats() {
        agentsLostNumber.setText("" + dataController.getArchivedList().size());
        netAgentsNumber.setText("" + dataController.getNumOfActiveAgents());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        loader = parentActivity.findViewById(R.id.parentLoader);

        initializeTimelineSelector();
        spinner.setSelection(parentActivity.getTimelineSelection());
        initializeButtons();
        initProgressBars();
        initOtherStats();
        calculateVolumes();
        setupUiVisuals();


        if(parentActivity.shouldDisplayPushNotification()) {
            parentActivity.setShouldDisplayPushNotification(false);
            String title = parentActivity.getPushNotificationTitle();
            String body = parentActivity.getPushNotificationBody();
            Intent intent = new Intent(parentActivity, NotificationActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("body", body);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void setupLabels() {

        TextView pendingVolume = parentActivity.findViewById(R.id.pendingVolumeLabel);
        String pendingVolumeLocalized = parentActivity.localizeLabel(getResources().getString(R.string.pending_volume));
        pendingVolume.setText(parentActivity.localizeLabel(pendingVolumeLocalized));

        TextView closedVolume = parentActivity.findViewById(R.id.closedVolumeLabel);
        String closedVolumeLocalized = parentActivity.localizeLabel(getResources().getString(R.string.closed_volume));
        closedVolume.setText(parentActivity.localizeLabel(closedVolumeLocalized));

        TextView contacts = parentActivity.findViewById(R.id.contactsProgressText);
        contacts.setText(parentActivity.localizeLabel(getResources().getString(R.string.contacts)));

        TextView buyerSigned = parentActivity.findViewById(R.id.bbSignedProgressText);
        buyerSigned.setText(parentActivity.localizeLabel(getResources().getString(R.string.buyers_signed)));

        TextView underContract = parentActivity.findViewById(R.id.underContractProgressText);
        underContract.setText("Agents Lost");

        TextView appointments = parentActivity.findViewById(R.id.appointmentsProgressText);
        appointments.setText(parentActivity.localizeLabel(getResources().getString(R.string.first_time_appts)));

        TextView listingsTaken = parentActivity.findViewById(R.id.listingsTakenProgressText);
        listingsTaken.setText(parentActivity.localizeLabel(getResources().getString(R.string.listings_taken)));

        TextView closed = parentActivity.findViewById(R.id.closedProgressText);
        closed.setText("Net Agents");
    }

    private void setColorScheme() {
        ConstraintLayout layout = getView().findViewById(R.id.scoreboard_fragment_parent);
        layout.setBackgroundColor(colorSchemeManager.getAppBackground());

        TextView contactsProgressText = getView().findViewById(R.id.contactsProgressText);
        contactsProgressText.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        contactsProgressText.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView appointmentsProgressText = getView().findViewById(R.id.appointmentsProgressText);
        appointmentsProgressText.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        appointmentsProgressText.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView bbSignedProgressText = getView().findViewById(R.id.bbSignedProgressText);
        bbSignedProgressText.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        bbSignedProgressText.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView listingsTakenProgressText = getView().findViewById(R.id.listingsTakenProgressText);
        listingsTakenProgressText.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        listingsTakenProgressText.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView underContractProgressText = getView().findViewById(R.id.underContractProgressText);
        underContractProgressText.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        underContractProgressText.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView closedProgressText = getView().findViewById(R.id.closedProgressText);
        closedProgressText.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        closedProgressText.setTextColor(colorSchemeManager.getDarkerTextColor());

        pendingVolumeDisplay.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        pendingVolumeDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());

        closedVolumeDisplay.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        closedVolumeDisplay.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView pendingVolumeLabel = getView().findViewById(R.id.pendingVolumeLabel);
        pendingVolumeLabel.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        pendingVolumeLabel.setTextColor(colorSchemeManager.getDarkerTextColor());

        TextView closedVolumeLabel = getView().findViewById(R.id.closedVolumeLabel);
        closedVolumeLabel.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        closedVolumeLabel.setTextColor(colorSchemeManager.getDarkerTextColor());

        contactsProgress.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        appointmentsProgress.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        bbSignedProgress.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        listingsTakenProgress.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
//        underContractProgress.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
//        closedProgress.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));

        contactsCurrentNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        contactsCurrentNumber.setTextColor(colorSchemeManager.getDarkerTextColor());
        contactsGoalNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        contactsGoalNumber.setTextColor(colorSchemeManager.getDarkerTextColor());

        appointmentsCurrentNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        appointmentsCurrentNumber.setTextColor(colorSchemeManager.getDarkerTextColor());
        appointmentsGoalNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        appointmentsGoalNumber.setTextColor(colorSchemeManager.getDarkerTextColor());

        bbSignedCurrentNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        bbSignedCurrentNumber.setTextColor(colorSchemeManager.getDarkerTextColor());
        bbSignedGoalNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        bbSignedGoalNumber.setTextColor(colorSchemeManager.getDarkerTextColor());

        listingsTakenCurrentNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        listingsTakenCurrentNumber.setTextColor(colorSchemeManager.getDarkerTextColor());
        listingsTakenGoalNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        listingsTakenGoalNumber.setTextColor(colorSchemeManager.getDarkerTextColor());

        agentsLostNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        agentsLostNumber.setTextColor(colorSchemeManager.getDarkerTextColor());
//        underContractGoalNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
//        underContractGoalNumber.setTextColor(colorSchemeManager.getDarkerTextColor());

        netAgentsNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
        netAgentsNumber.setTextColor(colorSchemeManager.getDarkerTextColor());
//        closedGoalNumber.setBackground(new ColorDrawable(colorSchemeManager.getAppBackground()));
//        closedGoalNumber.setTextColor(colorSchemeManager.getDarkerTextColor());

        spinner.setPopupBackgroundDrawable(new ColorDrawable(colorSchemeManager.getAppBackground()));
        spinner.getBackground().setColorFilter(colorSchemeManager.getIconActive(), PorterDuff.Mode.SRC_ATOP);

        VectorChildFinder plusVector = new VectorChildFinder(getContext(), R.drawable.add_icon, addButton);
        VectorDrawableCompat.VFullPath plusPath = plusVector.findPathByName("orange_area");
        plusPath.setFillColor(colorSchemeManager.getRoundedButtonColor());
        plusPath.setStrokeColor(colorSchemeManager.getRoundedButtonColor());
        addButton.invalidate();

        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark));
            loader.getIndeterminateDrawable().setBounds(bounds);
        } else {
            Rect bounds = loader.getIndeterminateDrawable().getBounds();
            loader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            loader.getIndeterminateDrawable().setBounds(bounds);
        }
    }

    private void calculateVolumes() {
        if(getView() != null) {
            pendingVolume = 0;
            closedVolume = 0;

            //Pending
            List<ClientObject> underContractClients = dataController.getContractList();
            for(ClientObject co : underContractClients) {
                pendingVolume += Integer.valueOf(co.getTrans_amt());
            }
            NumberFormat format = NumberFormat.getNumberInstance();

            pendingVolumeDisplay = getView().findViewById(R.id.underContractAmount);
            pendingVolumeDisplay.setText("$" + format.format(pendingVolume));


            //Closed
            List<ClientObject> closedClients = dataController.getClosedList();
            for(ClientObject co : closedClients) {

                if(insideSelectedTimeRange(co.getClosed_dt())) {
                    closedVolume += Integer.valueOf(co.getTrans_amt());
                }
            }

            closedVolumeDisplay = getView().findViewById(R.id.closedAmount);
            closedVolumeDisplay.setText("$" + format.format(closedVolume));
        }

    }

    private boolean insideSelectedTimeRange(String closedDate) {
        closedDate = closedDate.replace("00:00:00 GMT", "");
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        Date d = null;
        try {
            d = sdf.parse(closedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar closedTime = Calendar.getInstance();
        closedTime.setTime(d);

        if(selectedStartTime != null && selectedEndTime != null) {
            if(closedTime.getTimeInMillis() > selectedStartTime.getTime() && closedTime.getTimeInMillis() < selectedEndTime.getTime()) {
                return true;
            }
        }
        else {
            Calendar currentTime = Calendar.getInstance();
            if(closedTime.getTimeInMillis() > currentTime.getTimeInMillis() && closedTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                return true;
            }
        }
        return false;
    }


    private void initializeTimelineSelector() {
        spinner = getView().findViewById(R.id.timelineSelector);
        List<String> spinnerArray = initSpinnerArray();

        DropdownAdapter adapter = new DropdownAdapter(getContext(), R.layout.spinner_item, spinnerArray, colorSchemeManager);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.spinner_item,
//                spinnerArray
//        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calendar = Calendar.getInstance();
                loader.setVisibility(View.VISIBLE);

                switch (position) {
                    case 0:
                        //Yesterday
                        pastTimeline = true;
                        parentActivity.setTimeline("day");
                        parentActivity.setTimelineSelection(0);
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        needsProgress = false;
                        break;
                    case 1:
                        //Today
                        pastTimeline = false;
                        parentActivity.setTimeline("day");
                        parentActivity.setTimelineSelection(1);

                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        needsProgress = false;
                        break;
                    case 2:
                        //Last Week
                        needsProgress = false;
                        pastTimeline = true;
                        parentActivity.setTimeline("week");
                        parentActivity.setTimelineSelection(2);

                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;

                        calendar.add(Calendar.DAY_OF_WEEK, 6);
                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                        break;
                    case 3:
                        //This Week
                        needsProgress = true;
                        pastTimeline = false;
                        parentActivity.setTimeline("week");
                        parentActivity.setTimelineSelection(3);

                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;

                        calendar.add(Calendar.DAY_OF_WEEK, 6);
                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
                        break;
                    case 4:
                        //Last Month
                        needsProgress = false;
                        pastTimeline = true;
                        parentActivity.setTimeline("month");
                        parentActivity.setTimelineSelection(4);

                        calendar.add(Calendar.MONTH, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 5:
                        //This Month
                        needsProgress = true;
                        pastTimeline = false;
                        parentActivity.setTimeline("month");
                        parentActivity.setTimelineSelection(5);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        break;
                    case 6:
                        //Last year
                        needsProgress = false;
                        pastTimeline = true;
                        parentActivity.setTimeline("year");
                        parentActivity.setTimelineSelection(6);
                        calendar.add(Calendar.YEAR, -1);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = 12;
                        selectedEndDay = 31;
                        break;
                    case 7:
                        //This year
                        needsProgress = true;
                        pastTimeline = false;
                        parentActivity.setTimeline("year");
                        parentActivity.setTimelineSelection(7);
                        selectedStartYear = calendar.get(Calendar.YEAR);
                        selectedStartMonth = 1;
                        selectedStartDay = 1;

                        selectedEndYear = calendar.get(Calendar.YEAR);
                        selectedEndMonth = 12;
                        selectedEndDay = 31;
                        break;
                }

                String formattedStartMonth = String.valueOf(selectedStartMonth);
                String formattedEndMonth = String.valueOf(selectedEndMonth);
                String formattedStartDay = String.valueOf(selectedStartDay);
                String formattedEndDay = String.valueOf(selectedEndDay);

                if(selectedStartDay < 10) {
                    formattedStartDay = "0" + selectedStartDay;
                }

                if(selectedEndDay < 10) {
                    formattedEndDay = "0" + selectedEndDay;
                }

                if(selectedStartMonth < 10) {
                    formattedStartMonth = "0" + selectedStartMonth;
                }

                if(selectedEndMonth < 10) {
                    formattedEndMonth = "0" + selectedEndMonth;
                }



                formattedStartTime = selectedStartYear + "-" + formattedStartMonth + "-" + formattedStartDay;
                formattedEndTime = selectedEndYear + "-" + formattedEndMonth + "-" + formattedEndDay;
                selectedStartTime = getDateFromFormattedTime(formattedStartTime);
                selectedEndTime = getDateFromFormattedTime(formattedEndTime);

                apiManager.sendAsyncActivities(RecruitingScoreboardFragment.this, dataController.getAgent().getAgent_id(), formattedStartTime, formattedEndTime, parentActivity.getSelectedTeamMarketId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not sure what this does
            }
        });
    }

    private Date getDateFromFormattedTime(String formattedTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d = formatter.parse(formattedTime);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> initSpinnerArray() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Yesterday");
        spinnerArray.add("Today");
        spinnerArray.add("Last Week");
        spinnerArray.add("This Week");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");

        String thisMonth = sdf.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String lastMonth = sdf.format(calendar.getTime());
        spinnerArray.add(lastMonth);
        spinnerArray.add(thisMonth);

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy");
        String thisYear = sdf.format(calendar.getTime());

        calendar.add(Calendar.YEAR, -1);
        String lastYear = sdf.format(calendar.getTime());
        spinnerArray.add(lastYear);
        spinnerArray.add(thisYear);
//        spinnerArray.add("All Records");

        return spinnerArray;
    }

    private void initializeButtons(){
        addButton = getView().findViewById(R.id.addView);
        addButton.setOnClickListener(this);

        contact = getView().findViewById(R.id.contactsProgressMark);
        contact.setOnClickListener(this);

        appointments = getView().findViewById(R.id.appointmentsProgressMark);
        appointments.setOnClickListener(this);

        signed = getView().findViewById(R.id.bbSignedProgressMark);
        signed.setOnClickListener(this);

        listing = getView().findViewById(R.id.listingsTakenProgressMark);
        listing.setOnClickListener(this);

        underContract = getView().findViewById(R.id.underContractProgressMark);
        underContract.setOnClickListener(this);

        closed = getView().findViewById(R.id.closedProgressMark);
        closed.setOnClickListener(this);
    }

    private void initProgressBars() {
        contactsProgress = getView().findViewById(R.id.contactsProgress);
        contactsProgressMark = getView().findViewById(R.id.contactsProgressMark);
        contactsCurrentNumber = getView().findViewById(R.id.contactsCurrentNumber);
        contactsGoalNumber = getView().findViewById(R.id.contactsGoalNumber);

        appointmentsProgress = getView().findViewById(R.id.appointmentsProgress);
        appointmentsProgressMark = getView().findViewById(R.id.appointmentsProgressMark);
        appointmentsCurrentNumber = getView().findViewById(R.id.appointmentsCurrentNumber);
        appointmentsGoalNumber = getView().findViewById(R.id.appointmentsGoalNumber);

        bbSignedProgress = getView().findViewById(R.id.bbSignedProgress);
        bbSignedProgressMark = getView().findViewById(R.id.bbSignedProgressMark);
        bbSignedCurrentNumber = getView().findViewById(R.id.bbsignedCurrentNumber);
        bbSignedGoalNumber = getView().findViewById(R.id.bbsignedGoalNumber);

        listingsTakenProgress = getView().findViewById(R.id.listingsTakenProgress);
        listingsTakenProgressMark = getView().findViewById(R.id.listingsTakenProgressMark);
        listingsTakenCurrentNumber = getView().findViewById(R.id.listingsTakenCurrentNumber);
        listingsTakenGoalNumber = getView().findViewById(R.id.listingsTakenGoalNumber);

//        underContractProgress = getView().findViewById(R.id.underContractProgress);
        underContractProgressMark = getView().findViewById(R.id.underContractProgressMark);
        agentsLostNumber = getView().findViewById(R.id.underContractCurrentNumber);
//        underContractGoalNumber = getView().findViewById(R.id.underContactGoalNumber);

//        closedProgress = getView().findViewById(R.id.closedProgress);
        closedProgressMark = getView().findViewById(R.id.closedProgressMark);
        netAgentsNumber = getView().findViewById(R.id.closedCurrentNumber);
//        closedGoalNumber = getView().findViewById(R.id.closedGoalNumber);
    }

    private void animateProgressBars(List<Metric> metricList){
        for(int i = 0; i < metricList.size(); i++) {

            switch(metricList.get(i).getType()) {
                case "CONTA":
                    Metric contactsMetric = metricList.get(i);
                    setupProgressBar(contactsMetric, contactsProgress, contactsProgressMark, contactsCurrentNumber, contactsGoalNumber);
                    break;

                case "1TAPT":
                    Metric appointmentsMetric = metricList.get(i);
                    setupProgressBar(appointmentsMetric, appointmentsProgress, appointmentsProgressMark, appointmentsCurrentNumber, appointmentsGoalNumber);
                    break;

                case "BBSGD":
                    Metric bbSignedMetric = metricList.get(i);
                    setupProgressBar(bbSignedMetric, bbSignedProgress, bbSignedProgressMark, bbSignedCurrentNumber, bbSignedGoalNumber);
                    break;

                case "LSTT":
                    Metric listingsTakenMetric = metricList.get(i);
                    setupProgressBar(listingsTakenMetric, listingsTakenProgress, listingsTakenProgressMark, listingsTakenCurrentNumber, listingsTakenGoalNumber);
                    break;

                case "UCNTR":
                    Metric underContractMetric = metricList.get(i);
//                    setupProgressBar(underContractMetric, underContractProgress, underContractProgressMark, agentsLostNumber, underContractGoalNumber);
                    break;

                case "CLSD":
                    Metric closedMetric = metricList.get(i);
//                    setupProgressBar(closedMetric, closedProgress, closedProgressMark, netAgentsNumber, closedGoalNumber);
                    break;
            }
        }
    }

    public void setupProgressBar(Metric metric, CircularProgressBar progress, CircularProgressBar progressMark, TextView currentNumber, TextView goalNumber) {
        if(getContext() != null) {
            final int ANIMATION_DURATION = 1500; // Time in millis
            final int PROGRESS_MARK = calculateProgressMarkPosition();
            calculateProgressColor(metric, calculateProgressOnTrack(metric));
            Context context = getContext();
            progress.setColor(metric.getColor());
            progress.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCorporateGrey));
            progress.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
            progress.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
            progress.setProgressWithAnimation(metric.getPercentComplete(parentActivity.getTimeline()), ANIMATION_DURATION);
            currentNumber.setText(String.valueOf(metric.getCurrentNum()));
            double goalNum = 0;
            String displayNum = "0";
            switch (parentActivity.getTimeline()) {
                case "day":
                    goalNum = metric.getDailyGoalNum();
                    displayNum = String.valueOf(metric.getDailyGoalNum());
                    if(goalNum == 0) {
                        displayNum = "1";
                    }
                    goalNumber.setText(displayNum);
                    break;
                case "week":
                    goalNum = metric.getWeeklyGoalNum();
                    displayNum = String.valueOf(metric.getWeeklyGoalNum());
                    if(goalNum == 0) {
                        displayNum = "1";
                    }
                    goalNumber.setText(displayNum);
                    break;
                case "month":
                    goalNum = metric.getGoalNum();
                    displayNum = String.valueOf(metric.getGoalNum());
                    if(goalNum == 0) {
                        displayNum = "1";
                    }
                    goalNumber.setText(displayNum);
                    break;
                case "year":
                    goalNum = metric.getYearlyGoalNum();
                    displayNum = String.valueOf(metric.getYearlyGoalNum());
                    if(goalNum == 0) {
                        displayNum = "1";
                    }
                    goalNumber.setText(displayNum);
                    break;
            }
            progressMark.setStartAngle(PROGRESS_MARK);
            progressMark.setColor(ContextCompat.getColor(context, R.color.colorWhite));
            progressMark.setProgressBarWidth(getResources().getDimension(R.dimen.circularBarWidth));
            progressMark.setProgressWithAnimation(1, 0);
        }

    }

    private int calculateProgressMarkPosition() {
        int maximum = 0;
        int increment = 0;
        int current = 0;
        calendar = Calendar.getInstance(TimeZone.getDefault());
        switch (parentActivity.getTimeline()) {
            case "week":
                maximum = 7;
                current = calendar.get(Calendar.DAY_OF_WEEK);
                increment = 51;
            break;
            case "month":
                maximum = Calendar.DAY_OF_MONTH;
                current = calendar.get(maximum);
                increment = 12;
            break;
            case "year":
                maximum = Calendar.DAY_OF_YEAR;
                current = calendar.get(maximum);
                increment = 1;
            break;
        }
        int position = -90;
        if(pastTimeline) {
            position += 360;
        }
        else {
            if(needsProgress) {
                for(int i = 1; i <= calendar.getActualMaximum(maximum); i++) {
                    position += increment;
                    if(i == current) {
                        break;
                    }
                }
            }
        }

        return position;
    }

    private void calculateProgressColor(Metric metric, int positionPercent) {
        if(getContext() != null) {
            if(pastTimeline) {
                if(metric.getPercentComplete(parentActivity.getTimeline()) < 100) {
                    metric.setColor(ContextCompat.getColor(getContext(),R.color.colorMoonBlue));
                }
                else {
                    metric.setColor(ContextCompat.getColor(getContext(),R.color.colorCorporateOrange));
                }
            }
            else {
                if (metric.getPercentComplete(parentActivity.getTimeline()) < positionPercent) {
                    metric.setColor(ContextCompat.getColor(getContext(), R.color.colorMoonBlue));
                } else if (metric.getPercentComplete(parentActivity.getTimeline()) > 99) {
                    metric.setColor(ContextCompat.getColor(getContext(), R.color.colorCorporateOrange));
                } else {
                    metric.setColor(ContextCompat.getColor(getContext(), R.color.colorYellow));
                }
            }
        }

    }

    private int calculateProgressOnTrack(Metric metric) {
        int positionPercent = 0; //will determine blue
        double goalNum = metric.getGoalNum(); //monthly goal
        Calendar calendar = Calendar.getInstance();
        switch (parentActivity.getTimeline()) {
            case "day":
                goalNum = metric.getDailyGoalNum();
                break;
            case "week":
                goalNum = metric.getWeeklyGoalNum();
                break;
            case "month":
                goalNum = metric.getGoalNum();
                break;
            case "year":
                goalNum = metric.getYearlyGoalNum();
                break;
        }

        if(parentActivity.getTimeline().equalsIgnoreCase("week")) { //week
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            positionPercent = (int) (((double)dayOfWeek / (double)calendar.getActualMaximum(Calendar.DAY_OF_WEEK)) * 100);
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getPercentComplete(parentActivity.getTimeline()) >= positionPercent) {
                positionPercent = metric.getPercentComplete(parentActivity.getTimeline()) - 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        } else if(parentActivity.getTimeline().equalsIgnoreCase("month")) { //month
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            positionPercent = (int) (((double)dayOfMonth / (double)calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) * 100);
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getPercentComplete(parentActivity.getTimeline()) >= positionPercent) {
                positionPercent = metric.getPercentComplete(parentActivity.getTimeline()) - 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        } else if(parentActivity.getTimeline().equalsIgnoreCase("year")) { //year
//            goalNum = goalNum * 12; //annual goal
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            positionPercent = (int) (((double)dayOfYear / (double)calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) * 100);
            if(metric.getCurrentNum() >= goalNum) {
                positionPercent = 100; //hit goal, orange
            } else if (metric.getPercentComplete(parentActivity.getTimeline()) >= positionPercent) {
                positionPercent = metric.getPercentComplete(parentActivity.getTimeline()) - 1; //setting color for yellow as returning percent will be higher than pacer percent
            }
        }
        return positionPercent;
    }

    @Override
    public void onClick(View v) {
        if(!parentActivity.isTeamSwapOccurring()) {
            switch (v.getId()) {
                case R.id.addView:
                    launchAddClient();
                    break;
                case R.id.contactsProgressMark:
                    navigateToClientList("pipeline");
                    break;
                case R.id.appointmentsProgressMark:
                    navigateToClientList("pipeline");
                    break;
                case R.id.bbSignedProgressMark:
                    navigateToClientList("signed");
                    break;
                case R.id.listingsTakenProgressMark:
                    navigateToClientList("signed");
                    break;
                case R.id.underContractProgressMark:
                    navigateToClientList("contract");
                    break;
                case R.id.closedProgressMark:
                    navigateToClientList("closed");
                    break;
                default:
                    break;
            }
        }

    }

    private void navigateToClientList(String tabName){
        navigationManager.navigateToClientList(tabName);
    }

    private void launchAddClient() {
        //TODO: Change this back
        navigationManager.stackReplaceFragment(AddClientFragment.class);
//        Intent intent = new Intent(parentActivity, HtmlNotificationActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
//        if(asyncReturnType.equals("Activities")) {
//            dataController.setScoreboardActivities(returnObject);
//            dataController.setActivitiesObject(returnObject);
//            parentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loader.setVisibility(View.GONE);
//                    animateProgressBars(dataController.getScoreboardObject());
//                    calculateVolumes();
//                }
//            });
//        }
    }

    @Override
    public void onEventCompleted(Object returnObject, ApiReturnTypes returnType) {
        if(returnType == ApiReturnTypes.GET_ACTIVITIES) {
            AsyncActivitiesJsonObject activitiesObject = parentActivity.getGson().fromJson(((Response) returnObject).body().charStream(), AsyncActivitiesJsonObject.class);
            dataController.setScoreboardActivities(activitiesObject, parentActivity.isRecruiting());
            dataController.setActivitiesObject(activitiesObject);
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loader.setVisibility(View.GONE);
                    animateProgressBars(dataController.getScoreboardObject());
                    calculateVolumes();
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
}
