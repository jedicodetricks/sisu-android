package co.sisu.mobile.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateManager {
    //TODO: Move all the date related stuff into this class
    private int selectedStartYear = 0;
    private int selectedStartMonth = 0;
    private int selectedStartDay = 0;
    private int selectedEndYear = 0;
    private int selectedEndMonth = 0;
    private int selectedEndDay = 0;
    private String formattedStartTime;
    private String formattedEndTime;
    private Date selectedStartTime;
    private Date selectedEndTime;

    private int recordYear = 0;
    private int recordMonth = 0;
    private int recordDay = 0;
    private String formattedRecordDate;


    private Calendar calendar = Calendar.getInstance();

    private int timelineSelection = 5;
    private String timeline = "month";

    public void initTimelineDate() {
        calendar = Calendar.getInstance();

        switch (timelineSelection) {
            case 0:
                //Yesterday
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                selectedStartYear = calendar.get(Calendar.YEAR);
                selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                selectedEndYear = calendar.get(Calendar.YEAR);
                selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            case 1:
                //Today
                selectedStartYear = calendar.get(Calendar.YEAR);
                selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

                selectedEndYear = calendar.get(Calendar.YEAR);
                selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                break;
            case 2:
                //Last Week
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
                selectedStartYear = calendar.get(Calendar.YEAR);
                selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
                selectedStartDay = 1;

                selectedEndYear = calendar.get(Calendar.YEAR);
                selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
                selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
            case 6:
                //Last year
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

    public int getSelectedStartYear() {
        return selectedStartYear;
    }

    public void setSelectedStartYear(int selectedStartYear) {
        this.selectedStartYear = selectedStartYear;
    }

    public int getSelectedStartMonth() {
        return selectedStartMonth;
    }

    public void setSelectedStartMonth(int selectedStartMonth) {
        this.selectedStartMonth = selectedStartMonth;
    }

    public int getSelectedStartDay() {
        return selectedStartDay;
    }

    public void setSelectedStartDay(int selectedStartDay) {
        this.selectedStartDay = selectedStartDay;
    }

    public int getSelectedEndYear() {
        return selectedEndYear;
    }

    public void setSelectedEndYear(int selectedEndYear) {
        this.selectedEndYear = selectedEndYear;
    }

    public int getSelectedEndMonth() {
        return selectedEndMonth;
    }

    public void setSelectedEndMonth(int selectedEndMonth) {
        this.selectedEndMonth = selectedEndMonth;
    }

    public int getSelectedEndDay() {
        return selectedEndDay;
    }

    public void setSelectedEndDay(int selectedEndDay) {
        this.selectedEndDay = selectedEndDay;
    }

    public String getFormattedStartTime() {
        return formattedStartTime;
    }

    public void setFormattedStartTime(String formattedStartTime) {
        this.formattedStartTime = formattedStartTime;
    }

    public String getFormattedEndTime() {
        return formattedEndTime;
    }

    public void setFormattedEndTime(String formattedEndTime) {
        this.formattedEndTime = formattedEndTime;
    }

    public Date getSelectedStartTime() {
        return selectedStartTime;
    }

    public void setSelectedStartTime(Date selectedStartTime) {
        this.selectedStartTime = selectedStartTime;
    }

    public Date getSelectedEndTime() {
        return selectedEndTime;
    }

    public void setSelectedEndTime(Date selectedEndTime) {
        this.selectedEndTime = selectedEndTime;
    }

    public int getTimelineSelection() {
        return timelineSelection;
    }

    public void setTimelineSelection(int timelineSelection) {
        this.timelineSelection = timelineSelection;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getFormattedRecordDate() {
        return formattedRecordDate;
    }

    public void setFormattedRecordDate(String formattedRecordDate) {
        this.formattedRecordDate = formattedRecordDate;
    }

    public int getRecordYear() {
        return recordYear;
    }

    public int getRecordMonth() {
        return recordMonth;
    }

    public int getRecordDay() {
        return recordDay;
    }

    public void setFormattedTimes() {
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
    }

    public void setFormattedRecordDate() {
        String formattedStartMonth = String.valueOf(recordMonth);
        String formattedStartDay = String.valueOf(recordDay);

        if(recordDay < 10) {
            formattedStartDay = "0" + recordDay;
        }

        if(recordMonth < 10) {
            formattedStartMonth = "0" + recordMonth;
        }

        formattedRecordDate = recordYear + "-" + formattedStartMonth + "-" + formattedStartDay;
    }

    public void setToYesterday() {
        calendar = Calendar.getInstance();
        timeline = "day";
        timelineSelection = 0;
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
        setFormattedTimes();
    }

    public void setToToday() {
        calendar = Calendar.getInstance();
        timeline = "day";
        timelineSelection = 1;

        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
        setFormattedTimes();
    }

    public void setToLastWeek() {
        calendar = Calendar.getInstance();
        timeline = "week";
        timelineSelection = 2;

        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        setFormattedTimes();
    }

    public void setToThisWeek() {
        calendar = Calendar.getInstance();
        timeline = "week";
        timelineSelection = 3;

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        setFormattedTimes();
    }

    public void setToLastMonth() {
        calendar = Calendar.getInstance();
        timeline = "month";
        timelineSelection = 4;

        calendar.add(Calendar.MONTH, -1);
        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
        selectedStartDay = 1;

        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        setFormattedTimes();
    }

    public void setToThisMonth() {
        calendar = Calendar.getInstance();
        timeline = "month";
        timelineSelection = 5;

        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
        selectedStartDay = 1;

        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
        selectedEndDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        setFormattedTimes();
    }

    public void setToLastYear() {
        calendar = Calendar.getInstance();
        timeline = "year";
        timelineSelection = 6;

        calendar.add(Calendar.YEAR, -1);
        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = 1;
        selectedStartDay = 1;

        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = 12;
        selectedEndDay = 31;
        setFormattedTimes();
    }

    public void setToThisYear() {
        calendar = Calendar.getInstance();
        timeline = "year";
        timelineSelection = 7;

        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = 1;
        selectedStartDay = 1;

        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = 12;
        selectedEndDay = 31;
        setFormattedTimes();
    }

    public void setToDate(Calendar calendar) {
        this.calendar = calendar;
        timeline = "day";
        timelineSelection = 0;
        selectedStartYear = calendar.get(Calendar.YEAR);
        selectedStartMonth = calendar.get(Calendar.MONTH) + 1;
        selectedStartDay = calendar.get(Calendar.DAY_OF_MONTH);

        selectedEndYear = calendar.get(Calendar.YEAR);
        selectedEndMonth = calendar.get(Calendar.MONTH) + 1;
        selectedEndDay = calendar.get(Calendar.DAY_OF_MONTH);
        setFormattedTimes();
    }

    public void setRecordDateToDate(Calendar calendar) {
        this.calendar = calendar;
        recordYear = calendar.get(Calendar.YEAR);
        recordMonth = calendar.get(Calendar.MONTH) + 1;
        recordDay = calendar.get(Calendar.DAY_OF_MONTH);

        setFormattedRecordDate();
    }

    public void setRecordDateToYesterday() {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        recordYear = calendar.get(Calendar.YEAR);
        recordMonth = calendar.get(Calendar.MONTH) + 1;
        recordDay = calendar.get(Calendar.DAY_OF_MONTH);

        setFormattedRecordDate();
    }

    public void setRecordDateToToday() {
        calendar = Calendar.getInstance();
        recordYear = calendar.get(Calendar.YEAR);
        recordMonth = calendar.get(Calendar.MONTH) + 1;
        recordDay = calendar.get(Calendar.DAY_OF_MONTH);

        setFormattedRecordDate();
    }
}
