package com.example.TaskManagement.validation;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueDateValidator implements ConstraintValidator<ValidDueDate, LocalDate> {

	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		// dueDate is optional; validate only when present
		return value == null || !value.isBefore(LocalDate.now());
	}
}
