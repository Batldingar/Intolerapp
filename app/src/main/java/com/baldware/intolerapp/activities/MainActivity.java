package com.baldware.intolerapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import com.baldware.intolerapp.customTools.Constants;
import com.baldware.intolerapp.R;
import com.baldware.intolerapp.customTools.SearchViewListener;
import com.baldware.intolerapp.customTools.StarListViewAdapter;
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

    // Initializes everything on start up
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

        loadFirstStartUpMessage();
        loadSettings();
        loadData();
    }

    // Loads message for first start up if necessary
    private void loadFirstStartUpMessage(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean firstStartUp = sharedPreferences.getBoolean(getString(R.string.firstStartUpFlag), true);

        if(firstStartUp) {
            RuleDialogFragment ruleDialogFragment = RuleDialogFragment.newInstance(getString(R.string.start_up_message_title), getString(R.string.start_up_message_text));
            ruleDialogFragment.show(getSupportFragmentManager(), getString(R.string.start_up_message_title));

            disableFirstStartUpMessage();
        }
    }

    // Disables the first start up message after the first start up has occured
    private void disableFirstStartUpMessage() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.firstStartUpFlag), false);
        editor.apply();
    }

    // Loads settings (main intolerance preferences)
    private void loadSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.settingsFlag), Context.MODE_PRIVATE);
        String mainIntolerance = sharedPreferences.getString(getString(R.string.settingsFlag), null);

        if(mainIntolerance != null) {
            RuleDialogFragment ruleDialogFragment = RuleDialogFragment.newInstance("Your Main-Intolerance:", mainIntolerance);
            ruleDialogFragment.show(getSupportFragmentManager(), "Your Main-Intolerance:");
        }
    }

    // Downloads the product data and calls loadJSONIntoListView
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

    // Gets the json data from the JSONHandler and loads it into the listView
    public static void loadJSONIntoListView() throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        String[] products = new String[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            products[i] = jsonObject.getString("name") + " - " + jsonObject.getString("brand");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, products);
        // TODO
        StarListViewAdapter starListViewAdapter = new StarListViewAdapter(getContext(), R.layout.star_listview_item, R.id.star_listView_textView, products);
        listView.setAdapter(starListViewAdapter);
    }

    // Loads search results into the listView
    public static void loadSearchIntoListView() {
        ArrayList<String[]> result = SearchViewListener.getSearchResult();
        String[] resultArray = new String[result.size()];

        for(int i = 0; i < result.size(); i++) {
            resultArray[i] = result.get(i)[0];
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, resultArray);
        listView.setAdapter(arrayAdapter);
    }

    // Creates the top bar (the menu consisting of search bar and buttons)
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

    // Runs commands when pressing a button in the top bar menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu) {
            drawerLayout.openDrawer(GravityCompat.END);
        }

        return super.onOptionsItemSelected(item);
    }

    // For the listView
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

    // For the listView
    public class onItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ReportDialogFragment reportDialogFragment = ReportDialogFragment.newInstance("Report");
            reportDialogFragment.show(getSupportFragmentManager(), "report");
            return true;
        }
    }

    // For the swipeRefreshLayout
    public static class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            MainActivity.loadData();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    // For the floatingActionButton
    public class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AdditionActivity.class);
            startActivity(intent);
        }
    }

    // For the navigationView
    public class onNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.nav_settings:{
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.nav_legal_notice:{
                    RuleDialogFragment ruleDialogFragment = RuleDialogFragment.newInstance(getString(R.string.rules_title), getResources().getString(R.string.rule_text));
                    ruleDialogFragment.show(getSupportFragmentManager(), getString(R.string.rules_title));
                    break;
                }
                case R.id.nav_share_app:{
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Share", "https://play.google.com/store/apps/details?id=com.baldware.intolerapp");
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copied link to clipboard!", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.nav_rate_app:{
                    Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                    playStoreIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.baldware.intolerapp"));
                    startActivity(playStoreIntent);
                    break;
                }
            }
            //drawerLayout.closeDrawer(GravityCompat.END);
            return false;
        }
    }

    // The reportDialogFragment
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

    // The ruleDialogFragment
    public static class RuleDialogFragment extends DialogFragment {
        public RuleDialogFragment() {
            // Empty constructor required
        }

        public static RuleDialogFragment newInstance(String title, String message) {
            RuleDialogFragment ruleDialogFragment = new RuleDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            ruleDialogFragment.setArguments(args);
            return ruleDialogFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            String title = null;
            String message = null;
            if (getArguments() != null) {
                title = getArguments().getString("title");
                message = getArguments().getString("message");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.acceptance_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            return builder.create();
        }
    }

    public static Context getContext() {
        return context;
    }
}