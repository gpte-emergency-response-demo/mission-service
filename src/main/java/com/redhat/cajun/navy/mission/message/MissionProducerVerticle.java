package com.redhat.cajun.navy.mission.message;

import com.redhat.cajun.navy.mission.data.CreateMissionCommand;
import com.redhat.cajun.navy.mission.data.Mission;
import com.redhat.cajun.navy.mission.http.MissionRestVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.kafka.client.producer.RecordMetadata;

public class MissionProducerVerticle extends MissionMessageVerticle {

    @Override
    public void init(Future<Void> startFuture) throws Exception {

        producer = KafkaProducer.create(vertx,config);


    }


    public void onMessage(Message<JsonObject> message) {

        if (!message.headers().contains("action")) {
            message.fail(MissionRestVerticle.ErrorCodes.NO_ACTION_SPECIFIED.ordinal(), "No action header specified");
            return;
        }


        String action = message.headers().get("action");
        switch (action) {
            case "publish-update":
                final Mission m = Json.decodeValue(String.valueOf(message.body()), CreateMissionCommand.class).getBody();

                KafkaProducerRecord<String, String> record =
                        KafkaProducerRecord.create(missionUpdateCommandTopic, m.toString());

                producer.write(record, done -> {

                    if (done.succeeded()) {

                        RecordMetadata recordMetadata = done.result();
                        System.out.println("Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
                                ", partition=" + recordMetadata.getPartition() +
                                ", offset=" + recordMetadata.getOffset());

                        message.reply("Message delivered to topic");
                    }

                });

                break;

            default:
                message.fail(MissionRestVerticle.ErrorCodes.BAD_ACTION.ordinal(), "Bad action: " + action);

        }
    }


}
