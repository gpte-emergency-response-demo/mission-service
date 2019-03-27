package com.redhat.cajun.navy.mission.message;

import com.redhat.cajun.navy.mission.data.Mission;
import com.redhat.cajun.navy.mission.data.MissionCommand;
import io.vertx.core.json.Json;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MissionProducerVerticleTest {

    @Test
    public void getMissionCommand() {
        String message = "{\"id\": \"6932de7b-8435-4ded-8f32-c3e5b704998c\", \"messageType\": \"CreateMissionCommand\", \"invokingService\": \"ProcessService\", \"timestamp\": 1552899692390, \"body\": { \"incidentId\": \"98965816-e6eb-4edc-9b85-9a6b6ca474b3\",    \"responderId\": \"1\",    \"responderStartLat\": \"31.12345\",    \"responderStartLong\": \"-71.98765\",    \"incidentLat\": \"30.12345\",    \"incidentLong\": \"-70.98765\",    \"destinationLat\": \"32.12345\",    \"destinationLong\": \"-72.98765\" }}";
        final Mission m = Json.decodeValue(message, MissionCommand.class).getBody();
        assertEquals(m.getResponderId(), "1");
    }
}