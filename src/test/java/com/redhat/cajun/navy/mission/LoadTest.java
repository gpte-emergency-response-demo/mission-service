package com.redhat.cajun.navy.mission;


import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import rx.Observable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.*;

public class LoadTest {

    /**
     *
     * To run this test, Ideally responder simulator and Mission service should both be online
     * This test assumes, you have kafka running and configured on localhost as well as JDG/Infinispan
     * The current JsonFile holds 55 records.
     *
     * */

    public static void main(String args[]) throws Exception{
        //testMissionResponders();
        //testMissionsWithSameResponders();
        testMission1Responders();
       // test2Missions1Responder();
    }


    public static void test2Missions1Responder() throws Exception {
        Properties initProps = new Properties();
        String MissionCommandsFile = "src/test/resources/CreateMissionCommand.json";
        initProps.load(new FileInputStream(new File("src/main/resources/local-app-config.properties")));

        Properties props = new Properties();

        props.put("bootstrap.servers", initProps.getProperty("kafka.connect", "localhost:9092"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        String topicName = initProps.get("kafka.sub").toString();

        producer.send(new ProducerRecord<String, String>(topicName, getMissionCommandsFromFile(MissionCommandsFile).get(0)));
        producer.send(new ProducerRecord<String, String>(topicName, getMissionCommandsFromFile(MissionCommandsFile).get(1)));


        producer.close();


    }

    public static void testMission1Responders() throws Exception{
        Properties initProps = new Properties();
        String MissionCommandsFile = "src/test/resources/CreateMissionCommand.json";
        initProps.load(new FileInputStream(new File("src/main/resources/local-app-config.properties")));

        Properties props = new Properties();

        props.put("bootstrap.servers", initProps.getProperty("kafka.connect", "localhost:9092"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        String topicName = initProps.get("kafka.sub").toString();

        producer.send(new ProducerRecord<String, String>(topicName, getMissionCommandsFromFile(MissionCommandsFile).get(1)));

// 72d36a5f-85a3-4fd5-b2d5-1fa6abb6466254

        producer.close();
    }


    public static void testMissionResponders() throws Exception{

        Properties initProps = new Properties();

        initProps.load(new FileInputStream(new File("src/main/resources/local-app-config.properties")));

        Properties props = new Properties();

        props.put("bootstrap.servers", initProps.getProperty("kafka.connect", "localhost:9092"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        String topicName = initProps.get("kafka.sub").toString();
        Observable.from(JsonStrings.getMissionsCommands()).concatMap(entry->{
            producer.send(new ProducerRecord<String, String>(topicName, entry));
            return Observable.just(entry);
        }).doOnError(System.out::println).subscribe();

        producer.close();
    }



    public static void testMissionsWithSameResponders() throws Exception{

        Properties initProps = new Properties();
        initProps.load(new FileInputStream(new File("src/main/resources/local-app-config.properties")));

        Properties props = new Properties();

        props.put("bootstrap.servers", initProps.getProperty("kafka.connect", "localhost:9092"));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        String topicName = initProps.get("kafka.sub").toString();
        Observable.from(JsonStrings.getMissionsCommandWithSameResponders()).flatMap(entry->{
            producer.send(new ProducerRecord<String, String>(topicName, entry));
            return Observable.just(entry);
        }).doOnError(System.out::println).subscribe();

        producer.close();
    }


    private static List<String> getMissionCommandsFromFile(String fileName) {
        List<String> list = new ArrayList<>();
        int count = 1;
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                for (String line; (line = br.readLine()) != null; ) {
                    list.add(line);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
