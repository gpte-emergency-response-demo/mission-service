package com.redhat.cajun.navy.mission.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.vertx.core.json.Json;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class Mission {
    private String id;
    private String incidentId;
    private String responderId;
    private double responderStartLat;
    private double responderStartLong;
    private double incidentLat;
    private double incidentLong;
    private double destinationLat;
    private double destinationLong;
    private List<ResponderLocationHistory> responderLocationHistory;
    private String status;

    private MissionRoute route = null;

    public Mission(){
        id = UUID.randomUUID().toString();
        responderLocationHistory = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String input) {
        this.id = input;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String input) {
        this.incidentId = input;
    }

    public String getResponderId() {
        return responderId;
    }

    public void setResponderId(String input) {
        this.responderId = input;
    }

    public double getResponderStartLat() {
        return responderStartLat;
    }

    public void setResponderStartLat(double input) {
        this.responderStartLat = input;
    }

    public double getResponderStartLong() {
        return responderStartLong;
    }

    public void setResponderStartLong(double input) {
        this.responderStartLong = input;
    }

    public double getIncidentLat() {
        return incidentLat;
    }

    public void setIncidentLat(double input) {
        this.incidentLat = input;
    }

    public double getIncidentLong() {
        return incidentLong;
    }

    public void setIncidentLong(double input) {
        this.incidentLong = input;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double input) {
        this.destinationLat = input;
    }

    public double getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(double input) {
        this.destinationLong = input;
    }

    public List<ResponderLocationHistory> getResponderLocationHistory() {
        return responderLocationHistory;
    }

    public void setResponderLocationHistory(List<ResponderLocationHistory> input) {
        this.responderLocationHistory = input;
    }

    public void addResponderLocationHistory(ResponderLocationHistory history){
        responderLocationHistory.add(history);
        setResponderStartLat(history.getLocation().getLat());
        setResponderStartLong(history.getLocation().getLong());

    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String input) {
        this.status = input;
    }


    public MissionRoute getRoute() {
        return route;
    }

    public void setRoute(MissionRoute route) {
        this.route = route;
    }

    public String toJson() {
        return Json.encode(this);
    }

    @Override
    public String toString() {
        return toJson();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return Objects.equals(responderId, mission.responderId) && Objects.equals(incidentId, mission.incidentId);
    }

    @JsonIgnore
    public String getKey(){
        return this.incidentId+this.responderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(responderId+incidentId);
    }
}
