package com.sanofi.in.mobcast;

import java.io.Serializable;

public class FeedbackQuestion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String questionType;// type of Question
	String questionTitle;
	String questionNo;
	String optionA, optionB, optionC, optionD;
	String answerA = "null", answerB = "null", answerC = "null",
			answerD = "null", subjective = "null", rating = "null";
	String limit;
	String answer;

	public void setQuestionNo(String questionNo) {
		this.questionNo = questionNo;
	}

	public String getQuestionNo() {
		return questionNo;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getQuestionType() {
		return questionType;

	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public String getQuestionTitle() {
		return questionTitle;

	}

	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}

	public String getOptionA() {
		return optionA;

	}

	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}

	public String getOptionB() {
		return optionB;

	}

	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}

	public String getOptionC() {
		return optionC;

	}

	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}

	public String getOptionD() {
		return optionD;

	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getLimit() {
		return limit;

	}

	public void setAnswerA(String answerA) {
		this.answerA = answerA;
	}

	public String getAnswerA() {
		return answerA;

	}

	public void setAnswerB(String answerB) {
		this.answerB = answerB;
	}

	public String getAnswerB() {
		return answerB;

	}

	public void setAnswerC(String answerC) {
		this.answerC = answerC;
	}

	public String getAnswerC() {
		return answerC;

	}

	public void setAnswerD(String answerD) {
		this.answerD = answerD;
	}

	public String getAnswerD() {
		return answerD;

	}

	public void setSubjective(String subjective) {
		this.subjective = subjective;
	}

	public String getSubjective() {
		return subjective;

	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getRating() {
		return rating;

	}

}// end of Feedback class
