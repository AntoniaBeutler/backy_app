package com.backy.antoniabeutler.becky1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements MapEventsReceiver {


    protected FolderOverlay mRoadNodeMarkers;
    protected DirectedLocationOverlay myLocationOverlay;
    protected Polyline roadOverlay;
    MapView map = null;
    Location lastLocation = null;
    GeoPoint homepoint =new GeoPoint(51.029585,13.7455735);
    String poiType;
    Double longLocation, latLocation;
    Boolean noLocation = false;

    public ArrayList<POI> poiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        noLocation = getIntent().getExtras().getBoolean("noLocation");
        poiType = getIntent().getExtras().getString("type");
        longLocation = getIntent().getExtras().getDouble("longitudeLocation");
        latLocation = getIntent().getExtras().getDouble("latitudeLocation");

        mapfunc();

        poiList = MainActivity.mPois.get(poiType);
        if(poiList.isEmpty())
            Toast.makeText(getApplicationContext(),"List is empty",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),"List is not empty",Toast.LENGTH_LONG).show();
        POIMap();

        //getPOIAsync(poiType);
        //askPermission();

    }



    private void mapfunc(){
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setContentView(R.layout.map_activity);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setUseDataConnection(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(14);

        MapEventsOverlay overlay = new MapEventsOverlay(this);
        map.getOverlays().add(overlay);

        myLocationOverlay = new DirectedLocationOverlay(this);
        map.getOverlays().add(myLocationOverlay);

        mRoadNodeMarkers = new FolderOverlay();
        mRoadNodeMarkers.setName("Route Steps");

        //map.getOverlays().add(mRoadNodeMarkers);

        GeoPoint startPoint;
        if(!MainActivity.useLoc){
            startPoint = MainActivity.homepoint;
        }else{
            startPoint = new GeoPoint(latLocation, longLocation);
        }
        myLocationOverlay.setLocation(startPoint);

        mapController.setCenter(startPoint);
        //homepoint = startPoint;

        /*mapController.setCenter(startPoint);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        map.invalidate();*/
        //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        //startMarker.setTitle("Start point");
        map.invalidate();
    }

    private class roadTask extends AsyncTask<ArrayList<GeoPoint>, Void, Road>{


        @Override
        protected Road doInBackground(ArrayList<GeoPoint>... params) {

            ArrayList<GeoPoint> waypoints = params[0];
            RoadManager roadManager = new OSRMRoadManager(getApplicationContext());
            return roadManager.getRoad(waypoints);
        }

        protected void onPostExecute(Road result) {
            route(result);
        }
    }


    public void getRoadAsync(GeoPoint start, GeoPoint dest){
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(start);
        waypoints.add(dest);

        new roadTask().execute(waypoints);
    }

    private void route(Road road){

        mRoadNodeMarkers.getItems().clear();
        map.getOverlays().remove(mRoadNodeMarkers);

        if(roadOverlay != null) map.getOverlays().remove(roadOverlay);
        roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(1,roadOverlay);
        map.invalidate();

        ///Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
        for (int i=0; i<road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            //nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step "+i);

            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
            //Drawable icon = getResources().getDrawable(R.drawable.ic_continue);
            //nodeMarker.setImage(icon);
            mRoadNodeMarkers.add(nodeMarker);

            //map.getOverlays().add(nodeMarker);

        }
        map.getOverlays().add(mRoadNodeMarkers);
        map.invalidate();
    }

    public void POIMap(){

        FolderOverlay poiMarkers = new FolderOverlay(getApplicationContext());
        map.getOverlays().add(poiMarkers);




        //Drawable poiIcon = getResources().getDrawable(R.drawable.marker_poi_default);

        for (POI poi:poiList){
            Marker poiMarker = new Marker(map);
            poiMarker.setTitle(poi.mType);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setSubDescription(Integer.toString((int)poi.mLocation.distanceToAsDouble(homepoint))+ " m");
            poiMarker.setPosition(poi.mLocation);
            //poiMarker.setIcon(poiIcon);
            if (poi.mThumbnail != null){
                poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
            }
            poiMarkers.add(poiMarker);
        }
        map.invalidate();


    }

   public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        Toast.makeText(this, "Tapped long", Toast.LENGTH_LONG).show();
        getRoadAsync(MainActivity.homepoint,p);
        return false;
    }
}
