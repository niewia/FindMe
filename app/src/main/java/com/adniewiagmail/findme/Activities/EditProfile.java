package com.adniewiagmail.findme.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.adniewiagmail.findme.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by Adaś on 2015-11-29.
 */
public class EditProfile extends AppCompatActivity {
    private static int RESULT_LOAD_IMG = 1;
    private Button changePhotoButton;
    private Button savePhotoButton;
    private Button rotateRightButton;
    private Button rotateLeftButton;
    private ImageView imageProfilePhotoIcon;
    private String imgDecodableString;
    private Bitmap profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        imageProfilePhotoIcon = (ImageView) findViewById(R.id.profilePhotoPreview);
        loadPhoto();
        changePhotoButton = (Button) findViewById(R.id.buttonChangePhoto);
        changePhotoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePhoto();
                    }
                }
        );
        savePhotoButton = (Button) findViewById(R.id.buttonChangePhotoSave);
        savePhotoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePhoto();
                    }
                }
        );
        rotateRightButton = (Button) findViewById(R.id.buttonRotatePhotoRight);
        rotateRightButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rotatePhoto(90);
                    }
                }
        );
        rotateLeftButton = (Button) findViewById(R.id.buttonRotatePhotoLeft);
        rotateLeftButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rotatePhoto(-90);
                    }
                }
        );
    }

    private void loadPhoto() {
        byte[] profilePhotosData = ParseUser.getCurrentUser().getBytes("profile_photo");
        if (profilePhotosData == null || profilePhotosData.length == 0) {
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        options.inScreenDensity = metrics.densityDpi;
        options.inTargetDensity = metrics.densityDpi;
        options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
        profilePhoto = BitmapFactory.decodeByteArray(profilePhotosData, 0, profilePhotosData.length, options);
        imageProfilePhotoIcon.setImageBitmap(profilePhoto);
    }

    private void savePhoto() {
        final ProgressDialog progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setTitle("Aktualizowanie zdjęcia profilowego");
        progressDialog.show();
        final byte[] image = createByteArray(profilePhoto);
        ParseFile file = new ParseFile("profile_photo.png", image);
        // Upload the image into Parse Cloud
        file.saveInBackground();
        // Create a New Class called "ImageUpload" in Parse
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Create a column named "ImageFile" and insert the image
        currentUser.put("profile_photo", image);
        // Create the class and the columns
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                String message;
                if (e == null) {
                    message = getString(R.string.profileImageUpdateSuccessfull);
                } else {
                    Log.d("EDIT_PROFILE", e.getMessage());
                    message = e.getMessage();
                }
                Toast toast = Toast.makeText(EditProfile.this, message,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private byte[] createByteArray(Bitmap source) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        source.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public void rotatePhoto(float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        profilePhoto = Bitmap.createBitmap(profilePhoto, 0, 0, profilePhoto.getWidth(),
                profilePhoto.getHeight(), matrix, true);
        imageProfilePhotoIcon.setImageBitmap(profilePhoto);
    }

    private void changePhoto() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bitmap = BitmapFactory
                        .decodeFile(imgDecodableString);
                profilePhoto = createPhotoIcon(bitmap);
                imageProfilePhotoIcon.setImageBitmap(profilePhoto);
            } else {
                Toast.makeText(this, R.string.profileImageUpdatePhotoNotPicked,
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.errorSomethingWentWrong, Toast.LENGTH_LONG)
                    .show();
        }
    }

    private Bitmap createPhotoIcon(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/10, bitmap.getHeight()/10, true);
        bitmap = cropBitmap(bitmap);
        bitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, true);
        return createIconBitmap(bitmap);
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        if (height == width) {
            //do nothing
        } else if (height > width) {
            int heightDiff = height - width;
            int heightOffset = heightDiff / 2;
            bitmap = Bitmap.createBitmap(bitmap, 0, heightOffset, width, width);
        } else {
            int widthDiff = width - height;
            int widthOffset = widthDiff / 2;
            bitmap = Bitmap.createBitmap(bitmap, widthOffset, 0, height, height);
        }
        return bitmap;
    }

    private Bitmap createIconBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int borderSize = 4;
        int halfBorder = borderSize / 2;
        int radius = Math.min(h / 2, w / 2);
        final Bitmap output = Bitmap.createBitmap(w + borderSize,
                h + borderSize, Bitmap.Config.ARGB_8888);
        Paint p = new Paint();
        p.setAntiAlias(true);
        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);
        c.drawCircle((w / 2) + halfBorder, (h / 2) + halfBorder, radius, p);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(bitmap, halfBorder, halfBorder, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(halfBorder);
        c.drawCircle((w / 2) + halfBorder, (h / 2) + halfBorder, radius, p);
        return output;
    }
}
