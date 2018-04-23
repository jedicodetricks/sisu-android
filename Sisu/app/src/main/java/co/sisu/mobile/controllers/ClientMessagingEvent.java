package co.sisu.mobile.controllers;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public interface ClientMessagingEvent {
    void onPhoneClicked(String number);
    void onTextClicked(String number);
    void onEmailClicked(String email);
}
