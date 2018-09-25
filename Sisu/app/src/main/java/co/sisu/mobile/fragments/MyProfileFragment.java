package co.sisu.mobile.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.api.AsyncServerEventListener;
import co.sisu.mobile.controllers.ApiManager;
import co.sisu.mobile.controllers.ColorSchemeManager;
import co.sisu.mobile.controllers.DataController;
import co.sisu.mobile.controllers.NavigationManager;
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
    private ImageView profileImage;
    private ProgressBar imageLoader;
    private ParentActivity parentActivity;
    private DataController dataController;
    private NavigationManager navigationManager;
    private ApiManager apiManager;
    private ColorSchemeManager colorSchemeManager;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private AgentModel agent;
    private boolean imageChanged;
    private EditText username, firstName, lastName, phone;
    private TextInputLayout usernameLayout, firstNameLayout, lastNameLayout, phoneLayout;
    private String imageData, imageFormat;
    private Button passwordButton;
    String imageType = "";

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
        navigationManager = parentActivity.getNavigationManager();
        dataController = parentActivity.getDataController();
        apiManager = parentActivity.getApiManager();
        colorSchemeManager = parentActivity.getColorSchemeManager();
        agent = dataController.getAgent();
        imageLoader = view.findViewById(R.id.imageLoader);
        initButtons();
        initFields();
        apiManager.sendAsyncAgent(this, agent.getAgent_id());

        Bitmap profilePic = parentActivity.getBitmapFromMemCache("testImage");
        if(profilePic == null) {
            apiManager.sendAsyncProfileImage(this, dataController.getAgent().getAgent_id());
        }
        else {
            imageLoader.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);
            profileImage.setImageBitmap(profilePic);
        }
        setLabels();
        setColorScheme();
    }

    private void setLabels() {
        usernameLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.username_email_hint)));
        firstNameLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.first_name_hint_non_req)));
        lastNameLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.last_name_hint_non_req)));
        phoneLayout.setHint(parentActivity.localizeLabel(getResources().getString(R.string.phone_hint)));
    }

    private void setColorScheme() {
        username.setTextColor(colorSchemeManager.getDarkerTextColor());

        firstName.setTextColor(colorSchemeManager.getDarkerTextColor());
        lastName.setTextColor(colorSchemeManager.getDarkerTextColor());
        phone.setTextColor(colorSchemeManager.getDarkerTextColor());

        ColorStateList colorStateList = ColorStateList.valueOf(colorSchemeManager.getIconActive());
        username.setBackgroundTintList(colorStateList);
        firstName.setBackgroundTintList(colorStateList);
        lastName.setBackgroundTintList(colorStateList);
        phone.setBackgroundTintList(colorStateList);

        setInputTextLayoutColor(usernameLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(firstNameLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(lastNameLayout, colorSchemeManager.getIconActive());
        setInputTextLayoutColor(phoneLayout, colorSchemeManager.getIconActive());

        passwordButton.setTextColor(colorSchemeManager.getButtonText());
        passwordButton.setBackgroundResource(R.drawable.rounded_button);
        GradientDrawable drawable = (GradientDrawable) passwordButton.getBackground();
        drawable.setColor(colorSchemeManager.getButtonSelected());

        if(colorSchemeManager.getAppBackground() == Color.WHITE) {
            Rect bounds = imageLoader.getIndeterminateDrawable().getBounds();
            imageLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dark));
            imageLoader.getIndeterminateDrawable().setBounds(bounds);
        } else {
            Rect bounds = imageLoader.getIndeterminateDrawable().getBounds();
            imageLoader.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            imageLoader.getIndeterminateDrawable().setBounds(bounds);
        }
    }

    private void setInputTextLayoutColor(TextInputLayout layout, int color) {
        try {
//            layout.setBackgroundColor(colorSchemeManager.getAppBackground());

            Field fDefaultTextColor = TextInputLayout.class.getDeclaredField("mDefaultTextColor");
            fDefaultTextColor.setAccessible(true);
            fDefaultTextColor.set(layout, new ColorStateList(new int[][]{{0}}, new int[]{ color }));

//            Field fDefaultLineColor = TextInputLayout.class.getDeclaredField("")

            Field fFocusedTextColor = TextInputLayout.class.getDeclaredField("mFocusedTextColor");
            fFocusedTextColor.setAccessible(true);
            fFocusedTextColor.set(layout, new ColorStateList(new int[][]{{0}}, new int[]{ color }));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        profileImage.setVisibility(View.INVISIBLE);
        imageLoader.setVisibility(View.VISIBLE);
        usernameLayout = getView().findViewById(R.id.profileUsernameLayout);
        firstNameLayout = getView().findViewById(R.id.profileFirstNameLayout);
        lastNameLayout = getView().findViewById(R.id.profileLastNameLayout);
        phoneLayout = getView().findViewById(R.id.profilePhoneLayout);

    }

    private void fillInAgentInfo() {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                username.setText(agent.getEmail());
                firstName.setText(agent.getFirst_name());
                lastName.setText(agent.getLast_name());
                String agentPhone = agent.getMobile_phone();
                if(agentPhone != null) {
                    phone.setText(PhoneNumberUtils.formatNumber(agentPhone, Locale.getDefault().getCountry()));
                }
            }
        });

    }

    private void initButtons() {
        profileImage = getView().findViewById(R.id.profileImage);
        profileImage.setOnClickListener(this);
        passwordButton = getView().findViewById(R.id.passwordButton);
        passwordButton.setOnClickListener(this);

        TextView save = parentActivity.findViewById(R.id.saveButton);
        if(save != null) {
            save.setOnClickListener(this);
        }
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
                break;
            case R.id.passwordButton:
                navigationManager.stackReplaceFragment(ChangePasswordFragment.class);
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
        return isVerified;
    }

    private void saveProfile() {
        if(imageChanged) {
            AsyncUpdateProfileImageJsonObject asyncUpdateProfileImageJsonObject = new AsyncUpdateProfileImageJsonObject(imageData, dataController.getAgent().getAgent_id(), "3", imageFormat);
            apiManager.sendAsyncUpdateProfileImage(this, dataController.getAgent().getAgent_id(), asyncUpdateProfileImageJsonObject);
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

        if(changedFields.size() > 0) {
            apiManager.sendAsyncUpdateProfile(this, dataController.getAgent().getAgent_id(), changedFields);
        }
        else {
            if(!imageChanged) {
                parentActivity.showToast("You haven't updated anything.");
            }
            else {
                parentActivity.showToast("Saving profile picture...");
                Bitmap bitmap = ((BitmapDrawable)profileImage.getDrawable()).getBitmap();
                parentActivity.addBitmapToMemoryCache("testImage", bitmap);
            }
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

                    ContentResolver cR = getContext().getContentResolver();
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    imageType = mime.getExtensionFromMimeType(cR.getType(selectedImage));
                    Log.e("IMAGE TYPE", imageType);

                    // start cropping activity for pre-acquired image saved on the device
                    CropImage.activity(selectedImage)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .setMinCropResultSize(100,100)
                            .setMaxCropResultSize(600,600)
                            .start(parentActivity, this);

                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = result.getUri();
                    final InputStream imageStream;
                    try {
                        imageStream = getContext().getContentResolver().openInputStream(selectedImage);
                        final Bitmap bitImage = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        if(imageType.equalsIgnoreCase("jpeg")) {
                            imageFormat = "2";
                            bitImage.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        }
                        else if(imageType.equalsIgnoreCase("png")) {
                            imageFormat = "1";
                            bitImage.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                        }
                        else {
                            imageFormat = "1";
                            bitImage.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                        }

                        byte[] b = baos.toByteArray();
                        imageData = Base64.encodeToString(b, Base64.DEFAULT);

                        profileImage.setImageBitmap(bitImage);
                        imageChanged = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
                break;
        }
    }


    private void decodeBase64Image(String data) {
        if(data != null) {
            byte[] decodeValue = Base64.decode(data, Base64.DEFAULT);
            Bitmap bmp=BitmapFactory.decodeByteArray(decodeValue,0,decodeValue.length);
            parentActivity.addBitmapToMemoryCache("testImage", bmp);
            imageLoader.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);
            profileImage.setImageBitmap(bmp);
        } else {
            imageLoader.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onEventCompleted(Object returnObject, String asyncReturnType) {
        if(asyncReturnType.equals("Profile Image")) {
            Log.e("GOT THE PIC", "WOOT");
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
            navigationManager.clearStackReplaceFragment(MoreFragment.class);
//            navigationManager.swapToTitleBar("More");
        }
        else if(asyncReturnType.equals("Get Agent")) {
            AsyncAgentJsonObject agentJsonObject = (AsyncAgentJsonObject) returnObject;
            AgentModel agentModel = agentJsonObject.getAgent();
            dataController.setAgent(agentModel);
            agent = dataController.getAgent();
            fillInAgentInfo();
        }

    }

    @Override
    public void onEventFailed(Object returnObject, String asyncReturnType) {
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
