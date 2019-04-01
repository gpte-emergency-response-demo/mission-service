package com.redhat.cajun.navy.mission.data;

public class Location {

    private double lat;
    private double lon;

    public Location(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Location() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double input) {
        this.lat = input;
    }

    public double getLong() {
        return lon;
    }

    public void setLong(double input) {
        this.lon = input;
    }


}
