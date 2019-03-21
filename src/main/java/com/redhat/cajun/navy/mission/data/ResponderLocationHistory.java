package com.redhat.cajun.navy.mission.data;

public class ResponderLocationHistory {
    private String lat;
    private String lon;
    private int timestamp;

    public String getLat() {
        return lat;
    }

    public void setLat(String input) {
        this.lat = input;
    }

    public String getLong() {
        return lon;
    }

    public void setLong(String input) {
        this.lon = input;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int input) {
        this.timestamp = input;
    }
}
