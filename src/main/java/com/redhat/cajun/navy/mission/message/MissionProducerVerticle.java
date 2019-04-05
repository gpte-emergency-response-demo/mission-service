package com.redhat.cajun.navy.mission.message;

import com.redhat.cajun.navy.mission.data.Mission;
import com.redhat.cajun.navy.mission.data.MissionCommand;
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
        vertx.eventBus().consumer(config().getString(PUB_QUEUE, "pub.queue"), this::onMessage);

    }


    protected MissionCommand getMissionCommand(String message) {

        System.out.println(message);
        Mission m = Json.decodeValue(message, MissionCommand.class).getBody();
        MissionCommand mc = new MissionCommand();

        mc.createMissionCommandHeaders("MissionUpdatedCommand", "MissionService", System.currentTimeMillis());
        mc.setMission(m);

        return mc;

    }

    public void onMessage(Message<JsonObject> message) {

        if (!message.headers().contains("action")) {
            message.fail(MissionRestVerticle.ErrorCodes.NO_ACTION_SPECIFIED.ordinal(), "No action header specified");
            return;
        }


        String action = message.headers().get("action");
        switch (action) {
            case "PUBLISH_UPDATE":

                KafkaProducerRecord<String, String> record =
                        KafkaProducerRecord.create(missionUpdateCommandTopic, getMissionCommand(String.valueOf(message.body())).toString());

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
