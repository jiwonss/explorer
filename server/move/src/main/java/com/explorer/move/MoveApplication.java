package com.explorer.move;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MoveApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoveApplication.class, args);
	}

}
