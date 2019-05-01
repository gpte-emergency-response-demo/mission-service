package com.redhat.cajun.navy.mission.message;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ConsumerConfig {


    public static Map<String, String> getConfig(JsonObject vertxConfig){

        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", vertxConfig.getString("kafka.connect", "localhost:9092"));
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", vertxConfig.getString("kafka.group.id"));
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", vertxConfig.getBoolean("kafka.autocommit", true).toString());

        return config;
    }

}
