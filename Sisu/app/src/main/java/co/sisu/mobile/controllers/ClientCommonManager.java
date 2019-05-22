package co.sisu.mobile.controllers;

import android.app.Fragment;
import android.widget.TextView;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;

public class ClientCommonManager {
    private ParentActivity parentActivity;
    private Fragment fragment;
    private ColorSchemeManager colorSchemeManager;
    private TextView pipelineStatus, signedStatus, underContractStatus, closedStatus, archivedStatus, buyer, seller, saveButton,
            appointmentDateTitle, setAppointmentDateTitle, signedDateTitle, underContractDateTitle, settlementDateTitle, dollarSign1, dollarSign2, commissionEquals, gciEquals,
            percentSign1, percentSign2, statusLabel, priorityText, otherAppointmentsTitle;

    public ClientCommonManager(ParentActivity parentActivity, Fragment fragment, ColorSchemeManager colorSchemeManager) {
        this.parentActivity = parentActivity;
        this.fragment = fragment;
        this.colorSchemeManager = colorSchemeManager;
    }

    public void initializeCommonForm() {
        buyer.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.buyer)));
        seller.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.seller)));

        statusLabel.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.status)));
        pipelineStatus.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.pipeline)));
        signedStatus.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.signed)));
        underContractStatus.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.contract)));
        closedStatus.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.closed)));
        archivedStatus.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.archived)));
        appointmentDateTitle.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.appointmentDateTitle)));
        setAppointmentDateTitle.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.appointmentSetDateTitle)));
        signedDateTitle.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.signedDateTitle)));
        underContractDateTitle.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.underContractTitle)));
        settlementDateTitle.setText(parentActivity.localizeLabel(fragment.getResources().getString(R.string.settlementDateTitle)));

        pipelineStatus.setTextColor(colorSchemeManager.getButtonText());
        pipelineStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        signedStatus.setTextColor(colorSchemeManager.getButtonText());
        signedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        underContractStatus.setTextColor(colorSchemeManager.getButtonText());
        underContractStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        closedStatus.setTextColor(colorSchemeManager.getButtonText());
        closedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());
        archivedStatus.setTextColor(colorSchemeManager.getButtonText());
        archivedStatus.setBackgroundColor(colorSchemeManager.getButtonBackground());

        appointmentDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        setAppointmentDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        signedDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        underContractDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());
        settlementDateTitle.setTextColor(colorSchemeManager.getDarkerTextColor());

        dollarSign1.setTextColor(colorSchemeManager.getDarkerTextColor());
        dollarSign2.setTextColor(colorSchemeManager.getDarkerTextColor());
        percentSign1.setTextColor(colorSchemeManager.getDarkerTextColor());
        percentSign2.setTextColor(colorSchemeManager.getDarkerTextColor());

        commissionEquals.setTextColor(colorSchemeManager.getDarkerTextColor());
        gciEquals.setTextColor(colorSchemeManager.getDarkerTextColor());
        statusLabel.setTextColor(colorSchemeManager.getDarkerTextColor());

        pipelineStatus.setTextColor(colorSchemeManager.getButtonText());
        signedStatus.setTextColor(colorSchemeManager.getButtonText());
        underContractStatus.setTextColor(colorSchemeManager.getButtonText());
        closedStatus.setTextColor(colorSchemeManager.getButtonText());
        archivedStatus.setTextColor(colorSchemeManager.getButtonText());
    }
}
