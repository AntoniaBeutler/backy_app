package com.backy.antoniabeutler.becky1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.backy.antoniabeutler.becky1.fragment.MainFragment;
import com.backy.antoniabeutler.becky1.fragment.MapFragment;
import com.backy.antoniabeutler.becky1.fragment.SettingFragment;
import com.backy.antoniabeutler.becky1.fragment.SocialFragment;

public class MainActivity extends AppCompatActivity implements LocationListener, MapFragment.OnFragmentInteractionListener, SocialFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener{

    private LocationManager locationManager;
    private String provider;
    private Location location;
    private MyAdapter mAdapter;

    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            if(level < 30) {
                lowBattery();
            } else {
                okayBattery();
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment;
            Bundle args;
            switch (menuItem.getItemId()){
                case R.id.main_side:
                    args = new Bundle();
                    fragment = new MainFragment();
                    if (location != null){
                        args.putDouble("latitude", location.getLatitude());
                        args.putDouble("longitude", location.getLongitude());
                        fragment.setArguments(args);
                    }
                    loadFragment(fragment);
                    return true;
                case R.id.map_side:
                    args = new Bundle();
                    fragment = new MapFragment();
                    if (location != null){
                        args.putDouble("latitude", location.getLatitude());
                        args.putDouble("longitude", location.getLongitude());
                        Toast.makeText(getApplicationContext(), "Na sieh mal einer an", Toast.LENGTH_SHORT).show();
                        fragment.setArguments(args);
                    }
                    loadFragment(fragment);
                    return true;
                case R.id.social_side:
                    fragment = new SocialFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.setting_side:
                    fragment = new SettingFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navItemSelectedListener);

        Fragment frag = new MainFragment();
        Bundle args = new Bundle();
        if (location != null){
            args.putDouble("latitude", location.getLatitude());
            args.putDouble("longitude", location.getLongitude());
            frag.setArguments(args);
        }

        loadFragment(frag);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            location = locationManager.getLastKnownLocation(provider);
        }

        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
        } else {

        }
        //this.registerReceiver(this.mBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_LOW));
        //this.registerReceiver(this.mBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_OKAY));
        this.registerReceiver(this.mBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void giveAdapter(MyAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void okayBattery(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }
    public void lowBattery(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(provider, 1000, 1, this);
        }
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
        if(mAdapter != null) {
            mAdapter.setLocation(location.getLatitude(), location.getLongitude());
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
}