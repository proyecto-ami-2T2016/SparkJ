package com.github.grantwest.sparkj;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.grantwest.sparkj.Messages.ExpectedField;
import com.github.grantwest.sparkj.Messages.ExpectedRequest;
import com.github.grantwest.sparkj.Messages.TestResponse;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.AccessToken;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.FunctionResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeviceFunctionsTest extends DeviceTestsBase {

    @Test
    public void callsFunctionSuccessfully() throws JsonProcessingException {
        String funcName = "function1";
        String args = "args1";
        int returnValue = 5;
        expectFunctionCall(deviceId, funcName, args, sparklerToken());
        FunctionResult result = new FunctionResult();
        result.id = deviceId;
        result.name = "program1";
        result.connected = true;
        result.return_value = returnValue;
        respondWithFunctionResult(result);

        assertThat(device.callFunction(funcName, args), is(returnValue));
    }

    @Test(expected = SparkRestApi.InvalidVariableOrFunctionException.class)
    public void status400ThrowsInvalidVariableOrFunction() {
        String args = "args1";
        String function = "function1";

        expectFunctionCall(deviceId, function, args, sparklerToken());
        respondWithStatusCode(400);

        device.callFunction(function, args);
    }

    @Test(expected = SparkRestApi.NotAuthorizedForThisCoreException.class)
    public void status403ThrowsNotAuthorizedForThisCore() {
        String args = "args1";
        String function = "function1";

        expectFunctionCall(deviceId, function, args, sparklerToken());
        respondWithStatusCode(403);

        device.callFunction(function, args);
    }

    @Test(expected = SparkRestApi.CoreNotConnectedToCloudException.class)
    public void status404ThrowsCoreNotConnectedToCloud() {
        String args = "args1";
        String function = "function1";

        expectFunctionCall(deviceId, function, args, sparklerToken());
        respondWithStatusCode(404);

        device.callFunction(function, args);
    }

    @Test(expected = SparkRestApi.SparkCloudConnectionTimeoutException.class)
    public void status408ThrowsSparkCloudConnectionTimeout() {
        String args = "args1";
        String function = "function1";

        expectFunctionCall(deviceId, function, args, sparklerToken());
        respondWithStatusCode(408);

        device.callFunction(function, args);
    }

    @Test(expected = SparkRestApi.SparkCloudNotAvailableException.class)
    public void status500ThrowsSparkCloudNotAvailable() {
        String args = "args1";
        String function = "function1";

        expectFunctionCall(deviceId, function, args, sparklerToken());
        respondWithStatusCode(500);

        device.callFunction(function, args);
    }

    @Test(expected = SparkRestApi.UnknownNetworkConnectionErrorException.class)
    public void unknownStatusThrowsUnknownNetworkConnectionError() {
        String args = "args1";
        String function = "function1";

        expectFunctionCall(deviceId, function, args, sparklerToken());
        respondWithStatusCode(505);

        device.callFunction(function, args);
    }

    private void expectFunctionCall(String deviceId, String functionName, String args, AccessToken token) {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "post";
        req.path = "/v1/devices/" + deviceId + "/" + functionName;
        req.set(token);
        req.add(new ExpectedField("args", args));
        sim.expectRequest(req);
    }

    private void respondWithFunctionResult(FunctionResult result) throws JsonProcessingException {
        TestResponse res = new TestResponse();
        res.body = om.writeValueAsString(result);
        sim.sendResponse(res);
    }

}
