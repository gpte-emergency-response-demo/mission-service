package com.redhat.cajun.navy.mission.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponderLocationHistory{

    private long timestamp;
    private double lat;
    private double lon;

    public ResponderLocationHistory() {

    }

    public ResponderLocationHistory(long timestamp, double lat, double lon) {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lon = lon;

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double input) {
        this.lat = input;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double input) {
        this.lon = input;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long input) {
        this.timestamp = input;
    }



}
