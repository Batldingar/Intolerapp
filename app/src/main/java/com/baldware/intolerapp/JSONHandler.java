package com.baldware.intolerapp;

import android.widget.Toast;

public class JSONHandler {

    private static String json;
    private static String downloadServiceURL;
    private static String uploadServiceURL;

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

    public static String getJson() {
        return json;
    }

    public static void setJson(String json) {
        JSONHandler.json = json;
    }

    public static String getDownloadServiceURL() {
        return downloadServiceURL;
    }

    public static String getUploadServiceURL() {
        return uploadServiceURL;
    }
}
