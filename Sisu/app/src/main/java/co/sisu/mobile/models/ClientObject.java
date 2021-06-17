package co.sisu.mobile.models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Brady Groharing on 3/15/2018.
 */

public class ClientObject implements Comparable<ClientObject>{

    private String address_1;
    private String address_2;
    private String appt_dt;
    private String appt_set_dt;
    private String city;
    private String client_event_count; // Not using?
    private String client_id;
    private String closed_dt;
    private String commission_amt;
    private String created_ts;
    private String email;
    private String first_name;
    private String gross_commission_amt;
    private String home_phone;
    private int is_locked;
    private String last_name;
    private String lead_type_id;
    private String mobile_phone;
    private String note;
    private String paid_dt;
    private String postal_code;
    private String signed_dt;
    private String state;
    private String status;
    private String trans_amt;
    private String type_id;
    private String type_id_dscr;
    private String uc_dt;
    private String updated_ts;
    private int is_priority;
    private String activate_client;
    private String market_id;
    private int team_id;
    private String appt_set_by_agent_id;

    public ClientObject() {
    }

    public ClientObject(JSONObject clientJson) {
        try {
            this.address_1 = String.valueOf(clientJson.get("address_1")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("address_1"));
            this.address_2 = String.valueOf(clientJson.get("address_2")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("address_2"));
            this.appt_dt = String.valueOf(clientJson.get("appt_dt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("appt_dt"));
            this.appt_set_dt = String.valueOf(clientJson.get("appt_set_dt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("appt_set_dt"));
            this.city = String.valueOf(clientJson.get("city")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("city"));
            this.client_id = String.valueOf(clientJson.get("client_id")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("client_id"));
            this.closed_dt = String.valueOf(clientJson.get("closed_dt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("closed_dt"));
            this.commission_amt = String.valueOf(clientJson.get("commission_amt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("commission_amt"));
            this.created_ts = String.valueOf(clientJson.get("created_ts")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("created_ts"));
            this.email = String.valueOf(clientJson.get("email")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("email"));
            this.first_name = String.valueOf(clientJson.get("first_name")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("first_name"));
            this.gross_commission_amt = String.valueOf(clientJson.get("gross_commission_amt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("gross_commission_amt"));
            this.home_phone = String.valueOf(clientJson.get("home_phone")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("home_phone"));
            try {
                this.is_locked = Integer.valueOf((Integer) clientJson.get("is_locked"));
            } catch(Exception e) {
                this.is_locked = 0;
//                e.printStackTrace();
            }
            this.last_name = String.valueOf(clientJson.get("last_name")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("last_name"));
            this.lead_type_id = String.valueOf(clientJson.get("lead_type_id")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("lead_type_id"));
            this.mobile_phone = String.valueOf(clientJson.get("mobile_phone")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("mobile_phone"));
            this.note = String.valueOf(clientJson.get("note")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("note"));
            this.paid_dt = String.valueOf(clientJson.get("paid_dt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("paid_dt"));
            this.postal_code = String.valueOf(clientJson.get("postal_code")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("postal_code"));
            this.signed_dt = String.valueOf(clientJson.get("signed_dt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("signed_dt"));
            this.state = String.valueOf(clientJson.get("state")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("state"));
            this.status = String.valueOf(clientJson.get("status")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("status"));
            this.trans_amt = String.valueOf(clientJson.get("trans_amt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("trans_amt"));
            this.type_id = String.valueOf(clientJson.get("type_id")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("type_id"));
//            this.type_id_dscr = String.valueOf(clientJson.get("type_id_dscr")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("type_id_dscr"));
            this.uc_dt = String.valueOf(clientJson.get("uc_dt")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("uc_dt"));
            this.updated_ts = String.valueOf(clientJson.get("updated_ts")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("updated_ts"));
            this.is_priority = Integer.valueOf((Integer) clientJson.get("is_priority"));
//            this.activate_client = String.valueOf(clientJson.get("activate_client")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("activate_client"));
            this.market_id = String.valueOf(clientJson.get("market_id")).equalsIgnoreCase("null") ? null : String.valueOf(clientJson.get("market_id"));
            if(clientJson.has("team_id")) {
                this.team_id = Integer.valueOf((Integer) clientJson.get("team_id"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getActivate_client() {
        return activate_client;
    }

    public void setActivate_client(String activate_client) {
        this.activate_client = activate_client;
    }

    public String getAddress_1() {
        return address_1;
    }

    public void setAddress_1(String address_1) {
        this.address_1 = address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public void setAddress_2(String address_2) {
        this.address_2 = address_2;
    }

    public String getAppt_dt() {
        return appt_dt;
    }

    public void setAppt_dt(String appt_dt) {
        this.appt_dt = appt_dt;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClient_event_count() {
        return client_event_count;
    }

    public void setClient_event_count(String client_event_count) {
        this.client_event_count = client_event_count;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClosed_dt() {
        return closed_dt;
    }

    public void setClosed_dt(String closed_dt) {
        this.closed_dt = closed_dt;
    }

    public String getCommission_amt() {
        return commission_amt;
    }

    public void setCommission_amt(String commission_amt) {
        this.commission_amt = commission_amt;
    }

    public String getCreated_ts() {
        return created_ts;
    }

    public void setCreated_ts(String created_ts) {
        this.created_ts = created_ts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getGross_commission_amt() {
        return gross_commission_amt;
    }

    public void setGross_commission_amt(String gross_commission_amt) {
        this.gross_commission_amt = gross_commission_amt;
    }

    public String getHome_phone() {
        return home_phone;
    }

    public void setHome_phone(String home_phone) {
        this.home_phone = home_phone;
    }

    public int getIs_locked() {
        return is_locked;
    }

    public void setIs_locked(int is_locked) {
        this.is_locked = is_locked;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getLead_type_id() {
        return lead_type_id;
    }

    public void setLead_type_id(String lead_type_id) {
        this.lead_type_id = lead_type_id;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getPaid_dt() {
        return paid_dt;
    }

    public void setPaid_dt(String paid_dt) {
        this.paid_dt = paid_dt;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getSigned_dt() {
        return signed_dt;
    }

    public void setSigned_dt(String signed_dt) {
        this.signed_dt = signed_dt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrans_amt() {
        return trans_amt;
    }

    public void setTrans_amt(String trans_amt) {
        this.trans_amt = trans_amt;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getType_id_dscr() {
        return type_id_dscr;
    }

    public void setType_id_dscr(String type_id_dscr) {
        this.type_id_dscr = type_id_dscr;
    }

    public String getUc_dt() {
        return uc_dt;
    }

    public void setUc_dt(String uc_dt) {
        this.uc_dt = uc_dt;
    }

    public String getUpdated_ts() {
        return updated_ts;
    }

    public void setUpdated_ts(String updated_ts) {
        this.updated_ts = updated_ts;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int compareTo(@NonNull ClientObject c) {
        if(this.getLast_name() != null) {
            return (this.getLast_name().compareTo(c.last_name));
        }
        return 0;
    }

    public int getIs_priority() {
        return is_priority;
    }

    public void setIs_priority(int is_priority) {
        this.is_priority = is_priority;
    }

    public String getMarket_id() {
        return market_id;
    }

    public void setMarket_id(String market_id) {
        this.market_id = market_id;
    }

    public String getAppt_set_dt() {
        return appt_set_dt;
    }

    public void setAppt_set_dt(String appt_set_dt) {
        this.appt_set_dt = appt_set_dt;
    }


    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public String getAppt_set_by_agent_id() {
        return appt_set_by_agent_id;
    }

    public void setAppt_set_by_agent_id(String appt_set_by_agent_id) {
        this.appt_set_by_agent_id = appt_set_by_agent_id;
    }
}
