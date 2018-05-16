package co.sisu.mobile.api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import co.sisu.mobile.models.JWTObject;
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
    JWTObject jwt;

    public AsyncSignUp (AsyncServerEventListener cb, String email, String phone, String firstName, String lastName, String password, JWTObject JwtObject) {
        callback = cb;
        this.email = email;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        jwt = JwtObject;
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
                .url("https://api.sisu.co/api/v1/agent/edit-agent/366")
                .post(body)
                .addHeader("Authorization", jwt.getJwt())
                .addHeader("Client-Timestamp", jwt.getTimestamp())
                .addHeader("Content-Type", "application/json")
                .addHeader("Transaction-Id", jwt.getTransId())
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
            callback.onEventFailed(null, "Server Ping");
        }
        Log.d("ASYNC PING IS", "NULL");
        response.body().close();
        return null;
    }
}
