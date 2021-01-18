package com.mnykolaichuk.luv2code.springboot.thymeleafdemo.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

	private Pattern pattern;
	private Matcher matcher;
	//TO DO настроїти добру регулярку
	private static final String PHONE_NUMBER_PATTERN = "^(\\+\\d{1,2}[\\s\\-]?)?(\\d{3})([\\s\\-]?\\d{3})[\\s\\-]?\\d{3}$";

	@Override
	public boolean isValid(final String phoneNumber, final ConstraintValidatorContext context) {
		pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
		if (phoneNumber == null) {
			return false;
		}
		matcher = pattern.matcher(phoneNumber);
		return matcher.matches();
	}

}