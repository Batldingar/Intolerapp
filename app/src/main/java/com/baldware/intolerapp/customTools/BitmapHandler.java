package com.baldware.intolerapp.customTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapHandler {

    // Takes an image (by id), converts, compresses and encodes it and returns it as a string ready to be uploaded
    public static String createUploadable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Takes a downloaded string representing image data, decodes it and converts it into a bitmap
    public static Bitmap createShowable(String imageData) {
        byte[] imageDataBytes = imageData.getBytes();
        byte[] byteArray = Base64.decode(imageDataBytes, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
