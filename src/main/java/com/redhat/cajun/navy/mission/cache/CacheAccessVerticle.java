package com.redhat.cajun.navy.mission.cache;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public abstract class CacheAccessVerticle extends AbstractVerticle {

    protected RemoteCacheManager client;
    protected RemoteCache<String, String> defaultCache;

    protected abstract void init(Future<Void> startFuture);

    @Override
    public void start(Future<Void> startFuture) {


        vertx.<RemoteCache<String, String>>executeBlocking(fut -> {

            ConfigurationBuilder cfg = null;
            if (System.getenv("KUBERNETES_NAMESPACE") != null) {

                cfg = ClientConfiguration.create(config().getString("jdg.svc.name"),
                        config().getString("jdg.app.name"),
                        config().getString("jdg.app.user.name"),
                        config().getString("jdg.app.user.password"));
            } else { // Incase running local
                cfg = new ConfigurationBuilder().addServer()
                        .host(config().getString("jdg.host", "localhost"))
                        .port(config().getInteger("jdg.port", 11222))
                        .clientIntelligence(ClientIntelligence.BASIC);
            }

            client = new RemoteCacheManager(cfg.build());

            RemoteCache<String, String> cache = client.getCache();
            fut.complete(cache);

        }, res -> {
            if (res.succeeded()) {
                System.out.println("Cache connection successfully done");
                defaultCache = res.result();
                init(startFuture);
            } else {
                System.out.println("Cache connection error");
                startFuture.fail(res.cause());
            }
        });
    }
}
