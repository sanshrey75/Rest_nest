package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.utils.PlacesAutocompleteHelper;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

public class PlacesAutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    private List<AutocompletePrediction> predictions;
    private final PlacesAutocompleteHelper placesAutocompleteHelper;

    public PlacesAutoCompleteAdapter(Context context, PlacesAutocompleteHelper helper) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        this.predictions = new ArrayList<>();
        this.placesAutocompleteHelper = helper;
    }

    @Override
    public int getCount() {
        return predictions.size();
    }

    @Nullable
    @Override
    public AutocompletePrediction getItem(int position) {
        if (position >= 0 && position < predictions.size()) {
            return predictions.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        AutocompletePrediction item = getItem(position);
        TextView textView = convertView.findViewById(android.R.id.text1);

        if (item != null) {
            // Show both primary and secondary text for better context
            textView.setText(item.getFullText(null));
        } else {
            textView.setText("");
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() >= 3) { // Only search when at least 3 characters
                    List<AutocompletePrediction> filtered = placesAutocompleteHelper.getAutocompletePredictions(constraint.toString());
                    results.values = filtered;
                    results.count = filtered.size();
                } else {
                    results.values = new ArrayList<>();
                    results.count = 0;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    predictions = (List<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    predictions.clear();
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}