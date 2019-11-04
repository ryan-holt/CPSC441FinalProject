package userClient;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import util.Message;
import util.Survey;

/**
 * This class is responsible for communicating with the server
 * and holding the LoginController
 * Overall the client controller is used for communication with
 * the server
 */
public class ClientController {

    //MEMBER VARIABLES
    private ObjectOutputStream socketOut;
    private Socket aSocket;
    private ObjectInputStream socketIn;
    BufferedReader inFromUser;
    Scanner input;


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
        ClientController cc = new ClientController("192.168.86.34", 9000);
        cc.communicateWithServer();
    }

    public void communicateWithServer() throws IOException, ClassNotFoundException {
    	input = new Scanner(System.in);
    	Message first = new Message("Hello from client");
    	socketOut.writeObject(first);
    	socketOut.flush();
    	Message userMess = (Message)socketIn.readObject();
    	Message returnTo = new Message();
    	System.out.println(userMess.getAction());
    	String userIn = input.nextLine();
    	returnTo.setAction(userIn);
    	socketOut.reset();
    	//socketIn.reset();
    	socketOut.writeObject(returnTo);
    	Survey sur = new Survey();
    	socketOut.flush();
    	Message userX = (Message)socketIn.readObject();
    	System.out.println(userX.getAction());
    	/*
    	if(userMess.getAction().compareToIgnoreCase("Please Complete our survey") == 0) {
    		sur.fillOutSurvey();
    	} else {
    		System.out.println("Error");
    		System.exit(1);
    	}
    	*/
    	sur.fillOutSurvey();
    	socketOut.writeObject(sur.getSurAns());
    	socketOut.flush();
    	userMess = (Message)(socketIn.readObject());
    	if(userMess.getAction().compareToIgnoreCase("Recieved") == 0) {
    		System.out.println("Survey completed, Exiting");
    		socketOut.close();
    		socketIn.close();
    		aSocket.close();
    		System.exit(1);
    	}
    	/*
        String line;
        String ServerE;
        ServerE = (String)(socketIn.readObject());
        System.out.println("hi " + ServerE);
        line = inFromUser.readLine();
        socketOut.writeObject(line);
        ServerE = (String)(socketIn.readObject());
        System.out.println("hi " + ServerE);
        //System.out.println("Quesiton 2");
        line = inFromUser.readLine();
        socketOut.writeObject(line);
        ServerE = (String)(socketIn.readObject());
        System.out.println("hi " + ServerE);
        //System.out.println("Quesiton 3");
        line = inFromUser.readLine();
        socketOut.writeObject(line);
        ServerE = (String)(socketIn.readObject());
        System.out.println("hi " + ServerE);
        //System.out.println("Quesiton 3");
        line = inFromUser.readLine();
        socketOut.writeObject(line);
        */
    }

    //GETTERS AND SETTERS
    public ObjectOutputStream getSocketOut() {
        return socketOut;
    }

    public ObjectInputStream getSocketIn() {
        return socketIn;
    }


}