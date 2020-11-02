package com.baldware.intolerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton floatingActionButton;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new onItemClickListener());
        listView.setOnItemLongClickListener(new onItemLongClickListener());

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new onClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();

        JSONHandler.startDownload("http://intolerapp.com/austria_download_service.php");

        try {
            if(JSONHandler.getJson() != null) {
                loadIntoListView();
            } else {
                Toast.makeText(MainActivity.this, "Download failed! - Is your internet connection active?", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchViewListener());

        return true;
    }

    private void loadIntoListView() throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        String[] products = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            products[i] = jsonObject.getString("name") + " - " + jsonObject.getString("brand");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(arrayAdapter);

        Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
    }

    public class onItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);

            JSONArray jsonArray = null;
            JSONObject jsonObject = null;
            try {
                jsonArray = new JSONArray(JSONHandler.getJson());
                jsonObject = jsonArray.getJSONObject(position);
                intent.putExtra("name", jsonObject.getString("name"));
                intent.putExtra("brand", jsonObject.getString("brand"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            intent.putExtra("position", position);
            startActivity(intent);
        }
    }

    public class onItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ReportDialogFragment reportDialogFragment = ReportDialogFragment.newInstance("Report");
            reportDialogFragment.show(getSupportFragmentManager(), "report");
            return true;
        }
    }

    public static class ReportDialogFragment extends DialogFragment {
        public ReportDialogFragment() {
            // Empty constructor required
        }

        public static ReportDialogFragment newInstance(String title) {
            ReportDialogFragment reportDialogFragment = new ReportDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            reportDialogFragment.setArguments(args);
            return reportDialogFragment;
        }

        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            String title = getArguments().getString("title");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setMessage(R.string.report_text)
                    .setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });

            return builder.create();
        }
    }

    public class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AdditionActivity.class);
            startActivity(intent);
        }
    }

    public static Context getContext() {
        return context;
    }
}