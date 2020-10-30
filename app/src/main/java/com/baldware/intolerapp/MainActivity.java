package com.baldware.intolerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton floatingActionButton;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new onItemClickListener());

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new onClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();

        JSONHandler.startDownload("http://intolerapp.com/austria_download_service.php");

        try {
            if(JSONHandler.getJson() != null) {
                loadIntoListView();
            } else {
                Toast.makeText(MainActivity.this, "Download failed! - Is your internet connection active?", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadIntoListView() throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        String[] products = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            products[i] = jsonObject.getString("name") + " - " + jsonObject.getString("brand");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(arrayAdapter);

        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
    }

    public class onItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);

            JSONArray jsonArray = null;
            JSONObject jsonObject = null;
            try {
                jsonArray = new JSONArray(JSONHandler.getJson());
                jsonObject = jsonArray.getJSONObject(position);
                intent.putExtra("name", jsonObject.getString("name"));
                intent.putExtra("brand", jsonObject.getString("brand"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    public class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AdditionActivity.class);
            startActivity(intent);
        }
    }

    public static Context getContext() {
        return context;
    }
}