package userClient;

import com.sun.deploy.util.SessionState;
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

    //MEMBER VARIABLES
    private Socket aSocket;
//    private ObjectOutputStream socketOut;
//    private ObjectInputStream socketIn;
    private String user;
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
            aSocket = new Socket(serverName, portNumber);

//            socketIn = new ObjectInputStream(aSocket.getInputStream());
//            socketOut = new ObjectOutputStream(aSocket.getOutputStream());
	        clientSocketHandler = new ClientSocketHandler(aSocket, this, false); // TODO Set this looping flag true
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
		System.out.println("!!! master has replied with: " + msg.getAction());

    	switch(msg.getAction()) {

		    case "sendSurveyQuestions":
		    	break;
	    }

	    return new Message("quit"); // FIXME replace
	}

    public void communicateWithServer() throws IOException, ClassNotFoundException {
        // TODO Replace with ClientSocketHandler code and delete
//        Message msg = (Message)(socketIn.readObject());
//        System.out.println(msg.getAction());
//        msg.setAction(inFromUser.readLine());
//        user = msg.getAction();
//        writeObject(msg);
//        SurveyQuestions incomingSurvey = (SurveyQuestions) socketIn.readObject();
//        ArrayList<SurveyEntry> userSurveyAnswers = getSurveyAnswer(incomingSurvey);
//        SurveyAnswer userAnswer = new SurveyAnswer(userSurveyAnswers);
//        writeObject(userAnswer);
//        System.out.println("Survey has been completed. Have a great day!");

		clientSocketHandler.setMsgOut(new Message("requestSurvey"));
		clientSocketHandler.communicate();
	    System.out.println();
    }

    //GETTERS AND SETTERS
//    public ObjectOutputStream getSocketOut() {
//        return socketOut;
//    }
//
//    public ObjectInputStream getSocketIn() {
//        return socketIn;
//    }

    public ArrayList<SurveyEntry> getSurveyAnswer(SurveyQuestions incomingSurvey) throws IOException {
        ArrayList<String> surveyQuestionList = incomingSurvey.getSurveyQuestionList();
        ArrayList<ArrayList<String>> surveyAnswersLists = incomingSurvey.getSurveyAnswersLists();
        ArrayList<SurveyEntry> userAnswers = new ArrayList<SurveyEntry>();
        //ArrayList<String> allResponses = new ArrayList<String>();
        for (int i = 0; i < surveyQuestionList.size(); i++) {
            boolean invalidResponse = true;
            while (invalidResponse) {
                System.out.println(surveyQuestionList.get(i));
                ArrayList<String> currentQuestionResponses = new ArrayList<String>(Arrays.asList(inFromUser.readLine().split("\\s+")));
                if (!surveyAnswersLists.get(i).containsAll(currentQuestionResponses)) {
                    System.out.println("Invalid response, please try again.");
                    continue;
                }
                invalidResponse = false;
                userAnswers.add(new SurveyEntry(user, i+1, currentQuestionResponses));
            }
        }
        return userAnswers;
    }

//    private void writeObject(Object obj) throws IOException {
//        socketOut.writeObject(obj);
//        socketOut.reset();
//    }

}