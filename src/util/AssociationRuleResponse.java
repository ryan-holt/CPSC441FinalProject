package util;

import java.util.List;

public class AssociationRuleResponse extends HostAddressMessage {
	private int question;
	private List<Rule> rules;
	public AssociationRuleResponse(int question, List<Rule> rules) {
		super("associationRulesResponse");
		this.question = question;
		this.rules = rules;
	}

	public int getQuestion() {
		return question;
	}

	public List<Rule> getRules() {
		return rules;
	}
}
