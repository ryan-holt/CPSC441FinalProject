package Master;

import util.*;
import util.sockethandler.ClientSocketHandler;
import util.sockethandler.ServerSocketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is responsible for managing the thread pool as well as
 * the ServerCommunicationController. Everytime, a new client connects,
 * this class makes an instance of the ServerCommunicationController
 * for the client in a new thread hi
 */
public class MasterController implements MessageListener {

    /**
     * The socket port used for communication
     */
    private static final int SERVER_PORT = 9000;
    private static final String[] CLIENT_IPS = {"localhost"};
    private static final int[] CLIENT_PORTS = {9001};

    /**
     * Server socket used for socket communication between the master and the client
     */
    private ServerSocket serverSocket;

    private List<Socket> clientSockets;

    /**
     * Pool of threads used for multithreading
     */
    private ExecutorService pool;
    private ResettableCountDownLatch latch;

    private ServerSocketHandler serverSocketHandler;
    private List<ClientSocketHandler> clientSocketHandlers;

    public MasterController() {
        try {
			initializeClientSockets();

            serverSocket = new ServerSocket(SERVER_PORT);
            int threadCount = CLIENT_IPS.length;
            pool = Executors.newFixedThreadPool(threadCount);
            latch = new ResettableCountDownLatch(threadCount);
            System.out.println("Server is running");
            printIPInfo();
            System.out.println("********");
        } catch (IOException e) {
            System.out.println("ServerController: Create a new socket error");
            e.printStackTrace();
        }

    }

	/**
	 * Create new client sockets to connect to slave computers in the cluster
	 * @throws IOException
	 */
	private void initializeClientSockets() throws IOException {
	    clientSockets = new ArrayList<>();

	    for (int i = 0; i < CLIENT_IPS.length; i++) {
	    	String clientIP = CLIENT_IPS[i];
	    	int clientPort = CLIENT_PORTS[i];
	    	clientSockets.add(new Socket(clientIP, clientPort));
	    }
    }

    /**
     * Continuously checks for new clients to add to the thread pool
     */
    public void communicateWithClient() {
        try {
            while (true) {
	            ServerSocketHandler serverSocketHandler = new ServerSocketHandler(serverSocket.accept(), this);

                System.out.println("New Client Connected");

                pool.execute(serverSocketHandler);
            }
        } catch (IOException e) {
            System.out.println("ServerController: CommunicateWithClient error");
            e.printStackTrace();
        }
    }

    /**
     * Prints all the IP address information
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

    public Message handleMessage(Message msg) {
    	Message msgOut = new Message("");
	    switch (msg.getAction()) {
		    case "requestSurvey":
				msgOut = createSurveyQuestions();
		    	break;
		    case "sendSurveyAnswers":
		    	writeSurveyAnswerToFile((SurveyAnswer) msg);
		    	msgOut.setAction("finishedSurvey");
		    	break;
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
			    msgOut.setAction("terminate");
		    	break;
	        case "terminate":
				msgOut.setAction("terminate"); // TODO Handle quit (client dies) vs terminate (server dies) commands
			    break;
		    case "test":
		    	msgOut.setAction("masterControllerTestResponse");
		    	break;
		    default:
			    System.err.println("Error, unknown message action " + msg.getAction() + ", terminating");
			    msgOut.setAction("terminate");
			    break;
	    }

	    return msgOut;
    }

    private SurveyQuestions createSurveyQuestions() {
    	return new SurveyQuestions();
    }

    private void writeSurveyAnswerToFile(SurveyAnswer surveyAnswer) {
	    FileHandler fileHandler = new FileHandler();
	    try {
		    fileHandler.writeArrayToFile(surveyAnswer.getAnswer());
	    } catch (IOException e) {
		    System.err.println("Error: FileHandler failed to write survey answers to file");
	    	e.printStackTrace();
	    }
    }

	/**
	 * Append the appendList onto the sharedList. Used for shared resources in multithreading
	 * @param sharedList
	 * @param appendList
	 * @param <T>
	 */
	private synchronized <T> void addToSharedList(List<T> sharedList, List<T> appendList) {
    	sharedList.addAll(appendList);
    }

	/**
	 * Get the next entry from a shared list. Used for shared resources in multithreading
	 */
	private synchronized <T> T popSharedElement(List<T> sharedList) {
		return sharedList.remove(0);
    }

    public static void main(String[] args) {
        MasterController myServer = new MasterController();
        myServer.communicateWithClient();
    }
}