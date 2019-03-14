package com.redhat.cajun.navy.mission;



import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import rx.Completable;
import io.vertx.core.json.Json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class MissionRestVerticle extends RouterConsumer {

    private Map<String, Mission> missions = null;


    public MissionRestVerticle(Vertx vertx) {
        super(vertx);
    }

    @Override
    public void accept(Router router) {
        missions = new HashMap<String, Mission>();
        createData(missions);

        router.get("/api/missions").handler(this::getAll);

//        router.route("/api/missions*").handler(BodyHandler.create());
//        router.post("/api/missions").handler(this::addMission);
//        router.put("/api/missions").handler(this::updateMission);
    }

    @Override
    public Completable start() {
        return Completable.complete();
    }


    public void createData(Map<String, Mission> missions) {
        this.missions = missions;
        Mission m = new Mission("1", "1",
                new BigDecimal(34.16877), new BigDecimal(34.16877),
                new BigDecimal(34.16877), new BigDecimal(34.16877),
                new BigDecimal(34.16877), new BigDecimal(34.16877),
                "ASSIGNED");

        missions.put(m.getId(), m);

        // missions.put(2, new Mission()));

    }


    private void addMission(RoutingContext routingContext) {
        final Mission mission = Json.decodeValue(routingContext.getBodyAsString(),
                Mission.class);
        missions.put(mission.getId(), mission);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(mission));
    }


    private void updateMission(RoutingContext routingContext) {
        final Mission mission = Json.decodeValue(routingContext.getBodyAsString(),
                Mission.class);
        Mission m = missions.get(mission.getId());
        m.addResponderLocationHistory(mission.getResponderLocationHistory().get(0));
        missions.put(m.getId(), mission);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(mission));
    }


    private void getAll(RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(missions.values()));
    }


}
