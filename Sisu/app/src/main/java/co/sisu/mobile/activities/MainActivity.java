package co.sisu.mobile.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.List;

import co.sisu.mobile.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animateProgressBars();
    }


    public void initializeProgressBars(){
        List<Integer> progressBars = new ArrayList<>();
            progressBars.add(R.id.appointmentsProgress);
    }

    public void animateProgressBars(){
        CircularProgressBar circularProgressBar = (CircularProgressBar)findViewById(R.id.appointmentsProgress);
        circularProgressBar.setColor(ContextCompat.getColor(this, R.color.colorMoonBlue));
        circularProgressBar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
        circularProgressBar.setProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        circularProgressBar.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.default_background_stroke_width));
        int animationDuration = 2500; // 2500ms = 2,5s
        circularProgressBar.setProgressWithAnimation(65, animationDuration); // Default duration = 1500ms
    }
}
