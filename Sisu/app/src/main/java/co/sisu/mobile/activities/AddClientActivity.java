package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import co.sisu.mobile.R;

/**
 * Created by Brady Groharing on 3/5/2018.
 */

public class AddClientActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        initializeButtons();
    }

    @Override
    public void onClick(View v) {
        Button buyerButton = (Button) findViewById(R.id.buyerButton);
        Button sellerButton= (Button) findViewById(R.id.sellerButton);
        switch (v.getId()) {
            case R.id.buyerButton:
                buyerButton.setTextColor(ContextCompat.getColor(this, R.color.colorCorporateOrange));
                //buyerButton.setBackgroundColor(ContextCompat.getColor(this,R.color.colorCorporateOrange));
                sellerButton.setTextColor(ContextCompat.getColor(this,R.color.colorLightGrey));
                break;
            case R.id.sellerButton:
                buyerButton.setTextColor(ContextCompat.getColor(this,R.color.colorLightGrey));
                sellerButton.setTextColor(ContextCompat.getColor(this,R.color.colorCorporateOrange));
            case R.id.importContactButton:
                Button importContactButton = (Button) findViewById(R.id.importContactButton);
                //do stuff for import
                break;
            default:
                break;
        }
    }

    private void initializeButtons(){

        Button buyerButton= findViewById(R.id.buyerButton);
        buyerButton.setOnClickListener(this);

        Button sellerButton= findViewById(R.id.sellerButton);
        sellerButton.setOnClickListener(this);

        Button importContactButton = findViewById(R.id.importContactButton);
        importContactButton.setOnClickListener(this);
    }
}
