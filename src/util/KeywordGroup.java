package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class KeywordGroup implements Serializable {
	private ArrayList<String> keywords;

	public KeywordGroup(ArrayList<String> keywords) {
		this.keywords = keywords;
		sort();
	}

	public KeywordGroup(String... keywords) {
		this.keywords = (ArrayList<String>) Arrays.asList(keywords);
		sort();
	}

	private void sort() {
		Collections.sort(keywords);
	}
}
