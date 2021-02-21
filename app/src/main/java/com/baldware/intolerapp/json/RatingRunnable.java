package com.baldware.intolerapp.json;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.baldware.intolerapp.activities.MainActivity;
import com.baldware.intolerapp.activities.ProductActivity;
import com.baldware.intolerapp.customTools.Constants;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RatingRunnable implements Runnable {

    private final MainActivity mainActivity;
    private final String name;
    private final String brand;

    public RatingRunnable(MainActivity mainActivity, String name, String brand) {
        this.mainActivity = mainActivity;
        this.name = name;
        this.brand = brand;
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        boolean scriptSuccess = false;

        try {
            URL url = new URL(Constants.RATING_URL);

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("fructose", ProductActivity.getFructoseRating());
            jsonObject.put("glucose", ProductActivity.getGlucoseRating());
            jsonObject.put("histamine", ProductActivity.getHistamineRating());
            jsonObject.put("lactose", ProductActivity.getLactoseRating());
            jsonObject.put("sucrose", ProductActivity.getSucroseRating());
            jsonObject.put("sorbitol", ProductActivity.getSorbitolRating());
            jsonObject.put("name", name);
            jsonObject.put("brand", brand);

            String message = jsonObject.toString();

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(message.getBytes().length);

            //HTTP header properties
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            connection.connect();

            // Write the message
            outputStream = new BufferedOutputStream(connection.getOutputStream());
            outputStream.write(message.getBytes());
            outputStream.flush();

            // Get download finished sign
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String serverMessage;

            while ((serverMessage = bufferedReader.readLine()) != null) {
                if (serverMessage.equals(Constants.END_OF_SCRIPT)) {
                    scriptSuccess = true;
                }
            }

            // ----- Download is finished -----

            Handler handler = new Handler(Looper.getMainLooper()); // get Handler for UIThread

            if (scriptSuccess) {
                // post on UIThread
                handler.post(() -> {
                    Toast.makeText(mainActivity.getApplicationContext(), "Rating successful!", Toast.LENGTH_SHORT).show();
                    mainActivity.loadData();
                });
            } else {
                // post on UIThread
                handler.post(() -> Toast.makeText(mainActivity.getApplicationContext(), "Currently unable to rate the product!", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
