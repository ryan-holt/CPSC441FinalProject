package util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Holds the survey questions
 * @author Richard Lee, Tyler Lam, Ryan Holt, Gary Wu
 * @since November 1st, 2019
 * @version 1.0
 */
public class SurveyQuestions extends Message{

    /**
     * THe ArrayList of all the survey questions
     */
    private ArrayList<String> surveyQuestionList;

    /**
     * ArrayList of all the potential survey answers
     */
    private ArrayList<ArrayList<String>> surveyAnswersLists;

    public SurveyQuestions() {
        super("sendSurveyQuestions");
        setSurveyQuestionList();
        setSurveyAnswersLists();
    }

    /**
     * Sets all the questions in the survey list
     */
    private void setSurveyQuestionList(){
        surveyQuestionList = new ArrayList<String>();
        surveyQuestionList.add("What do you use at work? Please pick from Python, Java, Word or Excel");
        surveyQuestionList.add("Which of the following stores have you went to in the past month? Please pick from Walmart, Dollarama or Rolex and put spaces between them");
        surveyQuestionList.add("Which of the following is your favorite office hobby?  Please pick from Foosball, Tetris, Poker or Smash and put spaces between them");
        surveyQuestionList.add("What is your favorite physical activity?  Please pick from Soccer, Boxing, Running or Swimming and put spaces between them");
    }

    /**
     * Adds all the potential keyword responses to the arraylist
     */
    private void setSurveyAnswersLists() {
        surveyAnswersLists = new ArrayList<ArrayList<String>>();
        surveyAnswersLists.add(new ArrayList<String>(Arrays.asList("Python", "Java", "Word", "Excel")));
        surveyAnswersLists.add(new ArrayList<String>(Arrays.asList("Walmart", "Dollarama", "Rolex")));
        surveyAnswersLists.add(new ArrayList<String>(Arrays.asList("Foosball", "Tetris", "Poker", "Smash")));
        surveyAnswersLists.add(new ArrayList<String>(Arrays.asList("Soccer", "Boxing", "Running", "Swimming")));
    }

    /**
     * returns the survey question list
     * @return survey question arraylist
     */
    public ArrayList<String> getSurveyQuestionList() {
        return surveyQuestionList;
    }

    /**
     * Returns the potential survey answer list
     * @return potential survey answer arraylist
     */
    public ArrayList<ArrayList<String>> getSurveyAnswersLists() {
        return surveyAnswersLists;
    }
}
