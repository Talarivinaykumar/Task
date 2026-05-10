package com.example.TaskManagement.exception;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorResponse {

	Instant timestamp;
	int status;
	String error;
	String message;
	String path;
	Map<String, String> fieldErrors;
}
