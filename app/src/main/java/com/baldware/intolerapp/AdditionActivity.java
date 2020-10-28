package com.baldware.intolerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AdditionActivity extends AppCompatActivity {

    private static EditText productNameInput;
    private static EditText productBrandInput;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        productNameInput = findViewById(R.id.productName);
        productBrandInput = findViewById(R.id.productBrand);
        button = findViewById(R.id.button);
        button.setOnClickListener(new onClickListener());
    }

    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            JSONHandler.startUpload("http://intolerapp.com/austria_upload_service.php");
        }
    }

    public static String getProductName() {
        return productNameInput.getText().toString();
    }

    public static String getProductBrand() {
        return productBrandInput.getText().toString();
    }
}
