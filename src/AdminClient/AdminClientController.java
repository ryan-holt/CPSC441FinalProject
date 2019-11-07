package AdminClient;

import util.Message;

import java.io.*;
import java.net.*;

/**
 * This class is responsible for communicating with the server
 * and holding the LoginController
 * Overall the client controller is used for communication with
 * the server
 */
public class AdminClientController {

    //MEMBER VARIABLES
    private ObjectOutputStream socketOut;
    private Socket aSocket;
    private ObjectInputStream socketIn;
    BufferedReader inFromUser;


    /**
     * Constructs a Client controller object
     *
     * @param serverName name of server
     * @param portNumber port number
     */
    public AdminClientController(String serverName, int portNumber) {
        try {
            aSocket = new Socket(serverName, portNumber);

            socketOut = new ObjectOutputStream(aSocket.getOutputStream());
            socketIn = new ObjectInputStream(aSocket.getInputStream());
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
        AdminClientController cc = new AdminClientController("localhost", 9000);
        cc.communicateWithServer();
    }

    /**
     * Communicates with the server, by reading the user name
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void communicateWithServer() throws IOException, ClassNotFoundException {
        String line;
        Message ServerE;
        ServerE = (Message)(socketIn.readObject());
        System.out.println(ServerE.getAction());
        ServerE.setAction(inFromUser.readLine());
        writeObject(ServerE);
        ServerE = (Message)(socketIn.readObject());
        System.out.println(ServerE.getAction());
        ServerE.setAction(inFromUser.readLine());
        writeObject(ServerE);
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