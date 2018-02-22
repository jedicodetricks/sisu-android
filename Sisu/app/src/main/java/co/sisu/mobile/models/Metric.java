package co.sisu.mobile.models;

/**
 * Created by Jeff on 2/21/2018.
 */

public class Metric {
    private String title;
    private int currentNum;
    private int goalNum;

    public Metric(){

    }

    public Metric(String title, int currentNum, int goalNum){
        this.title = title;
        this.currentNum = currentNum;
        this.goalNum = goalNum;
    }

    public int getPercentComplete(){
        return (currentNum * goalNum) / 100;
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
}
