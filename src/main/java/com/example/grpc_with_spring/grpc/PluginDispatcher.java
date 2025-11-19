package com.example.grpc_with_spring.grpc;

import com.example.grpc_with_spring.grpc.db.entity.SecretEntity;
import com.example.grpc_with_spring.grpc.db.service.SecretService;
import com.example.grpc_with_spring.grpc.util.JWTUtil;
import com.example.grpc_with_spring.grpc.util.ResponseDto;
import com.example.grpc_with_spring.grpc.util.ResponseMaker;
import com.example.grpc_with_spring.grpc.util.SignatureUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import coprocess.CoprocessObject;
import coprocess.DispatcherGrpc;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Date;

public class PluginDispatcher extends DispatcherGrpc.DispatcherImplBase {
    private final SecretService secretService;

    private String privateKey = "MIIJRAIBADANBgkqhkiG9w0BAQEFAASCCS4wggkqAgEAAoICAQC3NBEjpUWai0GPundt4XXVOhwAaBmdL14hSVPiPon/Zb77abe4raichoRwX3ivke1IgOqpQ+nLsAMy0dLJf9DVFkWdt8xmp9b5iyQLGe2JSUYA1nWtmHs71/6Xis7nx8MPNBlG70ggDyJK2ucuaY37KxgkgBkP3E6Dl+ZbvYAmRTVLw0mWuFYHnhW93l6pCqHyuqLQyX7wsZkgpdNVG2SlWhF3IuUC11+egCcnTTiPkCFtPemhuDSwMbAReuun8uShSIYVADx7BSV/i4MsqvoBE00Te9w0nn16zvThuPToiTJBrmmPOClHzEtUS6WSYdfPMUAtO87zXM3yyEXDDtcgII0ZNElfA+YcEDRKdaoXDE4z33+UHMAvt5eRA+fC975V5MN72wFNpniV8MTINxFaaZ9b/pmuUSsZa41/1ioLwgelPkbOWX27jK8Co4JS0Qz919HN6eI7WSibjrf1JRj0UbB+Bwm8375VnQ6AYI6AiWgEi2PJIsIu+bX50znINvFYVKhjcacvYhJR9xLbGWTXvYEcFYvZBDKASTvnDdLcgXt/kzs09O5wiwZrtEynVdPzgw9SCTyljed2641axl1I5rnPSi6eIMmOrbff06K1XFkkzVmoO/6UBeMaruuI+zXDOyXuQCioqOtIRrZLM3EL9zFG3dtXpfSiB3M2lpAqcQIDAQABAoICAQCFswXfEbpeMsrB7b9C/YtU9XeCBHbM88iqWi1Rq3piXQqnyFs/99xZfwIgUExvNPwKMasPpHR3JhjveZlvXEw3dm2P0JbnRA3rR9QiFeX4jvT7n4d6DRHCdrtiNPcSyAJekhLwZagpXWIVZvPYN0hayPRxjtq7bHgQKUcAJKflRhTDGd4WlKiUHpaXXjWPACd4USSJYsO1PKcX4Gz+a2NkGPsuCtU+4ZbywWvRjtV8UB9U7h4W26v3+gsYdrCMfQLFgXv4z5dT4Emhb1tnuc+CAWf6lCX8cL36ZhbotGZnXdjDflmjH8VglINVHW/UCHvKxZKkcEVz7YKPcAEZfTcZCqPq1pujgQkW7b42zCwPMswnNudmz2GcjxlWx0G6IWfq4b9KGbOeGu90P3ekkuJ4E3DoA4lRDHkxaMAuGdLk6SczmO0VpGVsI6zr5ZLg5BoL8lQD3jjrheBt1W5X4uUqm7BRUMJ0dwul+AxPROMjOozFlInyiYH6KAoQzlFovOoz3+8LKkJaUdXL6Fhq5Je0T73lPRWF6WAbsI6DlF+Z505wtvWhMWjbt7zkXXow2m5W9lnoR6bnZcXZz0M7TWcOIctfRNrqFV+a6o7JHtS9nCspEI0e/arcIAry8RvB+rukhOF6bqWB/W8pbpoqnxDN3fAOiQGYnzkClDjqUTlCwQKCAQEA5ArTOHYIAiwLMYGm8AyodUvrKTLa3MBTJ+f8KFNvt+yF+h54HO6OQIkPdVa62SENzP5xzf0nvqdJV2YPufb4u/CnPrsdycKESPJbN4B0/glRJ71+YPBvvXqRcGSiASVLdZ1RDw/f7LyHXYQ5kc2FCF13fpJ3L8Zj2tPzJuA1ZQDijr3GeN1vpsf5VUyFIXCLegIv1pMh2GYSqkB9a4k8D2P1MQ0YUG0Ig9p8pFG1OtOl4LSayAAj/VQnjpQm23sO/j6qdAPaYjS/aN5MMS2KPTyxwdgvQfaew+7NHb1ZTnaEhfMcXTE5dHRQZ7cH0Be/JFIqbXdzLj5hZX4Z4ZcmvQKCAQEAzan1rrb10qoSoby9zPsVOv3yzHHyUHYnBYm9Z7oVySH4SXrAgnVdBthiqz03NcdN+Mso5cazSdDWCq6rgye2XF8pVNxvRjbVq/SUT49UuEHpz765ExP02a8BKLrV15kQzQeF8Wz/8le9fFqZnYTtNutriUxY45DJrOUE2fEDCgSUzmaPj+ncZg5ueunsY+POrN1AEmGLZLeRsYLqh0XCwBu8oBtbGdJUkCZd3kCfexKJg7DFKbuEAHql2pSlwFnITUWBiniIGDHZ0AGSiLZ896B6hMK2IVn4aSk77MeSnmTFI4OKGRdq6D0f1Y60O98sErS88cq3Wi3A9TlPm8v3xQKCAQEAmnoGyWmTVT1Wu8uTNWuttf+epoLvpbiW3CKXOw4CUPpla6C2RwsGzArMsPT2j92HRsVafGqwQMN7uDvIDXaS2mVUNfd1X1ZoXC3owrk/B1NyaVRBRxO47gcYfKV+5P3JlzvhTC25x9mSbTQD95PcOWVvbAt1t6RRwkvvm/cn1Gr8DivaRcfjOXyflzvFt3hQ026lz1pOBBRpMnGLRIxrZhWVZAv1241wxjj8VCeRd+8loAlYDIGj35jhTwFnLSi4aFw1BXASwJHw0+46eNnbmk4CMEjtAmc494tN4YaB99frP/4Gyc5tmEiuqwNexIVBky4h1ZOkL8BMI/9/MsU8lQKCAQBERsFOYGUZq+aFlUA5lb2FmRsK7mUwexkXSJP9pJRo8oMX+c2BQe9BP4L6MFds3h6vktqf9ag9wDvezgAYiY2L+veguZGjcL7c2FSIWSAaD5/MnReaEsz9m5C58dTeP22Jc47vCG2sCZ7ZcsZ+uct6qQtJSHRWEBIz+M/0foOwxraUEw5kC5JkuzPIUHJu8wkA8G696244YIzYroqnn+w4lLy5Nnu3BdGXDXWqPC/1zsCiXIQIB72Z3x3ChAjG48eQh5uq2brstVgHKw53noW8O6WQ/4cxaUoDt3GyeMrfFLsxwBCPpeuS7//qiPaizcM27pOvfcoDAR+HCaC5Kyy1AoIBAQCvG+QK/Xf1OssOveTquqSFxFwXoCX5zhM/z/wyW8L+bYCu9HFvtDqBClcjZN+wMBru27NSqvsgNt3iOpTavWh/lq+e0wHhgU2c2f7sGDJ5LAoC+aSfR4y+iHU8B1MJMn9ZttbrKKhCm99KK0GOiTnEezFNZi3icyIqn+VDhg7xpXgNd/bcG9RWP97rL/dPz9cveS2+kynxUxIVsCx67r/pwZMI1R9WwzUFbInYibleh+UHiDKVOeeQtKGM1Ks/Nwsq/dCEt96WGhIfJTPRkWS/hwD9sSs9O+FeXc3gz5m8gROJuEWxS68Ao9upnIT+3OpFwExJ5JTbyBdv9b0FTVRn";

    public PluginDispatcher(SecretService secretService) {
        this.secretService = secretService;
    }

    @Override
    public void dispatch(CoprocessObject.Object request, io.grpc.stub.StreamObserver<CoprocessObject.Object> responseObserver) {
        CoprocessObject.Object modifiedReq = null;
        System.out.println("Request Received : " + Timestamp.valueOf(OffsetDateTime.now().toLocalDateTime()));

        switch (request.getHookName()){
            case "MyPreMiddleware":
                modifiedReq = MyPreMiddleware(request);
                break;
            case "MyPostMiddleware":
                modifiedReq = MyPostMiddleware(request);
                break;
            case "VerifSignature":
                modifiedReq = VerifSignature(request);
                break;
            case "InboundTransaction":
                modifiedReq = InboundTransaction(request);
                break;
            case "OutboundTransaction":
                modifiedReq = OutboundTransaction(request);
                break;
            default:
        }

        if (modifiedReq != null){
            responseObserver.onNext(modifiedReq);
        }

        responseObserver.onCompleted();
    }

    CoprocessObject.Object MyPreMiddleware(CoprocessObject.Object request){
        CoprocessObject.Object.Builder builder = request.toBuilder();
        builder.getRequestBuilder().putSetHeaders("client_id", "ini pree");
        return  builder.build();
    }

    CoprocessObject.Object MyPostMiddleware(CoprocessObject.Object request){
        CoprocessObject.Object.Builder builder = request.toBuilder();
        builder.getRequestBuilder().getBody();
        builder.getResponseBuilder().putHeaders("client_id", "ini response");
        return  builder.build();
    }

    CoprocessObject.Object VerifSignature(CoprocessObject.Object request){
//        System.out.println("VerifSignature");
        CoprocessObject.Object.Builder builder = request.toBuilder();

        String clientKey =request.getRequest().getHeadersOrDefault("Client-Key","");
        String signature =request.getRequest().getHeadersOrDefault("Signature","");
        String timestamp =request.getRequest().getHeadersOrDefault("Timestamp",String.valueOf(OffsetDateTime.now()));

        ResponseDto response = new ResponseDto();
        try{
            if (clientKey.equals("")){
                System.out.println("client key is empty");
                ResponseMaker.missingMandatoryResponse(builder,response,"Client-Key");
                return builder.build();
            }
            if (signature.equals("")){
                System.out.println("signature is empty");
                ResponseMaker.missingMandatoryResponse(builder,response,"Signature");
                return builder.build();
            }
            if (timestamp.equals("")){
                System.out.println("timestamp is empty");
                ResponseMaker.missingMandatoryResponse(builder,response,"Timestamp");
                return builder.build();
            }

            SecretEntity secretEntities = secretService.getByClientKey(clientKey);
//            System.out.println("secretEntities " + secretEntities);

            if (secretEntities == null){
                System.out.println("client key not found");
                ResponseMaker.unauthorizedResponse(builder,response,"Client key not found");
                return builder.build();
            }

            String publicKey = secretEntities.getPublicKey();
            SignatureUtil signatureUtil = new SignatureUtil();

            System.out.println(signatureUtil.validateOauthSignature(publicKey,clientKey,timestamp,signature) + " <- ini signatureUtil.validateOauthSignature");
            if(!signatureUtil.validateOauthSignature(publicKey,clientKey,timestamp,signature)){
                ResponseMaker.unauthorizedResponse(builder,response,"Invalid Signature");
                return builder.build();
            }
                System.out.println("OK");

        }catch (Exception e){
            System.out.println("Exception -> " + e.getMessage());
            return builder.build();
        }
        return  builder.build();
    }

    CoprocessObject.Object InboundTransaction(CoprocessObject.Object request){
        CoprocessObject.Object.Builder builder = request.toBuilder();

        String clientKey = request.getRequest().getHeadersOrDefault("Client-Key","");
        String Timestamp = request.getRequest().getHeadersOrDefault("Timestamp",String.valueOf(OffsetDateTime.now()));
        String signature = request.getRequest().getHeadersOrDefault("Signature","");
        String auth = request.getRequest().getHeadersOrDefault("Authorization","");
        String token = "";
        if (auth.length() > 7){
            token = auth.substring(7);
        }

        String httpMethod =request.getRequest().getMethod();
        String relativeUrl = "/test/satu";
        String body = request.getRequest().getBody();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(body);
        body = gson.toJson(jsonElement);

        ResponseDto response = new ResponseDto();
        try{
            if (clientKey.equals("")){
                System.out.println("client key is empty");
                ResponseMaker.missingMandatoryResponse(builder,response,"Client-Key");
                return builder.build();
            }
            if (signature.equals("")){
                System.out.println("signature is empty");
                ResponseMaker.missingMandatoryResponse(builder,response,"Signature");
                return builder.build();
            }
            if (Timestamp.equals("")){
                System.out.println("timestamp is empty");
                ResponseMaker.missingMandatoryResponse(builder,response,"Timestamp");
                return builder.build();
            }
            if (token.equals("")){
                System.out.println("token is empty;");
                ResponseMaker.missingMandatoryResponse(builder, response, "Authorization token is missing");
            }

            SecretEntity secretEntities = secretService.getByClientKey(clientKey);
            if (secretEntities == null){
                System.out.println("client key not found");
                ResponseMaker.unauthorizedResponse(builder,response,"Client key not found");
                return builder.build();
            }
            String secretKey = secretEntities.getClientSecret();
            if(secretKey == null ){
                System.out.println("client key not found");
                ResponseMaker.unauthorizedResponse(builder,response,"Client key not found");
                return builder.build();
            }

            try{
                JWTUtil jwtUtil = new JWTUtil(privateKey);
                if(!jwtUtil.validateTokenJWT(token).equals(clientKey)){
                    System.out.println("token not valid");
                    ResponseMaker.unauthorizedResponse(builder,response,"Token not valid");
                    return builder.build();
                }

                if(jwtUtil.validateTokenJWT(token).equals("expire")){
                    System.out.println("token expired");
                    ResponseMaker.invalidTokenResponse(builder,response);
                    return builder.build();
                }
            }
            catch (Exception e){
                ResponseMaker.internalErrorResponse(builder,response);
                System.out.println("Exception 2 -> " + e.getMessage());
                return builder.build();
            }

            SignatureUtil signatureUtil = new SignatureUtil();
            if (!signatureUtil.validateServiceSignature(secretKey,httpMethod,relativeUrl,token,Timestamp,body,signature)){
                System.out.println("invalid signature");
                ResponseMaker.unauthorizedResponse(builder,response,"Invalid Signature");
                return builder.build();
            }
            System.out.println("Success Validation!");

        }catch (Exception e){
            System.out.println("Exception 1 -> " + e.getMessage());
            try{
                ResponseMaker.internalErrorResponse(builder,response);
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return builder.build();
        }

        return builder.build();
    }

    CoprocessObject.Object OutboundTransaction(CoprocessObject.Object request){
        CoprocessObject.Object.Builder builder = request.toBuilder();
        return builder.build();
    }
}
