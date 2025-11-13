package com.example.grpc_with_spring.grpc.db.service;

import com.example.grpc_with_spring.grpc.db.entity.SecretEntity;
import com.example.grpc_with_spring.grpc.db.repository.SecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretService {
    @Autowired
    private SecretRepository secretRepository;

    public SecretEntity getByClientKey (String clientKey){
        SecretEntity secretEntity = secretRepository.findByClientKey(clientKey);
        return  secretEntity;
    }
}
