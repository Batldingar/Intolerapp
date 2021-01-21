package com.baldware.intolerapp.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.baldware.intolerapp.customTools.BitmapHandler;
import com.baldware.intolerapp.customTools.Constants;
import com.baldware.intolerapp.R;
import com.baldware.intolerapp.customTools.SearchViewListener;
import com.baldware.intolerapp.json.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivity extends AppCompatActivity {

    private static String name;
    private static String brand;

    private RatingBar fructoseRatingBar;
    private RatingBar glucoseRatingBar;
    private RatingBar histamineRatingBar;
    private RatingBar lactoseRatingBar;
    private RatingBar sucroseRatingBar;
    private RatingBar sorbitolRatingBar;

    private TextView fructoseTextView;
    private TextView glucoseTextView;
    private TextView histamineTextView;
    private TextView lactoseTextView;
    private TextView sucroseTextView;
    private TextView sorbitolTextView;

    private static float fructoseRating;
    private static float glucoseRating;
    private static float histamineRating;
    private static float lactoseRating;
    private static float sucroseRating;
    private static float sorbitolRating;

    private boolean buttonInitialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        fructoseRatingBar = findViewById(R.id.rating_bar_fructose);
        glucoseRatingBar = findViewById(R.id.rating_bar_glucose);
        histamineRatingBar = findViewById(R.id.rating_bar_histamine);
        lactoseRatingBar = findViewById(R.id.rating_bar_lactose);
        sucroseRatingBar = findViewById(R.id.rating_bar_sucrose);
        sorbitolRatingBar = findViewById(R.id.rating_bar_sorbitol);

        fructoseTextView = findViewById(R.id.text_view_fructose);
        glucoseTextView = findViewById(R.id.text_view_glucose);
        histamineTextView = findViewById(R.id.text_view_histamine);
        lactoseTextView = findViewById(R.id.text_view_lactose);
        sucroseTextView = findViewById(R.id.text_view_sucrose);
        sorbitolTextView = findViewById(R.id.text_view_sorbitol);

        try {
            JSONObject jsonObject = JSONHandler.getJsonArray().getJSONObject(getIntent().getIntExtra("position", 0));
            name = jsonObject.getString("name");
            brand = jsonObject.getString("brand");

            fructoseTextView.setText(getResources().getString(R.string.fructose_text, jsonObject.getInt("fructoseRatingCount")));
            glucoseTextView.setText(getResources().getString(R.string.glucose_text, jsonObject.getInt("glucoseRatingCount")));
            histamineTextView.setText(getResources().getString(R.string.histamine_text, jsonObject.getInt("histamineRatingCount")));
            lactoseTextView.setText(getResources().getString(R.string.lactose_text, jsonObject.getInt("lactoseRatingCount")));
            sucroseTextView.setText(getResources().getString(R.string.sucrose_text, jsonObject.getInt("sucroseRatingCount")));
            sorbitolTextView.setText(getResources().getString(R.string.sorbitol_text, jsonObject.getInt("sorbitolRatingCount")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setTitle(name + " - " + brand);

        fructoseRatingBar.setOnRatingBarChangeListener(new onRatingBarChangeListener());
        glucoseRatingBar.setOnRatingBarChangeListener(new onRatingBarChangeListener());
        histamineRatingBar.setOnRatingBarChangeListener(new onRatingBarChangeListener());
        lactoseRatingBar.setOnRatingBarChangeListener(new onRatingBarChangeListener());
        sucroseRatingBar.setOnRatingBarChangeListener(new onRatingBarChangeListener());
        sorbitolRatingBar.setOnRatingBarChangeListener(new onRatingBarChangeListener());

        fructoseRatingBar.setTag(0);
        glucoseRatingBar.setTag(1);
        histamineRatingBar.setTag(2);
        lactoseRatingBar.setTag(3);
        sucroseRatingBar.setTag(4);
        sorbitolRatingBar.setTag(5);

        fructoseRating = -1;
        glucoseRating = -1;
        histamineRating = -1;
        lactoseRating = -1;
        sucroseRating = -1;
        sorbitolRating = -1;

        try {
            loadIntoRatingBars();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        buttonInitialized = false;

        // Fill the imageView
        ImageView imageView = findViewById(R.id.product_image_view);
        imageView.setOnClickListener(new onImageClickListener());

        JSONHandler.startImageDownload(name, brand);

        if(JSONHandler.getImage() != null) {
            imageView.setImageBitmap(BitmapHandler.createShowable(JSONHandler.getImage()));
        }
    }

    private void loadIntoRatingBars() throws JSONException {
        JSONObject jsonObject = JSONHandler.getJsonArray().getJSONObject(getIntent().getIntExtra("position", 0));
        
        fructoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("fructoseRating")));
        glucoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("glucoseRating")));
        histamineRatingBar.setRating(Float.parseFloat(jsonObject.getString("histamineRating")));
        lactoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("lactoseRating")));
        sucroseRatingBar.setRating(Float.parseFloat(jsonObject.getString("sucroseRating")));
        sorbitolRatingBar.setRating(Float.parseFloat(jsonObject.getString("sorbitolRating")));

        setColor(fructoseRatingBar);
        setColor(glucoseRatingBar);
        setColor(histamineRatingBar);
        setColor(lactoseRatingBar);
        setColor(sucroseRatingBar);
        setColor(sorbitolRatingBar);
    }

    private void setColor(RatingBar ratingBar) {
            if(ratingBar.getRating()<=1.0) {
                ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.starsRed)));
            } else if(ratingBar.getRating()<=2.0) {
                ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.starsOrange)));
            } else if(ratingBar.getRating()<=3.0) {
                ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.starsYellow)));
            } else if(ratingBar.getRating()<=4.0) {
                ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.starsLime)));
            } else {
                ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.starsGreen)));
            }
    }

    private class onRatingBarChangeListener implements RatingBar.OnRatingBarChangeListener {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            setColor(ratingBar);

            if(fromUser) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.starsBlue)));
                }

                // Add the button
                if (!buttonInitialized) {
                    RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    layoutParams.addRule(RelativeLayout.BELOW, sorbitolRatingBar.getId());

                    Button button = new Button(getApplicationContext());
                    button.setText(R.string.rating_button_text);
                    button.setOnClickListener(new onClickListener());

                    relativeLayout.addView(button, layoutParams);

                    buttonInitialized = true;
                }

                // Store the local values
                switch ((int) ratingBar.getTag()) {
                    case 0:
                        fructoseRating = fructoseRatingBar.getRating();
                        fructoseTextView.setText(R.string.fructose_rating);
                        break;
                    case 1:
                        glucoseRating = glucoseRatingBar.getRating();
                        glucoseTextView.setText(R.string.glucose_rating);
                        break;
                    case 2:
                        histamineRating = histamineRatingBar.getRating();
                        histamineTextView.setText(R.string.histamine_rating);
                        break;
                    case 3:
                        lactoseRating = lactoseRatingBar.getRating();
                        lactoseTextView.setText(R.string.lactose_rating);
                        break;
                    case 4:
                        sucroseRating = sucroseRatingBar.getRating();
                        sucroseTextView.setText(R.string.sucrose_rating);
                        break;
                    case 5:
                        sorbitolRating = sorbitolRatingBar.getRating();
                        sorbitolTextView.setText(R.string.sorbitol_rating);
                        break;
                }
            }
        }
    }

    private class onImageClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ProductActivity.this, PictureActivity.class);
            intent.putExtra("title", getTitle());
            startActivity(intent);
        }
    }

    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            JSONHandler.startRating();
            MainActivity.loadData();
            finish();
        }
    }

    public static String getName() {
        return name;
    }

    public static String getBrand() {
        return brand;
    }

    public static float getFructoseRating() {
        return fructoseRating;
    }

    public static float getGlucoseRating() {
        return glucoseRating;
    }

    public static float getHistamineRating() {
        return histamineRating;
    }

    public static float getLactoseRating() {
        return lactoseRating;
    }

    public static float getSucroseRating() {
        return sucroseRating;
    }

    public static float getSorbitolRating() {
        return sorbitolRating;
    }
}