package util;
import java.io.Serializable;
import java.util.Scanner;

public class Survey extends Message implements Serializable{
	SurveyQuestions surQues;
	SurveyAnswer surAns;
	Scanner input;
	
	
	public Survey() {
		surQues = new SurveyQuestions();
		surAns = new SurveyAnswer();
		input = new Scanner(System.in);
	}
	
	public SurveyAnswer getSurAns() {
		return surAns;
	}
	
	public void setSurAns(SurveyAnswer sa) {
		surAns = sa;
	}
	
	public void fillOutSurvey() {
		String userAns = "";
		String putToList = "";
		String[] userInput;
		boolean anyWrong = true;
		String[] valid = null;
		for(int i = 0; i < surQues.surveyQuestionList.size(); i++) {
			
			System.out.println(surQues.surveyQuestionList.get(i));
			userAns = input.nextLine();
        	userInput = userAns.split(" ");
        	//System.out.println("Lenght of input" + userInput.length);
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
					if(userInput[j].compareTo(valid[0]) == 0 || userInput[j].compareTo(valid[1]) == 0 || userInput[j].compareTo(valid[2]) == 0  || userInput[j].compareTo(valid[3]) == 0 ) {
						anyWrong = false;
					}
			}
			while(anyWrong) {
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
					if(userInput[j].compareTo(valid[0]) == 0 || userInput[j].compareTo(valid[1]) == 0 || userInput[j].compareTo(valid[2]) == 0  || userInput[j].compareTo(valid[3]) == 0 ) {
							anyWrong = false;
						} 
				}
			}	
			surAns.answerList.add(userAns);
			anyWrong = true;
		}
		input.close();
	}

}
