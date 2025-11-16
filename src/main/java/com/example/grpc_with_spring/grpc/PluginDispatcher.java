package com.example.grpc_with_spring.grpc;

import com.example.grpc_with_spring.grpc.db.entity.SecretEntity;
import com.example.grpc_with_spring.grpc.db.service.SecretService;
import com.example.grpc_with_spring.grpc.util.ResponseDto;
import com.example.grpc_with_spring.grpc.util.ResponseMaker;
import com.example.grpc_with_spring.grpc.util.SignatureUtil;
import coprocess.CoprocessObject;
import coprocess.DispatcherGrpc;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Date;

public class PluginDispatcher extends DispatcherGrpc.DispatcherImplBase {
    private final SecretService secretService;

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
//        String privateKey ="<KEY>";
        System.out.println(clientKey + " <- ini client key boss");
        System.out.println(signature + " <- ini signature boss");
        System.out.println(timestamp + " <- ini timestamp boss");

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
}
