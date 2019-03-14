package com.redhat.cajun.navy.mission;

import java.util.Objects;

import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.handler.BodyHandler;

import io.vertx.ext.web.handler.StaticHandler;
import rx.Observable;


public class MissionMain extends AbstractVerticle {


    @Override
    public void start(final Future<Void> future) {
        // Create Router
        Router router = createRouter();

        Observable.from(getRouterConsumers())
                .filter(Objects::nonNull)
                .flatMapCompletable(r -> {
                    r.accept(router);
                    return r.start();
                })
                .toCompletable()
                .doOnCompleted(future::complete)
                .doOnError(future::fail)
                .subscribe(() ->
                        vertx
                                .createHttpServer()
                                .requestHandler(router::accept)
                                .listen(
                                        // Retrieve the port from the configuration,
                                        // default to 8080.
                                        config().getInteger("http.port", 8080),
                                        result -> {
                                            if (result.succeeded()) {
                                                future.complete();
                                            } else {
                                                future.fail(result.cause());
                                            }
                                        }
                                ));

    }

    private Router createRouter() {
        // Create a router object.
        Router router = Router.router(vertx);
        // enable parsing of request bodies
        router.route().handler(BodyHandler.create());

        // health check
        router.get("/health").handler(rc -> rc.response().end("OK"));

        // web interface
        router.get().handler(StaticHandler.create());


        return router;
    }


    private RouterConsumer[] getRouterConsumers() {
        return new RouterConsumer[]{
                new HttpStaticVerticle(vertx),
                new MissionRestVerticle(vertx)
        };
    }


}
