package com.redhat.cajun.navy.mission.message;

import com.redhat.cajun.navy.mission.MessageAction;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.kafka.client.consumer.KafkaConsumer;

import java.util.HashMap;
import java.util.Map;

public class ResponderLocConsumer extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(ResponderLocConsumer.class.getName());
    KafkaConsumer<String, String> consumer = null;
    public static final String CACHE_QUEUE = "cache.queue";
    public String responderLoctationUpdateTopic = null;

    public String createMissionCommandTopic = null;

    @Override
    public void start(Future<Void> startFuture) throws Exception {


        responderLoctationUpdateTopic = config().getString("kafka.sub.responder.loc.update");
        consumer = KafkaConsumer.create(vertx,  ConsumerConfig.getConfig(config()));

        consumer.handler(record -> {
            DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.UPDATE_ENTRY.toString());
            vertx.eventBus().send(CACHE_QUEUE, record.value(), options, reply -> {
                if (reply.failed()) {
                    System.err.println("Incoming Message not accepted "+record.topic());
                    System.err.println(record.value());
                }
            });

        });


        consumer.subscribe(responderLoctationUpdateTopic, ar -> {
            if (ar.succeeded()) {
                logger.info("subscribed to ResponderLocationUpdate");
            } else {
                logger.fatal("Could not subscribe " + ar.cause().getMessage());
            }
        });

    }

    @Override
    public void stop() throws Exception {
        consumer.unsubscribe(ar -> {

            if (ar.succeeded()) {
                logger.info("Consumer unsubscribed");
            }
        });
    }

}
