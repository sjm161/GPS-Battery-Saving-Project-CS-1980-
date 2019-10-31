package com.gps.gpsoptimizationproject;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Location currentLocation;
    private GoogleMap mMap;
    LocationManager LocM;
    LocationListener newlistener;
    TextView velocitydisplay, distancedisplay, timedisplay;
    Location destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        velocitydisplay = findViewById(R.id.VelocityView);
        distancedisplay = findViewById(R.id.DistanceView);
        timedisplay = findViewById(R.id.TimeView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        FloatingActionButton distbut = findViewById(R.id.dist);

        destination = new Location("");
        destination.setLatitude(40.444396);
        destination.setLongitude(-79.954794);

        distbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcDistance(destination);
            }
        });
        setnewLocationListener();
        LocM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            //LocM.requestSingleUpdate(LocationManager.GPS_PROVIDER, null);
            LocM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, newlistener);
        } catch (SecurityException e){
            velocitydisplay.setText(e.getMessage());
        }
    }

    private void fetchlastlocation(){

    }
    /*protected void createLocationRequest(){
        LocationRequest LocR = LocationRequest.create();

    }*/


    private void calcDistance(Location dest) {
        try {
            LocationManager tempmanager;
            //tempmanager.requestSingleUpdate( LocationManager.GPS_PROVIDER, new MyLocationListenerGPS(), null );
            Location cur = LocM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Location cur = new Location("");
            //cur.setLongitude(-79.953829);
            //cur.setLatitude(40.442469);
            if(cur == null) {
                distancedisplay.setText("Cur is null");
            } else {
                float distance = cur.distanceTo(dest);
                distancedisplay.setText(String.valueOf(distance) + " || " + cur.getLatitude() + " || " + cur.getLongitude());
            }
        } catch (Exception e){
            velocitydisplay.setText(e.getMessage());
        }
    }
    private float calcDistance(Location cur, Location dest) {
        try {
            //Location cur = LocM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Location cur = new Location("");
            //cur.setLongitude(-79.953829);
            //cur.setLatitude(40.442469);
            if(cur == null) {
                distancedisplay.setText("Cur is null");
                return 0f;
            } else {
                float distance = cur.distanceTo(dest);
                //distancedisplay.setText(String.valueOf(distance) + " || " + cur.getLatitude() + " || " + cur.getLongitude());

                distancedisplay.setText(String.valueOf(distance) + " m");
                return distance;
            }
        } catch (Exception e){
            velocitydisplay.setText(e.getMessage());
        }
        return 0f;
    }

    private void setnewLocationListener(){
        newlistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                velocitydisplay.setText("Trying to get velocity");
                if(location.hasSpeed()) {

                    velocitydisplay.setText(String.valueOf(location.getSpeed()) + " m/s");
                    float time = calcDistance(location, destination)/location.getSpeed();
                    timedisplay.setText(String.valueOf(time) + " s");
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Now to get the user's live speed

        // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        
    }
}
