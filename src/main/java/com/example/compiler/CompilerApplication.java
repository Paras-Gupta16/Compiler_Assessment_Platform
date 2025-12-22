package com.example.compiler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CompilerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompilerApplication.class, args);
	}

}
