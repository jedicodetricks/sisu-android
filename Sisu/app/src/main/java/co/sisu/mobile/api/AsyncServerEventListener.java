package co.sisu.mobile.api;

/**
 * Created by bradygroharing on 3/22/18.
 */

public interface AsyncServerEventListener {
    void onEventCompleted(Object returnObject, String asyncReturnType);
    void onEventFailed(Object o, String s);
}
