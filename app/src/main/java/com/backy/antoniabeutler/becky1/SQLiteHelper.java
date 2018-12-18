package com.backy.antoniabeutler.becky1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;


public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BackyDatabase";
    public static final int DATABASE_VERSION = 1;

    private Map<String, Integer> img = new HashMap<>();

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String image_tab = "CREATE TABLE tile(poi TEXT PRIMARY KEY, image_res_id INTEGER, loaded INTEGER)";
        String setting_tab = "CREATE TABLE setting(user TEXT PRIMARY KEY, location TEXT, latitude REAL, longitude REAL, map_download INTEGER, poi_radius INTEGER, poi_amount INTEGER, power_saving INTEGER)";

        db.execSQL(image_tab);
        db.execSQL(setting_tab);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tile");
        db.execSQL("DROP TABLE IF EXISTS setting");
        onCreate(db);
    }

    public void loadImages(){
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

        SQLiteDatabase db = this.getWritableDatabase();

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
            db.insert("tile", null, cValues);
            System.out.println("loaded " + s);
        }
    }

    public void loadSettings(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();
        //"CREATE TABLE setting(user TEXT PRIMARY KEY, location TEXT, latitude REAL, longitude REAL, map_download INTEGER, poi_radius INTEGER, poi_amount INTEGER, power_saving INTEGER)";
        cValues.put("user", "Android");
        cValues.put("location", "");
        cValues.put("latitude", 0.0);
        cValues.put("longitude", 0.0);
        cValues.put("map_download", 0);
        cValues.put("poi_radius", 0);
        cValues.put("poi_amount", 0);
        cValues.put("power_saving", 0);

        db.insert("setting", null, cValues);
    }

    public Cursor getImage(String poi){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT image_res_id FROM tile WHERE poi=?";
        return db.rawQuery(query, new String[] { poi });
    }

    public Cursor getLoadedTiles(int loaded){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT poi FROM tile WHERE loaded=?";
        return db.rawQuery(query, new String[]{ Integer.toString(loaded) });
    }

    public boolean updateTileState(String poi, int loaded){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();
        cValues.put("loaded", loaded);

        long result = db.update("tile", cValues, "poi=?", new String[] { poi });

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getPOIRadius(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT poi_radius FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }

    public Cursor getPOIAmount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT poi_amount FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }

    public Cursor getPowerSaving(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT power_saving FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }


    public Cursor getMapDownload(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT map_download FROM setting WHERE user=?";
        return db.rawQuery(query, new String[]{ "Android" });
    }

    public boolean updateSettings(String location, double latitude, double longitude, int map_download, int poi_radius, int poi_amount, int power_saving, int type){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cValues = new ContentValues();

        switch (type){
            case 0: cValues.put("location", location); cValues.put("latitude", latitude); cValues.put("longitude", longitude); break;
            case 1: cValues.put("map_download", map_download); break;
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

}
