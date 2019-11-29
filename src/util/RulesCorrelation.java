package util;

import java.io.Serializable;
import java.util.*;

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

    public boolean similar(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof RulesCorrelation)) {
            return false;
        }

        RulesCorrelation other = (RulesCorrelation) obj;

        if (score != other.score) {
             return false;
        }

        ArrayList<KeywordGroup> groupsCopy1 = new ArrayList<>(keywordGroups);
        ArrayList<KeywordGroup> groupsCopy2 = new ArrayList<>(other.keywordGroups);
        Collections.sort(groupsCopy1);
        Collections.sort(groupsCopy2);

        Iterator<KeywordGroup> it1 = groupsCopy1.iterator();
        Iterator<KeywordGroup> it2 = groupsCopy2.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            if (!it1.next().similar(it2.next())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the hash code for a copy of this object where all the elements have been sorted
     * The copy of this object is similar to this object such that this.similar(copyOfThis) == true
     * @return A hash code based on the description above
     */
    public int similarHashCode() {
        ArrayList<KeywordGroup> groupsCopy = new ArrayList<>(keywordGroups);
        Collections.sort(groupsCopy);

        groupsCopy.forEach(group -> group.sort());

        return Objects.hash(groupsCopy, score);
    }

    public double getScore() {
        return score;
    }
}
