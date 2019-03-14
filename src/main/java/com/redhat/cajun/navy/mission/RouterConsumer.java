package com.redhat.cajun.navy.mission;

import java.util.function.Consumer;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import rx.Completable;

public abstract class RouterConsumer implements Consumer<Router> {

    protected final Vertx vertx;

    protected RouterConsumer(Vertx vertx) {
        this.vertx = vertx;
    }

    public abstract Completable start();

}