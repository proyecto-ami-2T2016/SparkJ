package com.infinity.sparkler.SparkCloudSim;

import com.infinity.sparkler.Messages.ExpectedRequest;
import com.infinity.sparkler.Messages.TestResponse;
import spark.Request;
import spark.Response;

import java.util.LinkedList;
import java.util.Queue;

import static spark.Spark.*;

public class SparkCloudSim {
    private Queue<ExpectedRequest> expectedRequests;
    private Queue<TestResponse> responses;

    public SparkCloudSim() {
        expectedRequests = new LinkedList<ExpectedRequest>();
        responses = new LinkedList<TestResponse>();
    }

    public void startSpark() {
        get("/*", (req, res) -> handleRequest(req, res, "get"));
        post("/*", (req, res) -> handleRequest(req, res, "post"));
        delete("/*", (req, res) -> handleRequest(req, res, "delete"));
    }

    public void stopSpark() {
        stop();
    }

    private String handleRequest(Request req, Response res, String requestType) {
        expectedRequests.remove().AssertMatch(req, requestType);
        return responses.remove().apply(res);
    }

    public void ExpectRequest(ExpectedRequest expectedRequest) {
        expectedRequests.add(expectedRequest);
    }

    public void SendResponse(TestResponse response) {
        responses.add(response);
    }

}
