package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

	private Pattern pattern;
	private Matcher matcher;
	//Minimum eight characters, at least one uppercase letter, one lowercase letter and one number
	private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$";

	@Override
	public boolean isValid(final String password, final ConstraintValidatorContext context) {
		pattern = Pattern.compile(PASSWORD_PATTERN);
		if (password == null) {
			return false;
		}
		matcher = pattern.matcher(password);
		return matcher.matches();
	}

}