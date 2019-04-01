package com.redhat.cajun.navy.mission.data;

import com.mapbox.geojson.Point;
import io.vertx.core.json.Json;

public class MissionStep {

    // Example waypoint message "instruction": "You have arrived at your 1st destination, on the right",

    private double distance = 0.0f;
    private double duration = 0.0f;
    private String name = null;
    private String instruction = null;
    private double weight = 0.0f;
    private Location loc = null;

    public MissionStep() {
    }

    public MissionStep(double distance, double duration, String name, String instruction, double weight, Point p) {
        this.distance = distance;
        this.duration = duration;
        this.name = name;
        this.instruction = instruction;
        this.weight = weight;
        this.loc = new Location(p.latitude(), p.longitude());
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public String toJson() {
        return Json.encode(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
