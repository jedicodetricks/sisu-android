package co.sisu.mobile.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.sisu.mobile.R;

/**
 * Created by Brady Groharing on 3/5/2018.
 */

public class AddClientActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
    }

    @Override
    public void onClick(View v) {

    }

//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        switch (v.getId()) {
//            case R.id.editName:
//                final EditText nameText = (EditText) findViewById(R.id.editName);
//                if(hasFocus){
//                    nameText.setHint(R.string.app_name);
//                } else {
//                    nameText.setHint("");
//                }
//                break;
//            case R.id.editEmail:
//                final EditText emailText = (EditText) findViewById(R.id.editEmail);
//                if(hasFocus){
//                    emailText.setHint(R.string.app_name);
//                } else {
//                    emailText.setHint("");
//                }
//                break;
//            case R.id.editPhone:
//                final EditText phoneText = (EditText) findViewById(R.id.editPhone);
//                if(hasFocus){
//                    phoneText.setHint(R.string.app_name);
//                } else {
//                    phoneText.setHint("");
//                }
//                break;
//            case R.id.editTransAmount:
//                final EditText transAmountText = (EditText) findViewById(R.id.editTransAmount);
//                if(hasFocus){
//                    transAmountText.setHint(R.string.app_name);
//                } else {
//                    transAmountText.setHint("");
//                }
//                break;
//            case R.id.editCommission:
//                final EditText commissionText = (EditText) findViewById(R.id.editCommission);
//                if(hasFocus){
//                    commissionText.setHint(R.string.app_name);
//                } else {
//                    commissionText.setHint("");
//                    commissionText.setHighlightColor(ContextCompat.getColor(this, R.color.colorCorporateGrey));
//                }
//                break;
//            default:
//                break;
//        }
//    }
}
