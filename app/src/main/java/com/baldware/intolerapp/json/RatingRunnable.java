package com.baldware.intolerapp.json;

import com.baldware.intolerapp.activities.ProductActivity;
import com.baldware.intolerapp.customTools.Constants;
import com.baldware.intolerapp.json.JSONHandler;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RatingRunnable implements Runnable {

    private String name;
    private String brand;

    public RatingRunnable(String name, String brand) {
        this.name = name;
        this.brand = brand;
    }

            @Override
            public void run() {
                OutputStream outputStream = null;
                HttpURLConnection connection;

                try {
                    URL url = new URL(Constants.RATING_URL);

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("fructose", ProductActivity.getFructoseRating());
                    jsonObject.put("glucose", ProductActivity.getGlucoseRating());
                    jsonObject.put("histamine", ProductActivity.getHistamineRating());
                    jsonObject.put("lactose", ProductActivity.getLactoseRating());
                    jsonObject.put("sucrose", ProductActivity.getSucroseRating());
                    jsonObject.put("sorbitol", ProductActivity.getSorbitolRating());
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
