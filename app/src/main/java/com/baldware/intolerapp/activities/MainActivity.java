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

import com.baldware.intolerapp.R;
import com.baldware.intolerapp.customTools.SearchViewListener;
import com.baldware.intolerapp.customTools.StarListViewAdapter;
import com.baldware.intolerapp.json.JSONHandler;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    public static final int ADDITION_CODE = 0;
    public static final int PRODUCT_CODE = 1;
    public static final int SETTINGS_CODE = 2;

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private int sortingStarNumber;

    // Initializes everything on start up
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new onItemClickListener());
        listView.setOnItemLongClickListener(new onItemLongClickListener());

        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new onRefreshListener());

        FloatingActionButton floatingSortingButton = findViewById(R.id.sorting_floating_action_button);
        floatingSortingButton.setOnClickListener(new sortingOnClickListener());

        FloatingActionButton floatingAdditionButton = findViewById(R.id.addition_floating_action_button);
        floatingAdditionButton.setOnClickListener(new additionOnClickListener());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new onNavigationItemSelectedListener(getApplicationContext()));

        sortingStarNumber = 0;

        loadFirstStartUpMessage();
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch(requestCode) {
                case ADDITION_CODE:
                    JSONHandler.startUpload(this, data.getStringExtra("productName"), data.getStringExtra("productBrand"));
                    break;
                case PRODUCT_CODE:
                    JSONHandler.startRating(this, data.getStringExtra("productName"), data.getStringExtra("productBrand"));
                    break;
                case SETTINGS_CODE:
                    loadData();
                    sortingStarNumber = 0;
                    break;
            }
        }
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
    private String getMainIntolerance(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.settingsFlag), Context.MODE_PRIVATE);
        String mainIntolerance = sharedPreferences.getString(context.getString(R.string.settingsFlag), context.getString(R.string.radio_none));

        return mainIntolerance;
    }

    // Downloads the product data and calls loadJSONIntoListView
    public void loadData() {
        Toast.makeText(getApplicationContext(), "Updating products...", Toast.LENGTH_SHORT).show();
        JSONHandler.startDownload(this, listView);
    }

    // Downloads the product data and calls loadJSONIntoListView, then shows the product
    public void loadData(String productName, String productBrand) {
        Toast.makeText(getApplicationContext(), "Updating products...", Toast.LENGTH_SHORT).show();
        JSONHandler.startDownload(this, listView, productName, productBrand);
    }

    // Gets the json data from the JSONHandler and loads it into the listView
    public void loadJSONIntoListView(Context context, ListView listView) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        String[] products = new String[jsonArray.length()];
        double[] ratings = new double[jsonArray.length()];
        int[] counts = new int[jsonArray.length()];

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            products[i] = jsonObject.getString("name") + " - " + jsonObject.getString("brand");

            if(getMainIntolerance(context).equals(context.getString(R.string.radio_fructose))) {
                ratings[i] = jsonObject.getDouble("fructoseRating");
                counts[i] = jsonObject.getInt("fructoseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_glucose))) {
                ratings[i] = jsonObject.getDouble("glucoseRating");
                counts[i] = jsonObject.getInt("glucoseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_histamine))) {
                ratings[i] = jsonObject.getDouble("histamineRating");
                counts[i] = jsonObject.getInt("histamineRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_lactose))) {
                ratings[i] = jsonObject.getDouble("lactoseRating");
                counts[i] = jsonObject.getInt("lactoseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_sucrose))) {
                ratings[i] = jsonObject.getDouble("sucroseRating");
                counts[i] = jsonObject.getInt("sucroseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_sorbitol))) {
                ratings[i] = jsonObject.getDouble("sorbitolRating");
                counts[i] = jsonObject.getInt("sorbitolRatingCount");
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, products);
        StarListViewAdapter starListViewAdapter = new StarListViewAdapter(context, R.layout.star_listview_item, R.id.star_listView_textView, products, ratings, counts, getMainIntolerance(context));

        if(getMainIntolerance(context).equals(context.getString(R.string.radio_none))) {
            listView.setAdapter(arrayAdapter);
        } else {
            listView.setAdapter(starListViewAdapter);
        }
    }

    // Gets the json data from the JSONHandler and loads it into the listView if the rating equals fullStarCount
    public void loadJSONIntoListView(Context context, ListView listView, int fullStarCount) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());

        ArrayList<String> starProducts = new ArrayList<>();
        ArrayList<Double> starRatings = new ArrayList<>();
        ArrayList<Integer> starCounts = new ArrayList<>();

        String[] products;
        double[] ratings;
        int[] counts;

        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if(getMainIntolerance(context).equals(context.getString(R.string.radio_fructose))) {
                if(jsonObject.getDouble("fructoseRating") == fullStarCount || jsonObject.getDouble("fructoseRating") == (fullStarCount-0.5)) {
                    starRatings.add(jsonObject.getDouble("fructoseRating"));
                    starCounts.add(jsonObject.getInt("fructoseRatingCount"));
                    starProducts.add(jsonObject.getString("name") + " - " + jsonObject.getString("brand"));
                }
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_glucose))) {
                if(jsonObject.getDouble("glucoseRating") == fullStarCount || jsonObject.getDouble("glucoseRating") == (fullStarCount-0.5)) {
                    starRatings.add(jsonObject.getDouble("glucoseRating"));
                    starCounts.add(jsonObject.getInt("glucoseRatingCount"));
                    starProducts.add(jsonObject.getString("name") + " - " + jsonObject.getString("brand"));
                }
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_histamine))) {
                if(jsonObject.getDouble("histamineRating") == fullStarCount || jsonObject.getDouble("histamineRating") == (fullStarCount-0.5)) {
                    starRatings.add(jsonObject.getDouble("histamineRating"));
                    starCounts.add(jsonObject.getInt("histamineRatingCount"));
                    starProducts.add(jsonObject.getString("name") + " - " + jsonObject.getString("brand"));
                }
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_lactose))) {
                if(jsonObject.getDouble("lactoseRating") == fullStarCount || jsonObject.getDouble("lactoseRating") == (fullStarCount-0.5)) {
                    starRatings.add(jsonObject.getDouble("lactoseRating"));
                    starCounts.add(jsonObject.getInt("lactoseRatingCount"));
                    starProducts.add(jsonObject.getString("name") + " - " + jsonObject.getString("brand"));
                }
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_sucrose))) {
                if(jsonObject.getDouble("sucroseRating") == fullStarCount || jsonObject.getDouble("sucroseRating") == (fullStarCount-0.5)) {
                    starRatings.add(jsonObject.getDouble("sucroseRating"));
                    starCounts.add(jsonObject.getInt("sucroseRatingCount"));
                    starProducts.add(jsonObject.getString("name") + " - " + jsonObject.getString("brand"));
                }
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_sorbitol))) {
                if(jsonObject.getDouble("sorbitolRating") == fullStarCount || jsonObject.getDouble("sorbitolRating") == (fullStarCount-0.5)) {
                    starRatings.add(jsonObject.getDouble("sorbitolRating"));
                    starCounts.add(jsonObject.getInt("sorbitolRatingCount"));
                    starProducts.add(jsonObject.getString("name") + " - " + jsonObject.getString("brand"));
                }
            }
        }

        products = new String[starProducts.size()];
        ratings = new double[starProducts.size()];
        counts = new int[starProducts.size()];

        for(int i = 0; i < starProducts.size(); i++) {
            products[i] = starProducts.get(i);
            ratings[i] = starRatings.get(i);
            counts[i] = starCounts.get(i);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, products);
        StarListViewAdapter starListViewAdapter = new StarListViewAdapter(context, R.layout.star_listview_item, R.id.star_listView_textView, products, ratings, counts, getMainIntolerance(context));

        if(getMainIntolerance(context).equals(context.getString(R.string.radio_none))) {
            listView.setAdapter(arrayAdapter);
        } else {
            listView.setAdapter(starListViewAdapter);
        }
    }

    // Loads search results into the listView
    public void loadSearchIntoListView(Context context, ListView listView) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        ArrayList<String[]> result = SearchViewListener.getSearchResult();
        String[] resultArray = new String[result.size()];
        double[] ratings = new double[result.size()];
        int[] counts = new int[jsonArray.length()];

        for(int i = 0; i < result.size(); i++) {
            resultArray[i] = result.get(i)[0];
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_fructose))) {
                ratings[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getDouble("fructoseRating");
                counts[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getInt("fructoseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_glucose))) {
                ratings[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getDouble("glucoseRating");
                counts[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getInt("glucoseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_histamine))) {
                ratings[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getDouble("histamineRating");
                counts[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getInt("histamineRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_lactose))) {
                ratings[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getDouble("lactoseRating");
                counts[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getInt("lactoseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_sucrose))) {
                ratings[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getDouble("sucroseRating");
                counts[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getInt("sucroseRatingCount");
            } else
            if(getMainIntolerance(context).equals(context.getString(R.string.radio_sorbitol))) {
                ratings[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getDouble("sorbitolRating");
                counts[i] = (jsonArray.getJSONObject(Integer.parseInt(result.get(i)[2]))).getInt("sorbitolRatingCount");
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, resultArray);
        StarListViewAdapter starListViewAdapter = new StarListViewAdapter(context, R.layout.star_listview_item, R.id.star_listView_textView, resultArray, ratings, counts, getMainIntolerance(context));

        if(getMainIntolerance(context).equals(context.getString(R.string.radio_none))) {
            listView.setAdapter(arrayAdapter);
        } else {
            listView.setAdapter(starListViewAdapter);
        }
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

        searchView.setOnQueryTextListener(new SearchViewListener(this, listView));

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

            if(SearchViewListener.getSearchResult()!=null) {
                position = Integer.parseInt(SearchViewListener.getSearchResult().get(position)[2]);
            }

            intent.putExtra("position", position);
            startActivityForResult(intent, PRODUCT_CODE);
        }
    }

    // For the listView
    public class onItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ReportDialogFragment reportDialogFragment = ReportDialogFragment.newInstance(getApplicationContext(), "Report", listView.getItemAtPosition(position).toString());
            reportDialogFragment.show(getSupportFragmentManager(), "report");
            return true;
        }
    }

    // For the swipeRefreshLayout
    public class onRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            loadData();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    // For the first floatingActionButton
    public class sortingOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(!getMainIntolerance(getApplicationContext()).equals(getApplicationContext().getString(R.string.radio_none))) {
                if (sortingStarNumber < 5) {
                    sortingStarNumber++;
                } else {
                    sortingStarNumber = 0;
                }
            } else {
                Toast.makeText(getApplicationContext(), "Please select your main intolerance in the settings first!", Toast.LENGTH_SHORT).show();
            }

            try {
                if(sortingStarNumber!=0) {
                    loadJSONIntoListView(getApplicationContext(), listView, sortingStarNumber);
                } else {
                    loadJSONIntoListView(getApplicationContext(), listView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // For the second floatingActionButton
    public class additionOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, AdditionActivity.class);
            startActivityForResult(intent, ADDITION_CODE);
        }
    }

    // For the navigationView
    public class onNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        private Context context;

        public onNavigationItemSelectedListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.nav_settings:{
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, SETTINGS_CODE);
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

        private Context context;

        public ReportDialogFragment(Context context) {
            this.context = context;
        }

        public static ReportDialogFragment newInstance(Context context, String title, String product) {
            ReportDialogFragment reportDialogFragment = new ReportDialogFragment(context);
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("product", product);
            reportDialogFragment.setArguments(args);
            return reportDialogFragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            String title = null;
            String productName = null;

            if (getArguments() != null) {
                title = getArguments().getString("title");
                productName = getArguments().getString("product");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String product = productName;
            builder.setTitle(title)
                    .setMessage(R.string.report_text)
                    .setPositiveButton(R.string.positive_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONHandler.startReport(context, product);
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

    // Uses name and brand to find product in json and opens it in a product activity
    public void showProduct(String productName, String productBrand){
        JSONArray jsonArray = null;
        int position = -1;

        try {
            jsonArray = new JSONArray(JSONHandler.getJson());
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (productName.equals(jsonObject.getString("name")) && productBrand.equals(jsonObject.getString("brand"))) {
                    position = i;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(position!=-1) { // if product was found
            Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
            intent.putExtra("position", position);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // necessary because showProduct is being called from the downloadRunnable (= outside of an activity)
            getApplication().startActivity(intent);
        } else {
            JSONHandler.startDownload(this, listView, productName, productBrand);
        }
    }
}