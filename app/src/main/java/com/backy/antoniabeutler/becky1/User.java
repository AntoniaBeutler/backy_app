package com.backy.antoniabeutler.becky1;

import android.location.Location;

import java.util.List;

public class User {
    private int id;



    private String name;
    private List<Tile> tile_List;
    private Location homeLocation;



    private String password;

    public User(int id,String name,String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public List<Tile> getTile_List() {
        return tile_List;
    }

    public void setTile_List(List<Tile> tile_List) {
        this.tile_List = tile_List;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }








}
