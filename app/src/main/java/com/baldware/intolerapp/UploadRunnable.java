package com.baldware.intolerapp;

import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadRunnable implements Runnable {

    @Override
    public void run() {
        OutputStream outputStream = null;
        HttpURLConnection connection;

        try{
            URL url = new URL(JSONHandler.getUploadServiceURL());

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("name", AdditionActivity.getProductName());
            jsonObject.put("brand", AdditionActivity.getProductBrand());

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
