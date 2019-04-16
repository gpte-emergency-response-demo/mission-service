package com.redhat.cajun.navy.mission.message;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
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


    public void onMessage(Message<JsonObject> message) {

        if (!message.headers().contains("action")) {
            message.fail(ErrorCodes.NO_ACTION_SPECIFIED.ordinal(), "No action header specified");
            return;
        }


        String action = message.headers().get("action");
        switch (action) {
            case "PUBLISH_UPDATE":
                sendMessage(missionUpdateCommandTopic, String.valueOf(message.body()));
                message.reply("Message sent "+missionUpdateCommandTopic);
                break;
            case "RESPONDER_UPDATE":
                sendMessage(responderUpdateTopic, String.valueOf(message.body()));
                message.reply("Message Sent "+responderUpdateTopic);
                break;

            default:
                message.fail(ErrorCodes.BAD_ACTION.ordinal(), "Bad action: " + action);

        }
    }


    public void sendMessage(String topic, String body){

        KafkaProducerRecord<String, String> record =
                KafkaProducerRecord.create(topic, body);

        producer.write(record, done -> {
            if (done.succeeded()) {

                RecordMetadata recordMetadata = done.result();
                System.out.println("Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
                        ", partition=" + recordMetadata.getPartition() +
                        ", offset=" + recordMetadata.getOffset());

            }
        });

    }


}
