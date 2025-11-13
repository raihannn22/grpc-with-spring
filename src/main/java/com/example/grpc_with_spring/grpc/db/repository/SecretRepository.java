package com.example.grpc_with_spring.grpc.db.repository;


import com.example.grpc_with_spring.grpc.db.entity.SecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretRepository extends JpaRepository<SecretEntity, Integer> {

    @Query("SELECT s FROM SecretEntity s WHERE s.clientKey = :clientKey")
    SecretEntity findByClientKey(@Param("clientKey") String clientKey);

}
