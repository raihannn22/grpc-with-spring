package com.example.grpc_with_spring.grpc.db.service;

import com.example.grpc_with_spring.grpc.db.entity.SecretEntity;
import com.example.grpc_with_spring.grpc.db.repository.SecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SecretService {
    @Autowired
    private SecretRepository secretRepository;

    @Autowired
    private  WebClient webClient;


    public SecretEntity getByClientKey (String clientKey){
        SecretEntity secretEntity = secretRepository.findByClientKey(clientKey);
        return  secretEntity;
    }

    public String callGenerateToken(String clientKey, String signature, String timestamp){
        return webClient.post()
                .uri("http://100.98.173.14:9080/generate-token")
                .header("Client-Key", clientKey)
                .header("Signature", signature)
                .header("Timestamp", timestamp)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
