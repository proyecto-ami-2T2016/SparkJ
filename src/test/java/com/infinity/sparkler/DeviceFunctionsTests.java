package com.infinity.sparkler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.infinity.sparkler.Messages.ExpectedField;
import com.infinity.sparkler.Messages.ExpectedRequest;
import com.infinity.sparkler.Messages.TestResponse;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.FunctionResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeviceFunctionsTests extends DeviceTestsBase {

    @Test
    public void callsFunctionSuccessfully() throws JsonProcessingException {
        String funcName = "function1";
        String args = "";
        expectFunctionCall(deviceId, funcName, args, sparklerToken());
        FunctionResult result = new FunctionResult();
        result.id = deviceId;
        result.name = "program1";
        result.connected = true;
        result.return_value = 5;
        respondWithFunctionResult(result);

        assertThat(device.callFunction(funcName, args), is(true));
    }

    private void expectFunctionCall(String deviceId, String functionName, String args, AccessToken token) {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "post";
        req.path = "/v1/devices/" + deviceId + "/" + functionName;
        req.set(token);
        req.add(new ExpectedField("", "args=" + args));
        sim.expectRequest(req);
    }

    private void respondWithFunctionResult(FunctionResult result) throws JsonProcessingException {
        TestResponse res = new TestResponse();
        res.body = om.writeValueAsString(result);
        sim.sendResponse(res);
    }

}
