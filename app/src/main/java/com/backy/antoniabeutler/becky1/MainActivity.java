package com.backy.antoniabeutler.becky1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.bonuspack.location.POI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton button;
    private Context context;
    private Location lastLocation;
    protected LocationManager mLocationManager;
    public static ArrayList<POI> mPOIs;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    boolean noLocation = false;
    private
    LocationListener lListener;

    private FusedLocationProviderClient mFusedLocationClient;

    List<Tile> tile_List = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        lListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location l) {
                lastLocation = l;
                Double lat = l.getLatitude();
                Double lng = l.getLongitude();

                Log.i("Location info: Lat", lat.toString());
                Log.i("Location info: Lng", lng.toString());
                writeLocation(lastLocation);
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
        addTiles();
        writeLocation(lastLocation,"gut");

        checkLocationPermission();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
        lastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //lListener.onLocationChanged(lastLocation);
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
        writeLocation(lastLocation);
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 100,this);

        /*if (savedInstanceState == null){
            Location location = null;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null)
                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {
                lastLocation = location;
                writeLocation(lastLocation);
                //location known:
                //onLocationChanged(location);
            } else {
                //no location known: hide myLocationOverlay
                noLocation = true;
                lastLocation =null;
            }
        } else {
           // myLocationOverlay.setLocation((GeoPoint)savedInstanceState.getParcelable("location"));
            //TODO: restore other aspects of myLocationOverlay...
           /* startPoint = savedInstanceState.getParcelable("start");
            destinationPoint = savedInstanceState.getParcelable("destination");
            viaPoints = savedInstanceState.getParcelableArrayList("viapoints");
        }*/

    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("location permission")
                        .setMessage("jo")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //mLocationManager.requestLocationUpdates(provider, 400, 1, this);
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private void writeLocation(Location location) {
        writeLocation(location,"Hallo" );
    }

    private void writeLocation(Location location, String s){
        TextView t1, t2;
        t1 = findViewById(R.id.latitude);
        t2 = findViewById(R.id.longitude);
        if(location == null){
            t1.setText(s);
            t2.setText(s);
            return;
        }
        t1.setText(new Double(location.getLatitude()).toString());
        t2.setText(Double.toString(location.getLongitude()));
    }



    private void addTiles(){
        context = getApplicationContext();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, button);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        for (Tile t : tile_List){
                            if (t.getTile_name().equals(item.getTitle().toString())){
                                return true;
                            }
                        }
                        tile_List.add(new Tile("0", item.getTitle().toString(), item.getItemId()));

                        mRecyclerView = findViewById(R.id.my_recycler_view);
                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        mRecyclerView.setHasFixedSize(true);

                        mLayoutManager = new GridLayoutManager(context,2);
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new MyAdapter(context, tile_List);
                        mRecyclerView.setAdapter(mAdapter);

                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 400, 1, lListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLocationManager.removeUpdates(lListener);
        }
    }


}