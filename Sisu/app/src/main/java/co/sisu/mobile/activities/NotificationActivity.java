package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.sisu.mobile.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        String title = getIntent().getStringExtra("title");
        String body = getIntent().getStringExtra("body");
        TextView titleView = findViewById(R.id.notificationTitle);
        TextView bodyView = findViewById(R.id.notificationBody);


        Pattern phonePattern = Patterns.PHONE;
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        final String URL_REGEX = "((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
        Pattern urlPattern = Pattern.compile(URL_REGEX);

        Matcher phoneMatcher = phonePattern.matcher(body);
        Matcher emailMatcher = emailPattern.matcher(body);
        Matcher urlMatcher = urlPattern.matcher(body);

        boolean phoneVal = phoneMatcher.find();
        boolean emailVal = emailMatcher.find();
        boolean urlVal = urlMatcher.find();
        String phoneNumber;
        String email ;
        String url ;

        if(phoneVal) {
            phoneNumber = phoneMatcher.group(0);
            body = body.replace(phoneNumber, "<a href=\"tel:" + phoneNumber + "\">" + phoneNumber + "</a>");
        }

        if(emailVal) {
            email = emailMatcher.group(0);
            body = body.replace(email, "<a href=\"mailto:" + email + "\">" + email + "</a>");

        }

        if(urlVal) {
            url = urlMatcher.group(0);
            if(!url.contains("https://") && !url.contains("http://")) {
                url = "https://" + url;
            }
            body = body.replace(url, "<a href=\"" + url + "\">" + url + "</a>");
        }


        if(!title.equals("")) {
            titleView.setText(title);
        }
        if(!body.equals("")) {
            bodyView.setText(Html.fromHtml(body));
            bodyView.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }

}