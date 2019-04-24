package com.redhat.cajun.navy.mission.http;

import com.redhat.cajun.navy.mission.DeployOptions;
import com.redhat.cajun.navy.mission.MessageAction;
import com.redhat.cajun.navy.mission.JsonStrings;
import com.redhat.cajun.navy.mission.data.Mission;
import com.redhat.cajun.navy.mission.data.MissionCommand;
import com.redhat.cajun.navy.mission.data.Responder;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;


@RunWith(VertxUnitRunner.class)
@org.junit.Ignore
public class MissionRestVerticleTest {

    private Vertx vertx;
    public static final String CACHE_QUEUE = "cache.queue";

    @Before
    public void before(TestContext context) throws Exception{
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());

        vertx.deployVerticle(MissionRestVerticle.class.getName(),
                new DeployOptions().getDeployOptions("src/main/resources/local-app-config.properties"),
                context.asyncAssertSuccess());

    }


//    @Test
//    public void checkCreateEntry(TestContext context){
//        Async async = context.async();
//
//        MissionCommand mc = Json.decodeValue(new JsonStrings().getMissionsCommands().get(0), MissionCommand.class);
//
//        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.CREATE_ENTRY.toString());
//
//        EventBus eb = vertx.eventBus();
//        eb.send(CACHE_QUEUE, mc.toString(), options, reply ->{
//            context.assertEquals(true, reply.succeeded());
//            async.complete();
//        });
//
//    }
//
//
//    @Test
//    public void checkUpdateWithoutMission(TestContext context){
//        Async async = context.async();
//
//        Responder r = Json.decodeValue(new JsonStrings().getResponderSimEvents().get(0), Responder.class);
//
//        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.UPDATE_ENTRY.toString());
//
//        EventBus eb = vertx.eventBus();
//        eb.send(CACHE_QUEUE, r.toString(), options, reply ->{
//            context.assertEquals(true, reply.failed());
//            async.complete();
//        });
//    }
//
//
//
//    @Test
//    public void checkMissionUpdatedPickedUp(TestContext context){
//        Async async = context.async();
//
//        MissionCommand mc = Json.decodeValue(new JsonStrings().getMissionsCommands().get(0), MissionCommand.class);
//
//        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.CREATE_ENTRY.toString());
//
//        EventBus eb = vertx.eventBus();
//        eb.send(CACHE_QUEUE, mc.toString(), options, reply ->{
//            context.assertEquals(true, reply.succeeded());
//            System.out.println(reply.result().body());
//
//        });
//
//
//        Responder r = Json.decodeValue(new JsonStrings().getResponderSimEvents().get(1), Responder.class);
//        options = new DeliveryOptions().addHeader("action", MessageAction.UPDATE_ENTRY.toString());
//        eb.send(CACHE_QUEUE, r.toString(), options, reply ->{
//            context.assertEquals(true, reply.succeeded());
//            Mission m = Json.decodeValue(String.valueOf(reply.result().body()), Mission.class);
//            context.assertEquals(MissionRestVerticle.MissionEvents.UPDATED.getActionType(), m.getStatus());
//
//        });
//
//        async.complete();
//
//    }
//
//
//    @Test
//    public void checkMissionUpdatedDropped(TestContext context){
//        Async async = context.async();
//
//        MissionCommand mc = Json.decodeValue(new JsonStrings().getMissionsCommands().get(0), MissionCommand.class);
//
//        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.CREATE_ENTRY.toString());
//
//        EventBus eb = vertx.eventBus();
//        eb.send(CACHE_QUEUE, mc.toString(), options, reply ->{
//            context.assertEquals(true, reply.succeeded());
//            System.out.println(reply.result().body());
//
//        });
//
//
//        Responder r = Json.decodeValue(new JsonStrings().getResponderSimEvents().get(2), Responder.class);
//        options = new DeliveryOptions().addHeader("action", MessageAction.UPDATE_ENTRY.toString());
//        eb.send(CACHE_QUEUE, r.toString(), options, reply ->{
//            context.assertEquals(true, reply.succeeded());
//            Mission m = Json.decodeValue(String.valueOf(reply.result().body()), Mission.class);
//            context.assertEquals(MissionRestVerticle.MissionEvents.COMPLETED.getActionType(), m.getStatus());
//
//        });
//
//        async.complete();
//
//    }


    @Test
    public void checkMissionMessagesFromSimulator(TestContext context){
        Async async = context.async();


        MissionCommand mc = Json.decodeValue(new JsonStrings().getMissionsCommands().get(1), MissionCommand.class);

        String missionId = mc.getBody().getId();

        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.CREATE_ENTRY.toString());

        EventBus eb = vertx.eventBus();
        eb.send(CACHE_QUEUE, mc.toString(), options, reply ->{
            context.assertEquals(true, reply.succeeded());
            System.out.println(reply.result().body());

        });


        Observable.from(new JsonStrings().getResponderLocationMessages()).concatMap(r->{

            Responder r1 = Json.decodeValue(r, Responder.class);
            r1.setMissionId(missionId);
            DeliveryOptions opts = new DeliveryOptions().addHeader("action", MessageAction.UPDATE_ENTRY.toString());

            eb.send(CACHE_QUEUE, r.toString(), opts, reply ->{
                context.assertEquals(true, reply.succeeded());
                Mission m = Json.decodeValue(String.valueOf(reply.result().body()), Mission.class);

            });
            return Observable.just(r);
        }).subscribe();

        async.complete();

    }



    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


}