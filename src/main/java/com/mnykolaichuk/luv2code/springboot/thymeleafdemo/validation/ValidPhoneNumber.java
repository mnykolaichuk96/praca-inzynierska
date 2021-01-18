package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPhoneNumber {
	String message() default " musie mieć format +48-XXX-XXX-XXX lub +48XXXXXXXXX " +
			"lub +48 XXX XXX XXX lub XXX-XXX-XXX lub XXXXXXXXX XXX XXX XXX";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
