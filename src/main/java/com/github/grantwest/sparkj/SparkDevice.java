package com.github.grantwest.sparkj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.FunctionResult;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.SparkEvent;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.VariableReadResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.MultipartBody;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.function.Consumer;

public class SparkDevice {

    private SparkSession session;
    public final String deviceId;

    public SparkDevice(String id, String username, String password) {
        this(id, new SparkSession(username, password));
    }

    public SparkDevice(String id, SparkSession session) {
        deviceId = id;
        this.session = session;
        this.session.connectIfNotConnected();
    }

    public String readVariable(String variableName) {
        session.connectIfNotConnected();
        HttpRequest req = Unirest.get(session.baseUrl + "/v1/devices/" + deviceId + "/" + variableName + "?access_token=" + session.getTokenKey())
                .header("accept", "application/json");
        HttpResponse<String> res = SparkRestApi.sendRequest(req);
        return ((VariableReadResult) SparkRestApi.jsonToObject(res.getBody(), new TypeReference<VariableReadResult>() {})).result;
    }

    public int callFunction(String functionName, String arguments) {
        session.connectIfNotConnected();
        MultipartBody req = Unirest.post(session.baseUrl + "/v1/devices/" + deviceId + "/" + functionName)
                .header("accept", "application/json")
                .field("access_token", session.getTokenKey())
                .field("args", arguments);
        HttpResponse<String> res = SparkRestApi.sendRequest(req);
        return ((FunctionResult) SparkRestApi.jsonToObject(res.getBody(), new TypeReference<FunctionResult>() {})).return_value;
    }

    public SparkEventStream eventStream(Consumer<SparkEvent> eventHandler) {
        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        WebTarget target = client.target(session.baseUrl).path("/v1/devices/" + deviceId + "/events/").queryParam("access_token", session.getTokenKey());
        return new SparkEventStream(target, eventHandler);
    }

    public SparkEventStream eventStream(String prefixFilter, Consumer<SparkEvent> eventHandler) {
        Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        WebTarget target = client.target(session.baseUrl).path("/v1/devices/" + deviceId + "/events/" + prefixFilter).queryParam("access_token", session.getTokenKey());
        return new SparkEventStream(target, eventHandler);
    }
}
