package co.sisu.mobile.api;

/**
 * Created by bradygroharing on 3/22/18.
 */

public interface AsyncPingEventListener {
    void onEventCompleted();
    void onEventFailed();
}
