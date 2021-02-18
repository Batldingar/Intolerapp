package com.baldware.intolerapp.json;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import com.baldware.intolerapp.activities.MainActivity;
import com.baldware.intolerapp.activities.ProductActivity;
import com.baldware.intolerapp.customTools.BitmapHandler;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONHandler {

    private static String json;

    public static void startDownload(MainActivity mainActivity, ListView listView) {
        Thread downloadThread = new Thread(new DownloadRunnable(mainActivity, listView));
        downloadThread.start();
    }

    public static void startDownload(MainActivity mainActivity, ListView listView, String productName, String productBrand) {
        Thread downloadThread = new Thread(new DownloadRunnable(mainActivity, listView, productName, productBrand));
        downloadThread.start();
    }

    public static void startUpload(MainActivity mainActivity, String name, String brand) {
        Thread uploadThread = new Thread(new UploadRunnable(mainActivity, name, brand));
        uploadThread.start();
    }

    public static void startRating(MainActivity mainActivity, String name, String brand) {
        Thread ratingThread = new Thread(new RatingRunnable(mainActivity, name, brand));
        ratingThread.start();
    }

    public static void startReport(Context context, String reportProduct) {
        Thread reportThread = new Thread(new ReportRunnable(context, reportProduct));
        reportThread.start();
    }

    public static void startImageUpload(String encodedImage, String name, String brand) {
        Thread imageUploadThread = new Thread(new ImageUploadRunnable(encodedImage, name, brand));
        imageUploadThread.start();
    }

    public static void startImageDownload(ProductActivity productActivity, String name, String brand) {
        Thread imageDownloadThread = new Thread(new ImageDownloadRunnable(productActivity, name, brand));
        imageDownloadThread.start();
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
}
