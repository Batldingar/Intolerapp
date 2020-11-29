package com.baldware.intolerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdditionActivity extends AppCompatActivity {

    private static String productNameInput;
    private static String productBrandInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addition);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new onClickListener());
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
                    JSONHandler.startUpload(Constants.UPLOAD_URL);
                    MainActivity.loadData();
                    finish();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Name and brand can't be empty", Toast.LENGTH_SHORT).show();
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
