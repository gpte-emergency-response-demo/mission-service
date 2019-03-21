package com.redhat.cajun.navy.mission.http;


import com.redhat.cajun.navy.mission.MessageAction;
import com.redhat.cajun.navy.mission.cache.CacheAccessVerticle;
import com.redhat.cajun.navy.mission.data.CreateMissionCommand;
import com.redhat.cajun.navy.mission.data.Mission;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MissionRestVerticle extends CacheAccessVerticle {

    final String MISSIONS_EP = "/api/missions";

    private final Logger logger = Logger.getLogger(MissionRestVerticle.class.getName());

    public static final String CACHE_QUEUE = "cache.queue";

    public static final String PUB_QUEUE = "pub.queue";

    @Override
    protected void init(Future<Void> startFuture) {
        String host = config().getString("http.host");
        int port = config().getInteger("http.port");
        Router router = Router.router(vertx);

        router.get("/").handler(rc -> {
            rc.response().putHeader("content-type", "text/html")
                    .end(" Missions API Service");
        });

        vertx.eventBus().consumer(config().getString(CACHE_QUEUE, "cache.queue"), this::onMessage);

        router.route().handler(BodyHandler.create());
        router.get(MISSIONS_EP).handler(this::getAll);
        router.put(MISSIONS_EP).handler(this::addMission);
        router.get(MISSIONS_EP + "/:id").handler(this::missionById);
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

    private void addMission(RoutingContext routingContext) {
        try {
            boolean prepareMessage = false;
            Mission mission = Json.decodeValue(routingContext.getBodyAsString(), Mission.class);
            if(mission.getId() == null){
                mission.setId(UUID.randomUUID().toString());
            }
            else{
                prepareMessage = true;
            }
            logger.log(Level.INFO,"putting.. " + mission.getId() + "\n " + mission);
            defaultCache.putAsync(mission.getId(), mission.toString())
                    .whenComplete((s, t) -> {
                        if (t == null) {
                            routingContext.response()
                                    .setStatusCode(201)
                                    .putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(mission));
                        } else {
                            routingContext.fail(500);
                        }
                    });

            if(prepareMessage){
                DeliveryOptions options = new DeliveryOptions().addHeader("action", MessageAction.PUBLISH_UPDATE.toString());
                vertx.eventBus().send(PUB_QUEUE, mission.toString(), options, reply -> {
                    if (reply.succeeded()) {
                        System.out.println("Message publish request accepted");
                    } else {
                        System.out.println("Message publish request not accepted");
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum ErrorCodes {
        NO_ACTION_SPECIFIED,
        BAD_ACTION
    }

    public void onMessage(Message<JsonObject> message) {

        if (!message.headers().contains("action")) {
            message.fail(ErrorCodes.NO_ACTION_SPECIFIED.ordinal(), "No action header specified");
            return;
        }

        String action = message.headers().get("action");
        switch (action) {
            case "CREATE_ENTRY":
                final Mission m = Json.decodeValue(String.valueOf(message.body()), CreateMissionCommand.class).getBody();
                defaultCache.putAsync(m.getId(), m.toString())
                        .whenComplete((s, t) -> {
                            if (t == null) {
                                message.reply(m.toString());
                            } else {

                            }
                        });
                break;

            default:
                message.fail(ErrorCodes.BAD_ACTION.ordinal(), "Bad action: " + action);

        }
    }

    private void getAll(RoutingContext routingContext) {
// THIS METHOD IS INCOMPLETE
        Set<String> m = defaultCache.keySet();

        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(m.toArray()));
    }

    private void missionById(RoutingContext routingContext) {

        String id = routingContext.request().getParam("id");
        logger.info("Get by id called id=" + id);
        defaultCache.getAsync(routingContext.request().getParam("id"))
                .thenAccept(value -> {
                    String m;
                    if (value == null) {
                        m = String.format("Mission id %s not found", id);
                        routingContext.response().setStatusCode(201);
                    } else {
                        m = Json.encode(value);
                    }
                    routingContext.response().end(m);
                });

    }

}
