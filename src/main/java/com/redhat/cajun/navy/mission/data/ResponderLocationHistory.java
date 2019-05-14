package com.redhat.cajun.navy.mission.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.redhat.cajun.navy.mission.util.DoubleContextualSerializer;
import com.redhat.cajun.navy.mission.util.Precision;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponderLocationHistory{

    private long timestamp;
    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
    private double lat;

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
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
