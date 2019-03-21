package com.redhat.cajun.navy.mission.cache;

import io.vertx.core.AbstractVerticle;
import org.infinispan.client.hotrod.RemoteCache;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;


import io.vertx.core.Future;

public abstract class CacheAccessVerticle extends AbstractVerticle {

    protected RemoteCacheManager client;
    protected RemoteCache<String, String> defaultCache;

    protected abstract void init(Future<Void> startFuture);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.<RemoteCache<String, String>>executeBlocking(fut -> {
            Configuration configuration = new ConfigurationBuilder().addServer()
                    .host(config().getString("jdg.host", "localhost"))
                    .port(config().getInteger("jdg.port", 11222))
                    .clientIntelligence(ClientIntelligence.BASIC)
                    .build();
            client = new RemoteCacheManager(
                    configuration);

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
