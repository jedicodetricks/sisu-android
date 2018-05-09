package co.sisu.mobile.models;

/**
 * Created by Brady Groharing on 5/8/2018.
 */

public class JWTObject {
    String jwt;
    String timestamp;
    String transId;

    public JWTObject(String jwt, String timestamp, String transId) {
        this.jwt = jwt;
        this.timestamp = timestamp;
        this.transId = transId;
    }

    public String getJwt() {

        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}
