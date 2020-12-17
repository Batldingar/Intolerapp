package com.baldware.intolerapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baldware.intolerapp.Constants;
import com.baldware.intolerapp.R;
import com.baldware.intolerapp.SearchViewListener;
import com.baldware.intolerapp.activities.AdditionActivity;
import com.baldware.intolerapp.activities.ProductActivity;
import com.baldware.intolerapp.json.JSONHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private static ListView listView;
    private static Context context;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new onItemClickListener());
        listView.setOnItemLongClickListener(new onItemLongClickListener());

        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new onRefreshListener());

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new onClickListener());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new onNavigationItemSelectedListener());

        loadData();
    }

    public static void loadData() {
        Toast.makeText(MainActivity.getContext(), "Updating products...", Toast.LENGTH_SHORT).show();

        JSONHandler.startDownload(Constants.DOWNLOAD_URL);

        try {
            if(JSONHandler.getJson() != null) {
                loadJSONIntoListView();
                Toast.makeText(getContext(), "Done!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Download failed! - Is your internet connection active?", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu) {
            drawerLayout.openDrawer(GravityCompat.END);
        }

        return super.onOptionsItemSelected(item);
    }

    public static void loadJSONIntoListView() throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        String[] products = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            products[i] = jsonObject.getString("name") + " - " + jsonObject.getString("brand");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, products);
        listView.setAdapter(arrayAdapter);
    }

    public static void loadSearchIntoListView() {
        ArrayList<String[]> result = SearchViewListener.getSearchResult();
        String[] resultArray = new String[result.size()];

        for(int i = 0; i < result.size(); i++) {
            resultArray[i] = result.get(i)[0];
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, resultArray);
        listView.setAdapter(arrayAdapter);
    }

    public class onItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);

            JSONArray jsonArray;
            JSONObject jsonObject;

            if(SearchViewListener.getSearchResult()!=null) {
                position = Integer.parseInt(SearchViewListener.getSearchResult().get(position)[2]);
            }

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

    public static class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            MainActivity.loadData();
            swipeRefreshLayout.setRefreshing(false);
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

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            String title = null;
            if (getArguments() != null) {
                title = getArguments().getString("title");
            }

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

    public static class RuleDialogFragment extends DialogFragment {
        public RuleDialogFragment() {
            // Empty constructor required
        }

        public static RuleDialogFragment newInstance(String title) {
            RuleDialogFragment ruleDialogFragment = new RuleDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            ruleDialogFragment.setArguments(args);
            return ruleDialogFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            String title = null;
            if (getArguments() != null) {
                title = getArguments().getString("title");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setMessage(R.string.rule_text)
                    .setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }

    // OnClickListener for the Floating Addition Button
    public class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AdditionActivity.class);
            startActivity(intent);
        }
    }

    public class onNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.nav_legal_notice:{
                    RuleDialogFragment ruleDialogFragment = RuleDialogFragment.newInstance("Rules");
                    ruleDialogFragment.show(getSupportFragmentManager(), "rules");
                    break;
                }
            }
            //drawerLayout.closeDrawer(GravityCompat.END);
            return false;
        }
    }

    public static Context getContext() {
        return context;
    }
}