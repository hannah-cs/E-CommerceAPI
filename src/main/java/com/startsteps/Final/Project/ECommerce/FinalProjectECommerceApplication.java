package com.startsteps.Final.Project.ECommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class FinalProjectECommerceApplication {
	static final Logger LOG = LoggerFactory.getLogger(FinalProjectECommerceApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(FinalProjectECommerceApplication.class, args);
	}

}