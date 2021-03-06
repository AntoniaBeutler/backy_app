package com.backy.antoniabeutler.becky1.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.backy.antoniabeutler.becky1.MainActivity;
import com.backy.antoniabeutler.becky1.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.util.ArrayList;

import static android.support.v7.content.res.AppCompatResources.getDrawable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class MapFragment extends Fragment implements MapEventsReceiver{

    private MapView map = null;
    CacheManager cachemanager = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    // TODO: Rename and change types of parameters
    private Double mLatitude, mLongitude;
    private String poiType;
    private Boolean locationAvailable;

    public ArrayList<POI> poiList;
    private  DirectedLocationOverlay myLocationOverlay;
    protected FolderOverlay mRoadNodeMarkers;
    protected Polyline roadOverlay;
    protected GeoPoint startPoint;
    private  IMapController mapController;

    private OnFragmentInteractionListener mListener;
    private ArrayList<GeoPoint> PoiGeoL = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2, String param3, String param4) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        final Button button = view.findViewById(R.id.route);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(roadOverlay != null) map.getOverlays().remove(roadOverlay);
                if(mRoadNodeMarkers != null){
                    mRoadNodeMarkers.getItems().clear();
                    map.getOverlays().remove(mRoadNodeMarkers);
                }
                map.invalidate();
            }
        });
        if (getArguments() != null) {
            mLatitude = getArguments().getDouble("latitude");
            mLongitude = getArguments().getDouble("longitude");
            String s = getArguments().getString("poiType");
            if(!s.equals("nothing")){
                poiType = s;
            }

            locationAvailable = getArguments().getBoolean("locationAvailable");

            if (MainActivity.sqLiteHelper.getUseLocation()){
                startPoint = MainActivity.sqLiteHelper.getLocation();
            } else if(locationAvailable){
                startPoint = new GeoPoint(0.0, 0.0);
                Toast.makeText(getContext(),"Location not available!",Toast.LENGTH_SHORT).show();
            }else if (!locationAvailable){
                startPoint = new GeoPoint(mLatitude, mLongitude);
            }
        }

        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));
        map = view.findViewById(R.id.map2);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setUseDataConnection(true);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(15.0);

        MapEventsOverlay overlay = new MapEventsOverlay(this);
        map.getOverlays().add(overlay);

        myLocationOverlay = new DirectedLocationOverlay(getContext());
        map.getOverlays().add(myLocationOverlay);

        mRoadNodeMarkers = new FolderOverlay();
        mRoadNodeMarkers.setName("Route Steps");

        myLocationOverlay.setLocation(startPoint);

        mapController.setCenter(startPoint);

        map.invalidate();

        if(poiType != null){
            POIMap();
        }
        return view;
    }

    //update the location overlay -> is called by main activity when location changed
    public void updateLocation(GeoPoint startPoint){
        this.startPoint = startPoint;
        myLocationOverlay.setLocation(startPoint);
        mapController.setCenter(startPoint);
        map.invalidate();
    }

    ////Async Task for Route calculation
    private class roadTask extends AsyncTask<ArrayList<GeoPoint>, Void, Road> {
        @Override
        protected Road doInBackground(ArrayList<GeoPoint>... params) {

            ArrayList<GeoPoint> wayPoints = params[0];
            RoadManager roadManager = new MapQuestRoadManager("8niVTA1aAVjZ1DLEiN9yAFa1UkVf3zSv");
            roadManager.addRequestOption("routeType=pedestrian");
            return roadManager.getRoad(wayPoints);
        }

        protected void onPostExecute(Road result) {
            route(result);
        }
    }

    //call for Async Task for Route calculation between start and end point
    public void getRoadAsync(GeoPoint start, GeoPoint dest){
        ArrayList<GeoPoint> wayPoints = new ArrayList<>();
        wayPoints.add(start);
        wayPoints.add(dest);

        new roadTask().execute(wayPoints);
    }

    //draw route on Map
    private void route(Road road){
        mRoadNodeMarkers.getItems().clear();
        map.getOverlays().remove(mRoadNodeMarkers);
        double d = road.mDuration;
        double s = road.mLength;

        if(roadOverlay != null) map.getOverlays().remove(roadOverlay);
        roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(1,roadOverlay);
        map.invalidate();

        for (int i=0; i<road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setTitle("Step "+i);

            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(getContext(), node.mLength, node.mDuration));
            mRoadNodeMarkers.add(nodeMarker);
        }
        map.getOverlays().add(mRoadNodeMarkers);
        map.invalidate();
    }

    //draw Poi Marker on Map
    public void POIMap(){
        poiList = MainActivity.mPois.get(poiType);
        int poi_image = 0;
        FolderOverlay poiMarkers = new FolderOverlay(getContext());
        map.getOverlays().add(poiMarkers);

        try{
            Cursor cursor = MainActivity.sqLiteHelper.getImage(poiType);
            cursor.moveToFirst();

            poi_image = cursor.getInt(cursor.getColumnIndex("image_res_id"));
            cursor.close();
        } catch (IllegalArgumentException e){
        }

        Drawable poiIcon = null;
        if (poi_image != 0){
            poiIcon = getDrawable(getContext(), poi_image);
        }

        if (poiList != null){
            for (POI poi:poiList){
                PoiGeoL.add(poi.mLocation);
                Marker poiMarker = new Marker(map);
                poiMarker.setTitle(poi.mType);
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setSubDescription(Integer.toString((int)poi.mLocation.distanceToAsDouble(startPoint))+ " m");
                poiMarker.setPosition(poi.mLocation);
                if (poi_image != 0){
                    poiMarker.setIcon(poiIcon);
                }
                if (poi.mThumbnail != null){
                    poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
                }
                poiMarkers.add(poiMarker);
            }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        getRoadAsync(startPoint,p);
        return false;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
