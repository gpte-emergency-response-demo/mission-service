package com.redhat.cajun.navy.mission.data;

import java.util.UUID;

public class ResponderCommand {
    private String id;
    private String messageType;
    private String invokingService;
    private long timestamp;
    Responder responder = null;

    public ResponderCommand(Responder responder, String messageType) {
        id = UUID.randomUUID().toString();
        this.responder = responder;
        this.messageType = messageType;
        this. invokingService = "MissionService";
        this.timestamp = System.currentTimeMillis();
    }


    public String getResponderCommand(boolean available){

        return "{ \"messageType\": \""+messageType+"\", " +
                "\"id\": \""+id+"\", " +
                "\"invokingService\": \""+invokingService+"\", " +
                "\"timestamp\": "+timestamp+", " +
                "\"body\": { \"responder\": { \"id\": "+responder.getResponderId()+", " +
                "\"latitude\": "+responder.getLocation().getLat()+", " +
                "\"longitude\": "+responder.getLocation().getLong()+", " +
                "\"available\": "+available+" } } }";
    }

}
