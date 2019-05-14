package com.redhat.cajun.navy.mission.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mapbox.geojson.Point;
import com.redhat.cajun.navy.mission.util.DoubleContextualSerializer;
import com.redhat.cajun.navy.mission.util.Precision;
import io.vertx.core.json.Json;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionStep {

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
    private double lat;

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
    private double lon;

    private boolean isWayPoint = false;
    private boolean isDestination = false;


    public MissionStep() {
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


    public boolean isWayPoint() {
        return isWayPoint;
    }

    public void setWayPoint(boolean wayPoint) {
        isWayPoint = wayPoint;
    }

    public boolean isDestination() {
        return isDestination;
    }

    public void setDestination(boolean destination) {
        isDestination = destination;
    }


    public MissionStep(Point p) {
        lat= p.latitude();
        lon = p.longitude();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissionStep step = (MissionStep) o;


        return Double.compare(step.lat, lat) == 0 &&
                Double.compare(step.lon, lon) == 0 &&
                step.isDestination == isDestination &&
                step.isWayPoint == isWayPoint;
    }

    public String toJson() {
        return Json.encode(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
