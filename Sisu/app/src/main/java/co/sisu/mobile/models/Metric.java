package co.sisu.mobile.models;

import android.support.annotation.NonNull;

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


    public Metric(String title, String type, int currentNum, int goalNum, int thumbnailId, int color, int weight){
        this.title = title;
        this.type = type;
        this.currentNum = currentNum;
        this.goalNum = goalNum;
        this.thumbnailId = thumbnailId;
        this.color = color;
        this.weight = weight;
    }

    public Metric() {

    }

    public int getPercentComplete(){
        return (int) (((double)currentNum/(double)goalNum) * 100);
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

    @Override
    public int compareTo(Metric other) {
        if(this.getWeight() > other.getWeight())
            return -1;
        else if (this.getWeight() == other.getWeight())
            return 0 ;
        return 1 ;
    }
}
