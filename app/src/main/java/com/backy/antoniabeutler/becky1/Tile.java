package com.backy.antoniabeutler.becky1;

public class Tile {

    String img_src;
    String tile_name;
    int distance;
    int tile_type;

    public Tile(String img_src, String tile_name, int tile_type){
        this.img_src = img_src;
        this.tile_name = tile_name;
        this.tile_type = tile_type;

        switch (tile_type) {
            case R.id.HOTEL:
            case R.id.HOSTEL:
            case R.id.CAMPING:
            case R.id.WATER:
            case R.id.SUPERMARKET:
            case R.id.BUS:
            case R.id.TRAIN:
        }
    }

    public String getTile_name() {
        return tile_name;
    }

}
