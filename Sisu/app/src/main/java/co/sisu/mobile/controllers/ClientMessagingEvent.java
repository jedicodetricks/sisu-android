package co.sisu.mobile.controllers;

import co.sisu.mobile.models.ClientObject;

/**
 * Created by Brady Groharing on 4/21/2018.
 */

public interface ClientMessagingEvent {
    void onPhoneClicked(String number, ClientObject client_id);
    void onTextClicked(String number, ClientObject client_id);
    void onEmailClicked(String email, ClientObject client_id);
    void onItemClicked(ClientObject clientObject);
}
