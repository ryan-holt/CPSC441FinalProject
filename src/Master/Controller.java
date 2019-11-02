package Master;

/**
 * The controller of the Master part of the application
 * @author Gary Wu, Richard Lee, Ryan Holt, Tyler Lam
 * @since November 1, 2019
 * @version 1.0
 */
public class Controller {
    /**
     * SocketHandler class that handles all socket communication with slave and client
     */
    SocketHandler sockethandle;
    /**
     * Controls all the survey questions and responses
     */
    SurveyController surveyController;
    /**
     * Each individual message  being sent between different packages
     */
    Message message;

    /**
     * Rule controller
     */
    RuleController ruleController;

    /**
     * Constructs the survey
     * @param newSocketHandler
     * @param newSurveyControl
     * @param newMessage
     * @param newFileHandle
     */
    public Controller(SocketHandler newSocketHandler, SurveyController newSurveyControl, Message newMessage, RuleController ruleControl) {
        socketHandle = newSocketHandler;
        surveyController = newSurveyControl;
        message = newMessage;
        ruleController = ruleControl;
    }

    /**
     * Switch statement to determine what type of Message we are receiving
     */
    public void communicate(Message incomingMessage) {

    }
}