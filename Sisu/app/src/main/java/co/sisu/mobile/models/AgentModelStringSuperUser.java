package co.sisu.mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Brady Groharing on 4/8/2018.
 * This class only exists because Rick is sending down either a boolean or a byte to indicate is_super_user
 */

public class AgentModelStringSuperUser{
    String agent_id;
    String desired_income;
    String email;
    String first_name;
    String last_name;
    String mobile_phone;
    String profile;
    String vision_statement;
    AgentGoalsObject[] agentGoalsObject;
    String is_superuser;

    public String getIs_superuser() {
        return is_superuser;
    }

    public void setIs_superuser(String is_superuser) {
        this.is_superuser = is_superuser;
    }

    public AgentGoalsObject[] getAgentGoalsObject() {
        return agentGoalsObject;
    }


    public void setAgentGoalsObject(AgentGoalsObject[] agentGoalsObject) {
        this.agentGoalsObject = agentGoalsObject;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public int describeContents() {
        return 0;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getDesired_income() {
        return desired_income;
    }

    public void setDesired_income(String desired_income) {
        this.desired_income = desired_income;
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

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getVision_statement() {
        return vision_statement;
    }

    public void setVision_statement(String vision_statement) {
        this.vision_statement = vision_statement;
    }

}
