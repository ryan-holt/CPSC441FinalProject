package userClient;

import util.*;
import util.sockethandler.ClientSocketHandler;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is responsible for communicating with the server
 * and holding the LoginController
 * Overall the client controller is used for communication with
 * the server
 */
public class ClientController implements MessageListener {

    private Socket socket;

    /**
     * BufferedReader to read in user input
     */
    BufferedReader inFromUser;

    private ClientSocketHandler clientSocketHandler;

    /**
     * Constructs a Client controller object
     *
     * @param serverName name of server
     * @param portNumber port number
     */
    public ClientController(String serverName, int portNumber) {
        try {
            socket = new Socket(serverName, portNumber);

	        clientSocketHandler = new ClientSocketHandler(socket, this, true);
            inFromUser = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * runs the client side
     *
     * @param args command line arguments
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ClientController cc = new ClientController("localhost", 9000);
        cc.communicateWithServer();
    }

	public Message handleMessage(Message msg) {
    	Message msgOut = new Message("quit");
    	switch(msg.getAction()) {

		    case "sendSurveyQuestions":
				msgOut = getSurveyAnswer((SurveyQuestions) msg);
		    	break;
		    case "finishedSurvey":
		    	msgOut = finishSurvey();
		    	break;
		    case "terminate":
		    	clientSocketHandler.stop(); // Server finally said to stop
				System.exit(-1);
			    break;
		    default:
		    	msgOut.setAction("terminate");
			    System.err.println("Error: ClientController does not recognize message with action " + msg.getAction() + ", terminating");
			    break;
	    }

	    return msgOut;
	}

    /**
     * Communicates with the server by reading in name, survey questions, and sending out survey answers
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void communicateWithServer() throws IOException, ClassNotFoundException {
		clientSocketHandler.setNextMsgOut(new Message("requestSurvey"));
		clientSocketHandler.communicate();
	    System.out.println();
    }
    /**
     * Prompts the user a list of survey questions and gets the answers
     * @param incomingSurvey
     * @return A SurveyAnswer object containing all the answers to all the questions
     * @throws IOException
     */
    public SurveyAnswer getSurveyAnswer(SurveyQuestions incomingSurvey) {
	    System.out.println("Please enter your name: ");
	    String user = null;
    	try {
		    user = inFromUser.readLine();
	    } catch (IOException e) {
    		e.printStackTrace();
	    }

        ArrayList<String> surveyQuestionList = incomingSurvey.getSurveyQuestionList();
        ArrayList<ArrayList<String>> surveyAnswersLists = incomingSurvey.getSurveyAnswersLists();
        ArrayList<SurveyEntry> userAnswers = new ArrayList<SurveyEntry>();
        for (int i = 0; i < surveyQuestionList.size(); i++) {
            boolean invalidResponse = true;
            while (invalidResponse) {
            	try {
		            System.out.println(surveyQuestionList.get(i));
		            ArrayList<String> currentQuestionResponses = new ArrayList<String>(Arrays.asList(inFromUser.readLine().split("\\s+")));
		            if (!surveyAnswersLists.get(i).containsAll(currentQuestionResponses)) {
			            System.out.println("Invalid response, please try again.");
			            continue;
		            }
		            invalidResponse = false;
		            userAnswers.add(new SurveyEntry(user, i + 1, currentQuestionResponses));
	            } catch (IOException e) {
            		e.printStackTrace();
	            }
            }
        }
        return new SurveyAnswer(userAnswers);
    }

	/**
	 * Sends a quit message and terminates connections
	 * @return a message with a quit action
	 */
	private Message finishSurvey() {
        System.out.println("Survey has been completed. Have a great day!");
        return new Message("quit");
    }
}