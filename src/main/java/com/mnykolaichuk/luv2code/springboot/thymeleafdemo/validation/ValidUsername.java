package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = UsernameValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidUsername {
	String message() default " musi zawierać od 8 do 20 znaków i zawierać tylko łacińskie litery, cyfry, kropki " +
			"i podkreślenia";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
