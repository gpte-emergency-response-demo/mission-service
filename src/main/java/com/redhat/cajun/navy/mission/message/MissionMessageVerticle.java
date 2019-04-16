package com.redhat.cajun.navy.mission.message;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

public abstract class MissionMessageVerticle extends AbstractVerticle {

    KafkaConsumer<String, String> consumer = null;

    KafkaProducer<String,String> producer = null;

    public static final String CACHE_QUEUE = "cache.queue";

    public static final String PUB_QUEUE = "pub.queue";

    public String missionUpdateCommandTopic = null;

    public String createMissionCommandTopic = null;

    public String responderLoctationUpdateTopic = null;

    public String responderUpdateTopic = null;

    public enum ErrorCodes {
        NO_ACTION_SPECIFIED,
        BAD_ACTION
    }


    Map<String, String> config = new HashMap<>();

    protected abstract void init(Future<Void> startFuture) throws Exception;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        //config.put("bootstrap.servers", "localhost:9092");
        config.put("bootstrap.servers", config().getString("kafka.connect", "localhost:9092"));
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", config().getString("kafka.group.id"));
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", config().getBoolean("kafka.autocommit", true).toString());
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        missionUpdateCommandTopic = config().getString("kafka.pub");
        createMissionCommandTopic = config().getString("kafka.sub");
        responderLoctationUpdateTopic = config().getString("kafka.sub.responder.loc.update");
        responderUpdateTopic = config().getString("kafka.sub.responder.update");
        init(startFuture);

    }

}
