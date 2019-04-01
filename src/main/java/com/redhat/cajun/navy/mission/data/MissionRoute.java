package com.redhat.cajun.navy.mission.data;

import io.vertx.core.json.Json;

import java.util.ArrayList;
import java.util.List;


public class MissionRoute {

    private double distance = 0.0f;
    private double duration = 0.0f;

    private List<MissionStep> steps = null;

    public MissionRoute() {
        this.steps = new ArrayList<MissionStep>();
    }

    public MissionRoute(double distance, double duration) {
        this.distance = distance;
        this.duration = duration;
        this.steps = new ArrayList<MissionStep>();
    }

    public void addMissionStep(MissionStep step){
        if(this.steps != null && step != null){
            steps.add(step);
        }
        else throw new IllegalArgumentException("Null value not acceptable");
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

    public List<MissionStep> getSteps() {
        return steps;
    }

    public void setSteps(List<MissionStep> steps) {
        this.steps = steps;
    }

    public String toJson() {
        return Json.encode(this);
    }

    @Override
    public String toString() {
        return toJson();
    }
}
