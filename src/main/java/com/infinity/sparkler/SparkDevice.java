package com.infinity.sparkler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infinity.sparkler.SparkCloudJsonObjects.FunctionResult;
import com.infinity.sparkler.SparkCloudJsonObjects.VariableReadResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.MultipartBody;

public class SparkDevice implements ISparkDevice {

    private SparkCloudSession session;
    private String deviceId;

    public SparkDevice(String id, String username, String password) {
        session = new SparkCloudSession(username, password);
        deviceId = id;
        init();
    }

    public SparkDevice(String id, SparkCloudSession session) {
        deviceId = id;
        this.session = session;
        init();
    }

    private void init() {
        if(!session.connected()) {
            session.connect();
        }
    }

    @Override
    public String readVariable(String variableName) {
        HttpRequest req = Unirest.get(session.baseUrl + "/v1/devices/" + deviceId + "/" + variableName + "?access_token=" + session.getTokenKey())
                .header("accept", "application/json");
        HttpResponse<String> res = SparkRestApi.sendRequest(req);
        return ((VariableReadResult) SparkRestApi.jsonToObject(res.getBody(), new TypeReference<VariableReadResult>() {})).result;
    }

    @Override
    public int callFunction(String functionName, String arguments) {
        MultipartBody req = Unirest.post(session.baseUrl + "/v1/devices/" + deviceId + "/" + functionName)
                .header("accept", "application/json")
                .field("access_token", session.getTokenKey())
                .field("args", arguments);
        HttpResponse<String> res = SparkRestApi.sendRequest(req);
        return ((FunctionResult) SparkRestApi.jsonToObject(res.getBody(), new TypeReference<FunctionResult>() {})).return_value;
    }
}
