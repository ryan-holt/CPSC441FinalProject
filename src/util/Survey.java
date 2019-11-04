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
		String[] userInput;
		boolean anyWrong = false;
		String[] valid = null;
		for(int i = 0; i < surQues.surveyQuestionList.size(); i++) {
			
			System.out.println(surQues.surveyQuestionList.get(i));
			userAns = input.nextLine();
        	userInput = userAns.split(" ");
        	valid = null;
			for(int j = 0; j < userInput.length; j++) {
				if(i == 0) {
					valid = surQues.validResponseQ1;
				} else if(i == 1) {
					valid = surQues.validResponseQ2;
				} else if(i == 2) {
					valid = surQues.validResponseQ3;
				} else if(i == 3) {
					valid = surQues.validResponseQ4;
				}
					if(userInput[j] != valid[0] ||userInput[j] != valid[1] || userInput[j] != valid[2] || userInput[j] != valid[3]) {
						anyWrong = true;
					}
			}
			while(anyWrong == true) {
				System.out.println("One of the choices was invalid! " + surQues.surveyQuestionList.get(i));
				userAns = input.nextLine();
	        	userInput = userAns.split(" ");
	        	valid = null;
				for(int j = 0; j < userInput.length; j++) {
					if(i == 0) {
						valid = surQues.validResponseQ1;
					} else if(i == 1) {
						valid = surQues.validResponseQ2;
					} else if(i == 2) {
						valid = surQues.validResponseQ3;
					} else if(i == 3) {
						valid = surQues.validResponseQ4;
					}
					if(userInput[j] != valid[0] ||userInput[j] != valid[1] || userInput[j] != valid[2] || userInput[j] != valid[3]) {
							anyWrong = true;
						}
				}
			}	
			surAns.answerList.add(userAns);
			anyWrong = false;
		}
	}

}
