package com.baldware.intolerapp.json;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.baldware.intolerapp.activities.AdditionActivity;
import com.baldware.intolerapp.customTools.Constants;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Blob;

public class ImageUploadRunnable implements Runnable {

    private String image;
    private String name;
    private String brand;

    public ImageUploadRunnable(String image, String name, String brand) {
        this.image = image;
        this.name = name;
        this.brand = brand;
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        HttpURLConnection connection = null;

        try{
            URL url = new URL(Constants.IMAGE_UPLOAD_URL);

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("image", image);
            jsonObject.put("name", name);
            jsonObject.put("brand", brand);

            String message = jsonObject.toString();

            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            //connection.setDoInput(true);
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection!=null) {
                connection.disconnect();
            }
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
