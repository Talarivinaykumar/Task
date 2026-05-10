package com.example.TaskManagement.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validates that a due date is not in the past.
 * Null is allowed to keep the field optional.
 */
@Documented
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = DueDateValidator.class)
public @interface ValidDueDate {

	String message() default "Due date cannot be in the past";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
