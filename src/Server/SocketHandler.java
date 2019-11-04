package Server;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import util.*;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 */
public class SocketHandler implements Runnable {

    private Socket aSocket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private ServerController serverController;

    public SocketHandler(Socket s, ServerController serverController) {
        try {
            aSocket = s;
            setServerController(serverController);

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
        //socketOut.writeObject("Please enter your name");
        Message action = new Message("Please enter your name");
        Message returnAction = new Message();
        	try {
        		Message first = (Message)socketIn.readObject();
        		socketOut.writeObject(action);
        		socketOut.flush();
        		returnAction = (Message)socketIn.readObject();
        		boolean endWhile = true;
        		Survey sur = new Survey();
        		if(returnAction.getAction().compareToIgnoreCase("admin") ==  0) {
        			action.setAction("Please enter one of the following actions: calculateCorrelation, listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
        			while(endWhile) {
        				try {
        					socketOut.writeObject(action);
        					socketOut.flush();
        					returnAction = (Message)socketIn.readObject();
        					switch(returnAction.getAction()) {
        						case "calculateCorrelation":
        							//Insert code to calculate correlations
        							action.setAction("Please enter one of the following actions: calculateCorrelation, listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
                					break;
        						case "listHistoricalCorrelation": 
        							//Insert code to do that
        							action.setAction("Please enter one of the following actions: calculateCorrelation, listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
                					break;
        						case "viewHistoricalCorrelation":
        							//Insert code to do that 
        							action.setAction("Please enter one of the following actions: calculateCorrelation, listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
                					break;
        						case "quit":
        							endWhile = false;
        							break;
        						default:
        							action.setAction("Invalid selction, please enter one of the following actions: calculateCorrelation, listHistoricalCorrelation, viewHistoricalCorrelation or Quit");
        							break;
        					}
        				} catch (Exception e) {
        					
        				}
        			}
        		} else {
        			action.setAction("Please Complete our survey");
        			System.out.println(action.getAction() + "hi");
        			socketOut.reset();
        			socketOut.writeObject(action);
        			socketOut.flush();
        			sur.setSurAns((SurveyAnswer)socketIn.readObject());
        			//sur = (Survey)socketIn.readObject();
        			action.setAction("Recieved");
        			socketOut.reset();
        			socketOut.writeObject(action);
        			socketOut.flush();
        			//socketOut.writeObject()
        		}
        		//Rn I've set server to close over user submits there survey
        		socketIn.close();
        		socketOut.close();
        		aSocket.close();
        		System.exit(1);
        	} catch (Exception e) {
        	}
        	/*
            try {
                socketOut.writeObject("First Questions");
                responses.add((String)socketIn.readObject());
                socketOut.writeObject("Second Questions");
                responses.add((String)socketIn.readObject());
                socketOut.writeObject("Third Questions");
                responses.add((String)socketIn.readObject());
                socketOut.writeObject("Forth Questions");
                responses.add((String)socketIn.readObject());
                for(int i = 0; i < responses.size(); i++) {
                    System.out.println("i " + responses.get(i));
                }
            } catch (Exception e) {
            }
            */
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
    public void setServerController(ServerController sc) {
        serverController = sc; // 2-way association
    }

    public void stop() throws IOException {
        aSocket.close();
    }
}