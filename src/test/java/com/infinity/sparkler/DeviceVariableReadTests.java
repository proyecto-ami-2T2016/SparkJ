package com.infinity.sparkler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infinity.sparkler.Messages.ExpectedField;
import com.infinity.sparkler.Messages.ExpectedRequest;
import com.infinity.sparkler.Messages.TestResponse;
import com.infinity.sparkler.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkler.SparkCloudJsonObjects.VariableReadResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeviceVariableReadTests extends DeviceControllerTestsBase{

    @Test
    public void readsVariableSuccessfully() throws JsonProcessingException {
        String varName = "var1";
        int returnValue = 10;
        expectVariableRead(deviceId, varName, sparklerToken());
        VariableReadResult result = new VariableReadResult();
        result.cmd = "VarReturn";
        result.name = varName;
        result.result = returnValue;
        respondWithVariableReadResult(result);

        assertThat(controller.readVariable(deviceId, varName), is(returnValue));
    }

    private void expectVariableRead(String deviceId, String variableName, AccessToken token) {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "get";
        req.path = "/v1/devices/" + deviceId + "/" + variableName + "?access_token=" + token.token;
        sim.expectRequest(req);
    }

    private void respondWithVariableReadResult(VariableReadResult result) throws JsonProcessingException {
        TestResponse res = new TestResponse();
        res.body = om.writeValueAsString(result);
        sim.sendResponse(res);
    }
}
