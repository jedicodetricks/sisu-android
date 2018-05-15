package co.sisu.mobile.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncAgent;
import co.sisu.mobile.api.AsyncProfileImage;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.api.AsyncUpdateProfile;
import co.sisu.mobile.api.AsyncUpdateProfileImage;
import co.sisu.mobile.models.AgentModel;
import co.sisu.mobile.models.AsyncAgentJsonObject;
import co.sisu.mobile.models.AsyncProfileImageJsonObject;
import co.sisu.mobile.models.AsyncUpdateProfileImageJsonObject;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener, AsyncServerEventListener, View.OnFocusChangeListener {

    private final int SELECT_PHOTO = 1;
    ImageView profileImage;
    ParentActivity parentActivity;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    AgentModel agent;
    private boolean imageChanged;
    EditText username, firstName, lastName, phone, password;
    private String imageData, imageFormat;
    //ProfileObject currentProfile;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ConstraintLayout contentView = (ConstraintLayout) inflater.inflate(R.layout.fragment_my_profile, container, false);
        ConstraintLayout.LayoutParams viewLayout = new ConstraintLayout.LayoutParams(container.getWidth(), container.getHeight());
        contentView.setLayoutParams(viewLayout);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parentActivity = (ParentActivity) getActivity();
        agent = parentActivity.getAgentInfo();
        initButtons();
        initFields();
        new AsyncAgent(this, agent.getAgent_id(), parentActivity.getJwtObject()).execute();
        new AsyncProfileImage(this, parentActivity.getAgentInfo().getAgent_id(), parentActivity.getJwtObject()).execute();
    }

    private void initFields() {
        username = getView().findViewById(R.id.profileUsername);
        username.setOnFocusChangeListener(this);
        firstName = getView().findViewById(R.id.profileFirstName);
        firstName.setOnFocusChangeListener(this);
        lastName = getView().findViewById(R.id.profileLastName);
        lastName.setOnFocusChangeListener(this);
        phone = getView().findViewById(R.id.profilePhone);
        phone.setOnFocusChangeListener(this);
        password = getView().findViewById(R.id.profilePassword);
        password.setOnFocusChangeListener(this);
        password.setTransformationMethod(new PasswordTransformationMethod());//this is needed to set the input type to Password. if we do it in the xml we lose styling.

    }

    private void fillInAgentInfo() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                username.setText(agent.getEmail());
                firstName.setText(agent.getFirst_name());
                lastName.setText(agent.getLast_name());
                phone.setText(PhoneNumberUtils.formatNumber(agent.getMobile_phone(), Locale.getDefault().getCountry()));
                password.setText("***********");
            }
        });

    }

    private void initButtons() {
        profileImage = getView().findViewById(R.id.profileImage);
        profileImage.setOnClickListener(this);

        TextView save = parentActivity.findViewById(R.id.saveButton);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.profileImage:
                if (ContextCompat.checkSelfPermission(parentActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(parentActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(parentActivity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_STORAGE);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                else {
                    launchImageSelector();
                }

                break;
            case R.id.saveButton:
                if(verifyInputs()) {
                    saveProfile();    
                }
//                parentActivity.stackReplaceFragment(MoreFragment.class);
//                parentActivity.swapToBacktionBar("My Profile", null);
                break;
            default:
                break;
        }
    }

    private boolean verifyInputs() {
        boolean isVerified = true;
        if(firstName.getText().toString().equals("")) {
            parentActivity.showToast("First Name is required");
            isVerified = false;
        }
        else if(lastName.getText().toString().equals("")) {
            parentActivity.showToast("Last Name is required");
            isVerified = false;
        }
        else if(phone.getText().toString().equals("")) {
            parentActivity.showToast("Phone is required");
            isVerified = false;
        }
        else if(password.getText().toString().equals("")) {
            parentActivity.showToast("Password is required");
            isVerified = false;
        }
        return isVerified;
    }

    private void saveProfile() {
        if(imageChanged) {
            AsyncUpdateProfileImageJsonObject asyncUpdateProfileImageJsonObject = new AsyncUpdateProfileImageJsonObject(imageData, parentActivity.getAgentInfo().getAgent_id(), "3", imageFormat);
            new AsyncUpdateProfileImage(this, asyncUpdateProfileImageJsonObject, parentActivity.getJwtObject()).execute();
        }

        HashMap<String, String> changedFields = new HashMap<>();

        if(!username.getText().toString().equals(agent.getEmail())) {
            changedFields.put("email", username.getText().toString());
        }
        if(!firstName.getText().toString().equals(agent.getFirst_name())) {
            changedFields.put("first_name", firstName.getText().toString());
        }
        if(!lastName.getText().toString().equals(agent.getLast_name())) {
            changedFields.put("last_name", lastName.getText().toString());
        }
        if(!phone.getText().toString().equals(agent.getMobile_phone())) {
            changedFields.put("mobile_phone", phone.getText().toString());
        }
        if(!password.getText().toString().equals("***********")) {
            changedFields.put("password", password.getText().toString());
        }

        if(changedFields.size() > 0) {
            new AsyncUpdateProfile(this, parentActivity.getAgentInfo().getAgent_id(), changedFields, parentActivity.getJwtObject()).execute();
        }
        else {
            parentActivity.showToast("You haven't updated anything.");
        }

    }

    private void launchImageSelector() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    int rotateImage = getCameraPhotoOrientation(parentActivity, selectedImage, filePath);

                    final InputStream imageStream;
                    try {
                        ContentResolver cR = getContext().getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String type = mime.getExtensionFromMimeType(cR.getType(selectedImage));
                        Log.e("IMAGE TYPE", type);

                        imageStream = getContext().getContentResolver().openInputStream(selectedImage);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(rotateImage);
                        final Bitmap bitImage = BitmapFactory.decodeStream(imageStream);
                        Bitmap rotatedImage = Bitmap.createBitmap(bitImage, 0, 0, bitImage.getWidth(), bitImage.getHeight(),
                                matrix, true);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        if(type.equals("jpeg")) {
                            imageFormat = "2";
                            rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        }
                        else if(type.equals("png")) {
                            imageFormat = "1";
                            rotatedImage.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                        }
                        else {
                            imageFormat = "0";
                            rotatedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        }

                        byte[] b = baos.toByteArray();
                        imageData = Base64.encodeToString(b, Base64.DEFAULT);

                        profileImage.setImageBitmap(rotatedImage);
                        imageChanged = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

//            Log.e("RotateImage", "Exif orientation: " + orientation);
//            Log.e("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private void decodeBase64Image(String data) {
        if(data != null) {
            byte[] decodeValue = Base64.decode(data, Base64.DEFAULT);
            Bitmap bmp=BitmapFactory.decodeByteArray(decodeValue,0,decodeValue.length);
            profileImage.setImageBitmap(bmp);
        }
    }
    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Profile Image")) {
            final AsyncProfileImageJsonObject profileObject = (AsyncProfileImageJsonObject) returnObject;
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    decodeBase64Image(profileObject.getData());
                }
            });
        }
        else if(asyncReturnType.equals("Update Profile")) {
            parentActivity.showToast("Your profile has been updated");
            parentActivity.stackReplaceFragment(MoreFragment.class);
            parentActivity.swapToTitleBar("More");
        }
        else if(asyncReturnType.equals("Get Agent")) {
            AsyncAgentJsonObject agentJsonObject = (AsyncAgentJsonObject) returnObject;
            AgentModel agentModel = agentJsonObject.getAgent();
            parentActivity.setAgent(agentModel);
            agent = parentActivity.getAgentInfo();
            fillInAgentInfo();
        }

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
        //TODO: We'll need a counter to make sure this just doesn't loop infinitely
//        if(asyncReturnType.equals("Profile Image")) {
//            new AsyncProfileImage(this, parentActivity.getAgentInfo().getAgent_id()).execute();
//        }
//        else if(asyncReturnType.equals("Update Profile")) {
//            parentActivity.showToast("Your profile has been updated");
//            parentActivity.stackReplaceFragment(MoreFragment.class);
//            parentActivity.swapToTitleBar("More");
//        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus) {
            hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)parentActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
