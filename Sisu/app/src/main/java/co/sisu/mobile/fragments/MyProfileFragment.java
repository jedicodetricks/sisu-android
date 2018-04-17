package co.sisu.mobile.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import co.sisu.mobile.R;
import co.sisu.mobile.activities.AddClientActivity;
import co.sisu.mobile.activities.ParentActivity;
import co.sisu.mobile.models.AgentModel;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private final int SELECT_PHOTO = 1;
    ImageView profileImage;
    ParentActivity parentActivity;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    AgentModel agent;

    EditText username, firstName, lastName, phone, password;

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
        if(agent != null) {
            fillInAgentInfo();
        }
    }

    private void initFields() {
        username = getView().findViewById(R.id.profileUsername);
        firstName = getView().findViewById(R.id.profileFirstName);
        lastName = getView().findViewById(R.id.profileLastName);
        phone = getView().findViewById(R.id.profilePhone);
        password = getView().findViewById(R.id.profilePassword);

    }

    private void fillInAgentInfo() {
        username.setText(agent.getEmail());
        firstName.setText(agent.getFirst_name());
        lastName.setText(agent.getLast_name());
        phone.setText(agent.getMobile_phone());
        password.setText("***********");
    }

    private void initButtons() {
        profileImage = getView().findViewById(R.id.profileImage);
        profileImage.setOnClickListener(this);
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
                    Toast.makeText(getContext(), "Profile Image", Toast.LENGTH_LONG).show();
                }

                break;
            default:
                break;
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

//                        final Uri imageUri = imageReturnedIntent.getData();
//                        getCameraPhotoOrientation(getContext(), imageUri);
                    final InputStream imageStream;
                    try {

                        imageStream = getContext().getContentResolver().openInputStream(selectedImage);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(rotateImage);
                        final Bitmap bitImage = BitmapFactory.decodeStream(imageStream);
                        Bitmap rotated = Bitmap.createBitmap(bitImage, 0, 0, bitImage.getWidth(), bitImage.getHeight(),
                                matrix, true);
                        
                        profileImage.setImageBitmap(rotated);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

//

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

            Log.e("RotateImage", "Exif orientation: " + orientation);
            Log.e("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

}
