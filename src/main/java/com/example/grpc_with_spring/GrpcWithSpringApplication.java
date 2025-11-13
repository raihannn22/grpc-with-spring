package com.example.grpc_with_spring;

import com.example.grpc_with_spring.grpc.PluginDispatcher;
import com.example.grpc_with_spring.grpc.db.service.SecretService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.IOException;

@SpringBootApplication
public class GrpcWithSpringApplication {
	@Autowired
	SecretService secretService;

	static int port = 5555;

	public static void main(String[] args) {
		SpringApplication.run(GrpcWithSpringApplication.class, args);
	}

	@Bean
	public Server grpcServer() throws IOException {
		System.out.println("Initializing gRPC server...");
		return ServerBuilder.forPort(port)
				.addService(new PluginDispatcher( secretService))
				.build()
				.start();
	}

}
