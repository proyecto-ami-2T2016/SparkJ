package com.github.grantwest.sparkj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.SparkEvent;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.media.sse.EventListener;

import javax.ws.rs.client.*;
import java.util.function.Consumer;

public class SparkEventStream implements AutoCloseable {
    private EventSource eventSource;

    protected SparkEventStream(WebTarget target, Consumer<SparkEvent> eventHandler) {
        eventSource = EventSource.target(target).build();
        eventSource.register(buildEventListener(eventHandler));
        eventSource.open();
    }

    public static SparkEventStream publicEvents(SparkSession session, Consumer<SparkEvent> eventHandler) {
        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        WebTarget target = client.target(session.baseUrl).path("/v1/events").queryParam("access_token", session.getTokenKey());
        return new SparkEventStream(target, eventHandler);
    }

    public static SparkEventStream publicEvents(SparkSession session, String prefixFilter, Consumer<SparkEvent> eventHandler) {
        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        WebTarget target = client.target(session.baseUrl).path("/v1/events/" + prefixFilter).queryParam("access_token", session.getTokenKey());
        return new SparkEventStream(target, eventHandler);
    }

    public static SparkEventStream myEvents(SparkSession session, Consumer<SparkEvent> eventHandler) {
        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        WebTarget target = client.target(session.baseUrl).path("/v1/devices/events").queryParam("access_token", session.getTokenKey());
        return new SparkEventStream(target, eventHandler);
    }

    public static SparkEventStream myEvents(SparkSession session, String prefixFilter, Consumer<SparkEvent> eventHandler) {
        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        WebTarget target = client.target(session.baseUrl).path("/v1/devices/events/" + prefixFilter).queryParam("access_token", session.getTokenKey());
        return new SparkEventStream(target, eventHandler);
    }

    @Override
    public void close() {
        eventSource.close();
    }

    private EventListener buildEventListener(Consumer<SparkEvent> eventHandler) {
        return inboundEvent -> {
            String rawJson = inboundEvent.readData().replaceFirst("^\\{", "{\"name\":\"" + inboundEvent.getName() + "\",");
            if(rawJson.isEmpty()) return;
            SparkEvent sparkEvent = (SparkEvent) SparkRestApi.jsonToObject(rawJson, new TypeReference<SparkEvent>() {});
            eventHandler.accept(sparkEvent);
        };
    }
}
