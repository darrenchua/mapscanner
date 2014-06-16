package com.example.mapscanner;
 
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
 
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.os.Bundle;

import android.widget.Toast;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;


public class MainActivity extends Activity {
	 
    // Google Map
    private GoogleMap googleMap;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(lat, lon)).zoom(18).build();
     
    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
    
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
 
}