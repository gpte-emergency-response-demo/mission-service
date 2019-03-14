package com.redhat.cajun.navy.mission;

import java.math.BigDecimal;

public class ResponderLocationHistory {

    private BigDecimal lat = null;

    private BigDecimal lon = null;

    private long timestamp = -1;

    private String event = null;


    public ResponderLocationHistory(BigDecimal lat, BigDecimal lon, long timestamp) {
        this.lat = lat;
        this.lon = lon;
        this.timestamp = timestamp;
    }


    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        if(lat == null)
            throw new IllegalArgumentException();
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        if(lon == null)
            throw new IllegalArgumentException();
        this.lon = lon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }



}
