package com.backy.antoniabeutler.becky1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.net.UnknownServiceException;
import java.util.LinkedList;
import java.util.List;

public class SQLiteDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AppUser";
    private static final String TABLE_NAME = "User";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOTEL = "hotel";
    private static final String KEY_HOSTEL = "hostel";
    private static final String KEY_CAMPSITE = "campsite";
    private static final String KEY_WATER = "water";
    private static final String KEY_RESTAURANT = "restaurant";
    private static final String KEY_SUPERMARKET = "supermarket";
    private static final String KEY_BUS = "bus";
    private static final String KEY_TRAIN = "train";

    private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_PASSWORD,KEY_HOTEL,KEY_HOSTEL, KEY_CAMPSITE,KEY_WATER,KEY_RESTAURANT,KEY_SUPERMARKET,KEY_BUS,KEY_TRAIN};

    public SQLiteDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATION_TABLE = "CREATE TABLE Players ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
                + "password TEXT, " + "hotel INTEGER, " + "hostel INTEGER, " + "campsite INTEGER, " +
                "water INTEGER, " + "restaurant INTEGER, " + "supermarket INTEGER, " +
                "bus INTEGER, " + "train INTEGER )";

        db.execSQL(CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public void deleteOne(User user) {
        // Get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = ?", new String[] { String.valueOf(user.getId()) });
        db.close();
    }

    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, // a. table
                COLUMNS, // b. column names
                " id = ?", // c. selections
                new String[] { String.valueOf(id) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();
        User user= new User(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));

        user.setHotel(intToBool(Integer.parseInt(cursor.getString(3))));
        user.setHostel(intToBool(Integer.parseInt(cursor.getString(4))));
        user.setCampsite(intToBool(Integer.parseInt(cursor.getString(5))));
        user.setWater(intToBool(Integer.parseInt(cursor.getString(6))));
        user.setRestaurant(intToBool(Integer.parseInt(cursor.getString(7))));
        user.setSupermarket(intToBool(Integer.parseInt(cursor.getString(8))));
        user.setBus(intToBool(Integer.parseInt(cursor.getString(9))));
        user.setTrain(intToBool(Integer.parseInt(cursor.getString(10))));


        return user;
    }

    public List<User> allUsers() {

        List<User> users = new LinkedList<User>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user = null;

        if (cursor.moveToFirst()) {
            do {
                user= new User(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));

                user.setHotel(intToBool(Integer.parseInt(cursor.getString(3))));
                user.setHostel(intToBool(Integer.parseInt(cursor.getString(4))));
                user.setCampsite(intToBool(Integer.parseInt(cursor.getString(5))));
                user.setWater(intToBool(Integer.parseInt(cursor.getString(6))));
                user.setRestaurant(intToBool(Integer.parseInt(cursor.getString(7))));
                user.setSupermarket(intToBool(Integer.parseInt(cursor.getString(8))));
                user.setBus(intToBool(Integer.parseInt(cursor.getString(9))));
                user.setTrain(intToBool(Integer.parseInt(cursor.getString(10))));


                users.add(user);
            } while (cursor.moveToNext());
        }

        return users;
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_HOTEL,boolToInt(user.getHotel()));
        values.put(KEY_HOSTEL,boolToInt(user.getHostel()));
        values.put(KEY_WATER,boolToInt(user.getWater()));
        values.put(KEY_RESTAURANT,boolToInt(user.getRestaurant()));
        values.put(KEY_SUPERMARKET,boolToInt(user.getSupermarket()));
        values.put(KEY_BUS,boolToInt(user.getBus()));
        values.put(KEY_TRAIN,boolToInt(user.getTrain()));
        values.put(KEY_CAMPSITE,boolToInt(user.getCampsite()));

        // insert
        db.insert(TABLE_NAME,null, values);
        db.close();
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PASSWORD, user.getPassword());
        values.put(KEY_HOTEL,boolToInt(user.getHotel()));
        values.put(KEY_HOSTEL,boolToInt(user.getHostel()));
        values.put(KEY_WATER,boolToInt(user.getWater()));
        values.put(KEY_RESTAURANT,boolToInt(user.getRestaurant()));
        values.put(KEY_SUPERMARKET,boolToInt(user.getSupermarket()));
        values.put(KEY_BUS,boolToInt(user.getBus()));
        values.put(KEY_TRAIN,boolToInt(user.getTrain()));
        values.put(KEY_CAMPSITE,boolToInt(user.getCampsite()));

        int i = db.update(TABLE_NAME, // table
                values, // column/value
                "id = ?", // selections
                new String[] { String.valueOf(user.getId()) });

        db.close();

        return i;
    }

    public int boolToInt(boolean b){
        return (b)? 1 : 0;
    }

    public boolean intToBool(int i){
        return i > 0;
    }
}
