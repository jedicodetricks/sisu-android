package co.sisu.mobile.api;

import co.sisu.mobile.enums.ApiReturnTypes;

/**
 * Created by bradygroharing on 3/22/18.
 */

public interface AsyncServerEventListener {
    void onEventCompleted(Object returnObject, String asyncReturnType);
    void onEventCompleted(Object returnObject, ApiReturnTypes returnType);
    void onEventFailed(Object returnObject, String asyncReturnType);
    void onEventFailed(Object returnObject, ApiReturnTypes returnType);
}
