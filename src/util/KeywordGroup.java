package util;

import javax.crypto.KeyGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class KeywordGroup implements Serializable {
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

	public void sort() {
		Collections.sort(keywords);
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}
}
