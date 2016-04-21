package com.github.grantwest.sparkj.SparkCloudSim;

import com.github.grantwest.sparkj.Messages.ExpectedRequest;
import com.github.grantwest.sparkj.Messages.TestResponse;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.LinkedList;
import java.util.Queue;

import static spark.Spark.*;

public class SparkCloudSim implements AutoCloseable{
    private Queue<ExpectedRequest> expectedRequests;
    private Queue<TestResponse> responses;

    public SparkCloudSim() {
        expectedRequests = new LinkedList<>();
        responses = new LinkedList<>();
    }

    public void startSpark() {
        get("/*", (req, res) -> handleRequest(req, res, "get"));
        post("/*", (req, res) -> handleRequest(req, res, "post"));
        delete("/*", (req, res) -> handleRequest(req, res, "delete"));
        Spark.awaitInitialization();
    }

    private String handleRequest(Request req, Response res, String requestType) {
        expectedRequests.remove().AssertMatch(req, requestType);
        return responses.remove().apply(res);
    }

    public void expectRequest(ExpectedRequest expectedRequest) {
        expectedRequests.add(expectedRequest);
    }

    public void sendResponse(TestResponse response) {
        responses.add(response);
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
