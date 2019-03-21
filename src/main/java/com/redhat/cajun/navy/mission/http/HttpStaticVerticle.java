package com.redhat.cajun.navy.mission.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Objects;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class HttpStaticVerticle extends AbstractVerticle {


    protected static final String TEMPLATE = "Hello, %s!";


    @Override
    public void start(Future<Void> future) {
        Router router = Router.router(vertx);

        router.get("/api/greeting").handler(this::greeting);

        // enable parsing of request bodies
        router.route().handler(BodyHandler.create());

        // health check
        router.get("/health").handler(rc -> rc.response().end("OK"));

        // web interface
        router.get().handler(StaticHandler.create());


        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 9191),
                        result -> {
                            if (result.succeeded()) {
                                future.complete();
                            } else {
                                future.fail(result.cause());
                            }
                        }
                );

    }


    private void greeting(RoutingContext rc) {
        String name = Objects.toString(rc.request().getParam("name"), "World");

        JsonObject response = new JsonObject()
                .put("content", String.format(TEMPLATE, name));

        rc.response()
                .putHeader(CONTENT_TYPE.toString(), "application/json; charset=utf-8")
                .end(response.encodePrettily());
    }

}
