package co.sisu.mobile.models;

/**
 * Created by bradygroharing on 7/31/18.
 */

public class FirebaseDeviceObject {
    private String device_type;
    private String device_id;
    private String device_name;
    private String fcm_id;


    public FirebaseDeviceObject(String device_type, String device_id, String device_name, String fcm_id) {
        this.device_type = device_type;
        this.device_id = device_id;
        this.device_name = device_name;
        this.fcm_id = fcm_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getFcm_id() {
        return fcm_id;
    }

    public void setFcm_id(String fcm_id) {
        this.fcm_id = fcm_id;
    }
}
