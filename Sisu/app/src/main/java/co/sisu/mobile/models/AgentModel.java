package co.sisu.mobile.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Brady Groharing on 4/8/2018.
 */

public class AgentModel implements Parcelable, Cloneable{
    String agent_id;
    String desired_income;
    String email;
    String first_name;
    String last_name;
    String mobile_phone;
    String profile;
    String vision_statement;
    AgentGoalsObject[] agentGoalsObject;
    boolean is_superuser;

    public boolean getIs_superuser() {
        return is_superuser;
    }

    public void setIs_superuser(boolean is_superuser) {
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

    @Override
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(agent_id);
        dest.writeString(desired_income);
        dest.writeString(email);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(mobile_phone);
        dest.writeString(profile);
        dest.writeString(vision_statement);
        dest.writeByte((byte) (is_superuser ? 1 : 0));
    }

    /**
     * Retrieving AgentModel data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private AgentModel(Parcel in){
        this.agent_id = in.readString();
        this.desired_income = in.readString();
        this.email = in.readString();
        this.first_name = in.readString();
        this.last_name = in.readString();
        this.mobile_phone = in.readString();
        this.profile = in.readString();
        this.vision_statement = in.readString();
        this.is_superuser = in.readByte() != 0;
    }

    public static final Parcelable.Creator<AgentModel> CREATOR = new Parcelable.Creator<AgentModel>() {
        @Override
        public AgentModel createFromParcel(Parcel source) {
            return new AgentModel(source);
        }

        @Override
        public AgentModel[] newArray(int size) {
            return new AgentModel[size];
        }
    };

//    @Override
//    public AgentModel clone() {
//        try {
//            return (AgentModel) super.clone();
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AgentModel cloned = (AgentModel)super.clone();
        return cloned;
    }
}
