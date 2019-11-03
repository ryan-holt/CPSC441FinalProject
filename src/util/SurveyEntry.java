package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class SurveyEntry implements Serializable {
	private String user;
	private int question;
	private ArrayList<String> selections;

	public SurveyEntry(String user, int question, ArrayList<String> selections) {
		this.user = user;
		this.question = question;
		this.selections = selections;
	}

	public SurveyEntry(String user, int question, String... selections) {
		this.user = user;
		this.question = question;
		this.selections = new ArrayList<String>(Arrays.asList(selections));
	}

	public String getUser() {
		return user;
	}

	public ArrayList<String> getSelections() {
		return selections;
	}

	public int getQuestion() {
		return question;
	}
}


