package com.baldware.intolerapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadRunnable implements Runnable {

    @Override
    public void run() {
        try{
            URL url = new URL(JSONHandler.getWebServiceURL());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json;

            while ((json = bufferedReader.readLine()) != null) {
                stringBuilder.append(json + "\n");
            }

            JSONHandler.setJson(stringBuilder.toString().trim());

            bufferedReader.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
