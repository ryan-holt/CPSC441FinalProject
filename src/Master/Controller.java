package Master;


import util.Message;
import util.SurveyAnswer;
import util.SurveyQuestions;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 *
 */
public class Controller implements Runnable {


    /**
     * File Handler used to handle files writing and reading into database
     */
    private FileHandler fileHandler;

    /**
     * Socket Handler object
     */
    private SocketHandler socketHandler;

    public Controller(ServerSocket serverSocket) throws IOException {
        fileHandler = new FileHandler();
        socketHandler = new SocketHandler(serverSocket.accept());
    }

    @Override
    public void run() {
        socketHandler.createInputStream();
        communicate();
    }

    /**
     * Continously reads in and sends out messages to the client and slaves.
     * Depending on the messages receives, different choices will be made
     */
    public void communicate() {
        ArrayList<String> responses = new ArrayList<String>();
        Message messageToSend = new Message("Please enter your name");
        Message responseMessage;
        try {
            socketHandler.writeObject(messageToSend);
            responseMessage = (Message) socketHandler.readObject();
            boolean endWhile = true;
            SurveyQuestions surQues = new SurveyQuestions();
            if (responseMessage.getAction().equalsIgnoreCase("admin")) {
                while (endWhile) {
                    messageToSend.setAction("Please enter one of the following actions: calculateCorrelation, " +
                            "listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
                    try {
                        socketHandler.writeObject(messageToSend);
                        responseMessage = (Message)socketHandler.readObject();
                        switch (responseMessage.getAction()) {
                            case "calculateCorrelation":
                                //Insert code to calculate correlations
                                break;
                            case "listHistoricalCorrelation":
                                //Insert code to do that
                                break;
                            case "viewHistoricalCorrelation":
                                //Insert code to do that
                                break;
                            case "quit":
                                endWhile = false;
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {

                    }
                }
            } else {
                createAndSendSurvey();
            }
            socketHandler.closeSockets();
            System.exit(1);
        } catch (Exception e) {
        }
    }

    /**
     * Creates the survey questions object and sends to the survey
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void createAndSendSurvey() throws IOException, ClassNotFoundException {
        SurveyQuestions newSurvey = new SurveyQuestions();
        socketHandler.writeObject(newSurvey);
        SurveyAnswer clientAnswer = (SurveyAnswer) socketHandler.readObject();
        fileHandler = new FileHandler();
        fileHandler.writeArrayToFile(clientAnswer.getAnswer());
    }


}
