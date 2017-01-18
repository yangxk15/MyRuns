package edu.dartmouth.cs.xiankai_yang_myruns.controller;

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
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;

import edu.dartmouth.cs.xiankai_yang_myruns.R;
import edu.dartmouth.cs.xiankai_yang_myruns.model.Profile;

public class ProfileActivity extends AppCompatActivity {

    public static final String DOMAIN = "edu.dartmouth.cs";
    public static final String AUTHORITY = DOMAIN + ".xiankai_yang_myruns.fileprovider";

    private static final String TAG = "ProfileActivity";
    private static final String REF = "ProfileRef";
    private static final String URI_INSTANCE_STATE_KEY = "SavedUri";
    private static final CharSequence[] PHOTO_OPTIONS = new CharSequence[]{"Take Photo", "Choose from Album", "Cancel"};

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

        String profileString = this.getSharedPreferences(DOMAIN, MODE_PRIVATE).getString(REF, null);

        if (profileString != null) {
            Profile profile = new Gson().fromJson(profileString, Profile.class);
            ((TextView) findViewById(R.id.profile_name)).setText(profile.getProfile_name());
            ((TextView) findViewById(R.id.profile_email)).setText(profile.getProfile_email());
            ((TextView) findViewById(R.id.profile_phone)).setText(profile.getProfile_phone());
            ((RadioGroup) findViewById(R.id.profile_gender)).check(profile.getProfile_gender());
            ((TextView) findViewById(R.id.profile_class)).setText(profile.getProfile_class());
            ((TextView) findViewById(R.id.profile_major)).setText(profile.getProfile_major());
            if (profile.getProfile_image() != null) {
                ((ImageView) findViewById(R.id.profile_image)).setImageURI(imageUri = Uri.parse(profile.getProfile_image()));
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
                    imageUri = FileProvider.getUriForFile(ProfileActivity.this, AUTHORITY,
                            File.createTempFile("photo", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
                if (PHOTO_OPTIONS[which].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, RequestCode.TAKE_PHOTO.ordinal());
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                    Crop.of(imageUri, imageUri).asSquare().start(this, RequestCode.CROP_PHOTO.ordinal());
                    break;
                case CHOOSE_FROM_ALBUM:
                    Crop.of(data.getData(), imageUri).asSquare().start(this, RequestCode.CROP_PHOTO.ordinal());
                    break;
                case CROP_PHOTO:
                    ((ImageView) findViewById(R.id.profile_image)).setImageURI(imageUri);
                    Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } else {
            Log.d(TAG, "not ok");
        }
    }

    public void onClickSaveProfile(View view) {
        SharedPreferences.Editor editor = this.getSharedPreferences(DOMAIN, MODE_PRIVATE).edit();

        editor.putString(REF,
                new Gson().toJson(new Profile(
                        ((TextView) findViewById(R.id.profile_name)).getText().toString(),
                        ((TextView) findViewById(R.id.profile_email)).getText().toString(),
                        ((TextView) findViewById(R.id.profile_phone)).getText().toString(),
                        ((RadioGroup) findViewById(R.id.profile_gender)).getCheckedRadioButtonId(),
                        ((TextView) findViewById(R.id.profile_class)).getText().toString(),
                        ((TextView) findViewById(R.id.profile_major)).getText().toString(),
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
        outState.putString(URI_INSTANCE_STATE_KEY, imageUri.toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(URI_INSTANCE_STATE_KEY));
            ((ImageView) findViewById(R.id.profile_image)).setImageURI(imageUri);
        }
    }
}
