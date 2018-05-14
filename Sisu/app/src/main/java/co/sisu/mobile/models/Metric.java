package co.sisu.mobile.models;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Jeff on 2/21/2018.
 */

public class Metric implements Comparable<Metric>{
    private String title;
    private String type;
    private int currentNum;
    private int goalNum;
    private int thumbnailId;
    private int color;
    private int weight;
    private int yearlyGoalNum;
    private int dailyGoalNum;
    private int weeklyGoalNum;


    public Metric(String title, String type, int currentNum, int goalNum, int thumbnailId, int color, int weight){
        this.title = title;
        this.type = type;
        this.currentNum = currentNum;
        this.goalNum = goalNum;
        this.yearlyGoalNum = goalNum * 12;
        this.dailyGoalNum = goalNum / 30;
        this.weeklyGoalNum = goalNum / 4;
        this.thumbnailId = thumbnailId;
        this.color = color;
        this.weight = weight;
    }

    public Metric() {

    }

    public int getPercentComplete(String timeline){
        if(goalNum == 0) {
            return 0;
        }
        int percentComplete = 0;

        switch (timeline) {
            case "day":
                percentComplete = (int) (((double)currentNum/(double)dailyGoalNum) * 100);
                break;
            case "week":
                percentComplete = (int) (((double)currentNum/(double)weeklyGoalNum) * 100);
                break;
            case "month":
                percentComplete = (int) (((double)currentNum/(double)goalNum) * 100);
                break;
            case "year":
                percentComplete = (int) (((double)currentNum/(double)yearlyGoalNum) * 100);
                break;
        }

        return percentComplete;
    }

    public int getPercentAroundCircleComplete(String timeline){
        if(goalNum == 0) {
            return 0;
        }
        double percentComplete = 0;

        switch (timeline) {
            case "day":
                percentComplete = (double) (((double)currentNum/(double)dailyGoalNum) * 100);
                break;
            case "week":
                percentComplete = (double) (((double)currentNum/(double)weeklyGoalNum) * 100);
                break;
            case "month":
                percentComplete = (double) (((double)currentNum/(double)goalNum) * 100);
                break;
            case "year":
                percentComplete = (double) (((double)currentNum/(double)yearlyGoalNum) * 100);
                break;
        }

        percentComplete = (percentComplete / 100);
        percentComplete = 360 * percentComplete;
        return (int) percentComplete;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public int getGoalNum() {
        return goalNum;
    }

    public void setGoalNum(int goalNum) {
        this.goalNum = goalNum;
    }

    public int getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(int thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getYearlyGoalNum() {
        return yearlyGoalNum;
    }

    public void setYearlyGoalNum(int yearlyGoalNum) {
        this.yearlyGoalNum = yearlyGoalNum;
    }

    public int getDailyGoalNum() {
        return dailyGoalNum;
    }

    public void setDailyGoalNum(int dailyGoalNum) {
        this.dailyGoalNum = dailyGoalNum;
    }

    public int getWeeklyGoalNum() {
        return weeklyGoalNum;
    }

    public void setWeeklyGoalNum(int weeklyGoalNum) {
        this.weeklyGoalNum = weeklyGoalNum;
    }

    @Override
    public int compareTo(Metric other) {
        if(this.getWeight() > other.getWeight())
            return -1;
        else if (this.getWeight() == other.getWeight())
            return 0 ;
        return 1 ;
    }
}
