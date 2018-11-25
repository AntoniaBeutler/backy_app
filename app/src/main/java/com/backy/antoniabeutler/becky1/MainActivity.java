package com.backy.antoniabeutler.becky1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private FloatingActionButton button;
    private Context context;

    private RecyclerView mRecyclerView;
   // private RecyclerView.Adapter mAdapter;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<Tile> tile_List = new ArrayList<>();

    private TextView latituteField, longitudeField;
    private LocationManager locationManager;
    private String provider;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latituteField = findViewById(R.id.lat);
        longitudeField = findViewById(R.id.longi);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
             location = locationManager.getLastKnownLocation(provider);
        }

        // Initialize the location fields
        if (location != null) {
            //Toast.makeText(context,"Provider " + provider + " has been selected.",Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        } else {
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }

        addTiles();
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
                        tile_List.add(new Tile(item.getTitle().toString()));

                        mRecyclerView = findViewById(R.id.my_recycler_view);
                        // use this setting to improve performance if you know that changes
                        // in content do not change the layout size of the RecyclerView
                        mRecyclerView.setHasFixedSize(true);

                        mLayoutManager = new GridLayoutManager(context,2);
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        mAdapter = new MyAdapter(context, tile_List, location);
                        mRecyclerView.setAdapter(mAdapter);

                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }

    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));

        if(mAdapter != null)
            mAdapter.setLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
}