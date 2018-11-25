package com.backy.antoniabeutler.becky1;

public class Tile {

    int img_src;
    String tile_name;
    int distance;

    public Tile(String tile_name){

        switch (tile_name){
            case "Hotel": this.img_src = R.drawable.hotel; break;
            case "Hostel": this.img_src = R.drawable.hostel; break;
            case "Campingside": this.img_src = R.drawable.tent; break;
            case "Water": this.img_src = R.drawable.water; break;
            case "Supermarket": this.img_src = R.drawable.cart; break;
            case "Restaurant": this.img_src = R.drawable.restaurant; break;
            case "Train Station": this.img_src = R.drawable.railway; break;
            case "Bus Station": this.img_src = R.drawable.bus; break;
        }

        this.tile_name = tile_name;

    }

    public String getTile_name() {
        return tile_name;
    }
    public int getImg_src() {return img_src;}
}
