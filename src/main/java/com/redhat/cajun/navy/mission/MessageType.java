package com.redhat.cajun.navy.mission;

public enum MessageType {
    MissionStartedEvent("MissionStartedEvent"),
    MissionPickedUpEvent("MissionPickedUpEvent"),
    MissionCompletedEvent("MissionCompletedEvent"),
    UpdateResponderCommand("UpdateResponderCommand");

    private String messageType;

    MessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }

}