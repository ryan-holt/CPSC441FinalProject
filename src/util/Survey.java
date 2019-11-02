package util;
import java.util.Scanner;

public class Survey extends Message{
	SurveyQuestions surQues;
	SurveyAnswer surAns;
	Scanner input;
	
	
	public Survey() {
		surQues = new SurveyQuestions();
		surAns = new SurveyAnswer();
		input = new Scanner(System.in);
	}
	
	public void fillOutSurvey() {
		String userAns = "";
		String putToList = "";
		for(int i = 0; i < surQues.surveyQuestionList.size(); i++) {
			System.out.println(surQues.surveyQuestionList.get(i));
			userAns = input.nextLine();
			for
			surAns.answerList.add(userAns);
		}
	}

}
