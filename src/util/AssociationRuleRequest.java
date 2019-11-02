package util;

import java.util.ArrayList;

public class AssociationRuleRequest extends Message { // TODO extends Message
	private int question;
	private ArrayList<KeywordGroup> keywordGroups;
	private ArrayList<SurveyEntry> surveyEntries;


	public AssociationRuleRequest(int question, ArrayList<KeywordGroup> keywordGroups, ArrayList<SurveyEntry> surveyEntries) {
		super("requestAssociationRules");
		this.question = question;
		this.keywordGroups = keywordGroups;
		this.surveyEntries = surveyEntries;
	}

	public int getQuestion() {
		return question;
	}

	public void setQuestion(int question) {
		this.question = question;
	}

	public ArrayList<KeywordGroup> getKeywordGroups() {
		return keywordGroups;
	}

	public ArrayList<SurveyEntry> getSurveyEntries() {
		return surveyEntries;
	}
}
