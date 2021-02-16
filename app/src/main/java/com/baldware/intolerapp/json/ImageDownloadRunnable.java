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

    private ProductActivity productActivity;
    private String productName;
    private String productBrand;

    public ImageDownloadRunnable(ProductActivity productActivity, String productName, String productBrand) {
        this.productActivity = productActivity;
        this.productName = productName;
        this.productBrand = productBrand;
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        HttpURLConnection connection;

        try{
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json;

            // Not really necessary since there is only one image = only one line
            while ((json = bufferedReader.readLine()) != null) {
                stringBuilder.append(json).append("\n");
            }

            // ----- Download is finished -----

            Handler handler = new Handler(Looper.getMainLooper()); // get Handler for UIThread
            handler.post(new Runnable() { // post on UIThread
                @Override
                public void run() {
                    //Asynchronously sets the imageView in a product activity and picture activity
                    productActivity.setProductImage(BitmapHandler.createShowable(stringBuilder.toString()));
                }
            });

            bufferedReader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream!=null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
