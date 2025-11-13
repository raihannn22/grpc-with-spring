package com.example.grpc_with_spring.grpc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import coprocess.CoprocessObject;

public class ResponseMaker {
    public static void succesResponse(CoprocessObject.Object.Builder builder , ResponseDto response, String token, int expireIn) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        response.setResponseCode("200");
        response.setResponseMessage("Successful");

        String newResponse = mapper.writeValueAsString(response);
        ByteString byteString = ByteString.copyFromUtf8(newResponse);
        builder.getRequestBuilder().setRawBody(byteString);
    }
    public static void missingMandatoryResponse(CoprocessObject.Object.Builder builder , ResponseDto response,String fieldName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        response.setResponseCode("400");
        response.setResponseMessage("Invalid Mandatory Field " + fieldName);
        String NewResponse = mapper.writeValueAsString(response);
        ByteString byteString = ByteString.copyFromUtf8(NewResponse);
        builder.getRequestBuilder().setRawBody(byteString);
    }
    public static void invalidFieldFormatResponse(CoprocessObject.Object.Builder builder , ResponseDto response,String fieldName) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        response.setResponseCode("400");
        response.setResponseMessage("Invalid Field Format" + fieldName);
        String NewResponse = mapper.writeValueAsString(response);
        ByteString byteString = ByteString.copyFromUtf8(NewResponse);
        builder.getRequestBuilder().setRawBody(byteString);
    }
    public static void unauthorizedResponse(CoprocessObject.Object.Builder builder , ResponseDto response,String reason) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        response.setResponseCode("401");
        response.setResponseMessage("Unauthorized[" + reason + "]");
        String newResponse = mapper.writeValueAsString(response);
        ByteString byteString = ByteString.copyFromUtf8(newResponse);
        builder.getRequestBuilder().setRawBody(byteString);
    }
    public static void internalErrorResponse(CoprocessObject.Object.Builder builder , ResponseDto response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        response.setResponseCode("500");
        response.setResponseMessage("Internal Server Error");
        String newResponse = mapper.writeValueAsString(response);
        ByteString byteString = ByteString.copyFromUtf8(newResponse);
        builder.getRequestBuilder().setRawBody(byteString);
    }
    public static void invalidTokenResponse(CoprocessObject.Object.Builder builder , ResponseDto response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        response.setResponseCode("401");
        response.setResponseMessage("Invalid Token");
        String newResponse = mapper.writeValueAsString(response);
        ByteString byteString = ByteString.copyFromUtf8(newResponse);
        builder.getRequestBuilder().setRawBody(byteString);
    }
    public static void tokenNotFoundResponse(CoprocessObject.Object.Builder builder , ResponseDto response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        response.setResponseCode("401");
        response.setResponseMessage("Token Not Found");
        String newResponse = mapper.writeValueAsString(response);
        ByteString byteString = ByteString.copyFromUtf8(newResponse);
        builder.getRequestBuilder().setRawBody(byteString);
    }
}
