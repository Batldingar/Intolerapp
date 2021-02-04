package com.baldware.intolerapp.json;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.baldware.intolerapp.activities.MainActivity;
import com.baldware.intolerapp.customTools.Constants;
import com.baldware.intolerapp.json.JSONHandler;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadRunnable implements Runnable {

    private String productName;
    private String productBrand;

    public DownloadRunnable() {

    }

    public DownloadRunnable(String productName, String productBrand) {
        this.productName = productName;
        this.productBrand = productBrand;
    }

    @Override
    public void run() {
        try{
            URL url = new URL(Constants.DOWNLOAD_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json;

            while ((json = bufferedReader.readLine()) != null) {
                stringBuilder.append(json).append("\n");
            }

            JSONHandler.setJson(stringBuilder.toString().trim());

            bufferedReader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ----- Download is finished -----

        Handler handler = new Handler(Looper.getMainLooper()); // get Handler for UIThread
        handler.post(new Runnable() { // post on UIThread
            @Override
            public void run() {
                try {
                    if(JSONHandler.getJson() != null) {
                        MainActivity.loadJSONIntoListView();
                        Toast.makeText(MainActivity.getContext(), "Done!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.getContext(), "Download failed! - Is your internet connection active?", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(productName != null && productBrand != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    MainActivity.showProduct(productName, productBrand);
                }
            });
        }
    }
}
