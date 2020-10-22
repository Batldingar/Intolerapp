package com.baldware.intolerapp;

import android.widget.Toast;

public class JSONHandler {

    private static String json;
    private static String webServiceURL;

    public static void start(String serviceURL) {
        webServiceURL = serviceURL;

        Thread downloadThread = new Thread(new DownloadRunnable());

        Toast.makeText(MainActivity.getContext(), "Downloading for you...", Toast.LENGTH_SHORT).show();

        downloadThread.start();

        while(downloadThread.isAlive()) {} // wait for thread to finish
    }

    public static String getJson() {
        return json;
    }

    public static void setJson(String json) {
        JSONHandler.json = json;
    }

    public static String getWebServiceURL() {
        return webServiceURL;
    }
}
