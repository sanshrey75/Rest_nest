package com.example.myapplication.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlacesAutocompleteHelper {
    private static final String TAG = "PlacesAutocompleteHelper";
    private final PlacesClient placesClient;

    public PlacesAutocompleteHelper(Context context) {
        placesClient = Places.createClient(context);
    }

    public List<AutocompletePrediction> getAutocompletePredictions(String query) {
        List<AutocompletePrediction> predictions = new ArrayList<>();
        try {
            // Create a new session token for the autocomplete request
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            // Build the autocomplete request with location biasing if available
            FindAutocompletePredictionsRequest.Builder requestBuilder = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .setSessionToken(token);

            // Perform the autocomplete request
            predictions = Tasks.await(placesClient.findAutocompletePredictions(requestBuilder.build()))
                    .getAutocompletePredictions();

        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error getting autocomplete predictions", e);
        }
        return predictions;
    }

    public void fetchPlaceDetails(String placeId, OnPlaceDetailsFetchedListener listener) {
        try {
            // Define the fields to be returned
            List<Place.Field> placeFields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS,
                    Place.Field.ADDRESS_COMPONENTS
            );

            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                    .build();

            placesClient.fetchPlace(request)
                    .addOnSuccessListener(response -> {
                        listener.onPlaceDetailsFetched(response.getPlace());
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Place fetch failed", e);
                        listener.onPlaceDetailsFailed(e);
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error fetching place details", e);
            listener.onPlaceDetailsFailed(e);
        }
    }

    public interface OnPlaceDetailsFetchedListener {
        void onPlaceDetailsFetched(Place place);
        void onPlaceDetailsFailed(Exception e);
    }
}