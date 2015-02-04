package com.mobcast.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static String formatDate(String oldDate) {
		String newDate = "";
		SimpleDateFormat curFormater = new SimpleDateFormat("dd-MM-yyyy");
		Date dateObj = new Date();
		try {
			dateObj = curFormater.parse(oldDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat postFormatter = new SimpleDateFormat("MMM dd, yyyy");
		newDate = postFormatter.format(dateObj);
		System.out.println("Olddate -->" + oldDate + " Newdate-->" + newDate);
		return newDate;
	}
}