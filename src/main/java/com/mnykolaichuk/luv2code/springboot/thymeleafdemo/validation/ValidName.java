package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = NameValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidName {
	String message() default " musi zaczynać się od dużej litery oraz zawierać tylko łacińskie litery";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
