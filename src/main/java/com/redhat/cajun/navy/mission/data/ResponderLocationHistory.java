package com.redhat.cajun.navy.mission.data;

public class ResponderLocationHistory extends Location{

    private long timestamp;

    private Location location = null;

    public ResponderLocationHistory(Location location, int timestamp) {
        this.timestamp = timestamp;
        this.location = location;
    }

    public ResponderLocationHistory() {

    }

    public ResponderLocationHistory(long timestamp, Location location) {
        this.timestamp = timestamp;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long input) {
        this.timestamp = input;
    }



}
