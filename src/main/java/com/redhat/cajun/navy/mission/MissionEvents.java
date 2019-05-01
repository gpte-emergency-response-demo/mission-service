package com.redhat.cajun.navy.mission;

public enum MissionEvents {
    CREATED("CREATED"),
    UPDATED("UPDATED"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");


    private String actionType;

    MissionEvents(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

}