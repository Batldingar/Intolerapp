package com.baldware.intolerapp.customTools;

import android.widget.SearchView;

import com.baldware.intolerapp.activities.MainActivity;
import com.baldware.intolerapp.json.JSONHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchViewListener implements SearchView.OnQueryTextListener {

    private static ArrayList<String[]> searchResult;

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(!newText.equals("")) {
            try {
                search(newText);
                MainActivity.loadSearchIntoListView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(JSONHandler.getJson() != null) {
            try {
                searchResult = null;
                MainActivity.loadJSONIntoListView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void search(String pattern) throws JSONException {
        JSONArray jsonArray = new JSONArray(JSONHandler.getJson());
        searchResult = new ArrayList<>();

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
                // Store in searchResultStrings: fullName, evaluationRating and index [0, 1, 2]
                if (productContains && brandContains) {
                    searchResult.add(new String[]{fullName, Integer.toString(Math.min(lowerCaseProduct.indexOf(lowerCasePattern), lowerCaseBrand.indexOf(lowerCasePattern))), Integer.toString(i)});
                } else {
                    // If one doesn't contain the pattern then the other one has to
                    if (productContains) {
                        searchResult.add(new String[]{fullName, Integer.toString(lowerCaseProduct.indexOf(lowerCasePattern)), Integer.toString(i)});
                    } else {
                        searchResult.add(new String[]{fullName, Integer.toString(lowerCaseBrand.indexOf(lowerCasePattern)), Integer.toString(i)});
                    }
                }
            }
        }

        // Sort the products
        quickSort(searchResult, 0, searchResult.size()-1);
    }

    private void quickSort(ArrayList<String[]> stringArrayList, int low, int high) {
        if (stringArrayList == null || stringArrayList.size() == 0) {
            return;
        }

        if (low >= high) {
            return;
        }

        // pick the pivot
        int middle = low + (high - low) / 2;
        int pivot = Integer.parseInt(stringArrayList.get(middle)[1]);

        // make left < pivot and right > pivot
        int i = low, j = high;
        while (i <= j) {
            while (Integer.parseInt(stringArrayList.get(i)[1]) < pivot) {
                i++;
            }

            while (Integer.parseInt(stringArrayList.get(j)[1]) > pivot) {
                j--;
            }

            if (i <= j) {
                String[] temp = stringArrayList.get(i);
                stringArrayList.set(i, stringArrayList.get(j));
                stringArrayList.set(j, temp);
                i++;
                j--;
            }
        }

        // recursively sort two sub parts
        if (low < j)
            quickSort(stringArrayList, low, j);

        if (high > i)
            quickSort(stringArrayList, i, high);
    }

    public static ArrayList<String[]> getSearchResult() {
        return searchResult;
    }
}