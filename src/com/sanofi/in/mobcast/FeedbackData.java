package com.sanofi.in.mobcast;

//Akshay sharma
import android.util.Log;

public class FeedbackData {

	public static int pointer = 0;
	public static FeedbackForm[] fflist = new FeedbackForm[10];

	int getpointer() {
		return pointer;
	}

	public void addFeedback(FeedbackForm fm2) {
		fflist[pointer] = new FeedbackForm(fm2);
		pointer++;
		Log.v("feedbackData", "successflly added");
		Log.v("pointer", "" + pointer);
	}

	void removeFeedback() {
		pointer--;
	}

	public FeedbackForm getff(int x) {
		if ((x >= 0) && (x < pointer))
			return fflist[x];
		else
			return null;
	}

}
