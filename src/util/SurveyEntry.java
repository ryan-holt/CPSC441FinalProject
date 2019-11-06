package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Each individual survey entry inside the survey answer list.
 * This holds the selections per question, as well as the user, and the question number
 */
public class SurveyEntry implements Serializable {

	/**
	 * The name of the user
	 */
	private String user;

	/**
	 * The question number
	 */
	private int question;

	/**
	 * The list of selections per question
	 */
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

	/**
	 * Returns the user name
	 * @return user name
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Returns the selection list
	 * @return arraylist of selections
	 */
	public ArrayList<String> getSelections() {
		return selections;
	}

	/**
	 * Returns the question number
	 * @return question number
	 */
	public int getQuestion() {
		return question;
	}
}


