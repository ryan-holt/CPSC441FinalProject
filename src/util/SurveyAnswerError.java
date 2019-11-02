/**
 * Holds the error to the survey questions
 * @author Richard Lee, Tyler Lam, Ryan Holt, Gary Wu
 * @since November 1st, 2019
 * @version 1.0
 */
public class SurveyAnswerError extends Message{
    public String errorMessage;

    public SurveyAnswerError() {
        errorMessage = new String("Unfortunately, an invalid message has been created.");
    }

    public String getError() {
        return errorMessage;
    }
}