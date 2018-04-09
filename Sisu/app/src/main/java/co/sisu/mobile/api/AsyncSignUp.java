package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Brady Groharing on 3/21/2018.
 */

public class AsyncSignUp extends AsyncTask<Void, Void, Void> {
    private AsyncServerEventListener callback;
    String email, phone, firstName, lastName, password;

    public AsyncSignUp (AsyncServerEventListener cb, String email, String phone, String firstName, String lastName, String password) {
        callback = cb;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    //Test: bg@test.com asdf123
    @Override
    protected Void doInBackground(Void... voids) {
        Response response = null;

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"email\":\"" + email +"\", \"first_name\":\"" + firstName + "\", \"last_name\":\"" + lastName + "\", \"password\":\""+ password +"\"}");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                //TODO: I don't think this route is actually correct or it needs some work on the backend.
                .url("http://staging.sisu.co/api/agent/edit-agent/366")
                .post(body)
                .addHeader("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJDbGllbnQtVGltZXN0YW1wIjoiMTUyMDk5OTA5NSIsImlzcyI6InNpc3UtaW9zOjk1YmI5ZDkxLWZlMDctNGZhZi1hYzIzLTIxOTFlMGQ1Y2RlNiIsImlhdCI6MTUyMDk5OTA5NS4xMTQ2OTc5LCJleHAiOjE1Mjg3NzUwOTUuMTE1OTEyLCJUcmFuc2FjdGlvbi1JZCI6IkU5NThEQzAyLThGNjEtNEU5Ny05MEI3LUYyNjZEQ0M1OTdFOSJ9.bFQhBCgnsujtl3PndALtAL8rcqFpm3rn5quqoXak0Hg")
                .addHeader("Client-Timestamp", "1520999095")
                .addHeader("Transaction-Id", "E958DC02-8F61-4E97-90B7-F266DCC597E9")
                .build();
        try {
            response = client.newCall(request).execute();
            Log.d("ASYNC", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response.code() == 200) {
            callback.onEventCompleted(null, "Sign Up");
        }
        else {
            callback.onEventFailed();
        }
        Log.d("ASYNC PING IS", "NULL");
        response.body().close();
        return null;
    }
}
