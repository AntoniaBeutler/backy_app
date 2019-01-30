package com.backy.antoniabeutler.becky1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.backy.antoniabeutler.becky1.fragment.MainFragment;
import com.backy.antoniabeutler.becky1.fragment.MapFragment;
import com.backy.antoniabeutler.becky1.fragment.SettingFragment;

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements LocationListener, MapFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener{

    public static SettingFragment settingF;
    public static MainFragment mainF;
    public static MapFragment mapF;
    public static FragmentManager fragManager;
    public static BottomNavigationView navigation;
    private MyAdapter mAdapter;

    public static SQLiteHelper sqLiteHelper;

    private LocationManager locationManager;
    private String provider;
    public static Location lastLocation,lastPoiLocation;
    public static HashMap<String,ArrayList<POI>> mPois = new HashMap<>();
    public static HashMap<String, Double> shortestdistance = new HashMap<>();

    private int updateTime = 300000;
    private boolean mapState = false;
    private boolean lowB = false;
    private boolean defaultLoc;

    //BroadcastReceiver to get information's about charging status and the battery level for adapting GPS update interval and for using a 'Power Saving' mode
    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int chargingStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING;

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Cursor cursor = sqLiteHelper.getPowerSaving();
            cursor.moveToFirst();
            int b = Integer.parseInt(cursor.getString(cursor.getColumnIndex("power_saving")));
            if((level <= 5)&&(b == 1) && !isCharging) {
                if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)){
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            } else if (level < 30 && !defaultLoc && !isCharging){
                lowB = true;
                lowBattery();
            } else if(!defaultLoc){
                lowB = false;
                okayBattery();
            }
        }
    };

    //Listener for navigation bar
    private BottomNavigationView.OnNavigationItemSelectedListener navItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Bundle args;
            switch (menuItem.getItemId()){
                case R.id.main_side:
                    args = new Bundle();
                    if(mapState){
                        mapState = false;
                        if(!defaultLoc) requestLocUpdate();
                    }

                    if(mainF == null)
                        mainF = new MainFragment();
                    if(mainF.isAdded()){
                        return true;
                    } else {

                        if (lastLocation != null && !defaultLoc){
                            args.putDouble("latitude", lastLocation.getLatitude());
                            args.putDouble("longitude", lastLocation.getLongitude());
                            mainF.setArguments(args);
                        }else if(defaultLoc){

                            args.putDouble("latitude", sqLiteHelper.getLocation().getLatitude());
                            args.putDouble("longitude", sqLiteHelper.getLocation().getLongitude());
                            mainF.setArguments(args);

                        }
                        loadFragment(mainF);
                        return true;
                    }

                case R.id.map_side:
                    args = new Bundle();
                    if(mapF == null)
                        mapF = new MapFragment();
                    args.putString("poiType","nothing");
                    if(mapF.isAdded())
                        return true;
                    if (lastLocation != null){
                        args.putDouble("latitude", lastLocation.getLatitude());
                        args.putDouble("longitude", lastLocation.getLongitude());
                        args.putBoolean("locationAvailable",false);

                    }else{
                        args.putBoolean("locationAvailable",true);
                    }
                    mapF.setArguments(args);
                    loadFragment(mapF);
                    mapState = true;
                    if(!defaultLoc) requestLocUpdate();
                    return true;

                case R.id.setting_side:
                    if(mapState){
                        mapState = false;
                        if(!defaultLoc) requestLocUpdate();
                    }
                    if(settingF == null)
                        settingF = new SettingFragment();
                    if(settingF.isAdded())
                        return true;
                    loadFragment(settingF);
                    return true;
            }
            return false;
        }
    };


    public class NetTask extends AsyncTask<String, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(String... params)
        {
            InetAddress addr = null;
            boolean b = false;
            try
            {
                addr = InetAddress.getByName(params[0]);
                boolean reach = addr.isReachable(1000);
                if (isOnline() && reach) {
                    System.out.println("Reachable");
                    b = true;
                } else if (isOnline() && !reach){
                    System.out.println("For testing purpose on virtual devices! Maybe not the best solution for testing if ping available.");
                    b = true;
                } else {
                    System.out.println("Not Reachable");
                    b = false;
                }
            }

            catch (UnknownHostException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return b;
        }


        protected void onPostExecute(Boolean b) {
            if (b){
                loadPois();
            }
        }

    }

    private void ping(){
        Boolean netAddress = false;
        try
        {
            netAddress = new NetTask().execute("www.google.com").get();

        }
        catch (Exception e1)
        {
            System.out.println("Error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //getApplicationContext().deleteDatabase("BackyDatabase");

        sqLiteHelper = new SQLiteHelper(this);
        try {
            sqLiteHelper.loadImages();
            sqLiteHelper.loadSettings();
        } catch (android.database.sqlite.SQLiteConstraintException e){
            System.out.println("Database already initialized!");
        }

        fragManager = getSupportFragmentManager();
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navItemSelectedListener);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            lastLocation = locationManager.getLastKnownLocation(provider);
        }

        // Initialize the location fields
        defaultLoc = sqLiteHelper.getUseLocation();
        if (lastLocation != null && !defaultLoc) {
            onLocationChanged(lastLocation);
        } else if(defaultLoc){
            if(isOnline()){
                ping();
            }
        }

        mainF = new MainFragment();
        Bundle args = new Bundle();
        if (lastLocation != null && !defaultLoc){
            args.putDouble("latitude", lastLocation.getLatitude());
            args.putDouble("longitude", lastLocation.getLongitude());
            mainF.setArguments(args);
        }else if(defaultLoc){
            GeoPoint g = sqLiteHelper.getLocation();
            args.putDouble("latitude", g.getLatitude());
            args.putDouble("longitude", g.getLongitude());
            mainF.setArguments(args);
        }
        loadFragment(mainF);

        this.registerReceiver(this.mBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //get the adapter from the main fragment
    @Override
    public void giveAdapter(MyAdapter adapter) {
        this.mAdapter = adapter;
    }

    // called by settingFragment -> use default Location
    @Override
    public void useDefaultLocation(boolean value) {
        defaultLoc = value;
        if(value){
            locationManager.removeUpdates(this);
            mAdapter.setLocation(sqLiteHelper.getLocation().getLatitude(), sqLiteHelper.getLocation().getLongitude());
            if(isOnline()){
                ping();
            }
        }else{
            requestLocUpdate();
            if(isOnline()){
                ping();
            }
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (!defaultLoc && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            requestLocUpdate();
        }

    }

    /* Remove the locationListener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        if(mAdapter != null) {
            lastLocation = location;
            if(isOnline()){
                if(lastPoiLocation != null){
                    //point from where POIs where loaded last is more than 5km away than load new POIs
                    if(lastLocation.distanceTo(lastPoiLocation) > 5000 ){
                        lastPoiLocation = lastLocation;
                        ping();
                    }
                }else {
                    lastPoiLocation = lastLocation;
                    ping();
                }
            }
            // if map is visible, update location
            if(mapF != null) {
                mapF.updateLocation(new GeoPoint(location));
                mapF.POIMap();
            }
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

    //used by BroadcastReceiver to change the GPS update interval according to battery capacity (here: >= 30)
    private void okayBattery(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(mapState) updateTime = 10000;
            else updateTime = 300000; // 300s = 5min
            locationManager.requestLocationUpdates(provider, updateTime, 1, this);
        }
    }

    //used by BroadcastReceiver to change the GPS update interval according to battery capacity (here: < 30)
    private void lowBattery(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(mapState) updateTime = 20000;
            else updateTime = 600000; // 600s = 10min
            locationManager.requestLocationUpdates(provider, updateTime, 1, this);
        }
    }

    //trigger LocationUpdates
    private void requestLocUpdate(){
        if(lowB)
            lowBattery();
        else
            okayBattery();

    }

    //load POIs
    private void loadPois(){
        getPOIAsync("Campingside");
        getPOIAsync("Train Station");
        getPOIAsync("Water");
        getPOIAsync("Restaurant");
        getPOIAsync("Hotel");
        getPOIAsync("Hostel");
        getPOIAsync("Bus Station");
        getPOIAsync("Supermarket");
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragManager.beginTransaction();
        transaction.add(fragment, "");
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // asyncTask for loading POIs in Background
    private class POILoadingTask extends AsyncTask<String, Void, ArrayList<POI>> {
        String mFeatureTag;
        Cursor cursor;
        double maxDistance;
        protected ArrayList<POI> doInBackground(String... params) {

            mFeatureTag = params[0];
            String osmTag = mFeatureTag;
            switch (mFeatureTag){
                case "Campingside": osmTag = "camp_site"; break;
                //case "Water": poiType = "drinking_water"; break;
                case "Train Station": osmTag = "station"; break;
                case "Bus Station": osmTag = "bus_station"; break;
            }

            cursor = sqLiteHelper.getPOIAmount();
            cursor.moveToFirst();
            int amount = Integer.parseInt(cursor.getString(cursor.getColumnIndex("poi_amount")));
            cursor.close();
            cursor = sqLiteHelper.getPOIRadius();
            cursor.moveToFirst();
            maxDistance = (Double.parseDouble(cursor.getString(cursor.getColumnIndex("poi_radius"))));
            cursor.close();
            maxDistance = maxDistance/100.0;
            GeoPoint gp;
            if (lastLocation!=null){
                gp = (!defaultLoc)? new GeoPoint(lastLocation.getLatitude(),lastLocation.getLongitude()): sqLiteHelper.getLocation();
            } else {
                gp = new GeoPoint(0.0, 0.0);
            }
            NominatimPOIProvider poiProvider = new NominatimPOIProvider("Backy-App_for_backpacker_and_travelers_university_project");
            ArrayList<POI> pois = poiProvider.getPOICloseTo(gp, osmTag, amount, maxDistance);
            if(pois != null && !pois.isEmpty()){
                double dist = pois.get(0).mLocation.distanceToAsDouble(gp);
                for(POI p : pois){
                    double distP = p.mLocation.distanceToAsDouble(gp);
                    if(distP < dist){
                        dist = distP;
                    }
                }
                shortestdistance.put(mFeatureTag,dist);
            }else{
                shortestdistance.remove(mFeatureTag);
            }
            return pois;
        }

        protected void onPostExecute(ArrayList<POI> pois) {
            mPois.put(mFeatureTag,pois);
            if(mapF != null){
                mapF.POIMap();
            }
            mAdapter.notifyDataSetChanged();
        }

    }

    // call a new PoiLoadingTask for a specific tag
    private void getPOIAsync(String tag){
        if(mPois.containsKey(tag)) mPois.remove(tag);
        new POILoadingTask().execute(tag);
    }

    //check if network connection is available
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }



}