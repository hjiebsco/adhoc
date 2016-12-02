package com.hji.adhoc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdhocApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AdhocApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
