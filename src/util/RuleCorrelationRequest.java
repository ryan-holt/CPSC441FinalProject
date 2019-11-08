package util;

import java.util.ArrayList;

public class RuleCorrelationRequest extends Message {
    private Rule baseRule;
    private ArrayList<Rule> rules;

    public RuleCorrelationRequest(Rule baseRule, ArrayList<Rule> rules) {
    	super("requestRuleCorrelation");
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
