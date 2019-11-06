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

    private Socket aSocket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private MasterController masterController;
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
                SurveyQuestions newSurvey = new SurveyQuestions();
                writeObject(newSurvey);
                SurveyAnswer clientAnswer = (SurveyAnswer) socketIn.readObject();
                fileHandler = new FileHandler();
                fileHandler.writeArrayToFile(clientAnswer.getAnswer());
            }
            socketIn.close();
            socketOut.close();
            aSocket.close();
            System.exit(1);
        } catch (Exception e) {
        }
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

    //GETTERS AND SETTERS
    public void setMasterController(MasterController sc) {
        masterController = sc; // 2-way association
    }

    public void stop() throws IOException {
        aSocket.close();
    }

    private void writeObject(Object obj) throws IOException {
        socketOut.writeObject(obj);
        socketOut.reset();
    }
}