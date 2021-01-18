package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

	private Pattern pattern;
	private Matcher matcher;
	//username is 8-20 characters, no _ or . at the beginning, no __ or _. or ._ or .. inside, allowed characters
	//, no _ or . at the end (за кожен розділ відповідає 1 пара() в регулярці)
	private static final String USERNAME_PATTERN = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";

	@Override
	public boolean isValid(final String username, final ConstraintValidatorContext context) {
		pattern = Pattern.compile(USERNAME_PATTERN);
		if (username == null) {
			return false;
		}
		matcher = pattern.matcher(username);
		return matcher.matches();
	}

}