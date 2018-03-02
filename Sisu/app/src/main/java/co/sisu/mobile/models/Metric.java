package co.sisu.mobile.models;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Jeff on 2/21/2018.
 */

public class Metric {
    private String title;
    private int currentNum;
    private int goalNum;
    private int thumbnailId;
    private int color;


    public Metric(String title, int currentNum, int goalNum, int thumbnailId, int color){
        this.title = title;
        this.currentNum = currentNum;
        this.goalNum = goalNum;
        this.thumbnailId = thumbnailId;
        this.color = color;
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

    public void setThumbnailId(int iconPath) {
        this.thumbnailId = thumbnailId;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
