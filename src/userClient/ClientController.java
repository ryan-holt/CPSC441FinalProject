package userClient;

import util.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is responsible for communicating with the server
 * and holding the LoginController
 * Overall the client controller is used for communication with
 * the server
 */
public class ClientController {

    /**
     * The output socket used for communication with master
     */
    private ObjectOutputStream socketOut;

    /**
     * Socket used for general input/output communication
     */
    private Socket aSocket;

    /**
     * The input socket used for receiving messages from master
     */
    private ObjectInputStream socketIn;

    /**
     * The name of the user
     */
    private String name;

    /**
     * BufferedReader to read in user input
     */
    BufferedReader inFromUser;


    /**
     * Constructs a Client controller object
     *
     * @param serverName name of server
     * @param portNumber port number
     */
    public ClientController(String serverName, int portNumber) {
        try {
            aSocket = new Socket(serverName, portNumber);

            socketIn = new ObjectInputStream(aSocket.getInputStream());
            socketOut = new ObjectOutputStream(aSocket.getOutputStream());
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

    /**
     * Communicates with the server by reading in name, survey questions, and sending out survey answers
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void communicateWithServer() throws IOException, ClassNotFoundException {
        Message msg = (Message)(socketIn.readObject());
        System.out.println(msg.getAction());
        msg.setAction(inFromUser.readLine());
        name = msg.getAction();
        writeObject(msg);
        SurveyQuestions incomingSurvey = (SurveyQuestions) socketIn.readObject();
        ArrayList<SurveyEntry> userSurveyAnswers = getSurveyAnswer(incomingSurvey);
        SurveyAnswer userAnswer = new SurveyAnswer(userSurveyAnswers);
        writeObject(userAnswer);
        System.out.println("Survey has been completed. Have a great day!");
    }

    /**
     * Prompts the user a list of survey questions and gets the answers
     * @param incomingSurvey
     * @return An ArrayList of all the answers to all the questions
     * @throws IOException
     */
    public ArrayList<SurveyEntry> getSurveyAnswer(SurveyQuestions incomingSurvey) throws IOException {
        ArrayList<String> surveyQuestionList = incomingSurvey.getSurveyQuestionList();
        ArrayList<ArrayList<String>> surveyAnswersLists = incomingSurvey.getSurveyAnswersLists();
        ArrayList<SurveyEntry> userAnswers = new ArrayList<SurveyEntry>();
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
                userAnswers.add(new SurveyEntry(name, i+1, currentQuestionResponses));
            }
        }
        return userAnswers;
    }

    /**
     * Writes the corresponding object to the output socket
     * @param obj The output object
     * @throws IOException
     */
    private void writeObject(Object obj) throws IOException {
        socketOut.writeObject(obj);
        socketOut.reset();
    }

}