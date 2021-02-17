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

    // TODO: Make fully asynchronous (should call open the product page itself)
    public static void startUpload(Context context, String name, String brand) {
        Thread uploadThread = new Thread(new UploadRunnable(context, name, brand));
        uploadThread.start();
    }

    // TODO: Make fully asynchronous (should call reload json itself)
    public static void startRating(Context context, String name, String brand) {
        Thread ratingThread = new Thread(new RatingRunnable(name, brand));
        ratingThread.start();

        while(ratingThread.isAlive()) {} // wait for thread to finish

        Toast.makeText(context, "Rating successful!", Toast.LENGTH_SHORT).show();
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
