package com.example.TaskManagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.security.demo-user")
public class DemoUserProperties {

	private String username = "demo";
	private String password = "demo123";
}
