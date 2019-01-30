package com.backy.antoniabeutler.becky1;

import android.database.Cursor;

//Class that represents one tile on the MainFragment
public class Tile {

    private int img_src;
    private String tile_name;
    private double distance;

    public Tile(String tile_name){

        Cursor cursor = MainActivity.sqLiteHelper.getImage(tile_name);
        cursor.moveToFirst();
        this.img_src = cursor.getInt(cursor.getColumnIndex("image_res_id"));

        this.tile_name = tile_name;

    }

    public String getTile_name() {
        return tile_name;
    }
    public int getImg_src() {return img_src;}

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
