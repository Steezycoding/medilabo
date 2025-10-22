package com.medilabo.patient_microservice.utils;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

public class DateUtils {
	public static Date createDate(int year, Month month, int day) {
		LocalDate localDate = LocalDate.of(year, month, day);
		return Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
	}
}
