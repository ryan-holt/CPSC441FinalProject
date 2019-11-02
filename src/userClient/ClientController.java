
import java.io.*;
import java.net.*;

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
        ClientController cc = new ClientController("10.13.71.24", 9000);
        cc.communicateWithServer();
    }

    public void communicateWithServer() throws IOException, ClassNotFoundException {
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

    }

    //GETTERS AND SETTERS
    public ObjectOutputStream getSocketOut() {
        return socketOut;
    }

    public ObjectInputStream getSocketIn() {
        return socketIn;
    }


}