package com.baldware.intolerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        JSONHandler jsonHandler = new JSONHandler();
        Thread downloadThread = new Thread(jsonHandler);
        downloadThread.start();
        Toast.makeText(this, "Downloading for you...", Toast.LENGTH_SHORT).show();

        while(downloadThread.isAlive()) {} // wait for thread to finish

        try {
            loadIntoListView(jsonHandler.jsonResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadIntoListView(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        String[] products = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            products[i] = jsonObject.getString("name") + " " + jsonObject.getString("brand");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(arrayAdapter);
    }
}