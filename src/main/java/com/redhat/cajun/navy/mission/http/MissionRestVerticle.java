package com.redhat.cajun.navy.mission.http;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.redhat.cajun.navy.mission.MessageAction;
import com.redhat.cajun.navy.mission.cache.CacheAccessVerticle;
import com.redhat.cajun.navy.mission.data.*;
import com.redhat.cajun.navy.mission.map.RoutePlanner;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.vertx.MetricsHandler;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import rx.Observable;
import scala.reflect.internal.Trees;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public class MissionRestVerticle extends CacheAccessVerticle {

    final String MISSIONS_EP = "/api/missions";

    private final Logger logger = Logger.getLogger(MissionRestVerticle.class.getName());

    public static final String CACHE_QUEUE = "cache.queue";

    public static final String PUB_QUEUE = "pub.queue";

    private String MAPBOX_ACCESS_TOKEN = null;

    public enum MessageType {
        MissionStartedEvent("MissionStartedEvent"),
        MissionPickedUpEvent("MissionPickedUpEvent"),
        MissionCompletedEvent("MissionCompletedEvent"),
        UpdateResponderCommand("UpdateResponderCommand");

        private String messageType;

        MessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getMessageType() {
            return messageType;
        }

    }


    @Override
    protected void init(Future<Void> startFuture) {
        String host = config().getString("http.host", "localhost");
        int port = config().getInteger("http.port", 8888);
        MAPBOX_ACCESS_TOKEN = config().getString("map.token");

        vertx.eventBus().consumer(config().getString(CACHE_QUEUE, "cache.queue"), this::onMessage);

//        CollectorRegistry registry = CollectorRegistry.defaultRegistry;
//        MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate("prometheus");
//        new DropwizardExports(metricRegistry).register(registry);
//        new StandardExports().register(registry);
//        new MemoryPoolsExports().register(registry);

        Router router = Router.router(vertx);

        router.get("/").handler(rc -> {
            rc.response().putHeader("content-type", "text/html")
                    .end(" Missions API Service");
        });
//        router.get("/metrics").handler(new MetricsHandler());

        router.route().handler(BodyHandler.create());
        router.get(MISSIONS_EP).handler(this::getAll);
        router.get(MISSIONS_EP + "/keys").handler(this::getKeysOnly);
        router.get(MISSIONS_EP + "/clear").handler(this::clearAll);
        router.get(MISSIONS_EP + "/:key").handler(this::missionByKey);
        router.get(MISSIONS_EP + "/responders/:id").handler(this::getByResponder);

        HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
                .register("health", f -> f.complete(Status.OK()));
        router.get("/health").handler(healthCheckHandler);


        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(port, ar -> {
                    if (ar.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(ar.cause());
                    }
                });
    }

    public enum ErrorCodes {
        NO_ACTION_SPECIFIED,
        BAD_ACTION
    }

    public enum MissionEvents {
        CREATED("CREATED"),
        UPDATED("UPDATED"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED");


        private String actionType;

        MissionEvents(String actionType) {
            this.actionType = actionType;
        }

        public String getActionType() {
            return actionType;
        }

    }

    public void onMessage(Message<JsonObject> message) {

        if (!message.headers().contains("action")) {
            message.fail(ErrorCodes.NO_ACTION_SPECIFIED.ordinal(), "No action header specified");
            return;
        }

        String action = message.headers().get("action");
        switch (action) {
            case "CREATE_ENTRY":
                Mission m = Json.decodeValue(String.valueOf(message.body()), MissionCommand.class).getBody();
                m.setStatus(MissionEvents.CREATED.getActionType());

                // Incase the responders lat,longs are not viable
                if(m.getResponderStartLat() == 0)
                    m.setResponderStartLat(m.getIncidentLat());
                if(m.getResponderStartLong() == 0)
                    m.setResponderStartLat(m.getIncidentLong());

                MissionRoute mRoute = new RoutePlanner(MAPBOX_ACCESS_TOKEN).getMapboxDirectionsRequest(
                        new Location(m.getResponderStartLat(), m.getResponderStartLong()),
                        new Location(m.getDestinationLat(), m.getDestinationLong()),
                        new Location(m.getIncidentLat(), m.getIncidentLong()));

                m.setRoute(mRoute);

                defaultCache.putIfAbsentAsync(m.getKey(), m.toString())
                        .whenComplete((s, t) -> {
                            message.reply(m.toString());
                        });

                sendUpdate(m, MessageType.MissionStartedEvent);

                break;

            case "UPDATE_ENTRY":
                Responder responder = Json.decodeValue(String.valueOf(message.body()), Responder.class);

                Mission mission = missionByKey(responder.getIncidentId()+responder.getResponderId());



                if(mission != null) {
                    mission.addResponderLocationHistory(new ResponderLocationHistory(System.currentTimeMillis(), responder.getLocation()));

                    // We are only interested in the following status for MissionEvents
                    if(responder.getStatus() == Responder.Status.PICKEDUP) {
                        mission.setStatus(MissionEvents.UPDATED.getActionType());
                        message.reply(mission.toString());
                        sendUpdate(mission, MessageType.MissionPickedUpEvent);
                    }
                    else if(responder.getStatus() == Responder.Status.DROPPED) {
                        mission.setStatus(MissionEvents.COMPLETED.getActionType());
                        message.reply(mission.toString());
                        sendUpdate(mission, MessageType.MissionCompletedEvent);
                        sendUpdate(responder, MessageType.UpdateResponderCommand, true);
                    }


                    defaultCache.putAsync(mission.getKey(), mission.toString())
                            .whenComplete((s, t) -> {
                                message.reply(mission.toString());
                                System.out.println(s);
                            });

                }
                else message.fail(ErrorCodes.BAD_ACTION.ordinal(), "Bad action: " + action +" MissionId Doest not exist");

                break;

            default:
                message.fail(ErrorCodes.BAD_ACTION.ordinal(), "Bad action: " + action);

        }
    }


    private void sendUpdate(Mission m, MessageType event) {
        // Possible issue here, since DG might not be updated and this message is publised for Mission Created.
        MissionCommand mc = new MissionCommand();
        mc.createMissionCommandHeaders(event.getMessageType());
        mc.setMission(m);

        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.PUBLISH_UPDATE.toString())
                .addHeader("key", m.getIncidentId());
        vertx.eventBus().send(PUB_QUEUE, mc.toString(), options, reply -> {
            if (reply.failed()) {
                System.err.println("Message publish request not accepted while sending update "+event);
            }
        });

    }


    private void sendUpdate(Responder responder, MessageType event, boolean available) {
        // Possible issue here, since DG might not be updated and this message is publised for Mission Created.
        ResponderCommand rc = new ResponderCommand(responder, event.getMessageType());
        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.RESPONDER_UPDATE.toString())
                .addHeader("key", responder.getResponderId());

        vertx.eventBus().send(PUB_QUEUE, rc.getResponderCommand(available), options, reply -> {
            if (reply.failed()) {
                System.err.println("Message publish request not accepted while sending update "+event);
            }
        });

    }


    private void getKeysOnly(RoutingContext routingContext) {

        Set<String> m = defaultCache.keySet();

        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(m.toArray()));
    }

    private void completeAll(){
        Observable.from(defaultCache.keySet()).flatMap(s->{
            Mission m = missionByKey(s);
            if(!m.getStatus().equalsIgnoreCase("COMPLETED")) {
                sendUpdate(m, MessageType.MissionCompletedEvent);
                Responder r = new Responder();
                r.setResponderId(m.getResponderId());
                r.setLocation(new Location(m.getDestinationLat(), m.getDestinationLong()));
                sendUpdate(r, MessageType.UpdateResponderCommand, true);
            }
            return Observable.just(m);
        }).subscribe();
        logger.info("Marked all data as complete");
    }

    private void clearAll(RoutingContext routingContext){
        completeAll();
        defaultCache.clearAsync().whenComplete((s, t) -> {

        });
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end("{\n" +
                        "  \"result\": \"completed\"\n" +
                        "}");
    }

    private void getAll(RoutingContext routingContext) {


        Set<String> set = defaultCache.keySet();
        Set<Mission> list = new HashSet<>(defaultCache.keySet().size());
        Observable.from(set).flatMap(s->{
            Mission m = missionByKey(s);
            list.add(m);
            return Observable.just(m);
        }).subscribe();

        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(list));
    }

    private void getByResponder(RoutingContext routingContext) {
        String responderId = routingContext.request().getParam("id");
        logger.info("getByResponder: responderId=" + responderId);

        Mission m = responderById(responderId);
        if (m == null) {
            routingContext.response()
                    .setStatusCode(HttpURLConnection.HTTP_NO_CONTENT)
                    .end("Response:" + HttpURLConnection.HTTP_NO_CONTENT);
        } else {
            routingContext.response()
                    .setStatusCode(HttpURLConnection.HTTP_CREATED)
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(m));
        }

    }

    private Mission responderById(String responderId) {
        Set<String> set = defaultCache.keySet();
        ArrayList<Mission> list = new ArrayList<>(1);

        Observable.from(set).flatMap(s -> {
            Mission m = missionByKey(s);
            if (m.getResponderId().equalsIgnoreCase(responderId))
                list.add(m);
            return Observable.just(s);
        }).subscribe();

        if (list.isEmpty())
            return null;
        else
            return list.get(0);
    }

    private void missionByKey(RoutingContext routingContext) {

        String key = routingContext.request().getParam("key");
        logger.info("missionByKey: key=" + key);

        int responseCode = HttpURLConnection.HTTP_CREATED;
        Mission m = missionByKey(key);
        if (m == null) {
            responseCode = HttpURLConnection.HTTP_NO_CONTENT;
            String.format("Mission key %s not found", key);
            routingContext.response()
                    .setStatusCode(responseCode)
                    .end("Response:"+HttpURLConnection.HTTP_NO_CONTENT);
        } else {
           routingContext.response()
               .setStatusCode(responseCode)
               .putHeader("content-type", "application/json; charset=utf-8")
               .end(Json.encodePrettily(m));
        }
    }

    private Mission missionByKey(String m){
       String val = defaultCache.get(m);
        if(val != null)
            return Json.decodeValue(val, Mission.class);
        else return null;
    }

}
