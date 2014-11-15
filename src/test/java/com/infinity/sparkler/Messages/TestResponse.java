package com.infinity.sparkler.Messages;

import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class TestResponse {
    public String body;
    private List<ResponseChanger> responseChangers;

    public TestResponse() {
        responseChangers = new ArrayList<>();
        body = "";
    }

    public String apply(Response res) {
        for (ResponseChanger responseChanger : responseChangers) {
            responseChanger.op(res);
        }
        return body;
    }

    public void changeResponse(ResponseChanger func) {
        responseChangers.add(func);
    }

    public interface ResponseChanger {
        public void op(Response res);
    }
}
