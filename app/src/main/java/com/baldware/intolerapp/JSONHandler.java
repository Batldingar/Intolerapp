package com.baldware.intolerapp;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONHandler {

    private static String json;
    private static String downloadServiceURL;
    private static String uploadServiceURL;

    // TODO: Instead of waiting all the start... methods should create a progress spinner

    public static void startDownload(String webServiceURL) {
        downloadServiceURL = webServiceURL;

        Thread downloadThread = new Thread(new DownloadRunnable());

        Toast.makeText(MainActivity.getContext(), "Downloading for you...", Toast.LENGTH_SHORT).show();

        downloadThread.start();

        while(downloadThread.isAlive()) {} // wait for thread to finish
    }

    public static void startUpload(String webServiceURL) {
        uploadServiceURL = webServiceURL;

        Thread uploadThread = new Thread(new UploadRunnable());

        uploadThread.start();

        while(uploadThread.isAlive()) {} // wait for thread to finish

        Toast.makeText(MainActivity.getContext(), "Addition successful!", Toast.LENGTH_SHORT).show();
    }

    public static void startRating(String webServiceURL) {
        uploadServiceURL = webServiceURL;

        Thread ratingThread = new Thread(new RatingRunnable());

        ratingThread.start();

        while(ratingThread.isAlive()) {} // wait for thread to finish

        Toast.makeText(MainActivity.getContext(), "Rating successful!", Toast.LENGTH_SHORT).show();
    }

    public static String getJson() {
        return json;
    }

    public static void setJson(String json) {
        JSONHandler.json = json;
    }

    public static JSONArray getJsonArray() {
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    public static JSONObject getJsonObject(int index) {
        JSONObject jsonObject = null;

        try {
            jsonObject =getJsonArray().getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static String getDownloadServiceURL() {
        return downloadServiceURL;
    }

    public static String getUploadServiceURL() {
        return uploadServiceURL;
    }
}
