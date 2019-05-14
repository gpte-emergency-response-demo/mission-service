package com.redhat.cajun.navy.mission.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.redhat.cajun.navy.mission.util.DoubleContextualSerializer;
import com.redhat.cajun.navy.mission.util.Precision;
import io.vertx.core.json.Json;

import java.util.Objects;
import java.util.Stack;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Responder {

    private String responderId = null;

    private String missionId = null;

    private String incidentId = null;

    private boolean isHuman = false;

    private boolean isContinue = true;

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
    private double lat;

    @JsonSerialize(using = DoubleContextualSerializer.class)
    @Precision(precision = 4)
    private double lon;


    private Status status = Status.RECEIVED;


    public enum Status {
        RECEIVED("RECEIVED"),
        PREP("PREP"),
        READY("READY"),
        MOVING("MOVING"),
        STUCK("STUCK"),
        PICKEDUP("PICKEDUP"),
        DROPPED("DROPPED");

        private String actionType;

        Status(String actionType) {
            this.actionType = actionType;
        }

        public String getActionType() {
            return actionType;
        }
    }



    public Responder() {

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


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public boolean isContinue() {
        return isContinue;
    }

    public void setContinue(boolean aContinue) {
        isContinue = aContinue;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }


    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getResponderId() {
        return responderId;
    }

    public void setResponderId(String responderId) {
        this.responderId = responderId;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public void setHuman(boolean human) {
        isHuman = human;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Responder responder = (Responder) o;
        return Objects.equals(responderId, responder.responderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responderId);
    }

    @Override
    public String toString() {
        return Json.encode(this).toString();
    }
}
