package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup UI components
        setupRideTypes();
        setupLocationInputs();
        setupButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission is required to use this app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                        }
                    }
                });
    }

    private void setupRideTypes() {
        LinearLayout rideTypeContainer = findViewById(R.id.rideTypeContainer);
        String[] rideTypes = {"Standard", "Premium", "XL", "Bike"};
        int[] rideIcons = {R.drawable.ic_car_standard, R.drawable.ic_car_premium,
                R.drawable.ic_car_xl, R.drawable.ic_bike};

        for (int i = 0; i < rideTypes.length; i++) {
            View rideTypeView = getLayoutInflater().inflate(R.layout.item_ride_type, rideTypeContainer, false);
            TextView tvRideType = rideTypeView.findViewById(R.id.tvRideType);
            tvRideType.setText(rideTypes[i]);
            tvRideType.setCompoundDrawablesWithIntrinsicBounds(0, rideIcons[i], 0, 0);

            final int position = i;
            rideTypeView.setOnClickListener(v -> selectRideType(position));

            rideTypeContainer.addView(rideTypeView);
        }
    }

    private void selectRideType(int position) {
        // Highlight selected ride type
        LinearLayout rideTypeContainer = findViewById(R.id.rideTypeContainer);
        for (int i = 0; i < rideTypeContainer.getChildCount(); i++) {
            View child = rideTypeContainer.getChildAt(i);
            child.setBackgroundResource(i == position ? R.drawable.ride_type_selected : R.drawable.ride_type_normal);
        }
    }

    private void setupLocationInputs() {
        EditText etPickupLocation = findViewById(R.id.etPickupLocation);
        EditText etDestination = findViewById(R.id.etDestination);

        // TODO: Implement location autocomplete
    }

    private void setupButtons() {
        Button btnRequestRide = findViewById(R.id.btnRequestRide);
        FloatingActionButton fabMyLocation = findViewById(R.id.fabMyLocation);

        btnRequestRide.setOnClickListener(v -> {
            // TODO: Implement ride request
            Toast.makeText(this, "Finding you a ride...", Toast.LENGTH_SHORT).show();
        });

        fabMyLocation.setOnClickListener(v -> {
            if (currentLatLng != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
            }
        });
    }
}