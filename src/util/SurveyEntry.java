package util;

import java.util.ArrayList;
import java.util.Arrays;

public class SurveyEntry {
	private String user;
	private ArrayList<String> selections;

	public SurveyEntry(String user, ArrayList<String> selections) {
		this.user = user;
		this.selections = selections;
	}

	public SurveyEntry(String user, String... selections) {
		this.user = user;
		this.selections = new ArrayList<String>(Arrays.asList(selections));
	}

	public String getUser() {
		return user;
	}

	public ArrayList<String> getSelections() {
		return selections;
	}
}


