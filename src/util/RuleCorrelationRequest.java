package util;

import java.util.ArrayList;

public class RuleCorrelationRequest {
    private String ruleType;
    private ArrayList<Rule> result;

    public RuleCorrelationRequest(String ruleType, ArrayList<Rule> result) {
        this.ruleType = ruleType;
        this.result = result;
    }

    public ArrayList<Rule> getResult() {
        return result;
    }

    public String getRuleType() {
        return ruleType;
    }
}
