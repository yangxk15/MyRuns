package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import edu.dartmouth.cs.xiankai_yang.myruns.R;
import edu.dartmouth.cs.xiankai_yang.myruns.model.Profile;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String REF = "ProfileRef";
    private static final String UNCROPPED_IMAGE_URI = "uncroppedImageUri";
    private static final String CROPPED_IMAGE_URI = "croppedImageUri";
    private static final String IMAGE_URI = "imageUri";
    private static final CharSequence[] PHOTO_OPTIONS = new CharSequence[]{"Take Photo", "Choose from Album", "Cancel"};

    private Uri uncroppedImageUri = null;
    private Uri croppedImageUri = null;
    private Uri imageUri = null;

    enum RequestCode {
        TAKE_PHOTO,
        CHOOSE_FROM_ALBUM,
        CROP_PHOTO,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String profileString = this.getSharedPreferences(getString(R.string.app_domain), MODE_PRIVATE).getString(REF, null);

        if (profileString != null) {
            Profile profile = new Gson().fromJson(profileString, Profile.class);
            ((EditText) findViewById(R.id.profile_name)).setText(profile.getProfile_name());
            ((EditText) findViewById(R.id.profile_email)).setText(profile.getProfile_email());
            ((EditText) findViewById(R.id.profile_phone)).setText(profile.getProfile_phone());
            ((RadioGroup) findViewById(R.id.profile_gender)).check(profile.getProfile_gender());
            ((EditText) findViewById(R.id.profile_class)).setText(profile.getProfile_class());
            ((EditText) findViewById(R.id.profile_major)).setText(profile.getProfile_major());
            if (profile.getProfile_image() != null) {
                imageUri = Uri.parse(profile.getProfile_image());
                ((ImageView) findViewById(R.id.profile_image)).setImageURI(imageUri);
            }
        }
    }

    public void onClickChangeProfilePhoto(View view) throws IOException {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setItems(PHOTO_OPTIONS, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PHOTO_OPTIONS[which].equals("Cancel")) {
                    return;
                }
                try {
                    uncroppedImageUri = getUriFromFilePrefix("uncropped_");
                    croppedImageUri = getUriFromFilePrefix("cropped_");
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
                if (PHOTO_OPTIONS[which].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uncroppedImageUri);
                    startActivityForResult(intent, RequestCode.TAKE_PHOTO.ordinal());
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RequestCode.CHOOSE_FROM_ALBUM.ordinal());
                }
            }
        }).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (RequestCode.values()[requestCode]) {
                case TAKE_PHOTO:
                    Crop.of(uncroppedImageUri, croppedImageUri).asSquare().start(this, RequestCode.CROP_PHOTO.ordinal());
                    break;
                case CHOOSE_FROM_ALBUM:
                    Crop.of(data.getData(), croppedImageUri).asSquare().start(this, RequestCode.CROP_PHOTO.ordinal());
                    break;
                case CROP_PHOTO:
                    ((ImageView) findViewById(R.id.profile_image)).setImageURI(imageUri = croppedImageUri);
                    Log.d(TAG, "Set the image uri as: " + imageUri.toString());
                    Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } else {
            Log.d(TAG, "Activity result not ok");
        }
    }

    public void onClickSaveProfile(View view) {
        SharedPreferences.Editor editor = this.getSharedPreferences(getString(R.string.app_domain), MODE_PRIVATE).edit();

        editor.putString(REF,
                new Gson().toJson(new Profile(
                        ((EditText) findViewById(R.id.profile_name)).getText().toString(),
                        ((EditText) findViewById(R.id.profile_email)).getText().toString(),
                        ((EditText) findViewById(R.id.profile_phone)).getText().toString(),
                        ((RadioGroup) findViewById(R.id.profile_gender)).getCheckedRadioButtonId(),
                        ((EditText) findViewById(R.id.profile_class)).getText().toString(),
                        ((EditText) findViewById(R.id.profile_major)).getText().toString(),
                        imageUri == null ? null : imageUri.toString()
                )));
        editor.commit();
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onClickCancelProfile(View view) {
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveUri(outState, UNCROPPED_IMAGE_URI, uncroppedImageUri);
        saveUri(outState, CROPPED_IMAGE_URI, croppedImageUri);
        saveUri(outState, IMAGE_URI, imageUri);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String imageUriString = savedInstanceState.getString(IMAGE_URI);
            if (imageUriString != null) {
                uncroppedImageUri = Uri.parse(savedInstanceState.getString(UNCROPPED_IMAGE_URI));
                croppedImageUri = Uri.parse(savedInstanceState.getString(CROPPED_IMAGE_URI));
                imageUri = Uri.parse(imageUriString);
                ((ImageView) findViewById(R.id.profile_image)).setImageURI(imageUri);
            }
        }
    }

    private Uri getUriFromFilePrefix(String prefix) throws IOException {
        return FileProvider.getUriForFile(
                ProfileActivity.this,
                getString(R.string.app_file_provider_authority),
                File.createTempFile(prefix, null, getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        );
    }

    private void saveUri(Bundle outState, String key, Uri uri) {
        if (uri != null) {
            outState.putString(key, uri.toString());
        }
    }
}
