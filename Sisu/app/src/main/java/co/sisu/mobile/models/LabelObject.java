package co.sisu.mobile.models;

public class LabelObject {

    String buyer;
    String buyers;
    String contacts;
    String listing;
    String listings;
    String scoreboard;
    String units;

    public LabelObject(String buyer, String buyers, String contacts, String listing, String listings, String scoreboard, String units) {
        this.buyer = buyer;
        this.buyers = buyers;
        this.contacts = contacts;
        this.listing = listing;
        this.listings = listings;
        this.scoreboard = scoreboard;
        this.units = units;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getBuyers() {
        return buyers;
    }

    public void setBuyers(String buyers) {
        this.buyers = buyers;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getListing() {
        return listing;
    }

    public void setListing(String listing) {
        this.listing = listing;
    }

    public String getListings() {
        return listings;
    }

    public void setListings(String listings) {
        this.listings = listings;
    }

    public String getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(String scoreboard) {
        this.scoreboard = scoreboard;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public String toString() {
        return this.buyer + " " + this.buyers + " " +this.contacts + " " +this.listing + " " +this.listings + " " +this.scoreboard + " " +this.units;
    }
}
