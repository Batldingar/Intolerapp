package com.baldware.intolerapp;

import android.util.Log;
import android.util.Pair;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class SearchViewListener implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //Start Thread that starts the sorting
        //When thread is finished it should automatically put the text into the listview
        //When this is called again (because of a new letter) then restart the thread with the new string
        //Somehow also sort the whole thing

        try {
            search("Spa");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void search(String pattern) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        String[] products = new String[jsonArray.length()];
        String[] brands = new String[jsonArray.length()];
        ArrayList<Pair> resultList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            products[i] = jsonObject.getString("name");
            brands[i] = jsonObject.getString("brand");
        }

        // Filter the products
        for(int i = 0; i < products.length; i++) {
            boolean productContains = products[i].toLowerCase().contains(pattern.toLowerCase());
            boolean brandContains = brands[i].toLowerCase().contains(pattern.toLowerCase());
            String fullName = products[i] + " - " + brands[i];
            if(productContains && brandContains) {
                resultList.add(new Pair(fullName, Math.min(products[i].indexOf(pattern), brands[i].indexOf(pattern))));
            } else {
                if(productContains) {
                    resultList.add(new Pair(fullName, products[i].indexOf(pattern)));
                } else if(brandContains){
                    resultList.add(new Pair(fullName, brands[i].indexOf(pattern)));
                }
            }
        }

        // Sort the products
        boolean neededToSwap;
        do {
            neededToSwap = false;

            for (int i = 1; i < resultList.size(); i++) {
                if ((Integer) resultList.get(i).second < (Integer) resultList.get(i - 1).second) {
                    Pair buffer = resultList.get(i-1);
                    resultList.set(i-1, resultList.get(i));
                    resultList.set(i, buffer);
                    neededToSwap = true;
                }
            }
        } while (neededToSwap);

        // List the new
        for(Pair pair : resultList) {
            Log.d("Main", "Result: " + pair.first + " Value: " + pair.second);
        }
    }
}
