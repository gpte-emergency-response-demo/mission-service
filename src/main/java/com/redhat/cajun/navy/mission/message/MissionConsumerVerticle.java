package com.redhat.cajun.navy.mission.message;

import com.redhat.cajun.navy.mission.MessageAction;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.kafka.client.consumer.KafkaConsumer;

public class MissionConsumerVerticle extends MissionMessageVerticle {



    @Override
    public void init(Future<Void> startFuture) throws Exception {

        consumer = KafkaConsumer.create(vertx, config);

        consumer.handler(record -> {
            DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.CREATE_ENTRY.toString());
            vertx.eventBus().send(CACHE_QUEUE, record.value(), options, reply -> {
                if (reply.succeeded()) {
                    System.out.println("Message accepted");
                } else {
                    System.out.println("Message not accepted");
                }
            });
        });

        consumer.subscribe(createMissionCommandTopic, ar -> {
            if (ar.succeeded()) {
                System.out.println("subscribed to CreateMissionCommand");
            } else {
                System.out.println("Could not subscribe " + ar.cause().getMessage());
            }
        });

    }


    @Override
    public void stop() throws Exception {
        consumer.unsubscribe(ar -> {

            if (ar.succeeded()) {
                System.out.println("Consumer unsubscribed");
            }
        });
    }
}
