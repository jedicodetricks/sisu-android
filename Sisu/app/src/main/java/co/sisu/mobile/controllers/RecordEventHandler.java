package co.sisu.mobile.controllers;

import co.sisu.mobile.models.Metric;

/**
 * Created by bradygroharing on 4/17/18.
 */

public interface RecordEventHandler {
    void onNumberChanged(Metric metric, int newNum);
}
