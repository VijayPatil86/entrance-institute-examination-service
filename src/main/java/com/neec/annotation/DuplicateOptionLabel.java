package com.neec.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.neec.annotation.impl.OptionLabelValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = {OptionLabelValidator.class})
public @interface DuplicateOptionLabel {
	String message() default "Invalid options: options labels are duplicated";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
