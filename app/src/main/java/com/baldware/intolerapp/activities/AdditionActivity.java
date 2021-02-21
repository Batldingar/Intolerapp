package com.baldware.intolerapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.baldware.intolerapp.R;
import com.baldware.intolerapp.customTools.BitmapHandler;
import com.baldware.intolerapp.customTools.Constants;
import com.baldware.intolerapp.json.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AdditionActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new onClickListener());

        imageView = findViewById(R.id.addition_image_view);
        imageView.setOnClickListener(new onImageClickListener());
    }

    private class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String productNameInput = ((EditText) findViewById(R.id.productName)).getText().toString();
            String productBrandInput = ((EditText) findViewById(R.id.productBrand)).getText().toString();

            if (!productNameInput.equals("") && !productBrandInput.equals("")) {
                if (bitmap != null) {
                    boolean productExists = false;

                    JSONArray jsonArray;
                    try {
                        jsonArray = new JSONArray(JSONHandler.getJson());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if (jsonObject.getString("name").equals(productNameInput)) {
                                if (jsonObject.getString("brand").equals(productBrandInput)) {
                                    productExists = true;
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (productExists) {
                        Toast.makeText(getApplicationContext(), "Product already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONHandler.startImageUpload(BitmapHandler.createUploadable(bitmap), productNameInput, productBrandInput);

                        Bundle data = new Bundle();
                        data.putString("productName", productNameInput);
                        data.putString("productBrand", productBrandInput);

                        Intent intent = new Intent();
                        intent.putExtras(data);
                        setResult(RESULT_OK, intent);

                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "There has to be a product picture", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Name and brand can't be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class onImageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showPictureOptions();
        }
    }

    private void showPictureOptions() {
        final CharSequence[] options = {"Take picture", "Choose from gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select a product picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int itemID) {
                switch (itemID) {
                    case 0: // take picture
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            takePicture();
                        } else {
                            ActivityCompat.requestPermissions(AdditionActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                        }
                        break;
                    case 1: // choose from gallery
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            selectPicture();
                        } else {
                            ActivityCompat.requestPermissions(AdditionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                        break;
                    case 2: // cancel
                        dialog.dismiss();
                        break;
                }
            }
        }).show();
    }

    private void takePicture() {
        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    private void selectPicture() {
        Intent choosePicture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePicture, 1);
    }

    // After calling startActivityForResults in takePicture()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0: // take picture
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap scaledImage = Bitmap.createScaledBitmap((Bitmap) data.getExtras().get("data"), Constants.PICTURE_WIDTH, Constants.PICTURE_HEIGHT, false);

                        bitmap = scaledImage;
                        imageView.setImageBitmap(scaledImage);
                    }
                    break;
                case 1: // choose from gallery
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                cursor.close();

                                ParcelFileDescriptor parcelFileDescriptor = null;
                                try {
                                    parcelFileDescriptor = getApplicationContext().getContentResolver().openFileDescriptor(selectedImage, "r", null);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                if (parcelFileDescriptor.getStatSize() > Constants.MAX_PICTURE_SIZE) {
                                    Toast.makeText(AdditionActivity.this, "File can't be bigger than 12MB", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                                File cacheDir = getApplicationContext().getCacheDir(); // cacheDir stores application specific files temporarily (android may delete them to recover space)
                                File file = null;
                                try {
                                    file = File.createTempFile("picture", ".tmp", cacheDir);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // Read from the photo in the gallery
                                BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(parcelFileDescriptor.getFileDescriptor()));

                                // Write in our custom file (so we don't have to work with the real one)
                                BufferedOutputStream bufferedOutputStream = null;
                                try {
                                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }

                                // Write date from the OutputStream into the reading InputStream
                                try {
                                    copyContent(bufferedInputStream, bufferedOutputStream);
                                    // file should now hold a copy of the selected gallery picture
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {
                                    if (bufferedInputStream != null) {
                                        bufferedInputStream.close();
                                    }
                                    if (bufferedOutputStream != null) {
                                        bufferedOutputStream.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Bitmap scaledImage = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getPath()), Constants.PICTURE_WIDTH, Constants.PICTURE_HEIGHT, false);

                                bitmap = fixOrientation(scaledImage, file.getAbsolutePath()); // returns scaledImage if nothing has changed
                                imageView.setImageBitmap(bitmap);

                                file.delete();
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void copyContent(BufferedInputStream dst, BufferedOutputStream src) throws Exception {
        try {
            WriteRunnable writeRunnable = new WriteRunnable(src);
            Thread thread1 = new Thread(writeRunnable);
            thread1.start();

            int n;
            boolean didProcess;

            while ((n = dst.read()) != -1) {
                didProcess = false;

                while (!didProcess) {
                    if (!writeRunnable.hasNewByte) {
                        writeRunnable.setNextByte(n);
                        didProcess = true;
                    }
                }
            }

            writeRunnable.stop();
        } finally {
            if (dst != null) {
                dst.close();
            }
            if (src != null) {
                src.close();
            }
        }
    }

    private static class WriteRunnable implements Runnable {

        private final OutputStream src;
        private int nextByte;
        private boolean hasNewByte;
        private boolean isRunning;

        public WriteRunnable(OutputStream src) {
            this.src = src;

            hasNewByte = false;
            isRunning = true;
        }

        public void setNextByte(int nextByte) {
            this.nextByte = nextByte;

            hasNewByte = true;
        }

        public void stop() {
            this.isRunning = false;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (hasNewByte) {
                    try {
                        src.write(nextByte);
                        hasNewByte = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // After calling ActivityCompat.requestPermissions in case 0 of showPictureOptions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(AdditionActivity.this, "Camera permissions denied: Unable to take a picture.", Toast.LENGTH_SHORT).show();
                }
            }
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPicture();
                } else {
                    Toast.makeText(AdditionActivity.this, "Gallery permissions denied: Unable to select a picture.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return;
    }

    public static Bitmap fixOrientation(Bitmap bitmap, String image_absolute_path) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(image_absolute_path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
