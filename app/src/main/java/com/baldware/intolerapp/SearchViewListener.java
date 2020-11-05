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
        //Somehow also sort the whole thing
        //Give the results back somehow
        //Look for multithreading opportunities

        long startTime = System.nanoTime();

        try {
            search("S");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        long delta = (System.nanoTime() - startTime) / 1000000;
        Log.d("Main", "Took " + delta + "mils");

        return false;
    }

    private ArrayList<Pair> search(String pattern) throws JSONException {
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
        for (int i = 0; i < products.length; i++) {
            String lowerCasePattern = pattern.toLowerCase();
            String lowerCaseProduct = products[i].toLowerCase();
            String lowerCaseBrand = brands[i].toLowerCase();

            boolean productContains = lowerCaseProduct.contains(lowerCasePattern);
            boolean brandContains = lowerCaseBrand.contains(lowerCasePattern);

            String fullName = products[i] + " - " + brands[i];
            if (productContains && brandContains) {
                resultList.add(new Pair(fullName, Math.min(lowerCaseProduct.indexOf(lowerCasePattern), lowerCaseBrand.indexOf(lowerCasePattern))));
            } else {
                if (productContains) {
                    resultList.add(new Pair(fullName, lowerCaseProduct.indexOf(lowerCasePattern)));
                } else if (brandContains) {
                    resultList.add(new Pair(fullName, lowerCaseBrand.indexOf(lowerCasePattern)));
                }
            }
        }

        // Sort the products
        quickSort(resultList, 0, resultList.size()-1);
        //slowSort(resultList);

        // List the new
        for (Pair pair : resultList) {
            Log.d("Main", "Result: " + pair.first + " Value: " + pair.second);
        }

        return resultList;
    }

    private void slowSort(ArrayList<Pair> resultList) {
        boolean neededToSwap;
        do {
            neededToSwap = false;

            for (int i = 1; i < resultList.size(); i++) {
                Pair pair1 = resultList.get(i);
                Pair pair2 = resultList.get(i - 1);
                if ((Integer) pair1.second < (Integer) pair2.second) {
                    Pair buffer = pair2;
                    resultList.set(i - 1, pair1);
                    resultList.set(i, buffer);
                    neededToSwap = true;
                }
            }
        } while (neededToSwap);
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