package co.sisu.mobile.system;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import co.sisu.mobile.activities.ParentActivity;

/**
 * Created by bradygroharing on 7/19/18.
 */

public class MySMSObserver extends ContentObserver{

    private String lastSmsId;
    private ParentActivity parentActivity;

    public MySMSObserver(Handler handler, ParentActivity parentActivity) {
        super(handler);
        this.parentActivity = parentActivity;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.e("PLEASE", "INTERCEPT!");
//        Uri uriSMSURI = Uri.parse("content://sms/sent");
//        Cursor cur = parentActivity.getContentResolver().query(uriSMSURI, null, null, null, null);
//        cur.moveToNext();
//        String id = cur.getString(cur.getColumnIndex("_id"));
//        if (smsChecker(id)) {
//            String address = cur.getString(cur.getColumnIndex("address"));
//            Log.e("ADDRESS", address);
//            // Optional: Check for a specific sender
////            if (address.equals(phoneNumber)) {
////                String message = cur.getString(cur.getColumnIndex("body"));
////                // Use message content for desired functionality
////            }
//        }
    }

    // Prevent duplicate results without overlooking legitimate duplicates
    public boolean smsChecker(String smsId) {
        boolean flagSMS = true;

        if (smsId.equals(lastSmsId)) {
            flagSMS = false;
        }
        else {
            lastSmsId = smsId;
        }

        return flagSMS;
    }
}
