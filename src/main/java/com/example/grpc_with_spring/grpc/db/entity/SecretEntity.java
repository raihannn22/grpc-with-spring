package com.example.grpc_with_spring.grpc.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "secret_key")
@Data
public class SecretEntity {
    @Id
    private Integer id;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column (name = "client_key")
    private String clientKey;

    @Column (name = "public_key",length = 10000)
    private String publicKey;
}
