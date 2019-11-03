package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class SurveyEntry implements Serializable {
	private String name;
	private int question;
	private ArrayList<String> selections;

	public SurveyEntry(String name, int question, ArrayList<String> selections) {
		this.name = name;
		this.selections = selections;
		this.question = question;
	}

	public SurveyEntry(String name, int question, String... selections) {
		this.name = name;
		this.question = question;
		this.selections = (ArrayList<String>) Arrays.asList(selections);
	}

	public ArrayList<String> getSelections() {
		return selections;
	}

	public String getName() {
		return name;
	}

	public int getQuestion() {
		return question;
	}
}


