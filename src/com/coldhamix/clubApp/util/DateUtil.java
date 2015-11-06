package com.coldhamix.clubApp.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
	
	private static final String DATE_PATTERN = "dd.MM.yyyy";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	
	public static String format(int date) {
		Date d = new Date(date * 1000L);
		LocalDate lDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return DATE_FORMATTER.format(lDate);
	}

}
