package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.sisu.mobile.R;
import co.sisu.mobile.TestComponentFragment;
import co.sisu.mobile.TestFragment;
import co.sisu.mobile.TestFragment2;

/**
 * Created by bradygroharing on 2/26/18.
 */

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);
        getSupportActionBar().setElevation(0);
        TextView pageTitle = findViewById(R.id.action_bar_title);
        pageTitle.setText("More");
        initializeButtons();


//        TestFragment test = new TestFragment();
//        test.setArguments(getIntent().getExtras());
//        getFragmentManager().beginTransaction().add(R.id.your_placeholder, test).commit();

    }

    private void initializeButtons(){
        View view = getSupportActionBar().getCustomView();

        ImageButton homeButton= view.findViewById(R.id.action_bar_home);
        homeButton.setOnClickListener(this);

        ImageView scoreBoardButton = findViewById(R.id.scoreboardView);
        scoreBoardButton.setOnClickListener(this);

        ImageView reportButton = findViewById(R.id.reportView);
        reportButton.setOnClickListener(this);

        ImageView recordButton = findViewById(R.id.recordView);
        recordButton.setOnClickListener(this);

        ImageView leaderBoardButton = findViewById(R.id.leaderBoardView);
        leaderBoardButton.setOnClickListener(this);

        ImageView moreButton = findViewById(R.id.moreView);
        moreButton.setOnClickListener(this);

//        ImageView addButton = findViewById(R.id.addView);
//        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.action_bar_home:
                //do stuff
                showToast("Home Button is clicked");
                break;
            case R.id.scoreboardView:
                //do stuff
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.your_placeholder, new TestComponentFragment());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                showToast("score Button is clicked");
                break;
            case R.id.reportView:
                //do stuff
                // Begin the transaction
                ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.your_placeholder, new TestFragment());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                showToast("report Button is clicked");
                break;
            case R.id.recordView:
                //do stuff
                ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.your_placeholder, new TestFragment2());
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                showToast("record Button is clicked");
                break;
            case R.id.leaderBoardView:
                //do stuff
                showToast("leader Button is clicked");
                break;
            case R.id.moreView:
                //do stuff
                showToast("more Button is clicked");
                break;
            case R.id.addView:
                //do stuff
                //open floating menu
                showToast("Add Button is clicked");
                break;
            default:
                //do stuff
                break;
        }
    }

    private void showToast(CharSequence msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
