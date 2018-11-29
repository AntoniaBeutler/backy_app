package com.backy.antoniabeutler.becky1;

import android.location.Location;

import java.util.List;

public class User {
    private int id;
    private String name;
    private Boolean hotel;
    private Boolean hostel;
    private Boolean campsite;
    private Boolean water;
    private Boolean restaurant;
    private Boolean supermarket;
    private Boolean bus;
    private Boolean train;
    private String password;
    private double longitude;
    private double latitude;
    private String location;
    private String email;



    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        hotel = false;
        hostel = false;
        campsite= false;
        water = false;
        restaurant = false;
        supermarket = false;
        bus = false;
        train = false;
        location = "";
        email = "";
        longitude = 0;
        latitude = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHotel() {
        return hotel;
    }

    public void setHotel(Boolean hotel) {
        this.hotel = hotel;
    }

    public Boolean getHostel() {
        return hostel;
    }

    public void setHostel(Boolean hostel) {
        this.hostel = hostel;
    }

    public Boolean getCampsite() {
        return campsite;
    }

    public void setCampsite(Boolean campsite) {
        this.campsite = campsite;
    }

    public Boolean getWater() {
        return water;
    }

    public void setWater(Boolean water) {
        this.water = water;
    }

    public Boolean getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Boolean restaurant) {
        this.restaurant = restaurant;
    }

    public Boolean getSupermarket() {
        return supermarket;
    }

    public void setSupermarket(Boolean supermarket) {
        this.supermarket = supermarket;
    }

    public Boolean getBus() {
        return bus;
    }

    public void setBus(Boolean bus) {
        this.bus = bus;
    }

    public Boolean getTrain() {
        return train;
    }

    public void setTrain(Boolean train) {
        this.train = train;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
