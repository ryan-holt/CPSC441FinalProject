package util;

import java.util.ArrayList;

public class RuleCorrelationRequest {
    private Rule baseRule;
    private ArrayList<Rule> rules;

    public RuleCorrelationRequest(Rule baseRule, ArrayList<Rule> rules) {
        this.baseRule = baseRule;
        this.rules = rules;
    }

    public Rule getBaseRule() {
    	return baseRule;
    }

	public ArrayList<Rule> getRules() {
		return rules;
	}
}
