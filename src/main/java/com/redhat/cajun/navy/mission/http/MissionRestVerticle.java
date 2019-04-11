package com.redhat.cajun.navy.mission.http;


import com.redhat.cajun.navy.mission.MessageAction;
import com.redhat.cajun.navy.mission.cache.CacheAccessVerticle;
import com.redhat.cajun.navy.mission.data.*;
import com.redhat.cajun.navy.mission.map.RoutePlanner;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import rx.Observable;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


public class MissionRestVerticle extends CacheAccessVerticle {

    final String MISSIONS_EP = "/api/missions";

    private final Logger logger = Logger.getLogger(MissionRestVerticle.class.getName());

    public static final String CACHE_QUEUE = "cache.queue";

    public static final String PUB_QUEUE = "pub.queue";

    private static final String MAPBOX_ACCESS_TOKEN_KEY = "map.token";

    private String MAPBOX_ACCESS_TOKEN = null;

    public enum MessageType {
        MissionStartedEvent("MissionStartedEvent"),
        MissionPickedUpEvent("MissionPickedUpEvent"),
        MissionCompletedEvent("MissionCompletedEvent");

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
        String host = config().getString("http.host");
        int port = config().getInteger("http.port");
        MAPBOX_ACCESS_TOKEN = config().getString("map.token");


        Router router = Router.router(vertx);

        router.get("/").handler(rc -> {
            rc.response().putHeader("content-type", "text/html")
                    .end(" Missions API Service");
        });

        vertx.eventBus().consumer(config().getString(CACHE_QUEUE, "cache.queue"), this::onMessage);

        router.route().handler(BodyHandler.create());
        router.get(MISSIONS_EP+"/keys").handler(this::getKeysOnly);
        router.get(MISSIONS_EP).handler(this::getAll);
        router.get(MISSIONS_EP + "/:key").handler(this::missionByKey);

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
                MissionRoute mRoute = new RoutePlanner(MAPBOX_ACCESS_TOKEN).getMapboxDirectionsRequest(
                        new Location(m.getResponderStartLat(), m.getResponderStartLong()),
                        new Location(m.getDestinationLat(), m.getDestinationLong()),
                        new Location(m.getIncidentLat(), m.getIncidentLong()));

                m.setRoute(mRoute);

                defaultCache.putIfAbsentAsync(m.getKey(), m.toString())
                        .whenComplete((s, t) -> {
                            if (t == null) {
                                message.reply(m.toString());
                                System.out.println(m.toString());
                            } else {
                                System.out.println(m.toString());
                            }
                        });

                sendUpdate(m, MessageType.MissionStartedEvent);

                break;

            case "UPDATE_ENTRY":
                Responder responder = Json.decodeValue(String.valueOf(message.body()), Responder.class);

                Mission mission = missionByKey(responder.getIncidentId()+responder.getResponderId());
                if(mission != null) {

                    mission.addResponderLocationHistory(new ResponderLocationHistory(System.currentTimeMillis(), responder.getLocation()));

                    if(responder.getStatus() == Responder.Status.PICKEDUP) {
                        mission.setStatus(MissionEvents.UPDATED.getActionType());
                        sendUpdate(mission, MessageType.MissionPickedUpEvent);
                    }
                    else if(responder.getStatus() == Responder.Status.DROPPED) {
                        mission.setStatus(MissionEvents.COMPLETED.getActionType());
                        sendUpdate(mission, MessageType.MissionCompletedEvent);
                    }


                    defaultCache.putAsync(mission.getKey(), mission.toString())
                            .whenComplete((s, t) -> {
                                if (t == null) {
                                    message.reply(mission.toString());
                                    System.out.println(mission.toString());
                                } else {
                                    System.out.println(mission.toString());
                                }
                            });

                    message.reply("Responder location updated");
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

        DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.PUBLISH_UPDATE.toString());
        vertx.eventBus().send(PUB_QUEUE, mc.toString(), options, reply -> {
            if (reply.succeeded()) {
                System.out.println("Message publish request accepted");
            } else {
                System.out.println("Message publish request not accepted");
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

    private void getAll(RoutingContext routingContext) {

        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(defaultCache.keySet().toArray()));
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
