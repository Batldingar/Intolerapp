package com.baldware.intolerapp.json;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.baldware.intolerapp.activities.AdditionActivity;
import com.baldware.intolerapp.customTools.Constants;
import com.baldware.intolerapp.json.JSONHandler;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadRunnable implements Runnable {

    private Context context;
    private String name;
    private String brand;

    public UploadRunnable(Context context, String name, String brand) {
        this.context = context;
        this.name = name;
        this.brand = brand;
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        HttpURLConnection connection = null;
        Boolean scriptSuccess = false;

        try{
            URL url = new URL(Constants.UPLOAD_URL);

            JSONObject jsonObject = new JSONObject();

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
                if(serverMessage.equals(Constants.END_OF_SCRIPT)) {
                    scriptSuccess = true;
                }
            }

            // ----- Download is finished -----

            Handler handler = new Handler(Looper.getMainLooper()); // get Handler for UIThread

            if(scriptSuccess) {
                handler.post(new Runnable() { // post on UIThread
                    @Override
                    public void run() {
                        Toast.makeText(context, "Addition successful!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                handler.post(new Runnable() { // post on UIThread
                    @Override
                    public void run() {
                        Toast.makeText(context, "Currently unable to add the product!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
                if(bufferedReader!=null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
