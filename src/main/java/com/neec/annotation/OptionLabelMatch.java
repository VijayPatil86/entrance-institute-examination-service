package com.neec.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.neec.annotation.impl.OptionLabelMatcher;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Constraint(validatedBy = {OptionLabelMatcher.class})
public @interface OptionLabelMatch {
	String message() default "Correct option label must match one of the provided option labels";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
