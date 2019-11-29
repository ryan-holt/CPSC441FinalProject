package util;

import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class KeywordGroup implements Serializable, Comparable<KeywordGroup> {
	private ArrayList<String> keywords;

	public KeywordGroup(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public KeywordGroup(String... keywords) {
		this.keywords = new ArrayList<String>(Arrays.asList(keywords));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof KeywordGroup)) {
			return false;
		}

		KeywordGroup group2 = (KeywordGroup) obj;
		return keywords.equals(group2.keywords);
	}

	@Override
	public int hashCode() {
		return keywords.hashCode();
	}

	/**
	 * Compares this to another object
	 * @param obj
	 * @return true if the other object is either this object or is a KeywordGroup and has the same keywords as this does
	 */
	public boolean similar(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof  KeywordGroup)) {
			return false;
		}

		ArrayList<String> keywords1 = new ArrayList<>(keywords);
		ArrayList<String> keywords2 = new ArrayList<>(((KeywordGroup) obj).keywords);
		Collections.sort(keywords1);
		Collections.sort(keywords2);

		return keywords1.equals(keywords2);
	}

	// TODO delete unused
//	public int similarHashCode() {
//		ArrayList<String> keywordsCopy = new ArrayList<>(keywords);
//		Collections.sort(keywordsCopy);
//		return keywordsCopy.hashCode();
//	}

	public int compareTo(KeywordGroup other) {
		ArrayList<String> keywords1 = new ArrayList<>(keywords);
		ArrayList<String> keywords2 = new ArrayList<>(other.keywords);
		Collections.sort(keywords1);
		Collections.sort(keywords2);

		if (keywords1.isEmpty() && keywords2.isEmpty()) {
			return 0;
		} else if (keywords1.isEmpty()) {
			return -1;
		} else if (keywords2.isEmpty()) {
			return 1;
		}

		return keywords1.get(0).compareTo(keywords2.get(0));
	}

	public void sort() {
		Collections.sort(keywords);
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}
}
