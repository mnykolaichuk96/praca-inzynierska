package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPassword {
	String message() default " musi zawierać co najmniej osiem znaków, jedną wielką literę" +
			", jedną małą literę i jedną cyfrę";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
