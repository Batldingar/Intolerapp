package com.baldware.intolerapp.customTools;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class HistoryHandler {

    public enum Mode {
        PRODUCT_ADDED,
        PRODUCT_RATED
    }

    private File file;

    public HistoryHandler(Context context, String fileName) {
        file = new File(context.getFilesDir(), fileName);
    }

    public void writeHistory(String product, Mode mode) {
        String message = null;
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        // Generate the message
        if(mode == Mode.PRODUCT_ADDED) {
            message = "A:" + product;
        } else if (mode == Mode.PRODUCT_RATED) {
            message = "R:" + product;
        }

        // Initialize the fileWriter
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize the bufferedWriter on top of the fileWriter
        bufferedWriter = new BufferedWriter(fileWriter);

        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readHistory(Context context) {
        byte[] bytes;
        FileReader fileReader = null;
        BufferedReader bufferedReader;
        ArrayList<String> resultStrings = new ArrayList<>();

        // Prepare the byte array
        bytes = new byte[(int) file.length()];

        // Initialize the fileReader
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(fileReader!=null) {
            // Initialize the bufferedReader on top of the fileReader
            bufferedReader = new BufferedReader(fileReader);

            try {
                String line = bufferedReader.readLine();
                while (line != null) {
                    resultStrings.add(line);
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "Unable to read history", Toast.LENGTH_SHORT).show();
        }

        return resultStrings;
    }

}
