package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<ValidName, String> {

	private Pattern pattern;
	private Matcher matcher;
	//перша літера велика решто маленькі
	private static final String NAME_PATTERN = "^[A-Z][a-z]+$";

	@Override
	public boolean isValid(final String name, final ConstraintValidatorContext context) {
		pattern = Pattern.compile(NAME_PATTERN);
		if (name == null) {
			return false;
		}
		matcher = pattern.matcher(name);
		return matcher.matches();
	}

}