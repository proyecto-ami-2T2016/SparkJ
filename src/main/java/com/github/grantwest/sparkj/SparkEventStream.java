package com.github.grantwest.sparkj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.SparkEvent;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.media.sse.EventListener;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.function.Consumer;

public class SparkEventStream implements AutoCloseable {
    private SparkSession session;
    private Consumer<SparkEvent> eventHandler;
    private EventSource eventSource;

    private SparkEventStream(String baseUrl, String key) {
        Client client = ClientBuilder.newClient();
        client.register(SseFeature.class);
        WebTarget target = client.target(baseUrl + "/v1/devices/events")
                .property("Authorization", "Bearer " + key);
//        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
//        WebTarget target = client.target(baseUrl + "/v1/devices/events/");
        eventSource = EventSource.target(target).build();
        eventSource.register(getEventListener());
        eventSource.open();
    }

    public SparkEventStream(SparkSession session, Consumer<SparkEvent> eventHandler) {
        this(session.baseUrl, session.getTokenKey());
        this.eventHandler = eventHandler;
        this.session = session;
    }

    @Override
    public void close() throws Exception {
        eventSource.close();
    }

    private EventListener getEventListener() {
        return inboundEvent -> {
            String rawJson = inboundEvent.readData().replaceFirst("^\\{", "{\"name\":\"" + inboundEvent.getName() + "\",");
            SparkEvent sparkEvent = (SparkEvent) SparkRestApi.jsonToObject(rawJson, new TypeReference<SparkEvent>() {});
            eventHandler.accept(sparkEvent);
        };
    }
}
