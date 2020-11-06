package com.baldware.intolerapp;

import android.util.Log;
import android.util.Pair;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class SearchViewListener implements SearchView.OnQueryTextListener {
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO: Implement the following tasks
        //Start Thread that starts the sorting
        //When thread is finished it should automatically put the text into the listview
        //When this is called again (because of a new letter) then restart the thread with the new string
        //Give the results back somehow
        //Look for multithreading opportunities

        try {
            search("Pa");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private ArrayList<Pair> search(String pattern) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        ArrayList<Pair> resultList = new ArrayList<>();

        // Perform search and valuation
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String product = jsonObject.getString("name");
            String brand = jsonObject.getString("brand");

            String lowerCasePattern = pattern.toLowerCase();
            String lowerCaseProduct = product.toLowerCase();
            String lowerCaseBrand = brand.toLowerCase();

            boolean productContains = lowerCaseProduct.contains(lowerCasePattern);
            boolean brandContains = lowerCaseBrand.contains(lowerCasePattern);

            // Only check if either product or brand contain the pattern
            if(productContains || brandContains) {
                String fullName = product + " - " + brand;
                if (productContains && brandContains) {
                    resultList.add(new Pair(fullName, Math.min(lowerCaseProduct.indexOf(lowerCasePattern), lowerCaseBrand.indexOf(lowerCasePattern))));
                } else {
                    // If one doesn't contain the pattern then the other one has to
                    if (productContains) {
                        resultList.add(new Pair(fullName, lowerCaseProduct.indexOf(lowerCasePattern)));
                    } else {
                        resultList.add(new Pair(fullName, lowerCaseBrand.indexOf(lowerCasePattern)));
                    }
                }
            }
        }

        // Sort the products
        quickSort(resultList, 0, resultList.size()-1);

        for(Pair pair : resultList) {
            Log.d("Main", "Result: " + pair.first + " Value: " + pair.second);
        }

        return resultList;
    }

    private void quickSort(ArrayList<Pair> pairArrayList, int low, int high) {
        if (pairArrayList == null || pairArrayList.size() == 0) {
            return;
        }

        if (low >= high) {
            return;
        }

        // pick the pivot
        int middle = low + (high - low) / 2;
        int pivot = (int)pairArrayList.get(middle).second;

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            while ((int)pairArrayList.get(i).second < pivot) {
                i++;
            }

            while ((int)pairArrayList.get(j).second > pivot) {
                j--;
            }

            if (i <= j) {
                Pair temp = pairArrayList.get(i);
                pairArrayList.set(i, pairArrayList.get(j));
                pairArrayList.set(j, temp);
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j)
            quickSort(pairArrayList, low, j);

        if (high > i)
            quickSort(pairArrayList, i, high);
    }
}