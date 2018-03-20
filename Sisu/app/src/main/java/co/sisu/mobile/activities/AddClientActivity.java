package co.sisu.mobile.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Iterator;
import java.util.Set;

import co.sisu.mobile.R;

/**
 * Created by Brady Groharing on 3/5/2018.
 */

public class AddClientActivity extends AppCompatActivity implements View.OnClickListener {

    public final int PICK_CONTACT = 2015;

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
                importContactButton.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(i, PICK_CONTACT);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {

            Uri contactUri = data.getData();
            Log.d("contact uri", contactUri.toString());
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);

            cursor.moveToFirst();
            int counter = 0;
            while (counter < 36) {
                if(cursor.getString(counter) != null) {
                    Log.d("cursor", cursor.getString(counter));
                }
                counter++;
            }
            int number = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
            int email = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
            Log.d("column numbers", number + ":" + name + ":" + email);
//            (new normalizePhoneNumberTask()).execute(cursor.getString(column));
            Log.d("phone number", cursor.getString(number));
            Log.d("Name", cursor.getString(name));
            Log.d("Email", cursor.getString(email));

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
