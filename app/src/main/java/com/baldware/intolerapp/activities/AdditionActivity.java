package com.baldware.intolerapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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

import com.baldware.intolerapp.customTools.BitmapHandler;
import com.baldware.intolerapp.customTools.Constants;
import com.baldware.intolerapp.R;
import com.baldware.intolerapp.json.ImageUploadRunnable;
import com.baldware.intolerapp.json.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class AdditionActivity extends AppCompatActivity {

    private static String productNameInput;
    private static String productBrandInput;
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

    private class onClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            productNameInput = ((EditText)findViewById(R.id.productName)).getText().toString();
            productBrandInput = ((EditText)findViewById(R.id.productBrand)).getText().toString();

            if (!productNameInput.equals("") && !productBrandInput.equals("")) {
                if(bitmap != null) {
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
                        JSONHandler.startImageUpload(BitmapHandler.createUploadable(bitmap));
                        JSONHandler.startUpload();
                        MainActivity.loadData();
                        MainActivity.showProduct(AdditionActivity.this, productNameInput, productBrandInput);
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

    private class onImageClickListener implements View.OnClickListener{
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
                switch(itemID) {
                    case 0: // take picture
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            takePicture();
                        } else {
                            ActivityCompat.requestPermissions(AdditionActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                        }
                        break;
                    case 1: // choose from gallery
                        Intent choosePicture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(choosePicture , 1);
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

    // After calling startActivityForResults in takePicture()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0: // take photo
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        bitmap = selectedImage;
                        imageView.setImageBitmap(selectedImage);
                    }
                    break;
                case 1: // choose from gallery
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    // After calling ActivityCompat.requestPermissions in case 0 of showPictureOptions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(AdditionActivity.this, "Camera permissions denied: Unable to take a picture.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public static String getProductName() {
        return productNameInput;
    }

    public static String getProductBrand() {
        return productBrandInput;
    }
}
