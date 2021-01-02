package com.baldware.intolerapp.customTools;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baldware.intolerapp.R;

public class StarListViewAdapter extends ArrayAdapter<String> {

    private double[] ratings;
    
    public StarListViewAdapter(Context context, Integer layoutID, Integer listViewId, String[] products, double[] ratings) {
        super(context, layoutID, listViewId, products);
        this.ratings = ratings;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Log.d("Main", "HELP, I NEED SOMEBODY! HELP! NOT JUST ANYBODY!  >>> " + position);
        //TODO: FIX WHATEVER THIS IS
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.star_listview_item, parent, false);

        RatingBar ratingBar = convertView.findViewById(R.id.star_listView_ratingBar);
        ratingBar.setRating((float)ratings[position]);

        return super.getView(position, convertView, parent);
    }
}
