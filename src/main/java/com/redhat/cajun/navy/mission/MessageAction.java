package com.redhat.cajun.navy.mission;

public enum MessageAction {
    CREATE_ENTRY ("create-entry"),
    UPDATE_ENTRY("update-entry"),
    PUBLISH_UPDATE ("publish-update");

    private String actionType;

    MessageAction(String actionType) {
        this.actionType = actionType;
    }

    public String getActionType() {
        return actionType;
    }

}