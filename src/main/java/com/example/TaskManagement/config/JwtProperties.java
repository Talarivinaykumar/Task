package com.example.TaskManagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

	/**
	 * HMAC secret (UTF-8 bytes). Must meet the minimum length required by the signing algorithm.
	 */
	private String secret;

	/**
	 * Access token time-to-live in milliseconds.
	 */
	private long expirationMs = 86_400_000L;
}
