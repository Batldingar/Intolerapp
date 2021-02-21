package com.baldware.intolerapp.customTools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.baldware.intolerapp.R;

public class StarListViewAdapter extends ArrayAdapter<String> {

    private final double[] ratings;
    private final int[] counts;
    private final String mainIntolerance;

    public StarListViewAdapter(Context context, Integer layoutID, Integer listViewId, String[] products, double[] ratings, int[] counts, String mainIntolerance) {
        super(context, layoutID, listViewId, products);
        this.ratings = ratings;
        this.counts = counts;
        this.mainIntolerance = mainIntolerance;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.star_listview_item, parent, false);

        RatingBar ratingBar = convertView.findViewById(R.id.star_listView_ratingBar);
        ratingBar.setRating((float) ratings[position]);
        setColor(ratingBar);

        TextView textView = convertView.findViewById(R.id.star_listView_ratings_textView);

        if (mainIntolerance.equals(getContext().getString(R.string.radio_fructose))) {
            textView.setText(getContext().getResources().getString(R.string.fructose_text, counts[position]));
        } else if (mainIntolerance.equals(getContext().getString(R.string.radio_glucose))) {
            textView.setText(getContext().getResources().getString(R.string.glucose_text, counts[position]));
        } else if (mainIntolerance.equals(getContext().getString(R.string.radio_histamine))) {
            textView.setText(getContext().getResources().getString(R.string.histamine_text, counts[position]));
        } else if (mainIntolerance.equals(getContext().getString(R.string.radio_lactose))) {
            textView.setText(getContext().getResources().getString(R.string.lactose_text, counts[position]));
        } else if (mainIntolerance.equals(getContext().getString(R.string.radio_sucrose))) {
            textView.setText(getContext().getResources().getString(R.string.sucrose_text, counts[position]));
        } else if (mainIntolerance.equals(getContext().getString(R.string.radio_sorbitol))) {
            textView.setText(getContext().getResources().getString(R.string.sorbitol_text, counts[position]));
        }

        return super.getView(position, convertView, parent);
    }

    private void setColor(RatingBar ratingBar) {
        if (ratingBar.getRating() <= 1.0) {
            ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.starsRed)));
        } else if (ratingBar.getRating() <= 2.0) {
            ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.starsOrange)));
        } else if (ratingBar.getRating() <= 3.0) {
            ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.starsYellow)));
        } else if (ratingBar.getRating() <= 4.0) {
            ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.starsLime)));
        } else {
            ratingBar.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.starsGreen)));
        }
    }
}
