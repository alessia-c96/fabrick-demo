package com.example.fabrick_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class FabrickDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(FabrickDemoApplication.class, args);
	}

}
