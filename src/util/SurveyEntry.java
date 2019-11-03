package util;

import java.util.ArrayList;
import java.util.Arrays;

public class SurveyEntry {
	private String name;
	private ArrayList<String> selections;

	public SurveyEntry(String name, ArrayList<String> selections) {
		this.name = name;
		this.selections = selections;
	}

	public SurveyEntry(String name, String... selections) {
		this.name = name;
		this.selections = new ArrayList<String>(Arrays.asList(selections));
	}

	public ArrayList<String> getSelections() {
		return selections;
	}
}


