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

        db.execSQL(image_tab);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tile");
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
}
