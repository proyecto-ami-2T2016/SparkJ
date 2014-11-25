package com.infinity.sparkj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.infinity.sparkj.Messages.ExpectedRequest;
import com.infinity.sparkj.Messages.TestResponse;
import com.infinity.sparkj.SparkCloudJsonObjects.AccessToken;
import com.infinity.sparkj.SparkCloudJsonObjects.VariableReadResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeviceVariableReadTests extends DeviceTestsBase {

    @Test
    public void readsVariableSuccessfully() throws JsonProcessingException {
        String varName = "var1";
        String returnValue = "10";
        expectVariableRead(deviceId, varName, sparklerToken());
        VariableReadResult result = new VariableReadResult();
        result.cmd = "VarReturn";
        result.name = varName;
        result.result = returnValue;
        respondWithVariableReadResult(result);

        assertThat(device.readVariable( varName), is(returnValue));
    }

    private void expectVariableRead(String deviceId, String variableName, AccessToken token) {
        ExpectedRequest req = new ExpectedRequest();
        req.type = "get";
        req.path = "/v1/devices/" + deviceId + "/" + variableName;
        req.set(token);
        sim.expectRequest(req);
    }

    private void respondWithVariableReadResult(VariableReadResult result) throws JsonProcessingException {
        TestResponse res = new TestResponse();
        res.body = om.writeValueAsString(result);
        sim.sendResponse(res);
    }
}
