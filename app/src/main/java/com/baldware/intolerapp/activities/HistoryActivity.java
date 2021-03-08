package com.baldware.intolerapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baldware.intolerapp.R;
import com.baldware.intolerapp.customTools.HistoryHandler;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTitle("History");

        listView = findViewById(R.id.history_list_view);

        HistoryHandler historyHandler = new HistoryHandler(this, "history");
        ArrayList<String> stringArrayList = historyHandler.readHistory(this);

        if(stringArrayList.isEmpty()) {
            Toast.makeText(this, "Unable to read history", Toast.LENGTH_SHORT).show();
        } else {

            String[] stringArray = new String[stringArrayList.size()];

            for (int i = 0; i < stringArrayList.size(); i++) {
                String prefix = stringArrayList.get(i).split(":")[0];
                String suffix = stringArrayList.get(i).split(":")[1];

                if (prefix.equals("A")) {
                    stringArray[(stringArrayList.size() - 1 - i)] = "You added: " + suffix;
                } else if (prefix.equals("R")) {
                    stringArray[(stringArrayList.size() - 1 - i)] = "You rated: " + suffix;
                }
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArray);
            listView.setAdapter(arrayAdapter);

        }
    }
}
