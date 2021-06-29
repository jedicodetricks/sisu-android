package co.sisu.mobile.models;

import android.graphics.Bitmap;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class LeaderboardAgentModel {
    String agentId;
    String name;
    String itemCount;
    String place;
    String imageUrl;
    Bitmap image;

    public LeaderboardAgentModel(String agentId, String name, String itemCount, String place, String imageUrl) {
        this.agentId = agentId;
        this.name = name;
        this.itemCount = itemCount;
        this.place = place;
        this.imageUrl = imageUrl;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemCount() {
        String returnNumber = "0";
        if(!itemCount.equalsIgnoreCase("null")) {
            returnNumber = itemCount;
        }
        return returnNumber;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
