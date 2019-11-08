package util;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * A class that holds the 3 pieces of data:
 * keywordGroup
 * score
 * users - the users who have selected the keywords in the keywordGroup
 */
public class Rule implements Serializable {
	private KeywordGroup keywordGroup;
	private double score;
	private List<String> users;

	public Rule(KeywordGroup keywordGroup, double score, List<String > users) {
		this.keywordGroup = keywordGroup;
		this.score = score;
		this.users = users;
	}

	public KeywordGroup getKeywordGroup() {
		return keywordGroup;
	}

	public double getScore() {
		return score;
	}

	public List<String> getUsers() {
		return users;
	}

	public void sortUsers() {
		Collections.sort(users);
	}
}
