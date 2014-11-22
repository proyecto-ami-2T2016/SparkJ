package com.infinity.sparkler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.SparkCloudJsonObjects.FunctionResult;
import com.infinity.sparkler.SparkCloudJsonObjects.VariableReadResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.MultipartBody;

import java.io.IOException;

public class SparkDevice implements ISparkDevice {

    private SparkCloudSession session;
    private String deviceId;
    private ObjectMapper objectMapper;

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
        objectMapper = new ObjectMapper();
        if(!session.connected()) {
            session.connect();
        }
    }

    @Override
    public String readVariable(String variableName) {
        try {
            HttpRequest req = Unirest.get(session.baseUrl + "/v1/devices/" + deviceId + "/" + variableName + "?access_token=" + session.getTokenKey())
                    .header("accept", "application/json");
            HttpResponse<String> res = session.sendRequest(req);
            VariableReadResult readResult = objectMapper.readValue(res.getBody(), new TypeReference<VariableReadResult>() {});
            return readResult.result;
        } catch (JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int callFunction(String functionName, String arguments) {
        try {
            MultipartBody req = Unirest.post(session.baseUrl + "/v1/devices/" + deviceId + "/" + functionName)
                    .header("accept", "application/json")
                    .field("access_token", session.getTokenKey())
                    .field("args", arguments);
            HttpResponse<String> res = session.sendRequest(req);
            FunctionResult result = objectMapper.readValue(res.getBody(), new TypeReference<FunctionResult>() {});
            return result.return_value;
        } catch (JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
