package com.infinity.sparkler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.VariableReadResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.List;

public class SparkDevice implements ISparkDevice {

    private SparkCloudSession session;
    private String deviceId;
    private ObjectMapper objectMapper;

    public SparkDevice(String id, String username, String password) {
        session = new SparkCloudSession(username, password);
        deviceId = id;
    }

    public SparkDevice(String id, SparkCloudSession session) {
        deviceId = id;
        this.session = session;
    }

    @Override
    public String readVariable(String variableName) {
        try {
            HttpResponse<String> res = Unirest.get(session.baseUrl + "/v1/devices/" + deviceId + "/" + variableName + "?access_token=" + session.getTokenKey())
                    .header("accept", "application/json")
                    .asString();
            return objectMapper.readValue(res.getBody(), new TypeReference<VariableReadResult>() {});
        } catch (UnirestException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String callFunction(String functionName, String argument) {
        return null;
    }
}
