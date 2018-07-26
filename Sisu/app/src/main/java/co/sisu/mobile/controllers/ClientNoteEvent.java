package co.sisu.mobile.controllers;

import co.sisu.mobile.models.NotesObject;

/**
 * Created by bradygroharing on 7/18/18.
 */

public interface ClientNoteEvent {
    void editButtonClicked(NotesObject note);
    void deleteButtonClicked(NotesObject note);
}
