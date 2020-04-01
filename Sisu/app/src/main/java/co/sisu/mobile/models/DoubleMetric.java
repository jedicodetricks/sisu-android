package co.sisu.mobile.models;

/**
 * Created by Jeff on 2/21/2018.
 */

public class DoubleMetric {
    private String left_title;
    private String left_type;
    private int left_currentNum;
    private double left_goalNum;
    private int left_thumbnailId;
    private int left_color;
    private int left_weight;
    private double left_yearlyGoalNum;
    private double left_dailyGoalNum;
    private double left_weeklyGoalNum;

    private String right_title;
    private String right_type;
    private int right_currentNum;
    private double right_goalNum;
    private int right_thumbnailId;
    private int right_color;
    private int right_weight;
    private double right_yearlyGoalNum;
    private double right_dailyGoalNum;
    private double right_weeklyGoalNum;

    private Metric leftMetric, rightMetric;


    public DoubleMetric(Metric leftMetric, Metric rightMetric){
        this.leftMetric = leftMetric;
        this.rightMetric = rightMetric;

        this.left_title = leftMetric.getTitle();
        this.left_type = leftMetric.getType();
        this.left_currentNum = leftMetric.getCurrentNum();
        this.left_goalNum = leftMetric.getGoalNum();
        this.left_yearlyGoalNum = leftMetric.getGoalNum() * 12;
        this.left_dailyGoalNum = leftMetric.getGoalNum() / 30;
        this.left_weeklyGoalNum = leftMetric.getGoalNum() / 4;
        this.left_thumbnailId = leftMetric.getThumbnailId();
        this.left_weight = leftMetric.getWeight();

        if(rightMetric != null) {
            this.right_title = rightMetric.getTitle();
            this.right_type = rightMetric.getType();
            this.right_currentNum = rightMetric.getCurrentNum();
            this.right_goalNum = rightMetric.getGoalNum();
            this.right_yearlyGoalNum = rightMetric.getGoalNum() * 12;
            this.right_dailyGoalNum = rightMetric.getGoalNum() / 30;
            this.right_weeklyGoalNum = rightMetric.getGoalNum() / 4;
            this.right_thumbnailId = rightMetric.getThumbnailId();
            this.right_weight = rightMetric.getWeight();
        }


    }


    public String getLeft_title() {
        return left_title;
    }

    public void setLeft_title(String left_title) {
        this.left_title = left_title;
    }

    public String getLeft_type() {
        return left_type;
    }

    public void setLeft_type(String left_type) {
        this.left_type = left_type;
    }

    public int getLeft_currentNum() {
        return left_currentNum;
    }

    public void setLeft_currentNum(int left_currentNum) {
        this.left_currentNum = left_currentNum;
    }

    public double getLeft_goalNum() {
        return left_goalNum;
    }

    public void setLeft_goalNum(double left_goalNum) {
        this.left_goalNum = left_goalNum;
    }

    public int getLeft_thumbnailId() {
        return left_thumbnailId;
    }

    public void setLeft_thumbnailId(int left_thumbnailId) {
        this.left_thumbnailId = left_thumbnailId;
    }

    public int getLeft_color() {
        return left_color;
    }

    public void setLeft_color(int left_color) {
        this.left_color = left_color;
    }

    public int getLeft_weight() {
        return left_weight;
    }

    public void setLeft_weight(int left_weight) {
        this.left_weight = left_weight;
    }

    public double getLeft_yearlyGoalNum() {
        return left_yearlyGoalNum;
    }

    public void setLeft_yearlyGoalNum(double left_yearlyGoalNum) {
        this.left_yearlyGoalNum = left_yearlyGoalNum;
    }

    public double getLeft_dailyGoalNum() {
        return left_dailyGoalNum;
    }

    public void setLeft_dailyGoalNum(double left_dailyGoalNum) {
        this.left_dailyGoalNum = left_dailyGoalNum;
    }

    public double getLeft_weeklyGoalNum() {
        return left_weeklyGoalNum;
    }

    public void setLeft_weeklyGoalNum(double left_weeklyGoalNum) {
        this.left_weeklyGoalNum = left_weeklyGoalNum;
    }

    public String getRight_title() {
        return right_title;
    }

    public void setRight_title(String right_title) {
        this.right_title = right_title;
    }

    public String getRight_type() {
        return right_type;
    }

    public void setRight_type(String right_type) {
        this.right_type = right_type;
    }

    public int getRight_currentNum() {
        return right_currentNum;
    }

    public void setRight_currentNum(int right_currentNum) {
        this.right_currentNum = right_currentNum;
    }

    public double getRight_goalNum() {
        return right_goalNum;
    }

    public void setRight_goalNum(double right_goalNum) {
        this.right_goalNum = right_goalNum;
    }

    public int getRight_thumbnailId() {
        return right_thumbnailId;
    }

    public void setRight_thumbnailId(int right_thumbnailId) {
        this.right_thumbnailId = right_thumbnailId;
    }

    public int getRight_color() {
        return right_color;
    }

    public void setRight_color(int right_color) {
        this.right_color = right_color;
    }

    public int getRight_weight() {
        return right_weight;
    }

    public void setRight_weight(int right_weight) {
        this.right_weight = right_weight;
    }

    public double getRight_yearlyGoalNum() {
        return right_yearlyGoalNum;
    }

    public void setRight_yearlyGoalNum(double right_yearlyGoalNum) {
        this.right_yearlyGoalNum = right_yearlyGoalNum;
    }

    public double getRight_dailyGoalNum() {
        return right_dailyGoalNum;
    }

    public void setRight_dailyGoalNum(double right_dailyGoalNum) {
        this.right_dailyGoalNum = right_dailyGoalNum;
    }

    public double getRight_weeklyGoalNum() {
        return right_weeklyGoalNum;
    }

    public void setRight_weeklyGoalNum(double right_weeklyGoalNum) {
        this.right_weeklyGoalNum = right_weeklyGoalNum;
    }

    public Metric getLeftMetric() {
        return leftMetric;
    }

    public void setLeftMetric(Metric leftMetric) {
        this.leftMetric = leftMetric;
    }

    public Metric getRightMetric() {
        return rightMetric;
    }

    public void setRightMetric(Metric rightMetric) {
        this.rightMetric = rightMetric;
    }
}
