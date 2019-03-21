package com.redhat.cajun.navy.mission.data;

import io.vertx.core.json.Json;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Mission {
    private String id;
    private String incidentId;
    private String responderId;
    private BigDecimal responderStartLat;
    private BigDecimal responderStartLong;
    private BigDecimal incidentLat;
    private BigDecimal incidentLong;
    private BigDecimal destinationLat;
    private BigDecimal destinationLong;
    private List<ResponderLocationHistory> responderLocationHistory;
    private String status;


    public Mission() {
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

    public BigDecimal getResponderStartLat() {
        return responderStartLat;
    }

    public void setResponderStartLat(BigDecimal input) {
        this.responderStartLat = input;
    }

    public BigDecimal getResponderStartLong() {
        return responderStartLong;
    }

    public void setResponderStartLong(BigDecimal input) {
        this.responderStartLong = input;
    }

    public BigDecimal getIncidentLat() {
        return incidentLat;
    }

    public void setIncidentLat(BigDecimal input) {
        this.incidentLat = input;
    }

    public BigDecimal getIncidentLong() {
        return incidentLong;
    }

    public void setIncidentLong(BigDecimal input) {
        this.incidentLong = input;
    }

    public BigDecimal getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(BigDecimal input) {
        this.destinationLat = input;
    }

    public BigDecimal getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(BigDecimal input) {
        this.destinationLong = input;
    }

    public List<ResponderLocationHistory> getResponderLocationHistory() {
        return responderLocationHistory;
    }

    public void setResponderLocationHistory(List<ResponderLocationHistory> input) {
        this.responderLocationHistory = input;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String input) {
        this.status = input;
    }

    @Override
    public String toString() {
        return Json.encode(this);
    }
}
