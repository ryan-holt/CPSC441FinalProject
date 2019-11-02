package util;

import java.util.ArrayList;

/**
 * Holds the survey questions
 * @author Richard Lee, Tyler Lam, Ryan Holt, Gary Wu
 * @since November 1st, 2019
 * @version 1.0
 */
public class SurveyQuestions extends Message{
    ArrayList<String> surveyQuestionList;

    public SurveyQuestions() {
        super("sendSurveyQuestions");
        surveyQuestionList = new ArrayList<String>();
        surveyQuestionList.add("What do you use at work? Please pick from Python, Java, Word or Excel");
        surveyQuestionList.add("Which of the following stores have you went to in the past month? Please pick from Walmart, Dollarama or Rolex and put spaces between them");
        surveyQuestionList.add("Which of the following is your favorite office hobby?  Please pick from Foosball, Tetris, Poker or Smash and put spaces between them");
        surveyQuestionList.add("What is your favorite physical activity?  Please pick from Soccer, Boxing, Running or Swimming and put spaces between them");
    }
/*
    public ArrayList<String> buildSurveyQuestionList() {
        surveyQuestionList.add("What do you use at work?");
        surveyQuestionList.add("Which of the following stores have you went to in the past month?");
        surveyQuestionList.add("Which of the following is your favorite office hobby?");
        surveyQuestionList.add("What is your favorite physical activity?");
        return surveyQuestionList;
    }
*/
    public ArrayList<String> getSurveyQuestionList() {
        return surveyQuestionList;
    }
}
