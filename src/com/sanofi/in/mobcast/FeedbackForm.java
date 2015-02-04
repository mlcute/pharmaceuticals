package com.sanofi.in.mobcast;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class FeedbackForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int totalQuestion;
	String formID;
	String title;
	String date;
	String attempt;

	ArrayList<FeedbackQuestion> al;

	public FeedbackForm() {
		super();
	}

	public FeedbackForm(FeedbackForm fm2) {
		super();
		this.totalQuestion = fm2.totalQuestion;
		this.formID = fm2.formID;
		this.title = fm2.title;
		this.date = fm2.date;
		this.attempt = fm2.attempt;
	}

	public void setformID(String formID) {
		this.formID = formID;
	}

	public String getformID() {
		return formID;

	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;

	}

	public void setAttempt(String attempt) {
		this.attempt = attempt;
	}

	public String getAttempt() {
		return attempt;

	}

	public void setTotalQuestion(int totalQuestion) {
		this.totalQuestion = totalQuestion;
	}

	public int getTotalQuestion() {
		// Log.v("feedback form", "total questions :"+totalQuestion);
		return totalQuestion;

	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDate() {
		return date;

	}

	public void setArrayList(ArrayList al) {
		this.al = al;
	}

	public ArrayList<FeedbackQuestion> getArrayList() {
		return al;

	}

}
