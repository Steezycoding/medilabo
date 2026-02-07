package com.medilabo.riskevaluatormicroservice.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeUtils {

	public static int calculateAgeFromBirthdate(String birthDate, String dateFormatPattern) {
		LocalDate now = LocalDate.now();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatPattern);
		LocalDate dob = LocalDate.parse(birthDate, formatter);

		Period period = Period.between(dob, now);
		return period.getYears();
	}
}
