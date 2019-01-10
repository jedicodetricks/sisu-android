package co.sisu.mobile.models;

/**
 * Created by Jeff on 2/21/2018.
 */

public class Metric implements Comparable<Metric>{
    private String title;
    private String type;
    private int currentNum;
    private double goalNum;
    private int thumbnailId;
    private int color;
    private int weight;
    private double yearlyGoalNum;
    private double dailyGoalNum;
    private double weeklyGoalNum;


    public Metric(String title, String type, int currentNum, double goalNum, int thumbnailId, int color, int weight){
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

    public int getPercentComplete(String timeline){
        if(goalNum == 0) {
            if(currentNum > 0) {
                return 100;
            }
            else {
                return 0;
            }
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

//        percentComplete = (percentComplete / 100);
//        percentComplete = 360 * percentComplete;
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

    public double getGoalNum() {
        return goalNum;
    }

    public void setGoalNum(double goalNum) {
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

    public double getYearlyGoalNum() {
        return yearlyGoalNum;
    }

    public void setYearlyGoalNum(double yearlyGoalNum) {
        this.yearlyGoalNum = yearlyGoalNum;
    }

    public double getDailyGoalNum() {
        return dailyGoalNum;
    }

    public void setDailyGoalNum(double dailyGoalNum) {
        this.dailyGoalNum = dailyGoalNum;
    }

    public double getWeeklyGoalNum() {
        return weeklyGoalNum;
    }

    public void setWeeklyGoalNum(double weeklyGoalNum) {
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
