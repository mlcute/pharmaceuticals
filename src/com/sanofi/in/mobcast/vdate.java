package com.sanofi.in.mobcast;

import java.util.Calendar;

import android.util.Log;

class vdate {

	public String date, day, rdate;
	String today, yesterday;
	String dates[] = new String[3];
	String past[] = new String[6];
	String date1, month, year;
	public int dayno;
	int dateCal, monthCal, yearCal;
	String weekdays[] = { "Sunday", "Monday", "Tuesday", "Wednesday",
			"Thursday", "Friday", "Saturday" };
	String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
			"Sep", "Oct", "Nov", "Dec" };

	public vdate(String d) {

		Log.d("Date in vdate constructor", "is:"+d);
		date = d;
		/*date1 = date.substring(0, date.indexOf('-')-1);
		month = date.substring(date.indexOf('-')+1, date.lastIndexOf('-')-1); // vdate vd = new vdate("13-04-2013");
		year = date.substring(date.lastIndexOf('-')+1);*/

		dates = date.split("-");
		for(int i=0;i<dates.length;i++){
			Log.d("Date"+i, "is: "+dates[i]);
		}
		
		
		date1 = dates[0];
		month = dates[1];
		year = dates[2];
		Log.d("DATE1", "is: "+date1);
		Log.d("MONTH", "is: "+month);
		Log.d("YEAR", "is: "+year);
		
		Calendar c = Calendar.getInstance();

		dateCal = c.get(Calendar.DATE);
		monthCal = c.get(Calendar.MONTH) + 1;
		yearCal = c.get(Calendar.YEAR);

		if ((dateCal < 10) && (monthCal < 10))
			today = "0" + dateCal + "-0" + monthCal + "-" + yearCal;
		else if ((monthCal < 10))
			today = dateCal + "-0" + monthCal + "-" + yearCal;
		else if ((dateCal < 10))
			today = "0" + dateCal + "-" + monthCal + "-" + yearCal;
		else
			today = dateCal + "-" + monthCal + "-" + yearCal;

		dateCal--;
		if (dateCal > 0) {
			if ((dateCal < 10) && (monthCal < 10))
				yesterday = "0" + dateCal + "-0" + monthCal + "-" + yearCal;
			else if ((monthCal < 10))
				yesterday = dateCal + "-0" + monthCal + "-" + yearCal;
			else if ((dateCal < 10))
				yesterday = "0" + dateCal + "-" + monthCal + "-" + yearCal;
			else
				yesterday = dateCal + "-" + monthCal + "-" + yearCal;
		}

		// weekdays
		// logic==========================================================
		for (int i = 0; i < 6; i++) {
			dateCal--;
			if (dateCal > 0) {
				if ((dateCal < 10) && (monthCal < 10))
					past[i] = "0" + dateCal + "-0" + monthCal + "-" + yearCal;
				else if ((monthCal < 10))
					past[i] = dateCal + "-0" + monthCal + "-" + yearCal;
				else if ((dateCal < 10))
					past[i] = "0" + dateCal + "-" + monthCal + "-" + yearCal;
				else
					past[i] = dateCal + "-" + monthCal + "-" + yearCal;

			}
		}
		// weekdays logic ends
		// ====================================================

		dayno = c.get(Calendar.DAY_OF_WEEK) - 3;
		rdate = date;

	}

	String getRDate() {
		try {
			if (today.equalsIgnoreCase(date)) {
				rdate = "Today";
			} else if (yesterday.equalsIgnoreCase(date)) {
				rdate = "Yesterday";
			} else if (Integer.parseInt(year) == yearCal)
				rdate = date1 + "-" + months[Integer.parseInt(month) - 1];

			try {
				// day of the week code
				for (int i = 0; i < Calendar.getInstance().get(
						Calendar.DAY_OF_WEEK) - 2; i++) {
					if (dayno > 0) {
						if (past[i].equalsIgnoreCase(date)) {
							rdate = weekdays[dayno];
						}

						dayno--;
					} else
						dayno = 6;
				}
			} catch (Exception e) {
				
			}

			return rdate;
		} catch (Exception e) {
			
			return date;
		}
	}

}
