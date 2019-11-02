/**
 * Holds the answer to the survey questions
 * @author Richard Lee, Tyler Lam, Ryan Holt, Gary Wu
 * @since November 1st, 2019
 * @version 1.0
 */
public class SurveyAnswer extends Message {
    ArrayList<String> answerList;

    public SurveyAnswer() {
        super("sendSurveyAnswer");
        answerList = new ArrayList<String>();
    }

    public void addAnswer(String newAnswer) {
        answerList.add(newAnswer);
    }

    public ArrayList<String> getAnswer() {
        return answerList;
    }
}