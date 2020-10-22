package com.baldware.intolerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new onItemClickListener());

        downloadJSON("http://intolerapp.com/austria_service.php");
    }

    private void downloadJSON(final String urlWebService) {

        class JSONHandler implements Runnable {
            public String jsonResult;

            @Override
            public void run() {
                try{
                    URL url = new URL(urlWebService);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String json;

                    while ((json = bufferedReader.readLine()) != null) {
                        stringBuilder.append(json + "\n");
                    }

                    jsonResult = stringBuilder.toString().trim();

                    bufferedReader.close();
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        JSONHandler jsonHandler = new JSONHandler();
        Thread downloadThread = new Thread(jsonHandler);

        Toast.makeText(this, "Downloading for you...", Toast.LENGTH_SHORT).show();

        downloadThread.start();

        while(downloadThread.isAlive()) {} // wait for thread to finish

        try {
            if(jsonHandler.jsonResult != null) {
                loadIntoListView(jsonHandler.jsonResult);
            } else {
                Toast.makeText(MainActivity.this, "Download failed! - Is your internet connection active?", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
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
            intent.putExtra("title", (String)parent.getItemAtPosition(position));
            startActivity(intent);
        }
    }
}