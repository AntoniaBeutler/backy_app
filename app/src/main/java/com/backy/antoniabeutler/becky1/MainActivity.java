package com.backy.antoniabeutler.becky1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener, MapFragment.OnFragmentInteractionListener, SocialFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener{

    private LocationManager locationManager;
    private String provider;
    private Location lastLocation,lastPoiLocation;
    private MyAdapter mAdapter;
    private Fragment mainF,mapF,socialF,settingF;


    public static HashMap<String,ArrayList<POI>> mPois = new HashMap<String,ArrayList<POI>>();
    public static GeoPoint homepoint = new GeoPoint(51.029585,13.7455735);
    protected static boolean useLoc = false;
    public static HashMap<String, Double> shortestdistance = new HashMap<>();

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
                    if(mainF == null)
                        mainF = new MainFragment();
                    if (lastLocation != null){
                        args.putDouble("latitude", lastLocation.getLatitude());
                        args.putDouble("longitude", lastLocation.getLongitude());
                        mainF.setArguments(args);
                    }
                    loadFragment(mainF);
                    return true;
                case R.id.map_side:
                    args = new Bundle();
                    mapF = new MapFragment();
                    if (lastLocation != null){
                        args.putDouble("latitude", lastLocation.getLatitude());
                        args.putDouble("longitude", lastLocation.getLongitude());
                        Toast.makeText(getApplicationContext(), "Na sieh mal einer an", Toast.LENGTH_SHORT).show();
                        mapF.setArguments(args);
                    }
                    loadFragment(mapF);
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
        if (lastLocation != null){
            args.putDouble("latitude", lastLocation.getLatitude());
            args.putDouble("longitude", lastLocation.getLongitude());
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
            lastLocation = locationManager.getLastKnownLocation(provider);
        }

        // Initialize the location fields
        if (lastLocation != null) {
            onLocationChanged(lastLocation);
        } else {
            loadPois();
        }

        if(lastPoiLocation != null) useLoc =true;

        //this.registerReceiver(this.mBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_LOW));
        //this.registerReceiver(this.mBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_OKAY));
        this.registerReceiver(this.mBatteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Toast.makeText(getApplicationContext(),"Hallo",Toast.LENGTH_SHORT).show();

        Toast.makeText(getApplicationContext(),"Hallo2",Toast.LENGTH_SHORT).show();

    }
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
            locationManager.requestLocationUpdates(provider, 2000, 1, this);
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
        //locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mAdapter != null) {
            lastLocation = location;
            if(lastPoiLocation != null){
                if(lastLocation.distanceTo(lastPoiLocation) > 100 ){
                    lastPoiLocation = lastLocation;
                    loadPois();
                }
            }else {
                lastPoiLocation = lastLocation;
                loadPois();

            }
            if(mapF != null)

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

    private class POILoadingTask extends AsyncTask<String, Void, ArrayList<POI>> {
        String mFeatureTag;
        String message;
        protected ArrayList<POI> doInBackground(String... params) {

            mFeatureTag = params[0];
            String osmTag = mFeatureTag;
            switch (mFeatureTag){
                case "Campingside": osmTag = "camp_site"; break;
                //case "Water": poiType = "drinking_water"; break;
                case "Train Station": osmTag = "station"; break;
                case "Bus Station": osmTag = "bus_station"; break;
            }

            double maxDistance = 0.1;
            //String osmTag = getOSMTag(mFeatureTag);

            GeoPoint gp = (useLoc)? new GeoPoint(lastLocation.getLatitude(),lastLocation.getLongitude()): homepoint;

            NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
            ArrayList<POI> pois = poiProvider.getPOICloseTo(gp, osmTag, 10, maxDistance);
            if(!pois.isEmpty()){
                double dist = pois.get(0).mLocation.distanceToAsDouble(gp);
                for(POI p : pois){
                    double distP = p.mLocation.distanceToAsDouble(gp);
                    if(distP < dist){
                        dist = distP;
                    }
                }
                shortestdistance.put(mFeatureTag,dist);
            }
            return pois;
        }
        protected void onPostExecute(ArrayList<POI> pois) {
            mPois.put(mFeatureTag,pois);
        }

    }
    void getPOIAsync(String tag){
        if(mPois.containsKey(tag)) mPois.remove(tag);
        new POILoadingTask().execute(tag);
    }

}