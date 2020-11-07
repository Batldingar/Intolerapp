package com.baldware.intolerapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        name = getIntent().getStringExtra("name");
        brand = getIntent().getStringExtra("brand");
        setTitle(name + " - " + brand);

        fructoseRatingBar = findViewById(R.id.rating_bar_fructose);
        glucoseRatingBar = findViewById(R.id.rating_bar_glucose);
        histamineRatingBar = findViewById(R.id.rating_bar_histamine);
        lactoseRatingBar = findViewById(R.id.rating_bar_lactose);
        sucroseRatingBar = findViewById(R.id.rating_bar_sucrose);
        sorbitolRatingBar = findViewById(R.id.rating_bar_sorbitol);

        TextView fructoseTextView = findViewById(R.id.text_view_fructose);
        TextView glucoseTextView = findViewById(R.id.text_view_glucose);
        TextView histamineTextView = findViewById(R.id.text_view_histamine);
        TextView lactoseTextView = findViewById(R.id.text_view_lactose);
        TextView sucroseTextView = findViewById(R.id.text_view_sucrose);
        TextView sorbitolTextView = findViewById(R.id.text_view_sorbitol);

        try {
            JSONObject jsonObject = JSONHandler.getJsonArray().getJSONObject(getIntent().getIntExtra("position", 0));
            fructoseTextView.setText(getResources().getString(R.string.fructose_text, jsonObject.getInt("fructoseRatingCount")));
            glucoseTextView.setText(getResources().getString(R.string.glucose_text, jsonObject.getInt("glucoseRatingCount")));
            histamineTextView.setText(getResources().getString(R.string.histamine_text, jsonObject.getInt("histamineRatingCount")));
            lactoseTextView.setText(getResources().getString(R.string.lactose_text, jsonObject.getInt("lactoseRatingCount")));
            sucroseTextView.setText(getResources().getString(R.string.sucrose_text, jsonObject.getInt("sucroseRatingCount")));
            sorbitolTextView.setText(getResources().getString(R.string.sorbitol_text, jsonObject.getInt("sorbitolRatingCount")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
    }

    private void loadIntoRatingBars() throws JSONException {
        JSONObject jsonObject = JSONHandler.getJsonArray().getJSONObject(getIntent().getIntExtra("position", 0));
        
        fructoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("fructoseRating")));
        glucoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("glucoseRating")));
        histamineRatingBar.setRating(Float.parseFloat(jsonObject.getString("histamineRating")));
        lactoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("lactoseRating")));
        sucroseRatingBar.setRating(Float.parseFloat(jsonObject.getString("sucroseRating")));
        sorbitolRatingBar.setRating(Float.parseFloat(jsonObject.getString("sorbitolRating")));
    }

    private class onRatingBarChangeListener implements RatingBar.OnRatingBarChangeListener {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            if(fromUser) {
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
                        break;
                    case 1:
                        glucoseRating = glucoseRatingBar.getRating();
                        break;
                    case 2:
                        histamineRating = histamineRatingBar.getRating();
                        break;
                    case 3:
                        lactoseRating = lactoseRatingBar.getRating();
                        break;
                    case 4:
                        sucroseRating = sucroseRatingBar.getRating();
                        break;
                    case 5:
                        sorbitolRating = sorbitolRatingBar.getRating();
                        break;
                }
            }
        }
    }

    private class onClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            JSONHandler.startRating("http://intolerapp.com/austria_rating_service.php");
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