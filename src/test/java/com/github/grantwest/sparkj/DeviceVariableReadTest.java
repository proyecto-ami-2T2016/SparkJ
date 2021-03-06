package com.github.grantwest.sparkj;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.grantwest.sparkj.Messages.ExpectedRequest;
import com.github.grantwest.sparkj.Messages.TestResponse;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.AccessToken;
import com.github.grantwest.sparkj.SparkCloudJsonObjects.VariableReadResult;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeviceVariableReadTest extends DeviceTestsBase {

    @Test
    public void readsVariableSuccessfully() throws JsonProcessingException {
        String varName = "var1";
        String returnValue = "10";
        expectVariableRead(deviceId, varName, sparkjToken());
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
