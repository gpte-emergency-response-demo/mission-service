package com.redhat.cajun.navy.mission;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Mission {


    private String name = null;

    private String id = null;

    private String incidentId = null;

    private String responderId = null;

    private BigDecimal responderStartLat = null;

    private BigDecimal responderStartLong = null;

    private BigDecimal incidentLat = null;

    private BigDecimal incidentLong = null;

    private BigDecimal destinationLat = null;

    private BigDecimal destinationLong = null;

    private List<ResponderLocationHistory> responderLocationHistory = null;

    private String status = null;


    public Mission(String incidentId, String responderId, BigDecimal responderStartLat, BigDecimal responderStartLong, BigDecimal incidentLat, BigDecimal incidentLong, BigDecimal destinationLat, BigDecimal destinationLong, String status) {
        setIncidentId(incidentId);
        setResponderId(responderId);
        setResponderStartLat(responderStartLat);
        setResponderStartLong(responderStartLong);
        setIncidentLat(incidentLat);
        setIncidentLong(incidentLong);
        setDestinationLat(destinationLat);
        setDestinationLong(destinationLong);
        setStatus(status);
        setResponderLocationHistory(new ArrayList<ResponderLocationHistory>());
        responderLocationHistory.add(new ResponderLocationHistory(new BigDecimal(34.16877) ,new BigDecimal(34.16877), System.currentTimeMillis()));
        id = UUID.randomUUID().toString();
    }


    public Mission(){
        responderLocationHistory.add(new ResponderLocationHistory(new BigDecimal(34.16877) ,new BigDecimal(34.16877), System.currentTimeMillis()));
        id = UUID.randomUUID().toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if(status == null)
            throw new IllegalArgumentException();
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name==null)
            throw new IllegalArgumentException();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if(id == null)
            throw new IllegalArgumentException();
        this.id = id;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        if(incidentId == null)
            throw new IllegalArgumentException();
        this.incidentId = incidentId;
    }

    public String getResponderId() {
        return responderId;
    }

    public void setResponderId(String responderId) {
        if(responderId == null)
            throw new IllegalArgumentException();
        this.responderId = responderId;
    }

    public BigDecimal getResponderStartLat() {
        return responderStartLat;
    }

    public void setResponderStartLat(BigDecimal responderStartLat) {
        if(responderStartLat == null)
            throw new IllegalArgumentException();
        this.responderStartLat = responderStartLat;
    }

    public BigDecimal getResponderStartLong() {
        return responderStartLong;
    }

    public void setResponderStartLong(BigDecimal responderStartLong) {
        if(responderStartLong == null)
            throw new IllegalArgumentException();
        this.responderStartLong = responderStartLong;
    }

    public BigDecimal getIncidentLat() {
        return incidentLat;
    }

    public void setIncidentLat(BigDecimal incidentLat) {
        if(incidentLat == null)
            throw new IllegalArgumentException();
        this.incidentLat = incidentLat;
    }

    public BigDecimal getIncidentLong() {
        return incidentLong;
    }

    public void setIncidentLong(BigDecimal incidentLong) {
        if(incidentLong == null)
            throw new IllegalArgumentException();
        this.incidentLong = incidentLong;
    }

    public BigDecimal getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(BigDecimal destinationLat) {
        if(destinationLat == null)
            throw new IllegalArgumentException();
        this.destinationLat = destinationLat;
    }

    public BigDecimal getDestinationLong() {
        return destinationLong;
    }

    public void setDestinationLong(BigDecimal destinationLong) {
        if(destinationLong == null)
            throw new IllegalArgumentException();
        this.destinationLong = destinationLong;
    }

    public List<ResponderLocationHistory> getResponderLocationHistory() {
        return responderLocationHistory;
    }

    public void setResponderLocationHistory(List<ResponderLocationHistory> responderLocationHistory) {
        if(responderLocationHistory == null)
            throw new IllegalArgumentException();
        this.responderLocationHistory = responderLocationHistory;
    }

    public void addResponderLocationHistory(ResponderLocationHistory responderLocationHistory){
        if(responderLocationHistory == null)
            throw new IllegalArgumentException();
        this.responderLocationHistory.add(responderLocationHistory);

    }


}
