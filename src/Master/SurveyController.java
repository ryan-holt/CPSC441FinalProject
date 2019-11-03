package Master;

import util.Message;
import util.SurveyAnswer;
import util.SurveyQuestions;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Survey controller manages all the surveys
 * @author Tyler Lam, Gary Wu, Ryan Holt, Richard Lee
 * @since November 1st, 2019
 *
 */
public class SurveyController {
    /**
     * FileHandler handles the reading and writing into each file, which acts as a database
     */
    FileHandler fileHandler;


    public SurveyController(FileHandler newHandle) {
        fileHandler = newHandle;
    }
    /**
     * Reviews the survey answers, write to file, and sends it to slaves
     * @param incomingMessage
     */
    public void setSurveyAnswers(SurveyAnswer incomingMessage) throws IOException {
        //Decompact the survey
        fileHandler.writeArrayToFile(incomingMessage.getAnswer());
        //TODO: Break down the survey before sending it back out
        //sockethandle.send(incomingMessage);
    }

    /**
     * Builds the survey and sends it
     *
     */
    public SurveyQuestions sendSurvey() {
        // could also just read a text file into the arraylist
        SurveyQuestions surveyQ = new SurveyQuestions();
        return surveyQ;
    }
}