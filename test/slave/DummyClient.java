package slave;

import util.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is used to mock up a connection to any server
 */
public class DummyClient {

	//MEMBER VARIABLES
	private ObjectOutputStream socketOut;
	private Socket socket;
	private ObjectInputStream socketIn;
	private String name;
	BufferedReader inFromUser;

	/**
	 * Constructs a Client controller object
	 *
	 * @param serverName name of server
	 * @param portNumber port number
	 */
	public DummyClient(String serverName, int portNumber) {
		try {
			socket = new Socket(serverName, portNumber);

			socketOut = new ObjectOutputStream(socket.getOutputStream());
			socketIn = new ObjectInputStream(socket.getInputStream());
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
		DummyClient client = new DummyClient("localhost", 9001);
		client.communicateWithServer();
	}

	public void communicateWithServer() throws IOException, ClassNotFoundException {
//		Message msgOut = new Message("test");
		Message msgOut = SlaveControllerTest.makeTestAssociationRuleRequest();
		System.out.println("DummyClient: sending message requestAssociationRules");


		writeObject(msgOut);
		System.out.println("DummyClient: waiting for response...");
		Message msgIn = (Message) socketIn.readObject();
		System.out.println("DummyClient: msgIn action is: " + msgIn.getAction());

		
		msgOut.setAction("quit");
		writeObject(msgOut);
		msgIn = (Message) socketIn.readObject();
		System.out.println("DummyClient: msgIn action is: " + msgIn.getAction());
		if (msgIn.getAction().equals("terminate")) {
			System.out.println("DummyClient: Received termination, dying");
			socketIn.close();
			socketOut.close();
			socket.close();
		}
	}

	private void writeObject(Object obj) throws IOException {
		socketOut.writeObject(obj);
		socketOut.reset();
	}

}