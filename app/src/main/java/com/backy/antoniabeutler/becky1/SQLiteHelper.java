package com.backy.antoniabeutler.becky1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.osmdroid.util.GeoPoint;

import java.util.HashMap;
import java.util.Map;


public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BackyDatabase";
    public static final int DATABASE_VERSION = 1;

    private Map<String, Integer> img = new HashMap<>();

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create 2 tables in the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String image_tab = "CREATE TABLE tile(poi TEXT PRIMARY KEY, image_res_id INTEGER, loaded INTEGER)";
        String setting_tab = "CREATE TABLE setting(user TEXT PRIMARY KEY, location TEXT, latitude REAL, longitude REAL, use_location INTEGER, poi_radius INTEGER, poi_amount INTEGER, power_saving INTEGER)";

        db.execSQL(image_tab);
        db.execSQL(setting_tab);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tile");
        db.execSQL("DROP TABLE IF EXISTS setting");
        onCreate(db);
    }

    //initially fill database with image resource id information and set the 'Add POI' tile as initially loaded
    public void loadImages(){
        SQLiteDatabase db = this.getWritableDatabase();

        img.put("Hotel", R.drawable.hotel);
        img.put("Hostel", R.drawable.hostel);
        img.put("Water", R.drawable.water);
        img.put("Campingside", R.drawable.tent);
        img.put("Supermarket", R.drawable.cart);
        img.put("Restaurant", R.drawable.restaurant);
        img.put("Train Station", R.drawable.railway);
        img.put("Bus Station", R.drawable.bus);
        img.put("Add POI", R.drawable.add);

        String[] key = img.keySet().toArray(new String[0]);
        int value = 0;

        ContentValues cValues = new ContentValues();

        for (String s : key){
            if (img.containsKey(s)){
                value = img.get(s);
            }
            cValues.put("poi", s);
            cValues.put("image_res_id", value);
            if (s.equals("Add POI")){
                cValues.put("loaded", 1);
            } else {
                cValues.put("loaded", 0);
            }
            db.insertOrThrow("tile", null, cValues);
        }
    }

    //initialise the setting table with some dummy values, except the user name, which is the key to get the database content
    public void loadSettings(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();
        cValues.put("user", "Android");
        cValues.put("location", "");
        cValues.put("latitude", 0.0);
        cValues.put("longitude", 0.0);
        cValues.put("use_location", 0);
        cValues.put("poi_radius", 10);
        cValues.put("poi_amount", 10);
        cValues.put("power_saving", 0);

        db.insertOrThrow("setting", null, cValues);
    }

    //Get a certain image specified as the string POI
    public Cursor getImage(String poi){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT image_res_id FROM tile WHERE poi=?";
        return db.rawQuery(query, new String[] { poi });
    }

    //Get all loaded tiles which will be displayed at startup
    public Cursor getLoadedTiles(int loaded){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT poi FROM tile WHERE loaded=?";
        return db.rawQuery(query, new String[]{ Integer.toString(loaded) });
    }

    //Updates the tiles, which are displayed at startup
    public void updateTileState(String poi, int loaded){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();
        cValues.put("loaded", loaded);

        long result = db.update("tile", cValues, "poi=?", new String[] { poi });

        if (result == -1) {
            System.out.println("Error!");
        } else {
            System.out.println("Update successful!");
        }
    }

    //Get the search radius for POIs defined by the user
    public Cursor getPOIRadius(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT poi_radius FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }

    //Get the amount of searched POIs defined by the user
    public Cursor getPOIAmount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT poi_amount FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }

    //Get the setting whether the user wants to use the power saving mode or not
    public Cursor getPowerSaving(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT power_saving FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }

    //Get the setting whether the user wants to use a self-defined location or not
    public boolean getUseLocation(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT use_location FROM setting WHERE user=?";

        Cursor cursor = db.rawQuery(query, new String[]{ "Android" });
        cursor.moveToFirst();
        int b = Integer.parseInt(cursor.getString(cursor.getColumnIndex("use_location")));
        cursor.close();
        if (b == 1){
            return true;
        } else {
            return false;
        }
    }

    //Updates the setting table -> when called a type must be specified
    public boolean updateSettings(String location, double latitude, double longitude, int use_location, int poi_radius, int poi_amount, int power_saving, int type){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();

        switch (type){
            case 0: cValues.put("location", location); cValues.put("latitude", latitude); cValues.put("longitude", longitude); break;
            case 1: cValues.put("use_location", use_location); break;
            case 2: cValues.put("poi_radius", poi_radius); break;
            case 3: cValues.put("poi_amount", poi_amount); break;
            case 4: cValues.put("power_saving", power_saving); break;
        }

        long result = db.update("setting", cValues, "user=?", new String[] { "Android" });

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //Get the name of the default location
    public Cursor getLocationName(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT location FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }

    //Get the latitude and longitude of the default location
    public GeoPoint getLocation(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query1 = "SELECT longitude FROM setting WHERE user=?";
        Cursor c1 = db.rawQuery(query1, new String[]{ "Android" });
        String query2 = "SELECT latitude FROM setting WHERE user=?";
        Cursor c2 = db.rawQuery(query2, new String[]{ "Android" });
        c1.moveToFirst();
        double longitude = (Double.parseDouble(c1.getString(c1.getColumnIndex("longitude"))));
        c1.close();
        c2.moveToFirst();
        double latitude = (Double.parseDouble(c2.getString(c2.getColumnIndex("latitude"))));
        c2.close();
        return new GeoPoint(latitude,longitude);
    }
}
