package com.example.TaskManagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.TaskManagement.config.JwtProperties;
import com.example.TaskManagement.dto.LoginRequestDto;
import com.example.TaskManagement.dto.TokenResponseDto;
import com.example.TaskManagement.security.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final JwtProperties jwtProperties;

	@PostMapping("/login")
	public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		String token = jwtService.generateToken(request.getUsername());
		TokenResponseDto body = TokenResponseDto.builder()
				.accessToken(token)
				.tokenType("Bearer")
				.expiresInMs(jwtProperties.getExpirationMs())
				.build();
		return ResponseEntity.ok(body);
	}
}
