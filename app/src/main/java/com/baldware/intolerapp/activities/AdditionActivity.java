package com.baldware.intolerapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new onClickListener());

        ImageView imageView = findViewById(R.id.addition_image_view);
        imageView.setOnClickListener(new onImageClickListener());
    }

    private class onClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            productNameInput = ((EditText)findViewById(R.id.productName)).getText().toString();
            productBrandInput = ((EditText)findViewById(R.id.productBrand)).getText().toString();

            if (!productNameInput.equals("") && !productBrandInput.equals("")) {
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
                    JSONHandler.startImageUpload(BitmapHandler.createUploadable(R.drawable.lasagne));
                    JSONHandler.startUpload();
                    MainActivity.loadData();
                    finish();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Name and brand can't be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class onImageClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            selectImage();
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take photo", "Choose from gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select a product picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int itemID) {
                switch(itemID) {
                    case 0: // take photo
                        Intent takePhoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePhoto, 0);
                        break;
                    case 1: // choose from gallery
                        Intent choosePhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(choosePhoto , 1);
                        break;
                    case 2: // cancel
                        dialog.dismiss();
                        break;
                }
            }
        }).show();
    }



    public static String getProductName() {
        return productNameInput;
    }

    public static String getProductBrand() {
        return productBrandInput;
    }
}
