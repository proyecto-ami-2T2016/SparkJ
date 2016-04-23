package com.github.grantwest.sparkj;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

class SparkRestApi {
    static protected HttpResponse<String> sendRequest(com.mashape.unirest.request.BaseRequest req) {
        try {
            HttpResponse<String> res = req.asString();
            switch(res.getCode()) {
                case 200:
                    return res;
                case 400:
                    throw new InvalidVariableOrFunctionException();
                case 401:
                    throw new UsernameOrPasswordIncorrectException();
                case 403:
                    throw new NotAuthorizedForThisCoreException();
                case 404:
                    throw new CoreNotConnectedToCloudException();
                case 408:
                    throw new SparkCloudConnectionTimeoutException();
                case 500:
                    throw new SparkCloudNotAvailableException();
                default:
                    throw new UnknownNetworkConnectionErrorException();
            }
        } catch (UnirestException e) {
            throw new UnknownNetworkConnectionErrorException(e);
        }
    }

    static protected Object jsonToObject(String json, TypeReference type) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new UnableToParseSparkCloudResponseException(e);
        }
    }

    static public class InvalidVariableOrFunctionException extends RuntimeException {}
    static public class UsernameOrPasswordIncorrectException extends RuntimeException {}
    static public class NotAuthorizedForThisCoreException extends RuntimeException {}
    static public class CoreNotConnectedToCloudException extends RuntimeException {}
    static public class SparkCloudConnectionTimeoutException extends RuntimeException {}
    static public class SparkCloudNotAvailableException extends RuntimeException {}
    static public class UnknownNetworkConnectionErrorException extends RuntimeException {
        public UnknownNetworkConnectionErrorException() {}
        public UnknownNetworkConnectionErrorException(Exception e) {
            super(e);
        }
    }
    static public class UnableToParseSparkCloudResponseException extends RuntimeException {
        public UnableToParseSparkCloudResponseException(Exception e) {
            super(e);
        }
    }
}
