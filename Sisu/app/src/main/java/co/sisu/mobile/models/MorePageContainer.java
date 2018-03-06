package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 2/28/2018.
 */

public class MorePageContainer {
    private String title;
    private String subTitle;
    private int thumbnailId;

    public MorePageContainer(String title, String subTitle, int thumbnailId) {
        this.title = title;
        this.subTitle = subTitle;
        this.thumbnailId = thumbnailId;
    }

    public int getThumbnailId() {
        return thumbnailId;
    }

    public void setThumbnailId(int thumbnailId) {
        this.thumbnailId = thumbnailId;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
