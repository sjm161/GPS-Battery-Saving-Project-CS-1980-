package com.gps.gpsoptimizationproject;

import android.content.Context;
import android.content.Intent;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    static LocationListener templistener;
    static LocationManager GPSDetector;
    static boolean textstatus = false;
    static String beforeEnable;
    static TextView MainText;
    static TextView GPSText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //declaring the menu items that were created in the xml files
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GPSText = findViewById(R.id.GPSStatus);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Hello World Again!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
               moveToMapActivity();
            }
        });
        final TextView gpsIndicator = (TextView) findViewById(R.id.GPSindicator);
        MainText = (TextView) findViewById(R.id.MainTextView);
        //Making the button do something
        Button gpsButton = (Button) findViewById(R.id.GPSToggle);

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textstatus){
                    textstatus = false;
                    gpsIndicator.setText("GPS ON");
                    turnGpsOn(getApplicationContext());
                }
                else{
                    textstatus = true;
                    gpsIndicator.setText("GPS OFF");
                    turnGpsOff(getApplicationContext());
                }
            }
        });
        //Now to add the GPS Status listener
        setUpGpsListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /* Note from Matt - Code from StackOverFlow at this link.
    https://stackoverflow.com/questions/4721449/how-can-i-enable-or-disable-the-gps-programmatically-on-android
    AS raises errors which means we need to figure out what does what and if this even works with
    current version of android*/

    private void turnGpsOn (Context context) {
        //startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        //Notes from Matt
        //This code here seems to not use caching as it can take a while to reacquire the GPS signal....
        /*beforeEnable = Settings.Secure.getString (context.getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        String newSet = String.format ("%s,%s",
                beforeEnable,
                LocationManager.GPS_PROVIDER);
        try {
            Settings.Secure.putString (context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
                    newSet);
        } catch(Exception e) {
            //Let's add in some error checking - lets set one of the text views to an error message
            //for exception handling
            MainText.setText(e.getMessage());
        }*/
        //boolean gpsStatus = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //This modified line seems to acquire the GPS signal quickly
        Settings.Secure.putString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "network,gps");
    }


    private void turnGpsOff (Context context) {
        //LocationServices.SettingsApi
        if (null == beforeEnable) {
            String str = Settings.Secure.getString (context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (null == str) {
                str = "";
            } else {
                String[] list = str.split (",");
                str = "";
                int j = 0;
                for (int i = 0; i < list.length; i++) {
                    if (!list[i].equals (LocationManager.GPS_PROVIDER)) {
                        if (j > 0) {
                            str += ",";
                        }
                        str += list[i];
                        j++;
                    }
                }
                beforeEnable = str;
            }
        }
        try {
            Settings.Secure.putString (context.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED,
                    beforeEnable);
        } catch(Exception e) {
            //Let's add in some error checking - lets set one of the text views to an error message
            //for exception handling
            MainText.setText(e.getMessage());
        }
    }
    //Okay- this is code to detect when the sattelite is connected
    private void setUpGpsListener(){
        //First initialize the GPS Locator
        GPSDetector = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setnewLocationListener();
        try{
            //Now add a GPS Status listener
            GPSDetector.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, templistener);
            @Deprecated GpsStatus.Listener test = new GpsStatus.Listener() {
                //Now to implement a listener
                @Override
                public void onGpsStatusChanged(int event) {
                    switch (event){
                        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                            break;
                        case GpsStatus.GPS_EVENT_FIRST_FIX:   // this means you  found GPS Co-ordinates
                            GPSText.setText("Got First Fix");
                            break;
                        case GpsStatus.GPS_EVENT_STARTED:
                            GPSText.setText("GPS Started!");
                            break;
                        case GpsStatus.GPS_EVENT_STOPPED:
                            GPSText.setText("GPS Stopped");
                            break;

                    }
                }
            };
            GPSDetector.addGpsStatusListener(test);
        }catch (SecurityException e){
            MainText.setText(e.getMessage());
        }
        catch (Exception e){
            MainText.setText(e.getMessage());
        }
    }

    private void moveToMapActivity(){
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    private void setnewLocationListener(){
       templistener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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
}
