package com.baldware.intolerapp.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baldware.intolerapp.R;
import com.baldware.intolerapp.customTools.HistoryHandler;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        setTitle("History");

        ListView listView = findViewById(R.id.history_list_view);

        HistoryHandler historyHandler = new HistoryHandler(this, "history");
        ArrayList<String> stringArrayList = historyHandler.readHistory();

        if(stringArrayList.isEmpty()) {
            Toast.makeText(this, "Unable to read history", Toast.LENGTH_SHORT).show();
            finish();
        } else {

            String[] stringArray = new String[stringArrayList.size()];

            for (int i = 0; i < stringArrayList.size(); i++) {
                String prefix = stringArrayList.get(i).split(":")[0];
                String suffix = stringArrayList.get(i).split(":")[1];

                switch (prefix) {
                    case "A":
                        stringArray[(stringArrayList.size() - 1 - i)] = "You added: " + suffix; // reverse indexing so that the list shows the newest history entry first

                        break;
                    case "R":
                        stringArray[(stringArrayList.size() - 1 - i)] = "You rated: " + suffix;
                        break;
                    case "D":
                        stringArray[(stringArrayList.size() - 1 - i)] = "You deleted: " + suffix;
                        break;
                }
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArray);
            listView.setAdapter(arrayAdapter);

        }
    }
}
