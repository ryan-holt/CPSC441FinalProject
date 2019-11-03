package util;
import java.util.ArrayList;

/**
 * Holds the answer to the survey questions
 * @author Richard Lee, Tyler Lam, Ryan Holt, Gary Wu
 * @since November 1st, 2019
 * @version 1.0
 */
public class SurveyAnswer extends Message {
    ArrayList<SurveyEntry> answerList;

    public SurveyAnswer(ArrayList<SurveyEntry> answerList) {
        super("sendSurveyAnswers");
        this.answerList = answerList;
    }

    public ArrayList<SurveyEntry> getAnswer() {
        return answerList;
    }
}