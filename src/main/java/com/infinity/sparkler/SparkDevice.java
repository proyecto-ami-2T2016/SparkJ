package com.infinity.sparkler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infinity.sparkler.SparkCloudJsonObjects.FunctionResult;
import com.infinity.sparkler.SparkCloudJsonObjects.VariableReadResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;

public class SparkDevice implements ISparkDevice {

    private SparkCloudSession session;
    private String deviceId;
    private ObjectMapper objectMapper;

    public SparkDevice(String id, String username, String password) {
        session = new SparkCloudSession(username, password);
        deviceId = id;
        objectMapper = new ObjectMapper();
        session.connect();
    }

    public SparkDevice(String id, SparkCloudSession session) {
        deviceId = id;
        this.session = session;
        objectMapper = new ObjectMapper();
        session.connect();
    }

    @Override
    public String readVariable(String variableName) {
        try {
            HttpResponse<String> res = Unirest.get(session.baseUrl + "/v1/devices/" + deviceId + "/" + variableName + "?access_token=" + session.getTokenKey())
                    .header("accept", "application/json")
                    .asString();
            VariableReadResult readResult = objectMapper.readValue(res.getBody(), new TypeReference<VariableReadResult>() {});
            return readResult.result;
        } catch (UnirestException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int callFunction(String functionName, String arguments) {
        try {
            HttpResponse<String> res = Unirest.post(session.baseUrl + "/v1/devices/" + deviceId + "/" + functionName)
                    .header("accept", "application/json")
                    .field("access_token", session.getTokenKey())
                    .field("args", arguments)
                    .asString();
            FunctionResult result = objectMapper.readValue(res.getBody(), new TypeReference<FunctionResult>() {});
            return result.return_value;
        } catch (UnirestException | JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
