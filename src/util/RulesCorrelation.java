package util;

import java.io.Serializable;
import java.util.ArrayList;

public class RulesCorrelation implements Serializable {
    private ArrayList<KeywordGroup> keywordGroups;
    private double score;

    public RulesCorrelation(ArrayList<KeywordGroup> keywordGroups, double score) {
        this.keywordGroups = keywordGroups;
        this.score = score;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int i = 0;
        for (KeywordGroup keywordGroup : keywordGroups) {
            if (i != 0) {
                s.append(", ");
            }
            s.append("(");
            int j = 0;
            for (String keyword : keywordGroup.getKeywords()) {
                if (j != 0) {
                    s.append(", ");
                }
                s.append(keyword);
                j++;
            }
            s.append(")");
            i++;
        }
        s.append(": ").append(score);
        return s.toString();
    }

    public double getScore() {
        return score;
    }
}
