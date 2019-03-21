package com.redhat.cajun.navy.mission.data;


public class CreateMissionCommand {

    private String id;
    private String messageType;
    private String invokingService;
    private float timestamp;
    Body bodyObject;

    // Getter Methods

    public String getId() {
        return id;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getInvokingService() {
        return invokingService;
    }

    public float getTimestamp() {
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

    public void setTimestamp( float timestamp ) {
        this.timestamp = timestamp;
    }

    public void setBody( Body bodyObject ) {
        this.bodyObject = bodyObject;
    }
}

