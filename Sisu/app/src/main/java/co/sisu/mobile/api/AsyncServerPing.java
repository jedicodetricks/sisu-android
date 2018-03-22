package co.sisu.mobile.api;

import android.os.AsyncTask;

/**
 * Created by Brady Groharing on 3/21/2018.
 */

public class AsyncServerPing extends AsyncTask<Boolean, Void, Boolean> {
    protected Boolean doInBackground(Boolean... params) {
        Boolean bool = params[0];
        // do stuff with Editable
        return true;
    }

    protected void onPostExecute(Integer result) {
        // here you have the result
    }
}
