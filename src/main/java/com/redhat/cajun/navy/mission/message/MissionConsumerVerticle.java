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

public class MissionConsumerVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(MissionConsumerVerticle.class.getName());
    private Map<String, String> config = new HashMap<>();
    KafkaConsumer<String, String> consumer = null;
    public static final String CACHE_QUEUE = "cache.queue";
    public String responderLoctationUpdateTopic = null;

    public String createMissionCommandTopic = null;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        createMissionCommandTopic = config().getString("kafka.sub");


        consumer = KafkaConsumer.create(vertx, ConsumerConfig.getConfig(config()));

        consumer.handler(record -> {
            DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.CREATE_ENTRY.toString());
            vertx.eventBus().send(CACHE_QUEUE, record.value(), options, reply -> {
                if (reply.succeeded()) {
                    logger.debug("Message accepted");
                } else {
                    logger.error("Incoming Message not accepted "+record.topic());
                    logger.error(record.value());
                }
            });
        });


        consumer.subscribe(createMissionCommandTopic,  ar -> {
            if (ar.succeeded()) {
                logger.info("subscribed to MissionCommand");
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
