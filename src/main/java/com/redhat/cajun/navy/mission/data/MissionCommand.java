package com.redhat.cajun.navy.mission.data;


import io.vertx.core.json.Json;

import java.util.UUID;

public class MissionCommand {

    private String id;
    private String messageType;
    private String invokingService;
    private long timestamp;
    Body bodyObject;

    // Getter Methods


    public MissionCommand(String messageType, String invokingService, long timestamp){
        this.id = UUID.randomUUID().toString();
        this.messageType = messageType;
        this.invokingService = invokingService;
        this.timestamp = timestamp;
    }

    public void createId(){
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getInvokingService() {
        return invokingService;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Body getBody() {
        return bodyObject;
    }

    // Setter Methods

    public void setId( String id ) {
        this.id = id;
    }

    public void setMessageType( String messageType ) {
        this.messageType = messageType;
    }

    public void setInvokingService( String invokingService ) {
        this.invokingService = invokingService;
    }

    public void setTimestamp(long timestamp ) {
        this.timestamp = timestamp;
    }

    public void setBody( Body bodyObject ) {
        this.bodyObject = bodyObject;
    }

    public void setMission(Mission m){
        this.setBody(Json.decodeValue(m.toString(), Body.class));
    }

    @Override
    public String toString() {
        return Json.encode(this);
    }

}

