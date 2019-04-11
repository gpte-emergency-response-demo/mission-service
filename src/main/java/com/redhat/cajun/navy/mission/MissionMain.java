package com.redhat.cajun.navy.mission;


import com.redhat.cajun.navy.mission.http.MissionRestVerticle;
import com.redhat.cajun.navy.mission.message.MissionConsumerVerticle;
import com.redhat.cajun.navy.mission.message.MissionProducerVerticle;
import com.redhat.cajun.navy.mission.message.ResponderLocConsumer;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;


public class MissionMain extends AbstractVerticle {
    @Override
    public void start(final Future<Void> future) {

        ConfigRetriever.create(vertx, selectConfigOptions())
                .getConfig(ar -> {
                    if (ar.succeeded()) {
                        deployVerticles(ar.result(), future);
                    } else {
                        System.out.println("Failed to retrieve the configuration.");
                        future.fail(ar.cause());
                    }
                });
    }


    private ConfigRetrieverOptions selectConfigOptions(){
        ConfigRetrieverOptions options = new ConfigRetrieverOptions();

        if (System.getenv("KUBERNETES_NAMESPACE") != null) {
            ConfigStoreOptions appStore = new ConfigStoreOptions()
                    .setType("file")
                    .setFormat("properties")
                    .setConfig(new JsonObject()
                            .put("name", System.getenv("APP_CONFIGMAP_NAME"))
                            .put("key", System.getenv("APP_CONFIGMAP_KEY"))
                            .put("path", "/deployments/config/app-config.properties"));
            options.addStore(appStore);
        } else {
            ConfigStoreOptions props = new ConfigStoreOptions()
                    .setType("file")
                    .setFormat("properties")
                    .setConfig(new JsonObject().put("path", "local-app-config.properties"));
            System.out.println(config());
            options.addStore(props);
        }

        return options;
    }


    private void deployVerticles(JsonObject config, Future<Void> future){

        Future<String> rFuture = Future.future();
        Future<String> cFuture = Future.future();
        Future<String> pFuture = Future.future();


        DeploymentOptions options = new DeploymentOptions();

        options.setConfig(config);
        vertx.deployVerticle(new MissionRestVerticle(), options, rFuture.completer());
        vertx.deployVerticle(new MissionConsumerVerticle(), options, cFuture.completer());
        vertx.deployVerticle(new MissionProducerVerticle(), options, cFuture.completer());
        vertx.deployVerticle(new ResponderLocConsumer(), options, rFuture.completer());

        CompositeFuture.all(rFuture, cFuture, pFuture).setHandler(ar -> {
            if (ar.succeeded()) {
                System.out.println("Verticles deployed successfully.");
                future.complete();
            } else {
                System.out.println("WARNINIG: Verticles NOT deployed successfully.");
                future.fail(ar.cause());
            }
        });

    }




    // Used for debugging in IDE
    public static void main(String[] args) {
        io.vertx.reactivex.core.Vertx vertx = io.vertx.reactivex.core.Vertx.vertx();

        vertx.rxDeployVerticle(MissionMain.class.getName())
                .subscribe();
    }

}
