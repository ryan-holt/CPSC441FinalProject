package util;

import java.util.ArrayList;

public class AssociationRuleResponse {
    private int question;
    private ArrayList<Rule> results;

    public AssociationRuleResponse(int question, ArrayList<Rule> results) {
        this.question = question;
        this.results = results;
    }

    public ArrayList<Rule> getResult() {
        return results;
    }

    public int getQuestion() {
        return question;
    }
}
