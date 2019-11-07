package Master;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import util.*;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class OLD_SocketHandler_again implements Runnable {

    /**
     * The socket used for general input and output communication
     */
    private Socket aSocket;

    /**
     * The input socket used for reading in messages from slave and client
     */
    private ObjectInputStream socketIn;

    /**
     * the output socket used for sending out messages to slave and client
     */
    private ObjectOutputStream socketOut;

    /**
     * Master Controller
     */
    private MasterController masterController;

    /**
     * File Handler used to handle files writing and reading into database
     */
    private FileHandler fileHandler;

    public OLD_SocketHandler_again(Socket s, MasterController masterController) {
        try {
            aSocket = s;
            setMasterController(masterController);
            socketOut = new ObjectOutputStream(aSocket.getOutputStream());


            printIPInfo();
        } catch (IOException e) {
            System.out.println("ServerCommController: Create ServerCommController Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        createInputStream();
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
            writeObject(messageToSend);
            responseMessage = (Message) socketIn.readObject();
            boolean endWhile = true;
            SurveyQuestions surQues = new SurveyQuestions();
            if (responseMessage.getAction().equalsIgnoreCase("admin")) {
                while (endWhile) {
                    messageToSend.setAction("Please enter one of the following actions: calculateCorrelation, " +
                            "listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
                    try {
                        writeObject(messageToSend);
                        responseMessage = (Message)socketIn.readObject();
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
            socketIn.close();
            socketOut.close();
            aSocket.close();
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
        writeObject(newSurvey);
        SurveyAnswer clientAnswer = (SurveyAnswer) socketIn.readObject();
        fileHandler = new FileHandler();
        fileHandler.writeArrayToFile(clientAnswer.getAnswer());
    }

    /**
     * Creates an input socket stream from server
     */
    public void createInputStream() {
        try {
            socketIn = new ObjectInputStream(aSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating server output stream");
            e.printStackTrace();
        }
    }

    /**
     * Prints the IP information (current IP address)
     */
    public void printIPInfo() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("You current IP address: " + ip);
        } catch (UnknownHostException e) {
            System.out.println("IP Print error");
            e.printStackTrace();
        }
    }

    /**
     * Sets the master controller
     * @param sc master controller object
     */
    public void setMasterController(MasterController sc) {
        masterController = sc; // 2-way association
    }

    /**
     * Closes the socket
     * @throws IOException
     */
    public void stop() throws IOException {
        aSocket.close();
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