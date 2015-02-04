package com.sanofi.in.mobcast;

public class FeedbackDBHandler {
	int id;
	String feedbackNo, feedbackTitle, feedbackDescription, feedbackAttempt,
			feedbackTotalQuestions, feedbackDate, questionNo, questionType,
			QuestionTitle, optionA, optionB, optionC, optionD, answerLimit,
			answer;

	public String getFeedbackNo() {
		return feedbackNo;
	}
 
	public void setFeedbackNo(String feedbackNo) {
		this.feedbackNo = feedbackNo;
	}

	public String getFeedbacktitle() {
		return feedbackTitle;
	}

	public void setFeedbacktitle(String feedbackTitle) {
		this.feedbackTitle = feedbackTitle;
	}

	public String getFeedbackDescription() {
		return feedbackDescription;
	}

	public void setFeedbackDescription(String feedbackDescription) {
		this.feedbackDescription = feedbackDescription;
	}

	public String getFeedbackAttempt() {
		return feedbackAttempt;
	}

	public void setFeedbackAttempt(String feedbackAttempt) {
		this.feedbackAttempt = feedbackAttempt;
	}

	public String getFeedbackTotalQuestions() {
		return feedbackTotalQuestions;
	}

	public void setFeedbackTotalQuestions(String feedbackTotalQuestions) {
		this.feedbackTotalQuestions = feedbackTotalQuestions;
	}

	public String getFeedbackDate() {
		return feedbackDate;
	}

	public void setFeedbackDate(String feedbackDate) {
		this.feedbackDate = feedbackDate;
	}

	public String getQuestionNo() {
		return questionNo;
	}

	public void setQuestionNo(String questionNo) {
		this.questionNo = questionNo;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getQuestionTitle() {
		return QuestionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		QuestionTitle = questionTitle;
	}

	public String getOptionA() {
		return optionA;
	}

	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}

	public String getOptionB() {
		return optionB;
	}

	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}

	public String getOptionC() {
		return optionC;
	}

	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}

	public String getOptionD() {
		return optionD;
	}

	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}

	public String getAnswerLimit() {
		return answerLimit;
	}

	public void setAnswerLimit(String answerLimit) {
		this.answerLimit = answerLimit;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getId() {
		return id;
	}

}
