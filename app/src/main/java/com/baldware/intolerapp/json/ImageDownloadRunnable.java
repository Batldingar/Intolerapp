package com.baldware.intolerapp.json;

import android.os.Handler;
import android.os.Looper;

import com.baldware.intolerapp.activities.ProductActivity;
import com.baldware.intolerapp.customTools.BitmapHandler;
import com.baldware.intolerapp.customTools.Constants;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloadRunnable implements Runnable {

    private final ProductActivity productActivity;
    private final String productName;
    private final String productBrand;

    public ImageDownloadRunnable(ProductActivity productActivity, String productName, String productBrand) {
        this.productActivity = productActivity;
        this.productName = productName;
        this.productBrand = productBrand;
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        boolean scriptSuccess = false;

        while (!scriptSuccess) {
            try {
                URL url = new URL(Constants.IMAGE_DOWNLOAD_URL);

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("name", productName);
                jsonObject.put("brand", productBrand);

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

                // Read the incoming json (from Server)
                StringBuilder stringBuilder = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String imageSourceLine;

                // Not really necessary since there is only one image = only one line
                while ((imageSourceLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(imageSourceLine).append("\n");
                }

                if (!stringBuilder.toString().equals("")) {
                    scriptSuccess = true;
                }

                // ----- Download is finished -----

                Handler handler = new Handler(Looper.getMainLooper()); // get Handler for UIThread
                // post on UIThread
                handler.post(() -> {
                    //Asynchronously sets the imageView in a product activity and picture activity
                    if (!stringBuilder.toString().equals("")) {
                        productActivity.setProductImage(BitmapHandler.createShowable(stringBuilder.toString()));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
