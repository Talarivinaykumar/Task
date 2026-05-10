package com.example.TaskManagement.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.TaskManagement.config.JwtProperties;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

	private final JwtProperties jwtProperties;

	public String generateToken(String subject) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + jwtProperties.getExpirationMs());
		return Jwts.builder()
				.subject(subject)
				.issuedAt(now)
				.expiration(exp)
				.signWith(signingKey())
				.compact();
	}

	public String extractSubject(String token) {
		return Jwts.parser()
				.verifyWith(signingKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}

	public boolean isTokenValid(String token) {
		try {
			extractSubject(token);
			return true;
		}
		catch (JwtException | IllegalArgumentException ex) {
			return false;
		}
	}

	private SecretKey signingKey() {
		byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
