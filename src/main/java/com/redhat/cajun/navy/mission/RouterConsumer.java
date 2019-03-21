package com.redhat.cajun.navy.mission;

import java.util.function.Consumer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import rx.Completable;

public abstract class RouterConsumer implements Consumer<Router> {

    protected final Vertx vertx;

    protected RouterConsumer(Vertx vertx) {
        this.vertx = vertx;
    }

    public abstract Completable start();

    public abstract void start(final Future<Void> future, JsonObject config);


}