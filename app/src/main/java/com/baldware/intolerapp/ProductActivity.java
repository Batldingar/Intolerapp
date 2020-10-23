package com.baldware.intolerapp;

import android.os.Bundle;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductActivity extends AppCompatActivity {

    private RatingBar fructoseRatingBar;
    private RatingBar glucoseRatingBar;
    private RatingBar histamineRatingBar;
    private RatingBar lactoseRatingBar;
    private RatingBar sucroseRatingBar;
    private RatingBar sorbitolRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        String title = getIntent().getStringExtra("title").toString();
        setTitle(title);

        fructoseRatingBar = findViewById(R.id.rating_bar_fructose);
        glucoseRatingBar = findViewById(R.id.rating_bar_glucose);
        histamineRatingBar = findViewById(R.id.rating_bar_histamine);
        lactoseRatingBar = findViewById(R.id.rating_bar_lactose);
        sucroseRatingBar = findViewById(R.id.rating_bar_sucrose);
        sorbitolRatingBar = findViewById(R.id.rating_bar_sorbitol);

        try {
            loadIntoRatingBars();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadIntoRatingBars() throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());

        JSONObject jsonObject = jsonArray.getJSONObject(getIntent().getIntExtra("position", 0));
        
        fructoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("fructose")));
        glucoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("glucose")));
        histamineRatingBar.setRating(Float.parseFloat(jsonObject.getString("histamine")));
        lactoseRatingBar.setRating(Float.parseFloat(jsonObject.getString("lactose")));
        sucroseRatingBar.setRating(Float.parseFloat(jsonObject.getString("sucrose")));
        sorbitolRatingBar.setRating(Float.parseFloat(jsonObject.getString("sorbitol")));
    }
}