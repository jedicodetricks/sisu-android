package co.sisu.mobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.AgentGoalsObject;
import co.sisu.mobile.models.AgentModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalSetupFragment extends Fragment {

    EditText desiredIncome, trackingReasons, contacts, bAppointments, sAppointments, bSigned, sSigned, bContract, sContract, bClosed, sClosed;
    ParentActivity parentActivity;

    public GoalSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ScrollView contentView = (ScrollView) inflater.inflate(R.layout.fragment_setup, container, false);
        ScrollView.LayoutParams viewLayout = new ScrollView.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        initFields();
        setupFieldsWithGoalData();
    }

    private void setupFieldsWithGoalData() {
        AgentModel agent = parentActivity.getAgentInfo();
        desiredIncome.setText(agent.getDesired_income());
        trackingReasons.setText(agent.getVision_statement());
        for(AgentGoalsObject go : agent.getAgentGoalsObject()) {
            Log.e("Goals Setup", go.getName() + " " + go.getValue());
            switch (go.getName()) {
                case "Contacts":
                    contacts.setText(go.getValue());
                    break;
                case "Sellers Closed":
                    sClosed.setText(go.getValue());
                    break;
                case "Sellers Under Contract":
                    sContract.setText(go.getValue());
                    break;
                case "Seller Appointments":
                    sAppointments.setText(go.getValue());
                    break;
                case "Sellers Signed":
                    sSigned.setText(go.getValue());
                    break;
                case "Buyers Signed":
                    bSigned.setText(go.getValue());
                    break;
                case "Buyer Appointments":
                    bAppointments.setText(go.getValue());
                    break;
                case "Buyers Under Contract":
                    bContract.setText(go.getValue());
                    break;
                case "Buyers Closed":
                    bClosed.setText(go.getValue());
                    break;
            }
        }

    }

    private void initFields() {
        desiredIncome = getView().findViewById(R.id.desiredIncome);
        trackingReasons = getView().findViewById(R.id.goalsReason);
        contacts = getView().findViewById(R.id.contacts);
        bAppointments = getView().findViewById(R.id.buyerAppts);
        sAppointments = getView().findViewById(R.id.sellerAppts);
        bSigned = getView().findViewById(R.id.signedBuyers);
        sSigned = getView().findViewById(R.id.signedSellers);
        bContract = getView().findViewById(R.id.buyersUnderContract);
        sContract = getView().findViewById(R.id.sellersUnderContract);
        bClosed = getView().findViewById(R.id.buyersClosed);
        sClosed = getView().findViewById(R.id.sellersClosed);
    }



}
