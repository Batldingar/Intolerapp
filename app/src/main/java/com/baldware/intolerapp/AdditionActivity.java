package com.baldware.intolerapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
            boolean productExists = false;

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(JSONHandler.getJson());

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if(jsonObject.getString("name").equals(productNameInput.getText().toString())) {
                        if(jsonObject.getString("brand").equals(productBrandInput.getText().toString())) {
                            productExists = true;
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(productExists) {
                Toast.makeText(getApplicationContext(), "Product already exists", Toast.LENGTH_SHORT).show();
            }else {
                JSONHandler.startUpload("http://intolerapp.com/austria_upload_service.php");
            }

            finish();
        }
    }

    public static String getProductName() {
        return productNameInput.getText().toString();
    }

    public static String getProductBrand() {
        return productBrandInput.getText().toString();
    }
}
